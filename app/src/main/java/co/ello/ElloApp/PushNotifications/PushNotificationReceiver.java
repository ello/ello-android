package co.ello.ElloApp.PushNotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.xwalk.core.XWalkView;

public class PushNotificationReceiver extends BroadcastReceiver {

    private final static String TAG = PushNotificationReceiver.class.getSimpleName();

    private XWalkView mWebView;

    public PushNotificationReceiver(XWalkView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String page = intent.getExtras().getString("push_notification_page");
        if(page != null) {
            Log.d(TAG, page);
            mWebView.load(page, null);
        }
    }
}
