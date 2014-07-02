package com.swingbyswing.SbsSmartWatchExtension.helpers;

import android.os.Bundle;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.swingbyswing.SbsSmartWatchExtension.R;
import com.swingbyswing.SbsSmartWatchExtension.SWControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/29/14
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWRoundLayoutHelper {

    public static void updateDistanceLayout(SWControl swControl, Map<String, Object> roundObject, Map<String, Object> locationObject, boolean shouldUpdateClubTracker) throws Throwable {
        //Distance info
        swControl.sendTextExternal(R.id.hole_selection_button, locationObject.get("holeNum") + "");
        swControl.sendTextExternal(R.id.par_text, "PAR " + locationObject.get("par"));
        swControl.sendTextExternal(R.id.distance_button, locationObject.get("distance") + "" + roundObject.get("measurementType"));

        if ((Boolean)locationObject.get("isSleeping") == true) {
            swControl.sendTextExternal(R.id.active_text, "sleeping");
        }
        else {
            swControl.sendTextExternal(R.id.active_text, "--AWAKE--");
        }

        if (shouldUpdateClubTracker) {
            if (locationObject.get("shotDistance") != null) {
                String clubTrackerString;

                if (locationObject.get("club") != null) {
                    clubTrackerString = locationObject.get("club") + " - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
                }
                else {
                    clubTrackerString = "?? - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
                }

                swControl.sendTextExternal(R.id.club_tracker_button, clubTrackerString);
            }
            else {
                swControl.sendTextExternal(R.id.club_tracker_button, "--");
            }
        }
    }

    public static void requestScorecardListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject, int scorecardType) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.scorecard_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)roundObject.get("scorecards");
        List<Bundle> bundles = new ArrayList<Bundle>();

        for (int i = 0; i < 3; i++) {
            if (i > scorecardMaps.size() - 1) {
                break;
            }

            Map<String, Object> scorecardMap = scorecardMaps.get(i);

            Bundle text1Bundle = new Bundle();

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            if (scorecardType == SWControl.NET_SCORE) {
                text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  " + scorecardMap.get("net") + " - " + scorecardMap.get("name"));
            }
            else if (scorecardType == SWControl.POINTS) {
                text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  " + scorecardMap.get("points") + " - " + scorecardMap.get("name"));
            }
            else {
                text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  " + scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
            }

            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestClubTrackerListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.club_tracker_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Map<String, Object>> clubShotMaps = (List<Map<String, Object>>)roundObject.get("clubShots");
        List<Bundle> bundles = new ArrayList<Bundle>();

        for (int i = 0; i < 3; i++) {
            if (i > clubShotMaps.size() - 1) {
                break;
            }

            Map<String, Object> clubShotMap = clubShotMaps.get(i);

            Bundle text1Bundle = new Bundle();

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  " + clubShotMap.get("club") + " - " + clubShotMap.get("distance") + roundObject.get("measurementType"));

            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestHoleSelectionListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.hole_selection_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)roundObject.get("holes");
        List<Bundle> bundles = new ArrayList<Bundle>();
        int start = 0;

        if (listItemPosition == 0) {
            start = 1;
            Bundle text1Bundle = new Bundle();
            text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  HOLE " + roundObject.get("holeNum") + " SCORE");
            bundles.add(text1Bundle);
        }

        for (int i = start; i < 3; i++) {
            int holeIndex = (listItemPosition * 3) + i - 1;

            if (holeIndex > holeMaps.size() - 1) {
                break;
            }

            Map<String, Object> holeMap = holeMaps.get(holeIndex);

            Bundle text1Bundle = new Bundle();

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, "  Hole " + holeMap.get("holeNum") + " / Par " + holeMap.get("par"));

            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestClubSelectionListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.club_selection_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)roundObject.get("clubs");
        List<Bundle> bundles = new ArrayList<Bundle>();
        String measurementType = (String)roundObject.get("measurementType");

        for (int i = 0; i < 3; i++) {
            int clubIndex = (listItemPosition * 3) + i;

            if (clubIndex > clubMaps.size() - 1) {
                break;
            }

            Map<String, Object> clubMap = clubMaps.get(clubIndex);

            Bundle text1Bundle = new Bundle();

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, clubMap.get("type") + " - " + clubMap.get("average") + measurementType);

            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }
}
