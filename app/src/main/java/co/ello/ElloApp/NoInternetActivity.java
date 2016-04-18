package co.ello.ElloApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class NoInternetActivity extends ActionBarActivity {

    private final static String TAG = NoInternetActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        setupRefreshButton();
    }

    protected void setupRefreshButton() {
        Button buttonRefresh = (Button) findViewById(R.id.refreshButton);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Reachability.isNetworkConnected(NoInternetActivity.this)) {
                    Alert.showErrorNoInternet(NoInternetActivity.this);
                } else {
                    Intent intent = new Intent(NoInternetActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
