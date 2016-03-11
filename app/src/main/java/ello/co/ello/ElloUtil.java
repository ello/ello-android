package ello.co.ello;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by s on 3/4/16.
 */
public class ElloUtil {
    // reachability detection ---------------------------------------------------------------------
    public static boolean isNetworkConnected( Context context ) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null);
    }

    public static void showErrorNoInternet(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error).setMessage(R.string.couldnt_connect_error);
        builder.create().show();
    }
}
