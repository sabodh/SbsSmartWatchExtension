package com.swingbyswing.SbsSmartWatchExtension;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swingbyswing.SbsSmartWatchExtension.helpers.SWErrorHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/30/14
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWBridgeService extends Service {
    public static final int MSG_TO_SMARTWATCH = 2021;
    public static final int MSG_REGISTER_CLIENT = 2022;
    public static final int MSG_UNREGISTER_CLIENT = 2023;
    public static final int MSG_FROM_SMARTWATCH = 2024;
    public static final String BUNDLE_DATA = "bundle_data";

    private Messenger _toMessenger = null;
    private Messenger _fromMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null && "send_message".equalsIgnoreCase(intent.getAction())) {
                String jsonString = intent.getStringExtra("json_string");

                Bundle bundle = new Bundle(1);
                bundle.putString(SWBridgeService.BUNDLE_DATA, jsonString);
                Message message = Message.obtain(null, SWBridgeService.MSG_FROM_SMARTWATCH, 0, 0);
                message.setData(bundle);

                if (_toMessenger != null) {
                    _toMessenger.send(message);
                }
            }
        }
        catch (Throwable throwable) {
            SWErrorHelper.handleError(throwable);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _fromMessenger.getBinder();
    }

    /** Public methods */

    public void handleMessage(Message message) {
        try {
            switch (message.what) {
                case MSG_TO_SMARTWATCH:
                    //Log.i("SwingBySwing", "FORWARD MESSAGE TO GEAR");
                    Bundle data = message.getData();
                    String jsonString = data.getString(BUNDLE_DATA);

                    Intent intent = new Intent();
                    intent.setClass(this, SWExtensionService.class);
                    intent.setAction("handle_message");
                    intent.putExtra("json_string", jsonString);
                    this.startService(intent);

                    break;
                case MSG_REGISTER_CLIENT:
                    Log.i("SwingBySwing", "MSG_REGISTER_CLIENT");
                    _toMessenger = message.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    Log.i("SwingBySwing", "MSG_UNREGISTER_CLIENT");
                    _toMessenger = null;
                    break;
                default:
                    Log.i("SwingBySwing", "UNSUPPORTED MESSAGE");
                    break;
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /** Public classes */

    static class IncomingHandler extends Handler {
        private final WeakReference<SWBridgeService> _service;

        IncomingHandler(SWBridgeService service) {
            _service = new WeakReference<SWBridgeService>(service);
        }

        @Override
        public void handleMessage(Message message) {
            SWBridgeService service = _service.get();

            if (service != null) {
                service.handleMessage(message);
            }
        }
    }
}
