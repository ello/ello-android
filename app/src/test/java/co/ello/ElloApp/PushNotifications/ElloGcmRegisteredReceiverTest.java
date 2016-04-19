package co.ello.ElloApp.PushNotifications;

import android.content.Intent;
import android.os.Build;
import android.support.v4.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.xwalk.core.XWalkView;

import co.ello.ElloApp.ElloPreferences;
import co.ello.ElloApp.ShadowXWalkView;

import static org.mockito.Mockito.verify;

@Config(
    constants = BuildConfig.class,
    sdk = Build.VERSION_CODES.LOLLIPOP,
    shadows = ShadowXWalkView.class
)

@RunWith(RobolectricTestRunner.class)
public class ElloGcmRegisteredReceiverTest {

    private ElloGcmRegisteredReceiver receiver;
    private XWalkView webView;


    @Before
    public void setup() {
        webView = Mockito.mock(XWalkView.class);
        receiver = new ElloGcmRegisteredReceiver(webView);
    }

    @Test
    public void testIntentHandling() {
        Intent registrationComplete = new Intent(ElloPreferences.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("GCM_REG_ID", "hello-token");
        receiver.onReceive(RuntimeEnvironment.application, registrationComplete);

        verify(webView).load(Matchers.eq("javascript:registerAndroidNotifications(\"hello-token\")"), Matchers.isNull(String.class));
    }
}
