package com.myst3ry.yandexgallery.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.myst3ry.yandexgallery.BuildConfig;
import com.myst3ry.yandexgallery.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkHelper {

    private static final String AUTH_TOKEN = BuildConfig.YANDEX_DISK_AUTH_TOKEN; //test token
    private static final int CACHE_SIZE = 100 * 1024 * 1024; //100MB disk cache size
    private static final int CONNECTION_TIMEOUT_SEC = 15;

    private Context appContext;

    public NetworkHelper(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    public OkHttpClient getOkHttpClient() {

        File httpCacheDir = new File(appContext.getCacheDir(), "http_cache");
        Cache httpCache = new Cache(httpCacheDir, CACHE_SIZE);

        return new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS) //connection timeout
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS) //socket timeout
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? Level.BASIC : Level.NONE))
                .addInterceptor(new YandexApiInterceptor())
                .addInterceptor(new OfflineCacheControlInterceptor())
                .addNetworkInterceptor(new OfflineCacheControlInterceptor())
                .cache(httpCache)
                .build();
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(YandexDiskApi.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    public YandexDiskApi getApi() {
        return getRetrofit().create(YandexDiskApi.class);
    }

    //YandexApi Auth Interceptor
    private final class YandexApiInterceptor implements Interceptor {

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

    //Offline Cache-Control Interceptor
    private final class OfflineCacheControlInterceptor implements Interceptor {

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
}
