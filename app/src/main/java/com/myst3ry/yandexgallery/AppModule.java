package com.myst3ry.yandexgallery;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/*
 * Dagger2 App Module (used in NetworkComponent)
 */

@Module
public final class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }
}
