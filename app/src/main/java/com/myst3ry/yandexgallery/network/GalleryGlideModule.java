package com.myst3ry.yandexgallery.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.myst3ry.yandexgallery.YandexGalleryApp;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/*
 * Ensures that Glide's generated API is created
 */

@GlideModule
public final class GalleryGlideModule extends AppGlideModule {

    @Inject
    OkHttpClient okHttpClient;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        //increase default image quality to argb_8888
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        YandexGalleryApp.getNetworkComponent(context).inject(this);
        //replace default networking logic to okhttp logic
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }
}
