package com.myst3ry.yandexgallery.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
 * OkHttp Interceptor for supporting offline cache
 */

public final class OfflineCacheControlInterceptor implements Interceptor {

    private Context appContext;

    public OfflineCacheControlInterceptor(Application app) {
        this.appContext = app.getApplicationContext();
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String cacheControlHeader = NetworkUtils.isNetworkAvailable(appContext)
                ? "public, max-age=2419200" //first read from cache for 4 weeks
                : "public, only-if-cached, max-stale=2419200"; //cache stale for 4 weeks
        Request request = originalRequest.newBuilder()
                .addHeader("Cache-Control", cacheControlHeader)
                .build();
        return chain.proceed(request);
    }
}
