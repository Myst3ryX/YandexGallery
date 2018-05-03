package com.myst3ry.yandexgallery.network;

import android.app.Application;

import com.myst3ry.yandexgallery.BuildConfig;
import com.myst3ry.yandexgallery.utils.OfflineCacheControlInterceptor;
import com.myst3ry.yandexgallery.utils.YandexAuthInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public final class NetworkModule {

    private static final int CACHE_SIZE = 100 * 1024 * 1024; //100MB disk cache size
    private static final int CONNECTION_TIMEOUT_SEC = 15; //15sec connection timeout

    @Provides
    @Singleton
    Cache providesOkHttpCache(Application application) {
        final File httpCacheDir = new File(application.getCacheDir(), "http_cache");
        return new Cache(httpCacheDir, CACHE_SIZE);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Application application, Cache httpCache) {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS) //set connection timeout
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS) //set socket timeout
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG
                        ? HttpLoggingInterceptor.Level.BASIC
                        : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new YandexAuthInterceptor())
                .addInterceptor(new OfflineCacheControlInterceptor(application))
                .addNetworkInterceptor(new OfflineCacheControlInterceptor(application))
                .cache(httpCache)
                .build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(YandexDiskApi.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    YandexDiskApi providesYandexDiskApi(Retrofit retrofit) {
        return retrofit.create(YandexDiskApi.class);
    }
}
