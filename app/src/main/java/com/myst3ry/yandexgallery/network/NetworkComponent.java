package com.myst3ry.yandexgallery.network;

import com.myst3ry.yandexgallery.AppModule;
import com.myst3ry.yandexgallery.ui.fragment.GalleryFragment;

import javax.inject.Singleton;

import dagger.Component;

/*
 * Dagger2 Network Component
 */

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class})
public interface NetworkComponent {

    void inject(GalleryGlideModule galleryGlideModule);

    void inject(GalleryFragment galleryFragment);
}