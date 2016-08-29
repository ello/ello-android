package co.ello.ElloApp;

import android.app.AlertDialog;
import android.content.Context;

public class Alert {

    private final static String TAG = Alert.class.getSimpleName();

    public static void showErrorNoInternet(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error).setMessage(R.string.couldnt_connect_error);
        builder.create().show();
    }
}
