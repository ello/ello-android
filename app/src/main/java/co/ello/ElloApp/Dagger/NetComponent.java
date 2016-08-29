package co.ello.ElloApp.Dagger;

import javax.inject.Singleton;

import co.ello.ElloApp.MainActivity;
import co.ello.ElloApp.NoInternetActivity;
import co.ello.ElloApp.PushNotifications.RegistrationIntentService;
import co.ello.ElloApp.Reachability;
import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MainActivity activity);
    void inject(Reachability reachability);
    void inject(RegistrationIntentService service);
    void inject(NoInternetActivity activity);
}
