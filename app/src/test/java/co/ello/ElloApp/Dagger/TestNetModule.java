package co.ello.ElloApp.Dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import org.mockito.Mockito;

import javax.inject.Singleton;

import co.ello.ElloApp.Reachability;
import dagger.Module;
import dagger.Provides;

@Module
public class TestNetModule {

    private final TestElloApp application;

    public TestNetModule(TestElloApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    ConnectivityManager providesConnectivityManager(Application application) {
        return Mockito.mock(ConnectivityManager.class);
    }

    @Provides
    @Singleton
    Reachability providesReachability(ConnectivityManager manager) {
        return Mockito.mock(Reachability.class);
    }
}
