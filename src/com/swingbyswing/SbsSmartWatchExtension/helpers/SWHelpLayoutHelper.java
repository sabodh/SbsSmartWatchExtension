package com.swingbyswing.SbsSmartWatchExtension.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlListItem;
import com.swingbyswing.SbsSmartWatchExtension.R;
import com.swingbyswing.SbsSmartWatchExtension.SWControl;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/29/14
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWHelpLayoutHelper {
    public static final int HELP_ITEMS = 5;
    public static final int HELP_CONFIG_ITEMS = 4;
    public static final int HELP_GETTING_STARTED_ITEMS = 4;
    public static final int HELP_DISTANCES_ITEMS = 3;
    public static final int HELP_SCORECARD_ITEMS = 2;
    public static final int HELP_CLUB_TRACKER_ITEMS = 2;

    public static void requestHelpGalleryItem(int listItemPosition, int helpItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.help_gallery;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        Context context = swControl.getContext();
        Bundle bodyBundle = new Bundle();

        if (listItemPosition == 0) { //Configuration
            controlListItem.dataXmlLayout = R.layout.help_config_item;

            bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.help_config_body);

            if (helpItemPosition == 0) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_config_body_1));
            }
            else if (helpItemPosition == 1) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_config_body_2));
            }
            else if (helpItemPosition == 2) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_config_body_3));
            }
            else if (helpItemPosition == 3) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_config_body_4));
            }
        }
        else if (listItemPosition == 1) {  //Getting started
            controlListItem.dataXmlLayout = R.layout.help_getting_started_item;

            bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.help_getting_started_body);

            if (helpItemPosition == 0) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_getting_started_body_1));
            }
            else if (helpItemPosition == 1) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_getting_started_body_2));
            }
            else if (helpItemPosition == 2) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_getting_started_body_3));
            }
            else if (helpItemPosition == 3) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_getting_started_body_4));
            }
        }
        else if (listItemPosition == 2) {  //Distances
            controlListItem.dataXmlLayout = R.layout.help_distances_item;

            bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.help_distances_body);

            if (helpItemPosition == 0) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_distances_body_1));
            }
            else if (helpItemPosition == 1) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_distances_body_2));
            }
            else if (helpItemPosition == 2) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_distances_body_3));
            }
        }
        else if (listItemPosition == 3) {  //Scorecard
            controlListItem.dataXmlLayout = R.layout.help_scorecard_item;

            bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.help_scorecard_body);

            if (helpItemPosition == 0) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_scorecard_body_1));
            }
            else if (helpItemPosition == 1) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_scorecard_body_2));
            }
        }
        else if (listItemPosition == 4) {  //Club tracker
            controlListItem.dataXmlLayout = R.layout.help_club_tracker_item;

            bodyBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.help_club_tracker_body);

            if (helpItemPosition == 0) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_club_tracker_body_1));
            }
            else if (helpItemPosition == 1) {
                bodyBundle.putString(Control.Intents.EXTRA_TEXT, context.getString(R.string.help_club_tracker_body_2));
            }

        }

        controlListItem.layoutData = new Bundle[1];
        controlListItem.layoutData[0] = bodyBundle;

        swControl.sendListItemExternal(controlListItem);
    }

    public static void updateConfigItem(int helpConfigItem, SWControl swControl) throws Throwable {
        requestHelpGalleryItem(0, helpConfigItem, swControl);
    }

    public static void updateGettingStartedItem(int helpGettingStartedItem, SWControl swControl) throws Throwable {
        requestHelpGalleryItem(1, helpGettingStartedItem, swControl);
    }

    public static void updateDistanceItem(int helpDistanceItem, SWControl swControl) throws Throwable {
        requestHelpGalleryItem(2, helpDistanceItem, swControl);
    }

    public static void updateScorecardItem(int helpScorecardItem, SWControl swControl) throws Throwable {
        requestHelpGalleryItem(3, helpScorecardItem, swControl);
    }

    public static void updateClubTrackerItem(int helpClubTrackerItem, SWControl swControl) throws Throwable {
        requestHelpGalleryItem(4, helpClubTrackerItem, swControl);
    }
}
