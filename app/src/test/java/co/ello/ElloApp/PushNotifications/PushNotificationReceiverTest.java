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
public class PushNotificationReceiverTest {

    private PushNotificationReceiver receiver;
    private XWalkView webView;

    @Before
    public void setup() {
        webView = Mockito.mock(XWalkView.class);
        receiver = new PushNotificationReceiver(webView);
    }

    @Test
    public void testIntentHandling() {
        Intent pushReceived = new Intent(ElloPreferences.PUSH_RECEIVED);
        pushReceived.putExtra("push_notification_page", "https://ello.co/thumbs-up");
        receiver.onReceive(RuntimeEnvironment.application, pushReceived);

        verify(webView).load(Matchers.eq("https://ello.co/thumbs-up"), Matchers.isNull(String.class));
    }
}
