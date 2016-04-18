package co.ello.ElloApp.PushNotifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ServiceController;

import co.ello.ElloApp.BuildConfig;

import static junit.framework.Assert.assertFalse;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class RegistrationIntentServiceTest {
//    private TestService service;
//    private ServiceController<TestService> controller;

    @Before
    public void setUp() {
//        controller = Robolectric.buildService(TestService.class);
//        service = controller.attach().create().get();
    }

    @Before
    public void setup() {}

    
    @Test
    public void testNoBundleExtrasFound() {
        Intent serviceIntent = new Intent(RuntimeEnvironment.application, RegistrationIntentServiceMock.class);
//        NotificationManager notificationManager = (NotificationManager) RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE);

        ShadowApplication.getInstance().startService(serviceIntent);
        RegistrationIntentServiceMock service = new RegistrationIntentServiceMock();
        service.onCreate();
        service.onHandleIntent(serviceIntent);

//        Assert.assertEquals("Expected no notifications", 0, Shadows.shadowOf(notificationManager).size());
    }

//    @Test
//    public void testWithIntent() {
//        Intent intent = new Intent(RuntimeEnvironment.application, TestService.class);
//        // add extras to intent
//        controller.withIntent(intent).startCommand(0, 0);
//        // assert here
//        assertFalse("Should be set", service.run);
//    }

    @After
    public void tearDown() {
//        controller.destroy();
    }

    class RegistrationIntentServiceMock extends RegistrationIntentService {
        @Override
        public void onHandleIntent(Intent intent) {
            super.onHandleIntent(intent);
        }
    }
//    public static class TestService extends RegistrationIntentService {
//        public boolean enabled = true;
//
//        @Override
//        public void onStart(Intent intent, int startId) {
//            // same logic as in internal ServiceHandler.handleMessage()
//            // but runs on same thread as Service
//            onHandleIntent(intent);
//            stopSelf(startId);
//        }
//    }
}
