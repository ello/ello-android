package ello.co.ello;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ElloWebViewClient extends WebViewClient {

    private Activity mActivity;

    ElloWebViewClient(Activity activity) {
        mActivity = activity;
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
