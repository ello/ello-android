package ello.co.ello;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Sean on 2/19/16.
 */
public class ElloWebViewClient extends WebViewClient {

    private WebView mWebView;

    ElloWebViewClient(WebView webView) {
        mWebView = webView;
    }

    public void onPageFinished(WebView view, String url) {
        mWebView.setAlpha(1.0f);
    }

}
