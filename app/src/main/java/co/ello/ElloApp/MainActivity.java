package co.ello.ElloApp;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.ValueCallback;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import co.ello.ElloApp.Dagger.ElloApp;
import co.ello.ElloApp.PushNotifications.RegistrationIntentService;

// Using a 3rd party Snackbar because we can't extend
// AppCompatActivity, thanks a lot XWalkActivity


public class MainActivity
        extends XWalkActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 333;

    @Inject
    protected Reachability reachability;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static Boolean inBackground = true;
    public XWalkView xWalkView;
    private ElloUIClient xWalkClient;
    private SwipeRefreshLayout swipeLayout;
    public String path = "http://10.62.105.250:6660";
    private ProgressDialog progress;
    private Boolean shouldReload = false;
    private Boolean webAppReady = false;
    private Boolean isDeepLink = false;
    private BroadcastReceiver registerDeviceReceiver;
    private BroadcastReceiver pushReceivedReceiver;
    private BroadcastReceiver imageResizeReceiver;
    private Boolean isXWalkReady = false;
    private Intent imageSelectedIntent;
    private Date lastReloaded;
    TmpTarget target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastReloaded = new Date();
        ((ElloApp) getApplication()).getNetComponent().inject(this);
        setContentView(R.layout.activity_main);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        swipeLayout.setOnRefreshListener(this);

        setupWebView();
        setupRegisterDeviceReceiver();
        setupPushReceivedReceiver();
        setupImageResizeReceiver();
    }

    protected void onXWalkReady() {
        isXWalkReady = true;
        xWalkView.getSettings().setUserAgentString(userAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)){
                XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            }
        }

        xWalkView.addJavascriptInterface(this, "AndroidInterface");
        displayScreenContent();
        deepLinkWhenPresent();
    }

    @Override
    public void onRefresh() {
        if(!reachability.isNetworkConnected()) {
            displayScreenContent();
        }
        if(isXWalkReady) {
            reloadXWalk();
            progress.show();
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

        if(shouldHardRefresh()) {
            shouldReload = true;
        }

        inBackground = false;
        if(isXWalkReady) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
            registerForGCM();
        }

        if(!reachability.isNetworkConnected() || xWalkView == null) {
            displayScreenContent();
        }
        else if(shouldReload && isXWalkReady) {
            shouldReload = false;
            reloadXWalk();
        }
        deepLinkWhenPresent();
    }

    private boolean shouldHardRefresh() {
        Date now = new Date();
        Date thirtyMinutesFromLastReloaded = new Date(lastReloaded.getTime() + (30 * 60 * 1000));
        return now.compareTo(thirtyMinutesFromLastReloaded) > 0;
    }

    private void reloadXWalk() {
        lastReloaded = new Date();
        xWalkView.reload(XWalkView.RELOAD_IGNORE_CACHE);
    }

    protected boolean cameraGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean writeExternalGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean checkPermissions() {
        if (!cameraGranted() || !writeExternalGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        xWalkClient.openFileChooser();
                    }
                    else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                            Alert.showCameraDenied(MainActivity.this);
                        }
                        else {
                            Alert.showCameraDenied(MainActivity.this);
                        }
                    }
                }
                return;
            }

        }
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
        if (imageResizeReceiver != null) {
            unregisterReceiver(imageResizeReceiver);
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
        target = new TmpTarget(this, "tmp.jpg");

        if (intent != null && intent.getData() != null) {
            Uri imageURI = intent.getData();
            File bitmap = new File(imageURI.toString());

            imageSelectedIntent = intent;
            Picasso.with(MainActivity.this)
                    .load(imageURI)
                    .resize(1200, 3600)
                    .centerInside()
                    .onlyScaleDown()
                    .into(target);
        }
        else {
            xWalkView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            path = data.toString();
            isDeepLink = true;
        }

        if (isXWalkReady) {
            xWalkView.onNewIntent(intent);
        }
    }

    @JavascriptInterface
    public void webAppLoaded() {
        webAppReady = true;
        if (progress != null) {
            progress.dismiss();
        }
        registerForGCM();
    }

    @JavascriptInterface
    public void launchEditor() {
        if (webAppReady) {
            Intent intent = new Intent(this, MyReactActivity.class);
            startActivity(intent);
        }
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

    private void setupImageResizeReceiver() {
        imageResizeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String path = intent.getExtras().getString("RESIZED_IMAGE_PATH");

                if(imageSelectedIntent != null && path != null) {
                    Uri resizedURI = Uri.parse(path);
                    if(resizedURI != null) {
                        imageSelectedIntent.setData(resizedURI);
                        xWalkView.onActivityResult(1, -1, imageSelectedIntent);
                    }
                }
                imageSelectedIntent = null;
            }
        };

        registerReceiver(imageResizeReceiver, new IntentFilter(ElloPreferences.IMAGE_RESIZED));
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
        if (progress == null) {
            progress = createProgressDialog(MainActivity.this);
        }

        Uri data = getIntent().getData();

        Intent get = getIntent();
        String webUrl = get.getStringExtra("web_url");
        if (isXWalkReady && webUrl != null) {
            path = webUrl;
            loadPage(path);
        } else if (isXWalkReady && data != null) {
            path = data.toString();
            getIntent().setData(null);
            loadPage(path);
        } else if (isXWalkReady && isDeepLink) {
            isDeepLink = false;
            loadPage(path);
        }
    }

    private void loadPage(String page) {
        xWalkView.load(page, null);
        progress.show();
    }

    private void displayScreenContent() {
        if(reachability.isNetworkConnected()) {
            loadPage(path);
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
        xWalkClient = new ElloUIClient(xWalkView);
        xWalkView.setUIClient(xWalkClient);
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
        if (checkPlayServices() && webAppReady) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    class ElloResourceClient extends XWalkResourceClient {

        public ElloResourceClient(XWalkView xwalkView) {
            super(xwalkView);
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
    }

    class ElloUIClient extends XWalkUIClient {
        public ElloUIClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        private XWalkView view;
        private ValueCallback<Uri> uploadMsg;
        private String acceptType;
        private String capture;

        @Override
        public void openFileChooser(
                XWalkView view,
                ValueCallback<Uri> uploadMsg,
                String acceptType,
                String capture)
        {
            boolean hasPermission = checkPermissions();
            if(hasPermission) {
                super.openFileChooser(view, uploadMsg, acceptType, capture);
            }
            else {
                this.view = view;
                this.uploadMsg = uploadMsg;
                this.acceptType = acceptType;
                this.capture = capture;
            }
        }

        public void openFileChooser() {
            this.openFileChooser(this.view, this.uploadMsg, this.acceptType, this.capture);
        }

    }
}
