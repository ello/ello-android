package ello.co.ello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class MainActivity extends ActionBarActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            displayScreenContent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ElloUtil.isNetworkConnected(this) || mWebView == null) {
            displayScreenContent();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayScreenContent() {
        if(ElloUtil.isNetworkConnected(this)) {
            setupWebView();
        } else {
            setupNoInternetView();
        }
    }

    private void setupNoInternetView() {
        Intent intent = new Intent(this, NoInternetActivity.class);
        startActivity(intent);
    }

    private void setupWebView() {
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.setAlpha(0.0f);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("https://ello-webapp-epic.herokuapp.com");

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new ElloWebViewClient(mWebView, this));
    }
}
