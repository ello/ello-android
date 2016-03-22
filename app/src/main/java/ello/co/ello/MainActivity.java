package ello.co.ello;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity
        extends ActionBarActivity
        implements SwipeRefreshLayout.OnRefreshListener, AdvancedWebView.Listener
{

    private AdvancedWebView mWebView;
    private SwipeRefreshLayout mSwipeLayout;
    private String path = "https://preview.ello.co";

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
        if (mWebView != null) {
            mWebView.onResume();
        }
        if(!Reachability.isNetworkConnected(this) || mWebView == null) {
            displayScreenContent();
        }
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
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
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

    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setAlpha(1.0f);
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

}
