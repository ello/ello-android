package co.ello.ElloApp.PushNotifications;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.support.v4.BuildConfig;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class ElloGcmRegisteredReceiverTest {
//    static {
//        // redirect the Log.x output to stdout. Stdout will be recorded in the test result report
//        ShadowLog.stream = System.out;
//    }

    @Before
    public void setup() {}

    /**
     * Let's first test if the BroadcastReceiver, which was defined in the manifest, is correctly
     * load in our tests
     */
//    @Test
//    public void testBroadcastReceiverRegistered() {
//        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();
//
//        Assert.assertEquals(false, registeredReceivers.isEmpty());
//
//        boolean receiverFound = false;
//        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
//            if (!receiverFound)
//                receiverFound = ElloGcmRegisteredReceiver.class.getSimpleName().equals(
//                        wrapper.broadcastReceiver.getClass().getSimpleName());
//        }
//
//        Assert.assertTrue(receiverFound); // will be false if not found
//    }

    @Test
    public void testIntentHandling() {
        /** TEST 1
         ----------
         We defined the Broadcast receiver with a certain action, so we should check if we have
         receivers listening to the defined action
         */
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");

        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Assert.assertTrue(shadowApplication.hasReceiverForIntent(intent));

        /**
         * TEST 2
         * ----------
         * Lets be sure that we only have a single receiver assigned for this intent
         */
        List<BroadcastReceiver> receiversForIntent = shadowApplication.getReceiversForIntent(intent);

//        Assert.assertEquals("Expected one broadcast receiver", 1, receiversForIntent.size());

        /**
         * TEST 3
         * ----------
         * Fetch the Broadcast receiver and cast it to the correct class.
         * Next call the "onReceive" method and check if the MyBroadcastIntentService was started
         */
        ElloGcmRegisteredReceiver receiver = (ElloGcmRegisteredReceiver) receiversForIntent.get(0);
        receiver.onReceive(ShadowApplication.getInstance().getApplicationContext(), intent);

//        Intent serviceIntent = Robolectric.getShadowApplication().peekNextStartedService();
//        Assert.assertEquals("Expected the MyBroadcast service to be invoked",
//                MyBroadcastIntentService.class.getCanonicalName(),
//                serviceIntent.getComponent().getClassName());

    }
}
