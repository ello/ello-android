package co.ello.ElloApp.Dagger;

public class TestElloApp extends ElloApp {

    private TestNetComponent netComponent;

    @Override public void onCreate() {
        super.onCreate();

        netComponent = DaggerTestNetComponent.builder()
                .appModule(new AppModule(this))
                .testNetModule(new TestNetModule(this))
                .build();
    }

    @Override
    public NetComponent getNetComponent() {
        return netComponent;
    }
}
