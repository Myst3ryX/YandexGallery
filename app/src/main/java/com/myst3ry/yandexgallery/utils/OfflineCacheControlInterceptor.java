package com.myst3ry.yandexgallery.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Interceptor for supporting offline Okhttp Cache
 */

public final class OfflineCacheControlInterceptor implements Interceptor {

    private final Context appContext;

    public OfflineCacheControlInterceptor(final Application application) {
        this.appContext = application.getApplicationContext();
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        final String cacheControlHeader = NetworkUtils.isNetworkAvailable(appContext)
                ? "public, max-age=2419200" //first read from cache for 4 weeks
                : "public, only-if-cached, max-stale=2419200"; //cache stale for 4 weeks
        final Request request = originalRequest.newBuilder()
                .addHeader("Cache-Control", cacheControlHeader)
                .build();
        return chain.proceed(request);
    }
}
