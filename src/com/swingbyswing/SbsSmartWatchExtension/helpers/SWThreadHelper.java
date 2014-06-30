package com.swingbyswing.SbsSmartWatchExtension.helpers;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/27/14
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWThreadHelper {
    private static ScheduledExecutorService _promptExecutorService = Executors.newScheduledThreadPool(8);
    private static ScheduledExecutorService _requestExecutorService = Executors.newScheduledThreadPool(2);
    private static ScheduledExecutorService _mapAssetExecutorService = Executors.newScheduledThreadPool(1);

    private static final Object _lock = new Object();

    public SWThreadHelper() {

    }

    /*** Static methods **/

    public static final void verifyBackgroundOperation() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            SWErrorHelper.handleError(new Throwable("A background operation is running on the main thread"));
        }
    }

    public static final void verifyForegroundOperation() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            SWErrorHelper.handleError(new Throwable("A foreground operation is running on a background thread"));
        }
    }

    public static final Future startPromptThread(Runnable runnable) {
        return startPromptThread(runnable, 0);
    }

    public static final Future startPromptThread(Runnable runnable, long milliseconds) {
        if (milliseconds <= 0) {
            return _promptExecutorService.submit(runnable);
        }
        else {
            return _promptExecutorService.schedule(runnable, milliseconds, TimeUnit.MILLISECONDS);
        }
    }

    public static final Future startRequestThread(Runnable runnable) {
        return startRequestThread(runnable, 0);
    }

    public static final Future startRequestThread(Runnable runnable, long milliseconds) {
        if (milliseconds <= 0) {
            return _requestExecutorService.submit(runnable);
        }
        else {
            return _requestExecutorService.schedule(runnable, milliseconds, TimeUnit.MILLISECONDS);
        }
    }

    public static final Future startMapAssetThread(Runnable runnable) {
        return startMapAssetThread(runnable, 0);
    }

    public static final Future startMapAssetThread(Runnable runnable, long milliseconds) {
        synchronized (_lock) {
            if (_mapAssetExecutorService == null || _mapAssetExecutorService.isShutdown()) {
                return null;
            }

            if (milliseconds <= 0) {
                return _mapAssetExecutorService.submit(runnable);
            }
            else {
                return _mapAssetExecutorService.schedule(runnable, milliseconds, TimeUnit.MILLISECONDS);
            }
        }
    }

    public static final Handler startOnMainThread(Runnable runnable) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);

        return handler;
    }

    public static final Future startOnMainThread(final Runnable runnable, long milliseconds) {
        if (milliseconds <= 0) {
            startOnMainThread(runnable);
            return null;
        }

        return SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(runnable);
            }
        }, milliseconds);
    }

    public static final void clearMapAssetThreads() {
        synchronized (_lock) {
            if (_mapAssetExecutorService != null) {
                _mapAssetExecutorService.shutdownNow();
            }

            _mapAssetExecutorService = Executors.newScheduledThreadPool(1);
        }
    }

    /*** Public methods **/

}
