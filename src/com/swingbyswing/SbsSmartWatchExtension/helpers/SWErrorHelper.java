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

            if (throwable.getClass() == EOFException.class
                    || throwable.getClass() == OutOfMemoryError.class
                    || (throwable.getCause() != null && throwable.getCause().getClass() == SSLHandshakeException.class)
                    || (throwable.getCause() != null && throwable.getCause().getClass() == EOFException.class)) {
                return;
            }
            else if (throwable.getMessage() == null) {

            }
            else if (throwable.getMessage().contains("I/O error during system call")
                    || throwable.getMessage().contains("Connection reset by peer")
                    || throwable.getMessage().contains("No authentication challenges found")
                    || throwable.getMessage().contains("Socket is closed")
                    || throwable.getMessage().contains("Connection closed by peer")
                    || throwable.getMessage().contains("failed to connect to")
                    || throwable.getMessage().contains("Log in attempt aborted")
                    || throwable.getMessage().contains("Read timed out")
                    || throwable.getMessage().contains("Unexpected response code for CONNECT")
                    || throwable.getMessage().contains("Unable to resolve host")
                    || throwable.getMessage().contains("Received authentication challenge is null")
                    || throwable.getMessage().contains("An error occurred, please try again or contact the administrator.")
                    || throwable.getMessage().contains("unexpected end of stream")
                    || throwable.getMessage().contains("No search parameters were passed in")
                    || throwable.getMessage().equalsIgnoreCase("Not Found")
                    || throwable.getMessage().contains("The resource you requested was not found")
                    || throwable.getMessage().contains("<!DOCTYPE html>")
                    || throwable.getMessage().contains("Couldn't establish a secure connection.")
                    || throwable.getMessage().contains("java.io.EOFException")) {
                return;
            }

            SWThreadHelper.startRequestThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Crashlytics.logException(throwable);
                    } catch (Throwable throwable1) {
                        throwable1.printStackTrace();
                        //Don't let errors leak from here
                    }
                }
            });
        }
        catch (Throwable throwable1) {
            throwable1.printStackTrace();
        }
    }
}
