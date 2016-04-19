package co.ello.ElloApp;

import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import co.ello.ElloApp.PushNotifications.ElloGcmRegisteredReceiver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows={ShadowXWalkView.class})
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setup()  {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void hasCorrectPath() throws Exception {
        assertEquals("https://preview.ello.co", activity.path);
    }

    @Test
    public void hasACrossWalkView() throws Exception {
        assertNotNull(activity.mWebView);
    }

    @Test
    public void registersReceiverForDeviceRegistered() throws Exception {
        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();

        Assert.assertEquals(false, registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = ElloGcmRegisteredReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        assertTrue(receiverFound); // will be false if not found

        Intent intent = new Intent(ElloPreferences.REGISTRATION_COMPLETE);
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        assertTrue("is registered for REGISTRATION_COMPLETE", shadowApplication.hasReceiverForIntent(intent));
    }
}
