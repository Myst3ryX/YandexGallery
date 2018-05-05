package com.myst3ry.yandexgallery.utils;

import android.app.Application;
import android.support.annotation.NonNull;

import com.myst3ry.yandexgallery.YandexGalleryApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Interceptor for adding Auth Headers to YandexDisk Requests
 */

public final class YandexAuthInterceptor implements Interceptor {

    private final YandexGalleryApp app;

    public YandexAuthInterceptor(final Application application) {
        this.app = (YandexGalleryApp) application;
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        final Request request = originalRequest
                .newBuilder()
                .addHeader("Authorization", "OAuth " + app.readAuthToken())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
        return chain.proceed(request);
    }
}
