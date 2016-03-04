package ello.co.ello;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Sean on 2/19/16.
 */
public class ElloWebViewClient extends WebViewClient {

    private WebView mWebView;
    private Activity mActivity;

    ElloWebViewClient(WebView webView, Activity activity) {
        mWebView = webView;
        mActivity = activity;
    }

    public void onPageFinished(WebView view, String url) {
        mWebView.setAlpha(1.0f);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (ElloURI.shouldLoadInApp(url)) {
            return false;
        }
        else {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }
    }
}
