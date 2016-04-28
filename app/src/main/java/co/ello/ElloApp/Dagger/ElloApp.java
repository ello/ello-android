package co.ello.ElloApp.Dagger;

import android.app.Application;

public class ElloApp extends Application {

    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        netComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(this))
                .build();
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
