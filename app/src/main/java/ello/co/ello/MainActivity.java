package ello.co.ello;

import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.WindowManager;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity
        extends ActionBarActivity
        implements SwipeRefreshLayout.OnRefreshListener, AdvancedWebView.Listener
{

    private AdvancedWebView mWebView;
    private SwipeRefreshLayout mSwipeLayout;
    private String path = "https://preview.ello.co";
    private ProgressDialog progress;
    private Boolean shouldReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mSwipeLayout.setOnRefreshListener(this);
        if (savedInstanceState == null) {
            displayScreenContent();
        }
        deepLinkWhenPresent();
        Log.d("onResume() getUrl", mWebView.getUrl());
    }

    @Override public void onRefresh() {
        if(!Reachability.isNetworkConnected(this)) {
            displayScreenContent();
        }
        else if(mWebView != null) {
            mWebView.reload();
        }
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume() getUrl", mWebView.getUrl());
        if (mWebView != null) {
            mWebView.onResume();
        }
        if(!Reachability.isNetworkConnected(this) || mWebView == null) {
            displayScreenContent();
        }
//        else if(shouldReload && mWebView != null) {
//            shouldReload = false;
//            mWebView.reload();
//        }
        deepLinkWhenPresent();
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            shouldReload = true;
        }
        Log.d("memory trimmed", new Integer(level).toString());
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mWebView != null) {
            mWebView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    // AdvancedWebView.Listener interface functions

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (progress == null) {
            progress = createProgressDialog(this);
        }
        progress.show();
    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setAlpha(1.0f);
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    private void deepLinkWhenPresent(){
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            path = data.toString();
            if (mWebView != null) {
                mWebView.loadUrl(path);
            }
        }
    }

    private void displayScreenContent() {
        if(Reachability.isNetworkConnected(this)) {
            setupWebView();
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
        mWebView = (AdvancedWebView) findViewById(R.id.activity_main_webview);
        mWebView.setAlpha(0.0f);
        mWebView.setListener(this, this);
        mWebView.loadUrl(path);
        mWebView.setWebViewClient(new ElloWebViewClient(this));
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

}
