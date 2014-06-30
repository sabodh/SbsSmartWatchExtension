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

    public static void updateRoundLayout(SWControl swControl, Map<String, Object> roundObject, Map<String, Object> locationObject) throws Throwable {
        List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)roundObject.get("scorecards");
        int i = 0;

        //Scorecards
        for (Map<String, Object> scorecardMap : scorecardMaps) {
            if (i == 0) {
                swControl.sendText(R.id.scorecard_player_1, scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
            }
            else if (i == 1) {
                swControl.sendText(R.id.scorecard_player_2, scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
            }
            else if (i == 2) {
                swControl.sendText(R.id.scorecard_player_3, scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
            }
            else if (i == 3) {
                swControl.sendText(R.id.scorecard_player_4, scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
            }

            i++;
        }

        //Distance info
        swControl.sendText(R.id.hole_selection_button, locationObject.get("holeNum") + "");
        swControl.sendText(R.id.par_text, "PAR " + locationObject.get("par"));
        swControl.sendText(R.id.distance_button, locationObject.get("distance") + "" + roundObject.get("measurementType"));

        if ((Boolean)locationObject.get("isSleeping") == true) {
            swControl.sendText(R.id.active_text, "GPS asleep");
        }
        else {
            swControl.sendText(R.id.active_text, "GPS awake");
        }

        if (locationObject.get("shotDistance") != null) {
            String clubTrackerString;

            if (locationObject.get("club") != null) {
                clubTrackerString = locationObject.get("club") + " - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
            }
            else {
                clubTrackerString = "?? - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
            }

            swControl.sendText(R.id.club_tracker_button, clubTrackerString);
        }
        else {
            swControl.sendText(R.id.club_tracker_button, "--");
        }

        //Shots
        List<Map<String, Object>> clubShotMaps = (List<Map<String, Object>>)roundObject.get("clubShots");

        i = 0;
        for (Map<String, Object> clubShotMap : clubShotMaps) {
            String clubShotString = clubShotMap.get("club") + " - " + clubShotMap.get("distance") + roundObject.get("measurementType");

            if (i == 0) {
                swControl.sendText(R.id.shot_1, clubShotString);
            }
            else if (i == 1) {
                swControl.sendText(R.id.shot_2, clubShotString);
            }
            else if (i == 2) {
                swControl.sendText(R.id.shot_3, clubShotString);
            }
            else {
                swControl.sendText(R.id.shot_4, clubShotString);
                break;
            }

            i++;
        }
    }

    public static void requestRoundGalleryItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject, Map<String, Object> locationObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.round_gallery;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();

        if (listItemPosition == 0) { //Scorecard
            controlListItem.dataXmlLayout = R.layout.round_scorecard_item;

            List<Map<String, Object>> scorecardMaps = (List<Map<String, Object>>)roundObject.get("scorecards");

            for (Map<String, Object> scorecardMap : scorecardMaps) {
                Bundle scoreBundle = new Bundle();

                if (bundles.size() == 0) {
                    scoreBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.scorecard_player_1);
                }
                else if (bundles.size() == 1) {
                    scoreBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.scorecard_player_2);
                }
                else if (bundles.size() == 2) {
                    scoreBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.scorecard_player_3);
                }
                else {
                    scoreBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.scorecard_player_4);
                }

                scoreBundle.putString(Control.Intents.EXTRA_TEXT, scorecardMap.get("gross") + " - " + scorecardMap.get("name"));
                bundles.add(scoreBundle);
            }
        }
        else if (listItemPosition == 1) { //Distances
            int locationAccuracyType = (Integer)locationObject.get("locationAccuracyType");

            if (locationAccuracyType == SWControl.ACCURACY_GOOD) {
                controlListItem.dataXmlLayout = R.layout.round_distances_good_item;
            }
            else if (locationAccuracyType == SWControl.ACCURACY_MEDIUM) {
                controlListItem.dataXmlLayout = R.layout.round_distances_medium_item;
            }
            else {
                controlListItem.dataXmlLayout = R.layout.round_distances_bad_item;
            }

            Bundle holeBundle = new Bundle();
            holeBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.hole_selection_button);
            holeBundle.putString(Control.Intents.EXTRA_TEXT, locationObject.get("holeNum") + "");
            bundles.add(holeBundle);

            Bundle parBundle = new Bundle();
            parBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.par_text);
            parBundle.putString(Control.Intents.EXTRA_TEXT, "PAR " + locationObject.get("par"));
            bundles.add(parBundle);

            Bundle distanceBundle = new Bundle();
            distanceBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.distance_button);
            distanceBundle.putString(Control.Intents.EXTRA_TEXT, locationObject.get("distance") + "" + roundObject.get("measurementType"));
            bundles.add(distanceBundle);

            Bundle activeBundle = new Bundle();
            activeBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.active_text);

            if ((Boolean)locationObject.get("isSleeping") == true) {
                activeBundle.putString(Control.Intents.EXTRA_TEXT, "GPS asleep");
            }
            else {
                activeBundle.putString(Control.Intents.EXTRA_TEXT, "GPS awake");
            }

            bundles.add(activeBundle);

            Bundle clubTrackerBundle = new Bundle();
            clubTrackerBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.club_tracker_button);

            if (locationObject.get("shotDistance") != null) {
                String clubTrackerString;

                if (locationObject.get("club") != null) {
                    clubTrackerString = locationObject.get("club") + " - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
                }
                else {
                    clubTrackerString = "?? - " + locationObject.get("shotDistance") + "" + roundObject.get("measurementType");
                }

                clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, clubTrackerString);
            }
            else {
                clubTrackerBundle.putString(Control.Intents.EXTRA_TEXT, "--");
            }

            bundles.add(clubTrackerBundle);
        }
        else if (listItemPosition == 2) { //Club Tracker
            controlListItem.dataXmlLayout = R.layout.round_club_tracker_item;

            List<Map<String, Object>> clubShotMaps = (List<Map<String, Object>>)roundObject.get("clubShots");

            for (Map<String, Object> clubShotMap : clubShotMaps) {
                Bundle clubShotBundle = new Bundle();

                if (bundles.size() == 0) {
                    clubShotBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.shot_1);
                }
                else if (bundles.size() == 1) {
                    clubShotBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.shot_2);
                }
                else if (bundles.size() == 2) {
                    clubShotBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.shot_3);
                }
                else {
                    clubShotBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.shot_4);
                    break;
                }

                clubShotBundle.putString(Control.Intents.EXTRA_TEXT, clubShotMap.get("club") + " - " + clubShotMap.get("distance") + roundObject.get("measurementType"));
                bundles.add(clubShotBundle);
            }
        }
        else if (listItemPosition == 3) { //Options
            controlListItem.dataXmlLayout = R.layout.round_options_item;
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }
}
