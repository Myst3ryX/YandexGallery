package com.myst3ry.yandexgallery;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.livefront.bridge.Bridge;
import com.livefront.bridge.SavedStateHandler;
import com.myst3ry.yandexgallery.network.DaggerNetworkComponent;
import com.myst3ry.yandexgallery.network.NetworkComponent;
import com.myst3ry.yandexgallery.network.NetworkModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import icepick.Icepick;
import timber.log.Timber;

public final class YandexGalleryApp extends Application {

    public static final String AUTH_TOKEN = "auth_token";

    private NetworkComponent networkComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        configureTimber();
        prepareDaggerComponents();
        installLeakCanary();
        initBridge();
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

    private void prepareDaggerComponents() {
        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    private void installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    private void initBridge() {
        Bridge.initialize(getApplicationContext(), new SavedStateHandler() {
            @Override
            public void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
                Icepick.saveInstanceState(target, state);
            }

            @Override
            public void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
                Icepick.restoreInstanceState(target, state);
            }
        });
    }

    public static NetworkComponent getNetworkComponent(Context context) {
        return ((YandexGalleryApp) context.getApplicationContext()).networkComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((YandexGalleryApp) context.getApplicationContext()).refWatcher;
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
