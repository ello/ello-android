package co.ello.ElloApp;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import ello.co.ello.BuildConfig;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class ElloURITest {

    // leaving this in for future reference even though it is empty
    @Before
    public void setup() {
    }

    @Test
    public void testShouldLoadInAppSucceeds() {
        assertTrue("https://co.ello.ello.co should load in app", ElloURI.shouldLoadInApp("https://co.ello.ello.co"));
        assertTrue("https://co.ello.ello-webapp-epic.herokuapp.com should load in app", ElloURI.shouldLoadInApp("https://co.ello.ello-webapp-epic.herokuapp.com"));
        assertTrue("https://co.ello.ello-webapp-rainbow.herokuapp.com should load in app", ElloURI.shouldLoadInApp("https://co.ello.ello-webapp-rainbow.herokuapp.com"));
        assertTrue("https://co.ello.ello.co/sean should load in app", ElloURI.shouldLoadInApp("https://co.ello.ello.co/sean"));
        assertTrue("https://preview.co.ello.ello.co should load in app", ElloURI.shouldLoadInApp("https://preview.co.ello.ello.co"));
        assertTrue("/sean should load in app", ElloURI.shouldLoadInApp("/sean"));
    }

    @Test
    public void testShouldLoadInAppFails() {
        assertFalse("https://hello.co should NOT load in app", ElloURI.shouldLoadInApp("https://hello.co"));
        assertFalse("https://www.google.com should NOT load in app", ElloURI.shouldLoadInApp("https://www.google.com"));
        assertFalse("yo/sean should NOT load in app", ElloURI.shouldLoadInApp("yo/sean"));
    }
}
