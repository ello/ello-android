package ello.co.ello;

import android.app.AlertDialog;
import android.content.Context;

public class Alert {
    public static void showErrorNoInternet(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error).setMessage(R.string.couldnt_connect_error);
        builder.create().show();
    }
}
