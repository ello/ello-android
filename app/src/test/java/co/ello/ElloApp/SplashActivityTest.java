package co.ello.ElloApp;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SplashActivityTest {

    private SplashActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(SplashActivity.class).create().get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldStartMainActivity()
    {
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertThat(startedIntent.getComponent().getClassName(),
                equalTo(MainActivity.class.getName()));
    }
}