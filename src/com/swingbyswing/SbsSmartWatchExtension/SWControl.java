package com.swingbyswing.SbsSmartWatchExtension;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.swingbyswing.SbsSmartWatchExtension.helpers.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final int ROUND_LAYOUT = 3;
    private static final int HOLE_SELECTION_LAYOUT = 4;
    private static final int SCORE_ENTRY_LAYOUT = 5;
    private static final int CLUB_SELECTION_LAYOUT = 6;

    private static final int SCORE_ENTRY_SCORE_SUB_LAYOUT = 1;
    private static final int SCORE_ENTRY_PUTTS_SUB_LAYOUT = 2;
    private static final int SCORE_ENTRY_DRIVE_RESULT_SUB_LAYOUT = 3;
    private static final int SCORE_ENTRY_PENALTIES_SUB_LAYOUT = 4;
    private static final int SCORE_ENTRY_BUNKER_HIT_SUB_LAYOUT = 5;

    public static final int ACCURACY_BAD = 1;
    public static final int ACCURACY_MEDIUM = 2;
    public static final int ACCURACY_GOOD = 3;

    private static final int ROUND_ITEMS = 4;

    private Map<String, Object> _roundObject = null;
    private Map<String, Object> _locationObject = null;
    private Map<String, Object> _scoreEntryObject = null;

    private int _currentLayout = -1;
    private int _currentSubLayout = -1;
    private int _helpConfigItem = 1;
    private int _helpGettingStartedItem = 0;
    private int _helpDistanceItem = 0;
    private int _helpScorecardItem = 0;
    private int _helpClubTrackerItem = 0;

    public SWControl(final String hostAppPackageName, final Context context) {
        super(context, hostAppPackageName);

    }

    /** Override ControlExtension */

    @Override
    public void onResume() {
        setScreenState(Control.Intents.SCREEN_STATE_ON);

        try {
            showWaitingForRoundLayout();
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onObjectClick(ControlObjectClickEvent event) {
        int id = event.getLayoutReference();

        try {
            if (id == R.id.waiting_for_round_button) {
                showHelpLayout();
            }
            else {
                Log.e("SwingBySwing", ":: " + id);
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onSwipe(int direction) {
        super.onSwipe(direction);

        try {
            if (_currentLayout == HELP_LAYOUT) {
                if (direction == Control.Intents.SWIPE_DIRECTION_DOWN) {
                    deincremementHelpSubLayout();
                }
                else if (direction == Control.Intents.SWIPE_DIRECTION_UP) {
                    incrementHelpSubLayout();
                }
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onKey(int action, int keyCode, long timeStamp) {
        try {
            if (action == Control.Intents.KEY_ACTION_RELEASE && keyCode == Control.KeyCodes.KEYCODE_BACK) {
                if (_currentLayout == HELP_LAYOUT) {
                    if (_roundObject == null || _locationObject == null) {
                        showWaitingForRoundLayout();
                    }
                    else {
                        showRoundLayout();
                    }
                }
                else if (_currentLayout == HOLE_SELECTION_LAYOUT) {
                    showRoundLayout();
                }
                else {
                    stopRequest();
                }
            } else {
                super.onKey(action, keyCode, timeStamp);
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onRequestListItem(final int layoutReference, final int listItemPosition) {
        try {
            if (layoutReference == R.id.help_gallery) {
                SWHelpLayoutHelper.requestHelpGalleryItem(listItemPosition, this);
            }
            else if (layoutReference == R.id.round_gallery) {
                SWRoundLayoutHelper.requestRoundGalleryItem(listItemPosition, this, _roundObject, _locationObject);
            }
            else if (layoutReference == R.id.hole_selection_listview) {
                SWHoleSelectionLayoutHelper.requestHoleSelectionListViewItem(listItemPosition, this, _roundObject);
            }
            else if (layoutReference == R.id.score_entry_listview) {
                if (_currentSubLayout == SWScoreEntryLayoutHelper.SCORE_SUB_LAYOUT) {
                    SWScoreEntryLayoutHelper.requestScoreEntryScoreListViewItem(listItemPosition, this, _roundObject);
                }
                else if (_currentSubLayout == SWScoreEntryLayoutHelper.PUTTS_SUB_LAYOUT) {
                    SWScoreEntryLayoutHelper.requestScoreEntryPuttsListViewItem(listItemPosition, this);
                }
                else if (_currentSubLayout == SWScoreEntryLayoutHelper.DRIVE_RESULT_SUB_LAYOUT) {
                    SWScoreEntryLayoutHelper.requestScoreEntryDriveResultListViewItem(listItemPosition, this);
                }
                else if (_currentSubLayout == SWScoreEntryLayoutHelper.PENALTIES_SUB_LAYOUT) {
                    SWScoreEntryLayoutHelper.requestScoreEntryPenaltiesListViewItem(listItemPosition, this);
                }
                else if (_currentSubLayout == SWScoreEntryLayoutHelper.BUNKER_HIT_SUB_LAYOUT) {
                    SWScoreEntryLayoutHelper.requestScoreEntryBunkerHitListViewItem(listItemPosition, this);
                }
            }
            else if (layoutReference == R.id.club_selection_listview) {
                SWClubSelectionLayoutHelper.requestClubSelectionListViewItem(listItemPosition, this, _roundObject);
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    @Override
    public void onListItemSelected(ControlListItem listItem) {
        super.onListItemSelected(listItem);

        _currentSubLayout = listItem.listItemPosition;
    }

    @Override
    public void onListItemClick(ControlListItem listItem, int clickType, int itemLayoutReference) {
        super.onListItemClick(listItem, clickType, itemLayoutReference);

        try {
            if (_currentLayout == ROUND_LAYOUT) {
                if (itemLayoutReference == R.id.hole_selection_button) {
                    showHoleSelectionLayout();
                }
                else if (itemLayoutReference == R.id.distance_button) {
                    wakeGPS();
                }
                else if (itemLayoutReference == R.id.club_tracker_button) {
                    toggleClubTracker();
                }
            }
            else if (_currentLayout == HOLE_SELECTION_LAYOUT) {
                if (listItem.listItemPosition == 0) {
                    showScoreEntryLayout();
                    return;
                }

                List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)_roundObject.get("holes");
                Map<String, Object> holeMap = holeMaps.get(listItem.listItemPosition - 1);

                changeHole((Integer)holeMap.get("holeNum"));
                showRoundLayout();
            }
            else if (_currentLayout == SCORE_ENTRY_LAYOUT) {
                updateScoreEntryData(listItem.listItemPosition);
                updateScoreEntryLayout();
            }
            else if (_currentLayout == CLUB_SELECTION_LAYOUT) {
                List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)_roundObject.get("clubs");
                Map<String, Object> clubMap = clubMaps.get(listItem.listItemPosition - 1);

                startShot((Integer)clubMap.get("clubId"));
                showRoundLayout();
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }
    }

    /** Public static methods */

    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }

    /** Public methods */

    public void sendText(int itemLayoutReference, String text) {
        sendText(itemLayoutReference, text);
    }

    public void sendListItem(ControlListItem controlListItem) {
        sendListItem(controlListItem);
    }

    public Context getContext() {
        return mContext;
    }

    public void updateRoundObject(Map<String, Object> roundObject) throws Throwable {
        _roundObject = roundObject;

        if (_currentLayout == WAITING_FOR_ROUND_LAYOUT) {
            if (_roundObject != null && _locationObject != null) {
                showRoundLayout();
            }

            return;
        }

        if (_currentLayout != ROUND_LAYOUT) {
            return;
        }

        if (_roundObject == null || _locationObject == null) {
            showWaitingForRoundLayout();
            return;
        }

        SWRoundLayoutHelper.updateRoundLayout(this, _roundObject, _locationObject);
    }

    public void updateLocationObject(Map<String, Object> locationObject) throws Throwable {
        boolean locationAccuracyTypeChanged = false;

        if (locationObject != null && _locationObject != null) {
            int locationAccuracyType1 = (Integer)locationObject.get("locationAccuracyType");
            int locationAccuracyType2 = (Integer)_locationObject.get("locationAccuracyType");

            if (locationAccuracyType1 != locationAccuracyType2) {
                locationAccuracyTypeChanged = true;
            }
        }

        _locationObject = locationObject;

        if (_currentLayout == WAITING_FOR_ROUND_LAYOUT) {
            if (_roundObject != null && _locationObject != null) {
                showRoundLayout();
            }

            return;
        }

        if (_currentLayout != ROUND_LAYOUT) {
            return;
        }

        if (_roundObject == null || _locationObject == null) {
            showWaitingForRoundLayout();
            return;
        }

        if (locationAccuracyTypeChanged) {
            showRoundLayout();
        }
        else {
            SWRoundLayoutHelper.updateRoundLayout(this, _roundObject, _locationObject);
        }

    }

    /** Private methods */

    private void showWaitingForRoundLayout() throws Throwable {
        _currentLayout = WAITING_FOR_ROUND_LAYOUT;
        _currentSubLayout = 0;
        showLayout(R.layout.waiting_for_round_control, null);
    }

    private void showRoundLayout() throws Throwable {
        if (_currentLayout != ROUND_LAYOUT) {
            _currentLayout = ROUND_LAYOUT;
            _currentSubLayout = 1;
        }

        showLayout(R.layout.round_control, null);
        sendListCount(R.id.round_gallery, ROUND_ITEMS);

        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                sendListPosition(R.id.round_gallery, _currentSubLayout);
            }
        }, 100);

    }

    private void showHoleSelectionLayout() throws Throwable {
        _currentLayout = HOLE_SELECTION_LAYOUT;
        _currentSubLayout = 0;

        List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)_roundObject.get("holes");

        showLayout(R.layout.hole_selection_control, null);
        sendListCount(R.id.hole_selection_listview, holeMaps.size() + 1);
    }

    private void showScoreEntryLayout() throws Throwable {
        _currentLayout = SCORE_ENTRY_LAYOUT;
        _currentSubLayout = 0;

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
            showLayout(R.layout.no_score_entry_control, null);
        }
    }

    private void updateScoreEntryLayout() throws Throwable {
        List<Map<String, Object>> scoreEntryMaps = (List<Map<String, Object>>)_scoreEntryObject.get("scores");
        List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)_roundObject.get("scorecards");
        Map<String, Object> scoreEntryObject = scoreEntryMaps.get(scoreEntryMaps.size() - 1);
        Map<String, Object> scorecardMap = scorecardMaps.get(scoreEntryObject.size() - 1);

        //Check to see if score entry is complete for this player
        if (scoreEntryIsCompleteForPlayer(scorecardMap)) {
            //Check to see if score entry is complete
            if (scoreEntryMaps.size() == scorecardMaps.size()) {
                sendScoreEntry(_scoreEntryObject);
                showRoundLayout();
                return;
            }

            _currentSubLayout = 0;
            scorecardMap = scorecardMaps.get(scoreEntryObject.size());
            scoreEntryObject = new HashMap<String, Object>();
            scoreEntryObject.put("playerId", scorecardMap.get("playerId"));
            scoreEntryMaps.add(scoreEntryObject);
        }

        String scorecardType = (String)scorecardMap.get("scorecardType");
        int listCount = 0;
        int listPosition = 0;

        if (_currentSubLayout == 0) {
            _currentSubLayout = SCORE_ENTRY_SCORE_SUB_LAYOUT;
            listCount = SWScoreEntryLayoutHelper.SCORE_ITEMS;
            listPosition = SWScoreEntryLayoutHelper.getPar(_roundObject);
        }
        else if (_currentSubLayout == SCORE_ENTRY_SCORE_SUB_LAYOUT) {
            _currentSubLayout = SCORE_ENTRY_PUTTS_SUB_LAYOUT;
            listCount = SWScoreEntryLayoutHelper.PUTT_ITEMS;
            listPosition = 1;
        }
        else if (_currentSubLayout == SCORE_ENTRY_PUTTS_SUB_LAYOUT) {
            _currentSubLayout = SCORE_ENTRY_DRIVE_RESULT_SUB_LAYOUT;
            listCount = SWScoreEntryLayoutHelper.DRIVE_RESULT_ITEMS;
            listPosition = 0;
        }
        else if (_currentSubLayout == SCORE_ENTRY_DRIVE_RESULT_SUB_LAYOUT) {
            _currentSubLayout = SCORE_ENTRY_PENALTIES_SUB_LAYOUT;
            listCount = SWScoreEntryLayoutHelper.PENALTY_ITEMS;
            listPosition = 0;
        }
        else if (_currentSubLayout == SCORE_ENTRY_PENALTIES_SUB_LAYOUT) {
            _currentSubLayout = SCORE_ENTRY_BUNKER_HIT_SUB_LAYOUT;
            listCount = SWScoreEntryLayoutHelper.BUNKER_HIT_ITEMS;
            listPosition = 0;
        }

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.score_entry_title);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, scorecardMap.get("name") + "");
        bundles.add(text1Bundle);

        showLayout(R.layout.score_entry_control, bundles.toArray(new Bundle[bundles.size()]));
        sendListCount(R.id.score_entry_listview, listCount);

        final int finalListPosition = listPosition;
        SWThreadHelper.startOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendListPosition(R.id.score_entry_listview, finalListPosition);
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        }, 100);
    }

    private boolean scoreEntryIsCompleteForPlayer(Map<String, Object> scorecardMap) throws Throwable {
        String scorecardType = (String)scorecardMap.get("scorecardType");

        if (scorecardType.equalsIgnoreCase("friend")) {
            return true;
        }
        else if (scorecardType.equalsIgnoreCase("full")) {
            if (_currentSubLayout == SCORE_ENTRY_BUNKER_HIT_SUB_LAYOUT) {
                return true;
            }

            return false;
        }

        if (_currentSubLayout == SCORE_ENTRY_PUTTS_SUB_LAYOUT) {
            return true;
        }

        return false;
    }

    private void updateScoreEntryData(int listItemPosition) throws Throwable {
        List<Map<String, Object>> scoreEntryMaps = (List<Map<String, Object>>)_scoreEntryObject.get("scores");
        Map<String, Object> scoreEntryMap = scoreEntryMaps.get(scoreEntryMaps.size() - 1);

        if (_currentSubLayout == SCORE_ENTRY_SCORE_SUB_LAYOUT) {
            scoreEntryMap.put("score", listItemPosition + 1);
        }
        else if (_currentSubLayout == SCORE_ENTRY_PUTTS_SUB_LAYOUT) {
            scoreEntryMap.put("putts", listItemPosition);
        }
        else if (_currentSubLayout == SCORE_ENTRY_DRIVE_RESULT_SUB_LAYOUT) {
            if (listItemPosition == 0) {
                scoreEntryMap.put("drive", 4);
            }
            else if (listItemPosition == 1) {
                scoreEntryMap.put("drive", 3);
            }
            else if (listItemPosition == 2) {
                scoreEntryMap.put("drive", 5);
            }
            else if (listItemPosition == 3) {
                scoreEntryMap.put("drive", 6);
            }
            else if (listItemPosition == 4) {
                scoreEntryMap.put("drive", 7);
            }
        }
        else if (_currentSubLayout == SCORE_ENTRY_PENALTIES_SUB_LAYOUT) {
            scoreEntryMap.put("penalties", listItemPosition);
        }
        else if (_currentSubLayout == SCORE_ENTRY_SCORE_SUB_LAYOUT) {
            if (listItemPosition == 0) {
                scoreEntryMap.put("bunker", true);
            }
            else if (listItemPosition == 1) {
                scoreEntryMap.put("bunker", false);
            }
        }
    }

    private void toggleClubTracker() throws Throwable {
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
        _currentLayout = CLUB_SELECTION_LAYOUT;
        _currentSubLayout = 0;

        List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)_roundObject.get("clubs");

        showLayout(R.layout.club_selection_control, null);
        sendListCount(R.id.club_selection_listview, clubMaps.size() + 1);
    }

    private void showHelpLayout() throws Throwable {
        _currentLayout = HELP_LAYOUT;
        _currentSubLayout = 0;

        _helpConfigItem = 0;
        _helpGettingStartedItem = 0;
        _helpDistanceItem = 0;
        _helpScorecardItem = 0;
        _helpClubTrackerItem = 0;

        showLayout(R.layout.help_control, null);
        sendListCount(R.id.help_gallery, SWHelpLayoutHelper.HELP_ITEMS);
    }

    private void incrementHelpSubLayout() throws Throwable {
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
        SWService swService = (SWService)mContext;
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/round/start-shot?clubId=" + clubId);
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        swService.sendMessage(jsonMap);
    }

    private void endShot() throws Throwable {
        SWService swService = (SWService)mContext;
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/round/end-open-shot");
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        swService.sendMessage(jsonMap);
    }

    private void sendScoreEntry(Map<String, Object> scoreEntryMap) throws Throwable {
        SWService swService = (SWService)mContext;
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/v1/scorecards");
        jsonMap.put("method", "POST");
        jsonMap.put("data", scoreEntryMap);

        swService.sendMessage(jsonMap);
    }

    private void wakeGPS() throws Throwable {
        SWService swService = (SWService)mContext;
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://api/location/wake");
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        swService.sendMessage(jsonMap);
    }

    private void changeHole(int holeNum) throws Throwable {
        SWService swService = (SWService)mContext;
        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put("path", "http://gear/set-hole?holeNum=" + holeNum);
        jsonMap.put("method", "GET");
        jsonMap.put("data", null);

        swService.sendMessage(jsonMap);
    }
}
