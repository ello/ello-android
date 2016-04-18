package co.ello.ElloApp;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowViewGroup;
import org.xwalk.core.XWalkView;


@Implements(value = XWalkView.class, isInAndroidSdk = false)
public class ShadowXWalkView extends ShadowViewGroup {}
