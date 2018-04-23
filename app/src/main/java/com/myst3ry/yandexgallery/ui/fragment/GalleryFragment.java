package com.myst3ry.yandexgallery.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.network.NetworkHelper;
import com.myst3ry.yandexgallery.network.YandexDiskApi;
import com.myst3ry.yandexgallery.ui.adapter.GalleryImageAdapter;

import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;


public final class GalleryFragment extends BaseFragment {

    //API Query Constants (actually, they shouldn't be here :])
    private static final String QUERY_MEDIA_TYPE = "image";
    private static final String QUERY_PREVIEW_SIZE = "XL";
    private static final boolean QUERY_PREVIEW_CROP = true;
    private static final int QUERY_ITEMS_LIMIT = 50;
//    private static final String[] QUERY_ITEM_FIELDS = {"items.name", "items.path", "items.created", "items.modified",
//            "items.file", "items.preview", "items.media_type", "items.mime_type", "items.size"};

    private List<Image> images;
    private YandexDiskApi yandexDiskApi;
    private GalleryImageAdapter imageAdapter;

    @BindView(R.id.fab_add_images) FloatingActionButton fabAddImages;
    @BindView(R.id.gallery_rec_view) RecyclerView galleryRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        yandexDiskApi = new NetworkHelper().getApi();
        imageAdapter = new GalleryImageAdapter();

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.MULTIPLY);

        galleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        galleryRecyclerView.setAdapter(imageAdapter);
        galleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //hide fab with scrolling down and show with scrolling up
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabAddImages.isShown()) {
                    fabAddImages.hide();
                } else if (dy < 0 && !fabAddImages.isShown()){
                    fabAddImages.show();
                }
            }
        });

        fabAddImages.setOnClickListener(view1 -> {
            Timber.i("Fab Clicked");
            //add new images from device to the gallery
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (images == null) {
            loadImages();
        }
    }

    @SuppressLint("CheckResult")
    private void loadImages() {
        yandexDiskApi.getLastUploadedImages(QUERY_ITEMS_LIMIT, QUERY_MEDIA_TYPE, QUERY_PREVIEW_CROP, QUERY_PREVIEW_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(d -> showProgressBar(true))
                .doAfterTerminate(() -> showProgressBar(false))
                .doOnError(this::handleError)
                .subscribe(response -> {
                    images = response.getImages();
                    updateImages();
                    Timber.i("Images was loaded, rows = %d", images != null ? images.size() : -1);
                }, t -> Timber.e("Error: %s", t.getMessage()));
    }

    private void updateImages() {
        if (imageAdapter != null && images != null) {
            imageAdapter.setImages(images);
        } else {
            //show error or empty text
        }
    }

    private void handleError(final Throwable t) {
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            int code = httpException.code();
            //...
            Snackbar.make(galleryRecyclerView, httpException.message(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void showProgressBar(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}
