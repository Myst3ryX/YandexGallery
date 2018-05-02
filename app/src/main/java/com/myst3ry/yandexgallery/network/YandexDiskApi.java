package com.myst3ry.yandexgallery.network;

import android.support.annotation.NonNull;

import com.myst3ry.yandexgallery.model.ImagesList;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexDiskApi {

    String API_BASE_URL = "https://cloud-api.yandex.net/v1/disk/";

    //GET last uploaded images
    @GET("resources/last-uploaded")
    Single<ImagesList> getLastUploadedImages(@Query("limit") final int limit,
                                             @Query("media_type") final String mediaType,
                                             @Query("preview_size") final String prevSize);

    //DELETE image (only to Trash now)
    @DELETE("resources")
    Completable deleteImage(@Query("path") @NonNull final String path);

//    //PUT image to publish
//    @PUT("resources/publish")
//    Completable publishImage(@Query("path") @NonNull final String path);
//
//    //UPLOAD images from device

}
