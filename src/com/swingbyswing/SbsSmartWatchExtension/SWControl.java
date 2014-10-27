package com.swingbyswing.SbsSmartWatchExtension;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.swingbyswing.SbsSmartWatchExtension.helpers.*;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/26/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWControl extends ControlExtension {
    private static final int WAITING_FOR_ROUND_LAYOUT = 1;
    private static final int HELP_LAYOUT = 2;
    private static final int DISTANCE_LAYOUT = 3;
    private static final int SCORECARD_LAYOUT = 4;
    private static final int CLUB_TRACKER_LAYOUT = 5;
    private static final int SCORE_ENTRY_LAYOUT = 6;
    private static final int HOLE_SELECTION_LAYOUT = 7;
    private static final int CLUB_SELECTION_LAYOUT = 8;

    private static final int VIEW_DISTANCES_MENU_ITEM = -101;
    private static final int VIEW_SCORECARD_MENU_ITEM = -102;
    private static final int VIEW_CLUB_TRACKER_MENU_ITEM = -103;
    private static final int SHOW_GROSS_SCORE_ITEM = -104;
    private static final int SHOW_NET_SCORE_MENU_ITEM = -105;
    private static final int SHOW_POINTS_MENU_ITEM = -106;
    private static final int ENTER_SCORE_MENU_ITEM = -107;

    public static final int ACCURACY_BAD = 1;
    public static final int ACCURACY_MEDIUM = 2;
    public static final int ACCURACY_GOOD = 3;

    public static final int GROSS_SCORE = 1;
    public static final int NET_SCORE = 2;
    public static final int POINTS = 3;

    private Map<String, Object> _roundObject = null;
    private Map<String, Object> _locationObject = null;
    private Map<String, Object> _scoreEntryObject = null;

    private int _currentLayout = -1;
    private int _currentSubLayout = 0;
    private int _scoreEntryArea = 0;
    private int _helpConfigItem = 1;
    private int _helpGettingStartedItem = 0;
    private int _helpDistanceItem = 0;
    private int _helpScorecardItem = 0;
    private int _helpClubTrackerItem = 0;
    private int _scorecardType = GROSS_SCORE;
    private boolean _ignoreShotData = false;
    private boolean _ignoreNoShotData = false;
    private Date _distancesEnteredDate = new Date();

    public SWControl(final String hostAppPackageName, final Context context) {
        super(context, hostAppPackageName);

    }

    /** Override ControlExtension */

    @Override
    public void onResume() {
        setScreenState(Control.Intents.SCREEN_STATE_ON);

        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (_roundObject != null && _locationObject != null) {
                        showDistanceLayout();
                    }
                    else {
                        showWaitingForRoundLayout();
                    }

                    wakeGPS();
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                int id = event.getLayoutReference();

                try {
                    if (_future != null && _future.isDone() == false) {
                        _future.cancel(true);
                    }

                    if (id == R.id.waiting_for_round_button) {
                        showHelpLayout();
                    }
                    else if (id == R.id.hole_selection_button) {
                        showHoleSelectionLayout();
                    }
                    else if (id == R.id.distance_button) {
                        wakeGPS();
                    }
                    else if (id == R.id.club_tracker_button) {
                        toggleClubTracker();
                    }
                    else {
                        Log.e("SwingBySwing", ":: " + id);
                    }
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    @Override
    public void onSwipe(final int direction) {
        super.onSwipe(direction);

        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (_future != null && _future.isDone() == false) {
                        _future.cancel(true);
                    }

                    if (_currentLayout == HELP_LAYOUT) {
                        if (direction == Control.Intents.SWIPE_DIRECTION_DOWN) {
                            deincremementHelpSubLayout();
                        }
                        else if (direction == Control.Intents.SWIPE_DIRECTION_UP) {
                            incrementHelpSubLayout();
                        }
                    }
                    else if (_currentLayout == DISTANCE_LAYOUT) {
                        if (direction == Control.Intents.SWIPE_DIRECTION_RIGHT) {
                            showScorecardLayout();
                        }
                        else if (direction == Control.Intents.SWIPE_DIRECTION_LEFT) {
                            showClubTrackerLayout();
                        }
                    }
                    else if (_currentLayout == SCORECARD_LAYOUT) {
                        if (direction == Control.Intents.SWIPE_DIRECTION_RIGHT) {
                            showClubTrackerLayout();
                        }
                        else if (direction == Control.Intents.SWIPE_DIRECTION_LEFT) {
                            showDistanceLayout();
                        }
                    }
                    else if (_currentLayout == CLUB_TRACKER_LAYOUT) {
                        if (direction == Control.Intents.SWIPE_DIRECTION_RIGHT) {
                            showDistanceLayout();
                        }
                        else if (direction == Control.Intents.SWIPE_DIRECTION_LEFT) {
                            showScorecardLayout();
                        }
                    }
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    @Override
    public void onKey(final int action, final int keyCode, final long timeStamp) {
        if (action != Control.Intents.KEY_ACTION_RELEASE || (keyCode != Control.KeyCodes.KEYCODE_BACK && keyCode != Control.KeyCodes.KEYCODE_OPTIONS)) {
            super.onKey(action, keyCode, timeStamp);
            return;
        }
        else if (keyCode == Control.KeyCodes.KEYCODE_OPTIONS
                && (_currentLayout != DISTANCE_LAYOUT
                    && _currentLayout != SCORECARD_LAYOUT
                    && _currentLayout != CLUB_TRACKER_LAYOUT)) {
            super.onKey(action, keyCode, timeStamp);
            return;
        }

        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (action == Control.Intents.KEY_ACTION_RELEASE && keyCode == Control.KeyCodes.KEYCODE_BACK) {
                        if (_currentLayout == HELP_LAYOUT
                                || _currentLayout == HOLE_SELECTION_LAYOUT
                                || _currentLayout == CLUB_SELECTION_LAYOUT
                                || _currentLayout == SCORE_ENTRY_LAYOUT) {
                            if (_roundObject == null || _locationObject == null) {
                                showWaitingForRoundLayout();
                            }
                            else {
                                showDistanceLayout();
                            }
                        }
                        else {
                            stopRequest();
                        }
                    }
                    else if (action == Control.Intents.KEY_ACTION_RELEASE && keyCode == Control.KeyCodes.KEYCODE_OPTIONS) {
                        if (_currentLayout == DISTANCE_LAYOUT) {
                            showDistanceMenuLayout();
                        }
                        else if (_currentLayout == SCORECARD_LAYOUT) {
                            showScorecardMenuLayout();
                        }
                        else if (_currentLayout == CLUB_TRACKER_LAYOUT) {
                            showClubTrackerMenuLayout();
                        }
                    }
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    @Override
    public void onRequestListItem(final int layoutReference, final int listItemPosition) {
        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (layoutReference == R.id.help_gallery) {
                        if (listItemPosition == 0) {
                            SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, _helpConfigItem, SWControl.this);
                        }
                        else if (listItemPosition == 1) {
                            SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, _helpGettingStartedItem, SWControl.this);
                        }
                        else if (listItemPosition == 2) {
                            SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, _helpDistanceItem, SWControl.this);
                        }
                        else if (listItemPosition == 3) {
                            SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, _helpScorecardItem, SWControl.this);
                        }
                        else if (listItemPosition == 4) {
                            SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, _helpClubTrackerItem, SWControl.this);
                        }
                    } else if (layoutReference == R.id.scorecard_listview) {
                        SWRoundLayoutHelper.requestScorecardListViewItem(listItemPosition, SWControl.this, _roundObject, _scorecardType);
                    } else if (layoutReference == R.id.club_tracker_listview) {
                        SWRoundLayoutHelper.requestClubTrackerListViewItem(listItemPosition, SWControl.this, _roundObject);
                    } else if (layoutReference == R.id.hole_selection_listview) {
                        SWRoundLayoutHelper.requestHoleSelectionListViewItem(listItemPosition, SWControl.this, _roundObject);
                    } else if (layoutReference == R.id.score_entry_listview) {
                        if (_scoreEntryArea == SWScoreEntryLayoutHelper.SCORE_AREA) {
                            SWScoreEntryLayoutHelper.requestScoreEntryScoreListViewItem(listItemPosition, SWControl.this, _roundObject);
                        } else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PUTTS_AREA) {
                            SWScoreEntryLayoutHelper.requestScoreEntryPuttsListViewItem(listItemPosition, SWControl.this);
                        } else if (_scoreEntryArea == SWScoreEntryLayoutHelper.DRIVE_RESULT_AREA) {
                            SWScoreEntryLayoutHelper.requestScoreEntryDriveResultListViewItem(listItemPosition, SWControl.this);
                        } else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PENALTIES_AREA) {
                            SWScoreEntryLayoutHelper.requestScoreEntryPenaltiesListViewItem(listItemPosition, SWControl.this);
                        } else if (_scoreEntryArea == SWScoreEntryLayoutHelper.BUNKER_HIT_AREA) {
                            SWScoreEntryLayoutHelper.requestScoreEntryBunkerHitListViewItem(listItemPosition, SWControl.this);
                        }
                    } else if (layoutReference == R.id.club_selection_listview) {
                        SWRoundLayoutHelper.requestClubSelectionListViewItem(listItemPosition, SWControl.this, _roundObject);
                    }
                } catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    @Override
    public void onListItemSelected(ControlListItem listItem) {
        super.onListItemSelected(listItem);
        _currentSubLayout = listItem.listItemPosition;

        try {
            if (_future != null && _future.isDone() == false) {
                _future.cancel(true);
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onMenuItemSelected(final int menuItem) {
        super.onMenuItemSelected(menuItem);

        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (menuItem == VIEW_DISTANCES_MENU_ITEM) {
                        showDistanceLayout();
                    }
                    else if (menuItem == VIEW_SCORECARD_MENU_ITEM) {
                        showScorecardLayout();
                    }
                    else if (menuItem == VIEW_CLUB_TRACKER_MENU_ITEM) {
                        showClubTrackerLayout();
                    }
                    else if (menuItem == SHOW_GROSS_SCORE_ITEM) {
                        _scorecardType = GROSS_SCORE;
                        showScorecardLayout();
                    }
                    else if (menuItem == SHOW_NET_SCORE_MENU_ITEM) {
                        _scorecardType = NET_SCORE;
                        showScorecardLayout();
                    }
                    else if (menuItem == SHOW_POINTS_MENU_ITEM) {
                        _scorecardType = POINTS;
                        showScorecardLayout();
                    }
                    else if (menuItem == ENTER_SCORE_MENU_ITEM) {
                        showScoreEntryLayout();
                    }
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    Future _future = null;
    @Override
    public void onTouch(ControlTouchEvent event) {
        final int y = event.getY();

        if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
            _future = SWThreadHelper.startPromptThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int buttonHeight = 108 / 3;
                        int selectedIndex = 2;

                        if (y - 17 < 0) {
                            return;
                        }

                        for (int i = 0; i < 3; i++) {
                            if ((y - 17) < buttonHeight * (i + 1)) {
                                selectedIndex = (_currentSubLayout * 3) + i;
                                break;
                            }
                        }

                        if (_currentLayout == HOLE_SELECTION_LAYOUT) {
                            if (selectedIndex == 0) {
                                showScoreEntryLayout();
                                return;
                            }
                            else {
                                changeHole(selectedIndex);
                                showDistanceLayout();
                                return;
                            }
                        }
                        else if (_currentLayout == SCORE_ENTRY_LAYOUT) {
                            updateScoreEntryData(selectedIndex);
                            updateScoreEntryLayout();
                        }
                        else if (_currentLayout == CLUB_SELECTION_LAYOUT) {
                            List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)_roundObject.get("clubs");
                            Map<String, Object> clubMap = clubMaps.get(selectedIndex);

                            startShot((Integer)clubMap.get("clubId"));
                            showDistanceLayout();
                        }
                    }
                    catch (Throwable throwable) {
                        SWErrorHelper.handleError(throwable);
                    }
                }
            }, 50);
        }

        super.onTouch(event);
    }

    /** Public static methods */

    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }

    /** Public methods */

    public void sendTextExternal(final int itemLayoutReference, final String text) {
        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                sendText(itemLayoutReference, text);
            }
        });
    }

    public void sendListItemExternal(final ControlListItem controlListItem) {
        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                sendListItem(controlListItem);
            }
        });
    }

    public Context getContext() {
        return mContext;
    }

    public void updateRoundObject(final Map<String, Object> roundObject) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _roundObject = roundObject;

        if (_currentLayout == WAITING_FOR_ROUND_LAYOUT) {
            if (_roundObject != null && _locationObject != null) {
                showDistanceLayout();
            }

            return;
        }
        else if (_roundObject == null || _locationObject == null) {
            showWaitingForRoundLayout();
            return;
        }

        if (_currentLayout == DISTANCE_LAYOUT) {
            SWRoundLayoutHelper.updateDistanceLayout(SWControl.this, _roundObject, _locationObject, true);
        }
        else if (_currentLayout == SCORECARD_LAYOUT) {
            showScorecardLayout();
        }
        else if (_currentLayout == CLUB_TRACKER_LAYOUT) {
            showClubTrackerLayout();
        }
    }

    public void updateLocationObject(final Map<String, Object> locationObject) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        boolean locationAccuracyTypeChanged = false;
        boolean shouldUpdateClubTracker = true;

        if (locationObject != null && _locationObject != null) {
            int locationAccuracyType1 = (Integer)locationObject.get("locationAccuracyType");
            int locationAccuracyType2 = (Integer)_locationObject.get("locationAccuracyType");

            if (locationAccuracyType1 != locationAccuracyType2) {
                locationAccuracyTypeChanged = true;
            }
        }

        _locationObject = locationObject;

        if (_locationObject != null
                && (_roundObject == null || ((Integer)_locationObject.get("holeNum")).intValue() != (Integer)_roundObject.get("holeNum"))) {
            requestRoundData();
            return;
        }

        if (_ignoreShotData && _locationObject.get("shotDistance") != null) {
            shouldUpdateClubTracker = false;
        }
        else {
            _ignoreShotData = false;
        }

        if (_ignoreNoShotData && _locationObject.get("shotDistance") == null) {
            shouldUpdateClubTracker = false;
        }
        else {
            _ignoreNoShotData = false;
        }

        if (_currentLayout == WAITING_FOR_ROUND_LAYOUT) {
            if (_roundObject != null && _locationObject != null) {
                showDistanceLayout();
            }

            return;
        }
        else if (_roundObject == null || _locationObject == null) {
            showWaitingForRoundLayout();
            return;
        }

        if (_currentLayout == DISTANCE_LAYOUT || _currentLayout == SCORECARD_LAYOUT || _currentLayout == CLUB_TRACKER_LAYOUT) {
            if ((Boolean)_locationObject.get("isOnNextHole") == true
                    && ((Integer)_locationObject.get("holeNum")).intValue() == (Integer)_roundObject.get("holeNum")
                    && ((List<Map<String, Object>>)_roundObject.get("scorecards")).size() > 0
                    && new Date().getTime() > _distancesEnteredDate.getTime() + 5000) {
                showScoreEntryLayout();
                return;
            }
        }

        if (_currentLayout == DISTANCE_LAYOUT) {
            if (locationAccuracyTypeChanged) {
                showDistanceLayout();
            }
            else {
                SWRoundLayoutHelper.updateDistanceLayout(SWControl.this, _roundObject, _locationObject, shouldUpdateClubTracker);
            }
        }
    }

    /** Private methods */

    private void showWaitingForRoundLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = WAITING_FOR_ROUND_LAYOUT;
        _currentSubLayout = 0;

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.waiting_for_round_control, null);
            }
        });
    }

    private void showDistanceLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = DISTANCE_LAYOUT;
        _currentSubLayout = 0;

        final int locationAccuracyType = (Integer)_locationObject.get("locationAccuracyType");
        final List<Bundle> bundles = new ArrayList<Bundle>();

        Bundle holeBundle = new Bundle();
        holeBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.hole_selection_button);
        holeBundle.putString(Control.Intents.EXTRA_TEXT, _locationObject.get("holeNum") + "");
        bundles.add(holeBundle);

        Bundle parBundle = new Bundle();
        parBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.par_text);
        parBundle.putString(Control.Intents.EXTRA_TEXT, "PAR " + _locationObject.get("par"));
        bundles.add(parBundle);

        Bundle distanceBundle = new Bundle();
        distanceBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.distance_button);

        if (_locationObject.get("distance") != null) {
            distanceBundle.putString(Control.Intents.EXTRA_TEXT, _locationObject.get("distance") + "" + _roundObject.get("measurementType"));
        }
        else {
            distanceBundle.putString(Control.Intents.EXTRA_TEXT, "--");
        }

        bundles.add(distanceBundle);

        Bundle activeBundle = new Bundle();
        activeBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.active_text);

        if ((Boolean)_locationObject.get("isSleeping") == true) {
            activeBundle.putString(Control.Intents.EXTRA_TEXT, "sleeping");
        }
        else {
            activeBundle.putString(Control.Intents.EXTRA_TEXT, "--AWAKE--");
        }

        bundles.add(activeBundle);

        Bundle clubTrackerBundle = new Bundle();
        clubTrackerBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.club_tracker_button);

        if (_ignoreShotData) {
            clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, "ENDING");
        }
        else if (_ignoreNoShotData) {
            clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, "STARTING");
        }
        else if (_locationObject.get("shotDistance") != null) {
            String clubTrackerString;

            if (_locationObject.get("club") != null) {
                clubTrackerString = _locationObject.get("club") + " - " + _locationObject.get("shotDistance") + "" + _roundObject.get("measurementType");
            }
            else {
                clubTrackerString = "?? - " + _locationObject.get("shotDistance") + "" + _roundObject.get("measurementType");
            }

            clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, clubTrackerString);
        }
        else {
            clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, "--");
        }

        bundles.add(clubTrackerBundle);

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (locationAccuracyType == SWControl.ACCURACY_GOOD) {
                    showLayout(R.layout.distance_good_control, bundles.toArray(new Bundle[bundles.size()]));
                }
                else if (locationAccuracyType == SWControl.ACCURACY_MEDIUM) {
                    showLayout(R.layout.distance_medium_control, bundles.toArray(new Bundle[bundles.size()]));
                }
                else {
                    showLayout(R.layout.distance_bad_control, bundles.toArray(new Bundle[bundles.size()]));
                }
            }
        });
    }

    private void showScorecardLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = SCORECARD_LAYOUT;
        _currentSubLayout = 0;

        final List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)_roundObject.get("scorecards");

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.scorecard_control, null);

                if (scorecardMaps.size() > 3) {
                    sendListCount(R.id.scorecard_listview, 2);
                }
                else {
                    sendListCount(R.id.scorecard_listview, 1);
                }
            }
        });
    }

    private void showClubTrackerLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = CLUB_TRACKER_LAYOUT;
        _currentSubLayout = 0;

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.club_tracker_control, null);
                sendListCount(R.id.club_tracker_listview, 1);
            }
        });
    }

    private void showDistanceMenuLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        final List<Bundle> bundles = new ArrayList<Bundle>();

        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, VIEW_SCORECARD_MENU_ITEM);
        bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "View Scorecard");
        bundles.add(bundle);

        bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, VIEW_CLUB_TRACKER_MENU_ITEM);
        bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "View Tracked Shots");
        bundles.add(bundle);

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showMenu(bundles.toArray(new Bundle[bundles.size()]));
            }
        });
    }

    private void showScorecardMenuLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        final List<Bundle> bundles = new ArrayList<Bundle>();

        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, VIEW_DISTANCES_MENU_ITEM);
        bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "View Distances");
        bundles.add(bundle);

        List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)_roundObject.get("scorecards");

        if (scorecardMaps.size() > 0) {
            bundle = new Bundle();
            bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, SHOW_GROSS_SCORE_ITEM);
            bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Show Gross Score");
            bundles.add(bundle);

            if (scorecardMaps.get(0).get("points") != null) {
                bundle = new Bundle();
                bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, SHOW_POINTS_MENU_ITEM);
                bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Show Points");
                bundles.add(bundle);
            }
        }

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showMenu(bundles.toArray(new Bundle[bundles.size()]));
            }
        });
    }

    private void showClubTrackerMenuLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        final List<Bundle> bundles = new ArrayList<Bundle>();

        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, VIEW_DISTANCES_MENU_ITEM);
        bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "View Distances");
        bundles.add(bundle);

        bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_MENU_ITEM_ID, VIEW_SCORECARD_MENU_ITEM);
        bundle.putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "View Scorecard");
        bundles.add(bundle);

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showMenu(bundles.toArray(new Bundle[bundles.size()]));
            }
        });
    }

    private void showHoleSelectionLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = HOLE_SELECTION_LAYOUT;
        _currentSubLayout = 0;

        final List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)_roundObject.get("holes");

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.hole_selection_control, null);
                sendListCount(R.id.hole_selection_listview, (int) Math.ceil(((double) holeMaps.size() + 1) / 3.0));
            }
        });
    }

    private void showScoreEntryLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = SCORE_ENTRY_LAYOUT;
        _currentSubLayout = 0;
        _scoreEntryArea = 0;

        List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)_roundObject.get("scorecards");

        if (scorecardMaps.size() > 0) {
            Map<String, Object> scorecardMap = scorecardMaps.get(0);

            _scoreEntryObject = new HashMap<String, Object>();
            _scoreEntryObject.put("holeNum", _roundObject.get("holeNum"));

            List<Map<String, Object>> scoreEntryMaps = new ArrayList<Map<String, Object>>();
            Map<String, Object> scoreEntryObject = new HashMap<String, Object>();
            scoreEntryObject.put("playerId", scorecardMap.get("playerId"));
            scoreEntryMaps.add(scoreEntryObject);

            _scoreEntryObject.put("scores", scoreEntryMaps);

            updateScoreEntryLayout();
        }
        else {
            SWThreadHelper.startOnMainThread(new Runnable() {
                @Override
                public void run() {
                    showLayout(R.layout.no_score_entry_control, null);
                }
            });
        }
    }

    private void updateScoreEntryLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        final List<Map<String, Object>> scoreEntryMaps = (List<Map<String, Object>>)_scoreEntryObject.get("scores");
        final List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)_roundObject.get("scorecards");
        Map<String, Object> scoreEntryObject = scoreEntryMaps.get(scoreEntryMaps.size() - 1);
        Map<String, Object> scorecardMap = scorecardMaps.get(scoreEntryMaps.size() - 1);

        //Check to see if score entry is complete for this player
        if (scoreEntryIsCompleteForPlayer(scorecardMap)) {
            //Check to see if score entry is complete
            if (scoreEntryMaps.size() == scorecardMaps.size()) {
                sendScoreEntry(_scoreEntryObject);
                showDistanceLayout();
                return;
            }

            _scoreEntryArea = 0;
            scorecardMap = scorecardMaps.get(scoreEntryMaps.size());
            scoreEntryObject = new HashMap<String, Object>();
            scoreEntryObject.put("playerId", scorecardMap.get("playerId"));
            scoreEntryMaps.add(scoreEntryObject);
        }

        int listCount = 0;
        int listPosition = 0;

        if (_scoreEntryArea == 0) {
            _scoreEntryArea = SWScoreEntryLayoutHelper.SCORE_AREA;
            listCount = SWScoreEntryLayoutHelper.SCORE_ITEMS;

            int par = SWScoreEntryLayoutHelper.getPar(_roundObject);

            if (par < 4) {
                listPosition = 1;
            }
            else {
                listPosition = 2;
            }
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.SCORE_AREA) {
            _scoreEntryArea = SWScoreEntryLayoutHelper.PUTTS_AREA;
            listCount = SWScoreEntryLayoutHelper.PUTT_ITEMS;
            listPosition = 1;
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PUTTS_AREA) {
            _scoreEntryArea = SWScoreEntryLayoutHelper.DRIVE_RESULT_AREA;
            listCount = SWScoreEntryLayoutHelper.DRIVE_RESULT_ITEMS;
            listPosition = 0;
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.DRIVE_RESULT_AREA) {
            _scoreEntryArea = SWScoreEntryLayoutHelper.PENALTIES_AREA;
            listCount = SWScoreEntryLayoutHelper.PENALTY_ITEMS;
            listPosition = 0;
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PENALTIES_AREA) {
            _scoreEntryArea = SWScoreEntryLayoutHelper.BUNKER_HIT_AREA;
            listCount = SWScoreEntryLayoutHelper.BUNKER_HIT_ITEMS;
            listPosition = 0;
        }

        final List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.score_entry_title);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, "#" + _roundObject.get("holeNum") + " | " + scorecardMap.get("name"));
        bundles.add(text1Bundle);

        final int finalListCount = listCount;

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.score_entry_control, bundles.toArray(new Bundle[bundles.size()]));
                sendListCount(R.id.score_entry_listview, (int)Math.ceil((double)finalListCount / 3.0));
            }
        });

        _currentSubLayout = listPosition;
        final int finalListPosition = listPosition;
        final String finalName = "#" + _roundObject.get("holeNum") + " | " + scorecardMap.get("name");
        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendText(R.id.score_entry_title, finalName);
                    sendListPosition(R.id.score_entry_listview, finalListPosition);
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        }, 100);
    }

    private boolean scoreEntryIsCompleteForPlayer(Map<String, Object> scorecardMap) throws Throwable {
        String scorecardDepth = (String)scorecardMap.get("scorecardDepth");

        if (scorecardDepth.equalsIgnoreCase("friend")) {
            return true;
        }
        else if (scorecardDepth.equalsIgnoreCase("full")) {
            if (_scoreEntryArea == SWScoreEntryLayoutHelper.BUNKER_HIT_AREA) {
                return true;
            }

            return false;
        }

        if (_scoreEntryArea == SWScoreEntryLayoutHelper.PUTTS_AREA) {
            return true;
        }

        return false;
    }

    private void updateScoreEntryData(int listItemPosition) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        List<Map<String, Object>> scoreEntryMaps = (List<Map<String, Object>>)_scoreEntryObject.get("scores");
        Map<String, Object> scoreEntryMap = scoreEntryMaps.get(scoreEntryMaps.size() - 1);

        if (_scoreEntryArea == SWScoreEntryLayoutHelper.SCORE_AREA) {
            int par = SWScoreEntryLayoutHelper.getPar(_roundObject);

            if (par == 5) {
                Log.e("SwingBySwing", "score: " + (listItemPosition - 1));
                scoreEntryMap.put("score", listItemPosition - 1);
            }
            else if (par == 4) {
                Log.e("SwingBySwing", "score: " + (listItemPosition - 2));
                scoreEntryMap.put("score", listItemPosition - 2);
            }
            else {
                Log.e("SwingBySwing", "score: " + (listItemPosition));
                scoreEntryMap.put("score", listItemPosition);
            }
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PUTTS_AREA) {
            Log.e("SwingBySwing", "putts: " + (listItemPosition - 2));
            scoreEntryMap.put("putts", listItemPosition - 2);
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.DRIVE_RESULT_AREA) {
            if (listItemPosition == 0) { //Left
                Log.e("SwingBySwing", "drive: Left");
                scoreEntryMap.put("drive", 4);
            }
            else if (listItemPosition == 1) { //Straight
                Log.e("SwingBySwing", "drive: Straight");
                scoreEntryMap.put("drive", 3);
            }
            else if (listItemPosition == 2) { //Right
                Log.e("SwingBySwing", "drive: Right");
                scoreEntryMap.put("drive", 5);
            }
            else if (listItemPosition == 3) { //Thin
                Log.e("SwingBySwing", "drive: Thin");
                scoreEntryMap.put("drive", 6);
            }
            else if (listItemPosition == 4) { //Fat
                Log.e("SwingBySwing", "drive: Fat");
                scoreEntryMap.put("drive", 7);
            }
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.PENALTIES_AREA) {
            Log.e("SwingBySwing", "penalties: " + (listItemPosition));
            scoreEntryMap.put("penalties", listItemPosition);
        }
        else if (_scoreEntryArea == SWScoreEntryLayoutHelper.BUNKER_HIT_AREA) {
            if (listItemPosition == 0) {
                Log.e("SwingBySwing", "bunker: true");
                scoreEntryMap.put("bunker", true);
            }
            else if (listItemPosition == 1) {
                Log.e("SwingBySwing", "bunker: false");
                scoreEntryMap.put("bunker", false);
            }
        }
    }

    private void toggleClubTracker() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        if (_locationObject.get("shotDistance") == null) {
            if ((Boolean)_roundObject.get("isLooper") == false) {
                startShot(-1);
            }
            else {
                showClubSelectionLayout();
            }

            return;
        }
        else {
            endShot();
        }
    }

    private void showClubSelectionLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = CLUB_SELECTION_LAYOUT;
        _currentSubLayout = 0;

        final List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)_roundObject.get("clubs");

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.club_selection_control, null);
                sendListCount(R.id.club_selection_listview, (int)Math.ceil((double)clubMaps.size() / 3.0));
            }
        });
    }

    private void showHelpLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        _currentLayout = HELP_LAYOUT;
        _currentSubLayout = 0;

        _helpConfigItem = 0;
        _helpGettingStartedItem = 0;
        _helpDistanceItem = 0;
        _helpScorecardItem = 0;
        _helpClubTrackerItem = 0;

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                showLayout(R.layout.help_control, null);
                sendListCount(R.id.help_gallery, SWHelpLayoutHelper.HELP_ITEMS);
            }
        });
    }

    private void incrementHelpSubLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        if (_currentSubLayout == 0) { //Configuration
            if (_helpConfigItem >= SWHelpLayoutHelper.HELP_CONFIG_ITEMS - 1) {
                return;
            }

            _helpConfigItem = _helpConfigItem + 1;

            SWHelpLayoutHelper.updateConfigItem(_helpConfigItem, this);
        }
        else if (_currentSubLayout == 1) { //Getting started
            if (_helpGettingStartedItem >= SWHelpLayoutHelper.HELP_GETTING_STARTED_ITEMS - 1) {
                return;
            }

            _helpGettingStartedItem = _helpGettingStartedItem + 1;

            SWHelpLayoutHelper.updateGettingStartedItem(_helpGettingStartedItem, this);
        }
        else if (_currentSubLayout == 2) { //Distances
            if (_helpDistanceItem >= SWHelpLayoutHelper.HELP_DISTANCES_ITEMS - 1) {
                return;
            }

            _helpDistanceItem = _helpDistanceItem + 1;

            SWHelpLayoutHelper.updateDistanceItem(_helpDistanceItem, this);
        }
        else if (_currentSubLayout == 3) { //Scorecard
            if (_helpScorecardItem >= SWHelpLayoutHelper.HELP_SCORECARD_ITEMS - 1) {
                return;
            }

            _helpScorecardItem = _helpScorecardItem + 1;

            SWHelpLayoutHelper.updateScorecardItem(_helpScorecardItem, this);
        }
        else if (_currentSubLayout == 4) { //Club tracker
            if (_helpClubTrackerItem >= SWHelpLayoutHelper.HELP_CLUB_TRACKER_ITEMS - 1) {
                return;
            }

            _helpClubTrackerItem = _helpClubTrackerItem + 1;

            SWHelpLayoutHelper.updateClubTrackerItem(_helpClubTrackerItem, this);
        }
    }

    private void deincremementHelpSubLayout() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        if (_currentSubLayout == 0) { //Configuration
            if (_helpConfigItem <= 0) {
                return;
            }

            _helpConfigItem = _helpConfigItem - 1;

            SWHelpLayoutHelper.updateConfigItem(_helpConfigItem, this);
        }
        else if (_currentSubLayout == 1) { //Getting started
            if (_helpGettingStartedItem <= 0) {
                return;
            }

            _helpGettingStartedItem = _helpGettingStartedItem - 1;

            SWHelpLayoutHelper.updateGettingStartedItem(_helpGettingStartedItem, this);
        }
        else if (_currentSubLayout == 2) { //Distances
            if (_helpDistanceItem <= 0) {
                return;
            }

            _helpDistanceItem = _helpDistanceItem - 1;

            SWHelpLayoutHelper.updateDistanceItem(_helpDistanceItem, this);
        }
        else if (_currentSubLayout == 3) { //Scorecard
            if (_helpScorecardItem <= 0) {
                return;
            }

            _helpScorecardItem = _helpScorecardItem - 1;

            SWHelpLayoutHelper.updateScorecardItem(_helpScorecardItem, this);
        }
        else if (_currentSubLayout == 4) { //Club tracker
            if (_helpClubTrackerItem <= 0) {
                return;
            }

            _helpClubTrackerItem = _helpClubTrackerItem - 1;

            SWHelpLayoutHelper.updateClubTrackerItem(_helpClubTrackerItem, this);
        }
    }

    private void startShot(int clubId) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/round/start-shot?clubId=" + clubId);
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);

        _ignoreNoShotData = true;
        sendText(R.id.club_tracker_button, "STARTING");
    }

    private void endShot() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/round/end-open-shot");
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);

        _ignoreShotData = true;
        sendText(R.id.club_tracker_button, "ENDING");
    }

    private void sendScoreEntry(Map<String, Object> scoreEntryMap) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/v1/scorecards");
        jsonMap.put("method", "POST");
        jsonMap.put("data", scoreEntryMap);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);

        _distancesEnteredDate = new Date();
    }

    private void wakeGPS() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/location/wake");
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);
    }

    private void changeHole(int holeNum) throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://gear/set-hole?holeNum=" + holeNum);
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);
    }

    private void requestRoundData() throws Throwable {
        SWThreadHelper.verifyBackgroundOperation();

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/v1/rounds");
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonMap);

        Intent intent = new Intent();
        intent.setClass(mContext, SWBridgeService.class);
        intent.setAction("send_message");
        intent.putExtra("json_string", jsonString);
        mContext.startService(intent);
    }
}
