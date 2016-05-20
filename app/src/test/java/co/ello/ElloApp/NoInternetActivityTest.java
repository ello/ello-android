package co.ello.ElloApp;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import javax.inject.Inject;

import co.ello.ElloApp.Dagger.TestElloApp;
import co.ello.ElloApp.Dagger.TestNetComponent;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class NoInternetActivityTest {

    private NoInternetActivity activity;
    private Button button;
    private TestNetComponent testNetComponent;

    @Inject
    protected Reachability reachability;

    @Before
    public void setup() {
        testNetComponent = ((TestNetComponent)((TestElloApp) RuntimeEnvironment.application).getNetComponent());
        testNetComponent.inject(this);
        activity = Robolectric.buildActivity(NoInternetActivity.class).create().get();
        button = (Button) activity.findViewById(R.id.refreshButton);
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void tapRefreshStartsMainActivityWhenInternetPresent()
    {
        when(reachability.isNetworkConnected()).thenReturn(true);

        ShadowActivity shadowActivity = shadowOf(activity);
        button.performClick();
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertThat(startedIntent.getComponent().getClassName(),
                equalTo(MainActivity.class.getName()));
    }

    @Test
    public void tapRefreshShowsAlertWhenInternetNotPresent()
    {
        when(reachability.isNetworkConnected()).thenReturn(false);

        button.performClick();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog sAlert = shadowOf(alert);

        assertThat(sAlert.getTitle().toString(),
                equalTo(activity.getString(R.string.error)));
        assertThat(sAlert.getMessage().toString(),
                equalTo(activity.getString(R.string.couldnt_connect_error)));
    }
}
