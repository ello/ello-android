package co.ello.ElloApp;

public class ElloURI {

    private final static String TAG = ElloURI.class.getSimpleName();

    public static boolean shouldLoadInApp(String url) {
        if (url.matches("^(https?://)?((w{3}|preview.)?ello.(co|ninja)|(ello-webapp-epic|ello-webapp-rainbow|ello-fg-stage1|ello-fg-stage2).herokuapp.com)/?\\S*$")) {
            return true;
        }
        else if (url.matches("/\\S*$")) {
            return true;
        }
        else {
            return false;
        }
    }
}
