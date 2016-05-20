package co.ello.ElloApp.PushNotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import javax.inject.Inject;

import co.ello.ElloApp.Dagger.ElloApp;
import co.ello.ElloApp.ElloPreferences;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    @Inject
    protected SharedPreferences sharedPreferences;

    protected TokenRetriever tokenRetriever = new TokenRetriever(this);

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ElloApp app = (ElloApp) getApplication();
        app.getNetComponent().inject(this);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String token = tokenRetriever.getToken();

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
