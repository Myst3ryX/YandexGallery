package com.myst3ry.yandexgallery.utils;

import android.support.annotation.NonNull;

import com.myst3ry.yandexgallery.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
 * OkHttp Interceptor for adding Auth headers to Yandex Disk api requests
 */

public final class YandexAuthInterceptor implements Interceptor {

    private static final String AUTH_TOKEN = BuildConfig.YANDEX_DISK_AUTH_TOKEN; //test token

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request request = originalRequest
                .newBuilder()
                .addHeader("Authorization", "OAuth " + AUTH_TOKEN)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
        return chain.proceed(request);
    }
}
