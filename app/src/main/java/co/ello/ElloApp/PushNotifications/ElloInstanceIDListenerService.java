package co.ello.ElloApp.PushNotifications;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class ElloInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = ElloInstanceIDListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
