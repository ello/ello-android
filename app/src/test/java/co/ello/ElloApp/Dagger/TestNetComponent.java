package co.ello.ElloApp.Dagger;

import javax.inject.Singleton;

import co.ello.ElloApp.MainActivity;
import co.ello.ElloApp.NoInternetActivity;
import co.ello.ElloApp.NoInternetActivityTest;
import co.ello.ElloApp.PushNotifications.RegistrationIntentService;
import co.ello.ElloApp.PushNotifications.RegistrationIntentServiceTest;
import co.ello.ElloApp.Reachability;
import dagger.Component;

@Singleton
@Component(modules={AppModule.class, TestNetModule.class})
public interface TestNetComponent extends NetComponent {
    void inject(MainActivity activity);
    void inject(Reachability reachability);
    void inject(RegistrationIntentService service);
    void inject(NoInternetActivity activity);
    void inject(RegistrationIntentServiceTest test);
    void inject(NoInternetActivityTest test);
}
