package com.myst3ry.yandexgallery.utils.listeners;

import android.support.annotation.NonNull;

import com.myst3ry.yandexgallery.model.Image;

public interface OnImageClickListener {

    void onClick(@NonNull final Image image, final int position);
}