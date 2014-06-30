package com.swingbyswing.SbsSmartWatchExtension;

import android.content.ContentValues;
import android.content.Context;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.swingbyswing.SbsSmartWatchExtension.activities.SWPreferenceActivity;

/**
 * Created with IntelliJ IDEA.
 * User: txgiggy
 * Date: 6/25/14
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWRegistrationInformation extends RegistrationInformation {

    final Context _context;

    protected SWRegistrationInformation(Context context) {

        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }

        _context = context;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 1;
    }

    @Override
    public int getTargetControlApiVersion() {
        return 2;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return 0;
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp = ExtensionUtils.getUriString(_context, R.drawable.icon);
        String iconExtension = ExtensionUtils.getUriString(_context, R.drawable.icon_extension);
        String iconExtension48 = ExtensionUtils.getUriString(_context, R.drawable.icon_extension48);
        String iconExtensionBw = ExtensionUtils.getUriString(_context, R.drawable.icon_extension_bw);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY, SWPreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT, _context.getString(R.string.configuration_text));
        values.put(Registration.ExtensionColumns.NAME, _context.getString(R.string.extension_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY, SWService.EXTENSION_KEY);
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.EXTENSION_48PX_ICON_URI, iconExtension48);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI_BLACK_WHITE, iconExtensionBw);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION, getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, _context.getPackageName());
        values.put(Registration.ExtensionColumns.LAUNCH_MODE, Registration.LaunchMode.NOTIFICATION);

        return values;
    }

    @Override
    public boolean isDisplaySizeSupported(int width, int height) {
        return (width == SWControl.getSupportedControlWidth(_context)
                && height == SWControl.getSupportedControlHeight(_context));
    }

    @Override
    public boolean controlInterceptsBackButton() {
        return true;
    }

}
