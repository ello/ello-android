package co.ello.ElloApp.PushNotifications;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import co.ello.ElloApp.R;

public class TokenRetriever {

    private static final String TAG = TokenRetriever.class.getSimpleName();

    private Context context;

    public TokenRetriever(Context context) {
        this.context = context;
    }

    public String getToken() {
        try {
            InstanceID instanceID = InstanceID.getInstance(context);
            String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);
            return token;
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            return null;
        }
    }
}
