package com.swingbyswing.SbsSmartWatchExtension.helpers;

import android.util.Log;

import javax.net.ssl.SSLHandshakeException;
import java.io.EOFException;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/27/14
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWErrorHelper {
    public static void handleError(final Throwable throwable) {
        try {
            if (throwable == null) {
                return;
            }

            Log.e("SwingBySwing", throwable.toString());
            throwable.printStackTrace();

//            SWThreadHelper.startRequestThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Crashlytics.logException(throwable);
//                    } catch (Throwable throwable1) {
//                        throwable1.printStackTrace();
//                        //Don't let errors leak from here
//                    }
//                }
//            });
        }
        catch (Throwable throwable1) {
            throwable1.printStackTrace();
        }
    }
}
