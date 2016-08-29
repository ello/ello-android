package co.ello.ElloApp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

public class Reachability {

    private final static String TAG = Reachability.class.getSimpleName();
    private final ConnectivityManager manager;

    @Inject
    public Reachability(ConnectivityManager manager) {
        this.manager = manager;
    }

    public boolean isNetworkConnected() {
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();
        return isConnected;
    }
}
