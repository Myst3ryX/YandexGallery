package com.myst3ry.yandexgallery;

import android.app.Application;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.myst3ry.yandexgallery.network.DaggerNetworkComponent;
import com.myst3ry.yandexgallery.network.NetworkComponent;
import com.myst3ry.yandexgallery.network.NetworkModule;

import timber.log.Timber;

public final class YandexGalleryApp extends Application {

    public static final String AUTH_TOKEN = "auth_token";

    private static NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        configureTimber();
        configureDaggerComponents();
    }

    private void configureTimber() {
        if (BuildConfig.DEBUG) {
            //Debug mode
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            //Release mode
            Timber.plant(new ReleaseTree());
        }
    }

    private void configureDaggerComponents() {
        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public static NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

    public void saveAuthToken(String authToken) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(AUTH_TOKEN, authToken).apply();
    }

    public String readAuthToken() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(AUTH_TOKEN, "");
    }

    public void clearAuthToken() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove(AUTH_TOKEN).apply();
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(readAuthToken());
    }
}
