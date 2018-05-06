package com.myst3ry.yandexgallery.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
 * Image model class
 */

public final class Image implements Parcelable {

    @SerializedName("name")
    private String imageName;

    @SerializedName("path")
    private String imagePath;

    @SerializedName("created")
    private String imageCreatedDate;

    @SerializedName("modified")
    private String imageModifiedDate;

    @SerializedName("file")
    private String imageUrl;

    @SerializedName("preview")
    private String imagePreviewUrl;

    @SerializedName("public_url")
    private String imagePublicUrl;

    @SerializedName("size")
    private long imageSize;

    protected Image(Parcel in) {
        imageName = in.readString();
        imagePath = in.readString();
        imageCreatedDate = in.readString();
        imageModifiedDate = in.readString();
        imageUrl = in.readString();
        imagePreviewUrl = in.readString();
        imagePublicUrl = in.readString();
        imageSize = in.readLong();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageCreatedDate() {
        return imageCreatedDate;
    }

    public String getImageModifiedDate() {
        return imageModifiedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImagePreviewUrl() {
        return imagePreviewUrl;
    }

    public String getImagePublicUrl() {
        return imagePublicUrl;
    }

    public long getImageSize() {
        return imageSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageName);
        dest.writeString(imagePath);
        dest.writeString(imageCreatedDate);
        dest.writeString(imageModifiedDate);
        dest.writeString(imageUrl);
        dest.writeString(imagePreviewUrl);
        dest.writeString(imagePublicUrl);
        dest.writeLong(imageSize);
    }
}