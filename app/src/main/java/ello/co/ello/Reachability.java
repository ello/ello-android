package ello.co.ello;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sean Dougherty on 3/15/16.
 */
public class Reachability {
    public static boolean isNetworkConnected( Context context ) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null);
    }
}
