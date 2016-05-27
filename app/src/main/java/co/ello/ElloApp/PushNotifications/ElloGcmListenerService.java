package co.ello.ElloApp.PushNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import co.ello.ElloApp.ElloPreferences;

public class ElloGcmListenerService extends GcmListenerService {
    private static final String TAG = ElloGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String page = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Page: " + page);
        if(page != null) {
            Intent pushReceived = new Intent(ElloPreferences.PUSH_RECEIVED);
            pushReceived.putExtra("push_notification_page", page);
        }
    }
}
