package com.swingbyswing.SbsSmartWatchExtension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        intent.setClass(context, SWExtensionService.class);
        context.startService(intent);
    }
}
