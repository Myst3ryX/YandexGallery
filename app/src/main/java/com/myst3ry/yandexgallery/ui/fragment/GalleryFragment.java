package com.myst3ry.yandexgallery.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private List<Image> images;
    private YandexDiskApi yandexDiskApi;
    private GalleryImageAdapter imageAdapter;

    private int itemsLimitInc = 0;
    private boolean isLoading = false;

    @BindView(R.id.fab_add_images)
    FloatingActionButton fabAddImages;
    @BindView(R.id.gallery_rec_view)
    RecyclerView galleryRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.refresher)
    SwipeRefreshLayout swipeRefreshLayout;

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

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);
        galleryRecyclerView.setAdapter(imageAdapter);
        galleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //hide fab with scrolling down and show with scrolling up
                if (dy > 0 && fabAddImages.isShown()) {
                    fabAddImages.hide();
                } else if (dy < 0 && !fabAddImages.isShown()) {
                    fabAddImages.show();
                }

                //simple pagination
                if (dy > 0) {
                    int visibleItemCount = gridLayoutManager.getChildCount();
                    int totalItemCount = gridLayoutManager.getItemCount();
                    int pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems + 25) >= totalItemCount) {
                            itemsLimitInc += QUERY_ITEMS_LIMIT;
                            loadImages(itemsLimitInc);
                        }
                    }
                }
            }
        });

        fabAddImages.setOnClickListener(view1 -> {
            Timber.i("Fab Clicked");
            //add new images from device to the gallery
        });

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(() -> loadImages(0));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (images == null) {
            progressBar.setVisibility(View.VISIBLE);
            loadImages(itemsLimitInc);
        }
    }

    @SuppressLint("CheckResult")
    private void loadImages(final int itemsLimitInc) {
        yandexDiskApi.getLastUploadedImages(QUERY_ITEMS_LIMIT + itemsLimitInc, QUERY_MEDIA_TYPE, QUERY_PREVIEW_CROP, QUERY_PREVIEW_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((s) -> isLoading = true)
                .doAfterTerminate(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                })
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
}
