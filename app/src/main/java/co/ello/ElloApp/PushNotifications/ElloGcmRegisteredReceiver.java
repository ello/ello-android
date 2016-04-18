package co.ello.ElloApp.PushNotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.xwalk.core.XWalkView;

public class ElloGcmRegisteredReceiver extends BroadcastReceiver {

    private final static String TAG = ElloGcmRegisteredReceiver.class.getSimpleName();

    private XWalkView mWebView;

    public ElloGcmRegisteredReceiver(XWalkView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String reg_id = intent.getExtras().getString("GCM_REG_ID");
        if(reg_id != null) {
            Log.d(TAG,reg_id);
            mWebView.load("javascript:registerAndroidNotifications(\"" + reg_id + "\")", null);
        }
    }
}
