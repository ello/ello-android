package ello.co.ello;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Sean on 2/29/16.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class ElloURITest {

    @Before
    public void setup() {
    }

    @Test
    public void testShouldLoadInAppSucceeds() {
        assertTrue("https://ello.co should load in app", ElloURI.shouldLoadInApp("https://ello.co"));
        assertTrue("https://ello-webapp-epic.herokuapp.com should load in app", ElloURI.shouldLoadInApp("https://ello-webapp-epic.herokuapp.com"));
        assertTrue("https://ello-webapp-rainbow.herokuapp.com should load in app", ElloURI.shouldLoadInApp("https://ello-webapp-rainbow.herokuapp.com"));
        assertTrue("https://ello.co/sean should load in app", ElloURI.shouldLoadInApp("https://ello.co/sean"));
        assertTrue("/sean should load in app", ElloURI.shouldLoadInApp("/sean"));
    }

    @Test
    public void testShouldLoadInAppFails() {
        assertFalse("https://hello.co should NOT load in app", ElloURI.shouldLoadInApp("https://hello.co"));
        assertFalse("https://www.google.com should NOT load in app", ElloURI.shouldLoadInApp("https://www.google.com"));
        assertFalse("yo/sean should NOT load in app", ElloURI.shouldLoadInApp("yo/sean"));
    }
}
