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
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWClubSelectionLayoutHelper {

    public static void requestClubSelectionListViewItem(int listItemPosition, SWControl swControl, Map<String, Object> roundObject) throws Throwable {
        ControlListItem controlListItem = new ControlListItem();
        controlListItem.layoutReference = R.id.hole_selection_listview;
        controlListItem.listItemId = listItemPosition;
        controlListItem.listItemPosition = listItemPosition;
        controlListItem.dataXmlLayout = R.layout.list_item_1;

        List<Map<String, Object>> clubMaps = (List<Map<String, Object>>)roundObject.get("clubs");
        Map<String, Object> clubMap = clubMaps.get(listItemPosition - 1);

        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle text1Bundle = new Bundle();
        text1Bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.text_1);
        text1Bundle.putString(Control.Intents.EXTRA_TEXT, clubMap.get("type") + " - " + clubMap.get("average") + roundObject.get("measurementType"));
        bundles.add(text1Bundle);

        controlListItem.layoutData = bundles.toArray(new Bundle[bundles.size()]);

        swControl.sendListItem(controlListItem);
    }
}
