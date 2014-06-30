package com.swingbyswing.SbsSmartWatchExtension;

import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.swingbyswing.SbsSmartWatchExtension.helpers.SWTestHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/25/14
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWService extends ExtensionService {
    public static final String EXTENSION_KEY = "com.swingbyswing.SbsSmartWatchExtension.key";

    public static final int MSG_TO_SMARTWATCH = 2021;
    public static final int MSG_REGISTER_CLIENT = 2022;
    public static final int MSG_UNREGISTER_CLIENT = 2023;
    public static final int MSG_FROM_SMARTWATCH = 2024;
    public static final String BUNDLE_DATA = "bundle_data";

    private SWControl _controlExtension = null;
    private Messenger _toMessenger = null;
    private Messenger _fromMessenger = new Messenger(new IncomingHandler(this));

    public SWService() {
        super(EXTENSION_KEY);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);

//        if (_controlExtension != null) {
//            _controlExtension.handleIntent(intent);
//        }
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new SWRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String hostAppPackageName) {
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);

        if (advancedFeaturesSupported) {
            _controlExtension = new SWControl(hostAppPackageName, this);
        }
        else {
            _controlExtension = new SWControl(hostAppPackageName, this);
        }

        SWTestHelper.injectRoundData(this);
        SWTestHelper.injectLocationData(this);

        return _controlExtension;
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
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonObject = mapper.readValue(jsonString, Map.class);

                    String path = (String)jsonObject.get("path");

                    if ("http://smartwatch/round".equalsIgnoreCase(path)) {
                        _controlExtension.updateRoundObject((Map<String, Object>)jsonObject.get("data"));
                    }
                    else if ("http://smartwatch/location".equalsIgnoreCase(path)) {
                        _controlExtension.updateLocationObject((Map<String, Object>)jsonObject.get("data"));
                    }

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

    public void sendMessage(Map<String, Object> jsonMap) {
        if (_toMessenger == null) {
            Log.i("SwingBySwing", "Messenger is not yet registered");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(jsonMap);

            Bundle bundle = new Bundle(1);
            bundle.putString(SWService.BUNDLE_DATA, jsonString);
            Message message = Message.obtain(null, SWService.MSG_FROM_SMARTWATCH, 0, 0);
            message.setData(bundle);

            _toMessenger.send(message);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /** Private methods */



    /** Public classes */

    static class IncomingHandler extends Handler {
        private final WeakReference<SWService> _service;

        IncomingHandler(SWService service) {
            _service = new WeakReference<SWService>(service);
        }

        @Override
        public void handleMessage(Message message) {
            SWService service = _service.get();

            if (service != null) {
                service.handleMessage(message);
            }
        }
    }
}
