package co.ello.ElloApp.PushNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import co.ello.ElloApp.ElloPreferences;
import co.ello.ElloApp.MainActivity;
import co.ello.ElloApp.R;

public class ElloGcmListenerService extends GcmListenerService {
    private static final String TAG = ElloGcmListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 999999;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String body = data.getString("body");
        String webUrl = data.getString("web_url");

        if(title != null && body != null && webUrl != null) {
            if(!MainActivity.inBackground){
                broadcastPushReceived(title, body, webUrl);
            }
            else{
                sendNotification(title, body, webUrl);
            }
        }
    }

    private void broadcastPushReceived(String title, String body, String webUrl) {
        Intent pushReceived = new Intent(ElloPreferences.PUSH_RECEIVED);
        pushReceived.putExtra("title", title);
        pushReceived.putExtra("body", body);
        pushReceived.putExtra("web_url", webUrl);

        sendBroadcast(pushReceived);
    }

    private void sendNotification(String title, String body, String webUrl) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_stat_gcm)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.putExtra("web_url", webUrl);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
