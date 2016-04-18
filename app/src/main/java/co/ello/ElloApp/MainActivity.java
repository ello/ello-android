package co.ello.ElloApp;

import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import co.ello.ElloApp.PushNotifications.ElloGcmRegisteredReceiver;
import co.ello.ElloApp.PushNotifications.RegistrationIntentService;

public class MainActivity
        extends ActionBarActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    private final static String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Reachability reachability;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public XWalkView xWalkView;
    private SwipeRefreshLayout swipeLayout;
    public String path = "https://ello-fg-stage1.herokuapp.com";
    private ProgressDialog progress;
    private Boolean shouldReload = false;
    private ElloGcmRegisteredReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mSwipeLayout.setOnRefreshListener(this);
        setupWebView();
        displayScreenContent();
        deepLinkWhenPresent();
        setupBroadcastReceiver();
    }

    @Override public void onRefresh() {
        if(!Reachability.isNetworkConnected(this)) {
            displayScreenContent();
        }
        mWebView.reload(XWalkView.RELOAD_IGNORE_CACHE);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.resumeTimers();
        mWebView.onShow();

        if(!Reachability.isNetworkConnected(this) || mWebView == null) {
            displayScreenContent();
        }
        else if(shouldReload) {
            shouldReload = false;
            mWebView.reload(XWalkView.RELOAD_IGNORE_CACHE);
        }
        deepLinkWhenPresent();
    }

    @Override
    protected void onPause() {
        mWebView.pauseTimers();
        mWebView.onHide();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            shouldReload = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mWebView.onNewIntent(intent);
    }

    private void setupBroadcastReceiver() {
        Log.d(TAG, "setupBroadcastReceiver");
        mReceiver = new ElloGcmRegisteredReceiver(mWebView);
        registerReceiver(mReceiver, new IntentFilter("co.ello.ElloApp.REGISTER_DEVICE"));
    }

    private void deepLinkWhenPresent(){
        Uri data = getIntent().getData();

        if (data != null) {
            path = data.toString();
            mWebView.load(path, null);
        }
    }

    private void displayScreenContent() {
        if(Reachability.isNetworkConnected(this)) {
            mWebView.load(path, null);
        } else {
            setupNoInternetView();
        }
    }

    private void setupNoInternetView() {
        Intent intent = new Intent(this, NoInternetActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupWebView() {
        mWebView = (XWalkView) findViewById(R.id.activity_main_webview);
        mWebView.setResourceClient(new ElloResourceClient(mWebView));
        mWebView.setAlpha(0.0f);
        mWebView.getSettings().setUserAgentString(userAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)){
                XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            }
        }
    }

    private String userAgentString() {
        String version = "";
        String versionCode = "";
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = new Integer(pInfo.versionCode).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mWebView.getSettings().getUserAgentString() + " Ello Android/" + version + " (" + versionCode + ")";
    }

    private ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {}
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        return dialog;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerForGCM() {
        Log.d(TAG, "registerForGCM");
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    class ElloResourceClient extends XWalkResourceClient {

        public ElloResourceClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
            if(urlWithoutSlash(url).equals(path)) {
                if (progress == null) {
                    progress = createProgressDialog(MainActivity.this);
                }
                progress.show();
            }
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            if(urlWithoutSlash(url).equals(path)) {
                mWebView.setAlpha(1.0f);
                if (progress != null) {
                    progress.dismiss();
                }
                registerForGCM();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
            if (ElloURI.shouldLoadInApp(url)) {
                return false;
            }
            else {
                MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        }

        private String urlWithoutSlash(String url) {
            if (url.endsWith("/")) {
               url = url.substring(0, url.length() - 1);
            }
            return url;
        }
    }
}
