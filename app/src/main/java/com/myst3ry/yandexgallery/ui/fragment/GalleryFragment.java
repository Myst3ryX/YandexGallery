package com.myst3ry.yandexgallery.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.YandexGalleryApp;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.model.ImagesList;
import com.myst3ry.yandexgallery.network.YandexDiskApi;
import com.myst3ry.yandexgallery.ui.activity.ImageDetailActivity;
import com.myst3ry.yandexgallery.ui.adapter.GalleryImageAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class GalleryFragment extends BaseFragment {

    //API Query Constants (actually, they shouldn't be here :])
    private static final String QUERY_MEDIA_TYPE = "image";
    private static final String QUERY_PREVIEW_SIZE = "XL";
    private static final int QUERY_ITEMS_LIMIT = 50;

    private static final String STATE_LAST_SAVED_IMAGES = "last saved images state";

    @Inject
    YandexDiskApi yandexDiskApi;

    private List<Image> images;
    private GalleryImageAdapter imageAdapter;
    private CompositeDisposable disposables;

    private int loadMoreLimit = 0;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
        YandexGalleryApp.getNetworkComponent().inject(this);
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                            loadMoreLimit += QUERY_ITEMS_LIMIT;
                            loadImages(loadMoreLimit);
                        } else if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            //show error when no connection to the internet (isNetworkAvailable = false)
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
        swipeRefreshLayout.setOnRefreshListener(() -> loadImages(loadMoreLimit));

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_LAST_SAVED_IMAGES)) {
            Timber.i("Restoring Images from Saved State");
            images = savedInstanceState.getParcelableArrayList(STATE_LAST_SAVED_IMAGES);
            updateImages();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (images == null) {
            progressBar.setVisibility(View.VISIBLE);
            loadImages(loadMoreLimit);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (images != null) {
            outState.putParcelableArrayList(STATE_LAST_SAVED_IMAGES, new ArrayList<>(images));
        }
        super.onSaveInstanceState(outState);
    }

    public void initAdapter() {
        imageAdapter = new GalleryImageAdapter((Image image, int position) -> {
            final Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_POSITION, position);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_DETAIL, image);
            startActivityForResult(intent, ImageDetailActivity.RCODE_IMAGE_DELETE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageDetailActivity.RCODE_IMAGE_DELETE && resultCode == Activity.RESULT_OK) {
            final Image image = data.getParcelableExtra(ImageDetailActivity.EXTRA_IMAGE_DETAIL);
            final int position = data.getExtras().getInt(ImageDetailActivity.EXTRA_IMAGE_POSITION);

            //delete current image from adapter and ya.disk (only to trash now)
            if (image != null) {
                disposables.add(yandexDiskApi.deleteImage(image.getImagePath())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        //.doOnError(this::handleError)
                        .subscribe(() -> {
                            Toast.makeText(getContext(), R.string.delete_success_toast, Toast.LENGTH_SHORT).show();
                            imageAdapter.deleteImage(position);
                            Timber.i("Image %s successfully deleted! Position: %d", image.getImageName(), position);
                        }, t -> Timber.e("Error: %s", t.getMessage())));
            }
        }
    }

    //get last uploaded images from ya.disk
    private void loadImages(final int loadMoreLimit) {
        disposables.add(yandexDiskApi.getLastUploadedImages(QUERY_ITEMS_LIMIT + loadMoreLimit, QUERY_MEDIA_TYPE, QUERY_PREVIEW_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((s) -> isLoading = true)
                //.doOnError(this::handleError)
                .subscribe((ImagesList response) -> {
                    images = response.getImages();
                    updateImages();
                    Timber.i("Images was loaded, rows = %d", images != null ? images.size() : -1);
                }, t -> Timber.e("Error: %s", t.getMessage())));
    }

    private void updateImages() {
        if (isLoading) {
            isLoading = false;
        }

        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (imageAdapter != null && images != null) {
            imageAdapter.setImages(images);
        } else {
            //show error or empty text
        }
    }

//    //refactor
//    private void handleError(final Throwable t) {
//        Timber.e("Error: %s", t.getMessage());
//
//        if (t instanceof HttpException) {
//            HttpException httpException = (HttpException) t;
//            int code = httpException.code();
//            //...
//            Snackbar.make(galleryRecyclerView, httpException.message(), Snackbar.LENGTH_SHORT).show();
//        } else {
//            Snackbar.make(galleryRecyclerView, t.getMessage(), Snackbar.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposables != null) {
            disposables.dispose();
            Timber.i("Destroyed. Disposables disposed.");
        }
    }
}
