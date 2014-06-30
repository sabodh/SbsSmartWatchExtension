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
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWHoleSelectionLayoutHelper {

    public static void requestHoleSelectionListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.hole_selection_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;

        List<Bundle> bundles = new ArrayList<Bundle>();

        if (listItemPosition == 0) {
            controlListItem.dataXmlLayout = R.layout.list_item_1;

            Bundle text1Bundle = new Bundle();
            text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            text1Bundle.putString(Control.Intents.EXTRA_TEXT, "HOLE " + roundObject.get("holeNum") + " SCORE");
            bundles.add(text1Bundle);
        }
        else {
            controlListItem.dataXmlLayout = R.layout.list_item_2;

            List<Map<String, Object>> holeMaps = (List<Map<String, Object>>)roundObject.get("holes");
            Map<String, Object> holeMap = holeMaps.get(listItemPosition - 1);

            Bundle text1Bundle = new Bundle();
            text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
            text1Bundle.putString(Control.Intents.EXTRA_TEXT, "Hole " + holeMap.get("holeNum"));
            bundles.add(text1Bundle);

            Bundle text2Bundle = new Bundle();
            text2Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_2);
            text2Bundle.putString(Control.Intents.EXTRA_TEXT, "Par " + holeMap.get("par"));
            bundles.add(text2Bundle);
        }

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }
}
