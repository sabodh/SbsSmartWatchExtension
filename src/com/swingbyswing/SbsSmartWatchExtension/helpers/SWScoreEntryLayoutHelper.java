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
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWScoreEntryLayoutHelper {
    public static int SCORE_ITEMS = 15;
    public static int PUTT_ITEMS = 7;
    public static int DRIVE_RESULT_ITEMS = 5;
    public static int PENALTY_ITEMS = 5;
    public static int BUNKER_HIT_ITEMS = 2;

    public static int SCORE_AREA = 1;
    public static int PUTTS_AREA = 2;
    public static int DRIVE_RESULT_AREA = 3;
    public static int PENALTIES_AREA = 4;
    public static int BUNKER_HIT_AREA = 5;

    private static String[] scoreTerms = new String[]{"Triple", "Double", "Eagle", "Birdie", "Par", "Bogey", "Double", "Triple"};

    /** Public static methods */

    public static void requestScoreEntryScoreListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Bundle> bundles = new ArrayList<Bundle>();
        int buffer = 0;
        int par = getPar(roundObject);

        if (par == 5) {
            buffer = 1;
        }
        else if (par == 3) {
            buffer = 0;
        }
        else if (par == 4) {
            buffer = 2;
        }

        for (int i = 0; i < 3; i++) {
            Bundle text1Bundle = new Bundle();

            int position = (listItemPosition * 3) + i;
            int termIndex = position - buffer;

            if (listItemPosition == 0 && termIndex < 0) {
                continue;
            }

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, getScoreTerm(termIndex, par));
            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestScoreEntryPuttsListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Bundle> bundles = new ArrayList<Bundle>();
        int buffer = 2;

        for (int i = 0; i < 3; i++) {
            Bundle text1Bundle = new Bundle();

            int position = (listItemPosition * 3) + i;
            int putts = position - buffer;

            if (listItemPosition == 0 && i < buffer) {
                continue;
            }

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, putts + " putts");
            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestScoreEntryDriveResultListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Bundle> bundles = new ArrayList<Bundle>();

        for (int i = 0; i < 3; i++) {
            Bundle text1Bundle = new Bundle();
            int driveResultTermIndex = (listItemPosition * 3) + i;

            if (driveResultTermIndex > 4) {
                break;
            }

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, getDriveResultTerm(driveResultTermIndex));
            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestScoreEntryPenaltiesListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Bundle> bundles = new ArrayList<Bundle>();

        for (int i = 0; i < 3; i++) {
            Bundle text1Bundle = new Bundle();

            int position = (listItemPosition * 3) + i;

            if (i == 0) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            }
            else if (i == 1) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            }
            else if (i == 2) {
                text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_3);
            }

            text1Bundle.putString(Control.Intents.EXTRA_TEXT, position + " pnlty");
            bundles.add(text1Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static void requestScoreEntryBunkerHitListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Bundle> bundles = new ArrayList<Bundle>();

        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, getBunkerHitTerm(0));
        bundles.add(text1Bundle);

        text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, getBunkerHitTerm(1));
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItemExternal(controlListItem);
    }

    public static int getPar(Map<String, Object> roundObject) throws Throwable {
        int currentHoleNum = (Integer)roundObject.get("holeNum");
        List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)roundObject.get("holes");

        for (Map<String, Object> holeMap : holeMaps) {
            int thisHoleNum = (Integer)holeMap.get("holeNum");

            if (thisHoleNum == currentHoleNum) {
                return (Integer)holeMap.get("par");
            }
        }

        return -1;
    }

    /** Private static methods */

    private static String getScoreTerm(int score, int par) {
        int offset = score - par + 4;

        if (offset < scoreTerms.length) {
            return score + " - " + scoreTerms[offset];
        }
        else {
            return score + " - " + (score - par) + " over";
        }
    }

    private static String getDriveResultTerm(int position) {
        if (position == 0) {
            return "Left";
        }
        else if (position == 1) {
            return "Straight";
        }
        else if (position == 2) {
            return "Right";
        }
        else if (position == 3) {
            return "Thin";
        }
        else if (position == 4) {
            return "Fat";
        }

        return null;
    }

    private static String getBunkerHitTerm(int position) {
        if (position == 0) {
            return "Yes - bunker hit";
        }
        else {
            return "No";
        }
    }
}
