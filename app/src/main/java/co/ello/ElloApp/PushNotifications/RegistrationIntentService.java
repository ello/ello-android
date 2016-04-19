package co.ello.ElloApp.PushNotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.ello.ElloApp.ElloPreferences;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    protected TokenRetriever mTokenRetriever = new TokenRetriever(this);

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = mTokenRetriever.getToken();

        Intent registrationComplete = new Intent(ElloPreferences.REGISTRATION_COMPLETE);
        if(token != null) {
            registrationComplete.putExtra("GCM_REG_ID", token);
            sharedPreferences.edit().putBoolean(ElloPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        }
        else {
            sharedPreferences.edit().putBoolean(ElloPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        sendBroadcast(registrationComplete);
    }
}
