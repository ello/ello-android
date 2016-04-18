package co.ello.ElloApp.PushNotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import co.ello.ElloApp.ElloPreferences;
import co.ello.ElloApp.R;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            sendBroadcast(new Intent("co.ello.ElloApp.REGISTER_DEVICE").putExtra("GCM_REG_ID", token));
            sharedPreferences.edit().putBoolean(ElloPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(ElloPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(ElloPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
