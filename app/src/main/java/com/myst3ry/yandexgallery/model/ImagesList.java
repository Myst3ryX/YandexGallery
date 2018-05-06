package com.myst3ry.yandexgallery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Response model class with list of Images
 */

public final class ImagesList {

    @SerializedName("items")
    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

}
