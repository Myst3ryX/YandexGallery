package com.myst3ry.yandexgallery;

import android.app.Application;
import android.support.annotation.NonNull;

import timber.log.Timber;

public final class YandexGalleryApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configureTimber();
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
}
