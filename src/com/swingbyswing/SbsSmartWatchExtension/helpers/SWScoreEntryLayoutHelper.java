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

    public static int SCORE_SUB_LAYOUT = 1;
    public static int PUTTS_SUB_LAYOUT = 2;
    public static int DRIVE_RESULT_SUB_LAYOUT = 3;
    public static int PENALTIES_SUB_LAYOUT = 4;
    public static int BUNKER_HIT_SUB_LAYOUT = 5;

    private static String[] scoreTerms = new String[]{"Triple", "Double", "Eagle", "Birdie", "Par", "Bogey", "Double", "Triple"};

    /** Public static methods */

    public static void requestScoreEntryScoreListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, getScoreTerm(listItemPosition + 1, getPar(roundObject)));
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }

    public static void requestScoreEntryPuttsListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, (listItemPosition) + " putts");
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }

    public static void requestScoreEntryDriveResultListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, getDriveResultTerm(listItemPosition));
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }

    public static void requestScoreEntryPenaltiesListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, (listItemPosition + 1) + " pnlty");
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }

    public static void requestScoreEntryBunkerHitListViewItem(int listItemPosition, SWControl swControl) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.score_entry_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, getBunkerHitTerm(listItemPosition));
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }

    public static int getPar(Map<String, Object> roundObject) throws Throwable {
        int currentholeNum = (Integer)roundObject.get("holeNum");
        List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)roundObject.get("holes");

        for (Map<String, Object> holeMap : holeMaps) {
            int thisHoleNum = (Integer)holeMap.get("holeNum");

            if (thisHoleNum == currentholeNum) {
                return (Integer)roundObject.get("par");
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
