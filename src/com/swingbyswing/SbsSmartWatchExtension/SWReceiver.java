package com.swingbyswing.SbsSmartWatchExtension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.sonyericsson.extras.liveware.aef.control.Control;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/25/14
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
//        intent.setClass(context, SmartWatchService.class);
//        context.startService(intent);
    }
}

//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: aea_package_name
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: aha_package_name
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: data_xml_layout
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: layout_reference
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: click_type
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: eventType
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: list_item_position
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: list_item_id
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: extension_key
//        06-27 22:18:50.229: ERROR/SwingBySwing(22331): onReceive: list_item_layout_reference