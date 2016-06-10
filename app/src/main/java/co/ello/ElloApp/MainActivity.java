package co.ello.ElloApp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

// Using a 3rd party Snackbar because we can't extend
// AppCompatActivity, thanks a lot XWalkActivity
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import javax.inject.Inject;

import co.ello.ElloApp.Dagger.ElloApp;
import co.ello.ElloApp.PushNotifications.RegistrationIntentService;


public class MainActivity
        extends XWalkActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    private final static String TAG = MainActivity.class.getSimpleName();

    @Inject
    protected Reachability reachability;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static Boolean inBackground = true;
    public XWalkView xWalkView;
    private SwipeRefreshLayout swipeLayout;
    public String path = "https://ello.co";
    private ProgressDialog progress;
    private Boolean shouldReload = false;
    private BroadcastReceiver registerDeviceReceiver;
    private BroadcastReceiver pushReceivedReceiver;
    private boolean isXWalkReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ElloApp) getApplication()).getNetComponent().inject(this);
        setContentView(R.layout.activity_main);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        swipeLayout.setOnRefreshListener(this);

        setupWebView();
        setupRegisterDeviceReceiver();
        setupPushReceivedReceiver();
    }

    protected void onXWalkReady() {
        isXWalkReady = true;
        xWalkView.getSettings().setUserAgentString(userAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)){
                XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            }
        }

        displayScreenContent();
        deepLinkWhenPresent();
    }

    @Override
    public void onRefresh() {
        if(!reachability.isNetworkConnected()) {
            displayScreenContent();
        }
        if(isXWalkReady) {
            xWalkView.reload(XWalkView.RELOAD_IGNORE_CACHE);
        }
        swipeLayout.setRefreshing(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        inBackground = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        inBackground = false;
        if(isXWalkReady) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }

        if(!reachability.isNetworkConnected() || xWalkView == null) {
            displayScreenContent();
        }
        else if(shouldReload && isXWalkReady) {
            shouldReload = false;
            xWalkView.reload(XWalkView.RELOAD_IGNORE_CACHE);
        }
        deepLinkWhenPresent();
    }

    @Override
    protected void onPause() {
        if (isXWalkReady) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        xWalkView.onDestroy();
        if (registerDeviceReceiver != null) {
            unregisterReceiver(registerDeviceReceiver);
        }
        if (pushReceivedReceiver != null) {
            unregisterReceiver(pushReceivedReceiver);
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
        xWalkView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        xWalkView.onNewIntent(intent);
    }

    private void setupRegisterDeviceReceiver() {
        registerDeviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String reg_id = intent.getExtras().getString("GCM_REG_ID");
                String registerFunctionCall =
                        "javascript:registerAndroidNotifications(\"" +
                                reg_id + "\", \"" +
                                packageName() + "\", \"" +
                                versionName() + "\", \"" +
                                versionCode() + "\")";
                if(reg_id != null) {
                    xWalkView.load(registerFunctionCall, null);
                }
            }
        };

        registerReceiver(registerDeviceReceiver, new IntentFilter(ElloPreferences.REGISTRATION_COMPLETE));
    }

    private void setupPushReceivedReceiver() {
        pushReceivedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");
                final String webUrl = intent.getExtras().getString("web_url");

                if (title != null && body != null && webUrl != null ) {
                    // Using a 3rd party Snackbar because we can't extend
                    // AppCompatActivity, thanks a lot XWalkActivity
                    Snackbar snackbar = Snackbar.with(context)
                            .type(SnackbarType.MULTI_LINE)
                            .text(title + " " + body)
                            .actionLabel(R.string.view)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    MainActivity.this.xWalkView.load(webUrl, null);
                                }
                            });
                    SnackbarManager.show(snackbar);
                }
            }
        };
        registerReceiver(pushReceivedReceiver, new IntentFilter(ElloPreferences.PUSH_RECEIVED));
    }



    private void deepLinkWhenPresent(){
        Uri data = getIntent().getData();

        Intent get = getIntent();
        String webUrl = get.getStringExtra("web_url");
        if (webUrl != null) {
            path = webUrl;
            xWalkView.load(path, null);
        } else if (data != null) {
            path = data.toString();
            getIntent().setData(null);
            xWalkView.load(path, null);
        }
    }

    private void displayScreenContent() {
        if(reachability.isNetworkConnected()) {
            xWalkView.load(path, null);
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
        xWalkView = (XWalkView) findViewById(R.id.activity_main_webview);
        xWalkView.setResourceClient(new ElloResourceClient(xWalkView));
        xWalkView.setAlpha(0.0f);
    }

    private String versionName() {
        PackageInfo pInfo;
        String versionName = "";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private String versionCode() {
        PackageInfo pInfo;
        String versionCode = "";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = Integer.valueOf(pInfo.versionCode).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private String packageName() {
        PackageInfo pInfo;
        String packageName = "";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageName = pInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    private String userAgentString() {
        return xWalkView.getSettings().getUserAgentString() + " Ello Android/" + versionName() + " (" + versionCode() + ")";
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
                xWalkView.setAlpha(1.0f);
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
