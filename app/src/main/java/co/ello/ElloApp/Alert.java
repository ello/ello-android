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

    public static void showCameraPermission(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.camera_permission_title).setMessage(R.string.camera_permission_message);
        builder.create().show();
    }

    public static void showCameraDenied(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.camera_permission_denied_title).setMessage(R.string.camera_permission_denied_message);
        builder.create().show();
    }
}
