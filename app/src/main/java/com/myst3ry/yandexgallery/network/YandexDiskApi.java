package com.myst3ry.yandexgallery.network;

import com.myst3ry.yandexgallery.model.ImagesList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexDiskApi {

    String API_BASE_URL = "https://cloud-api.yandex.net/v1/disk/";

    //GET last uploaded images
    @GET("resources/last-uploaded")
    Single<ImagesList> getLastUploadedImages(@Query("limit") final int limit,
                                              @Query("media_type") final String mediaType,
                                              @Query("preview_crop") final boolean prevCrop,
                                              @Query("preview_size") final String prevSize);
                                              //@Query("fields") final String fields);

}
