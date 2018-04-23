package com.myst3ry.yandexgallery.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public final class ImagesList {

    @SerializedName("items")
    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

}
