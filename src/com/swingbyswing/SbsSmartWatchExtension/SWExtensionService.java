package com.swingbyswing.SbsSmartWatchExtension;

import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.swingbyswing.SbsSmartWatchExtension.helpers.SWErrorHelper;
import com.swingbyswing.SbsSmartWatchExtension.helpers.SWThreadHelper;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/25/14
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWExtensionService extends ExtensionService {
    public static final String EXTENSION_KEY = "com.swingbyswing.SbsSmartWatchExtension.key";

    private SWControl _controlExtension = null;

    public SWExtensionService() {
        super(EXTENSION_KEY);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics.start(this);
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
            throw new IllegalArgumentException("No control for: " + hostAppPackageName);
        }

        //SWTestHelper.injectRoundData(this);
        //SWTestHelper.injectLocationData(this);

        return _controlExtension;
    }

    @Override
    protected void handleIntent(final Intent intent) {
        super.handleIntent(intent);

        SWThreadHelper.startPromptThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ("handle_message".equalsIgnoreCase(intent.getAction())) {
                        if (_controlExtension == null) {
                            return;
                        }

                        String jsonString = intent.getStringExtra("json_string");

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
                        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                        Map<String, Object> jsonObject = mapper.readValue(jsonString, Map.class);

                        String path = (String)jsonObject.get("path");

                        if ("http://smartwatch/round".equalsIgnoreCase(path)) {
                            _controlExtension.updateRoundObject((Map<String, Object>)jsonObject.get("data"));
                        }
                        else if ("http://smartwatch/location".equalsIgnoreCase(path)) {
                            _controlExtension.updateLocationObject((Map<String, Object>)jsonObject.get("data"));
                        }
                    }
                }
                catch (Throwable throwable) {
                    SWErrorHelper.handleError(throwable);
                }
            }
        });
    }

    /** Private methods */



}
