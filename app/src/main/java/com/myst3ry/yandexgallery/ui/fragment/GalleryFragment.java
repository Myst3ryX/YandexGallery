package com.myst3ry.yandexgallery.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.YandexGalleryApp;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.model.ImagesList;
import com.myst3ry.yandexgallery.network.YandexDiskApi;
import com.myst3ry.yandexgallery.ui.activity.ImageDetailActivity;
import com.myst3ry.yandexgallery.ui.adapter.GalleryImageAdapter;
import com.myst3ry.yandexgallery.utils.NetworkUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import icepick.State;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

/*
 * GalleryFragment used to displayed the main content of images loaded from Yandex.Disk
 */

public final class GalleryFragment extends BaseFragment {

    private static final String QUERY_MEDIA_TYPE = "image";
    private static final String QUERY_PREVIEW_SIZE = "XL";
    private static final int QUERY_ITEMS_LIMIT = 100;

    private GalleryImageAdapter imageAdapter;
    private CompositeDisposable disposables;

    @State
    ArrayList<Image> images;
    @State
    int loadMoreLimit;
    boolean isLoading;

    @Inject
    YandexDiskApi yandexDiskApi;

    @BindView(R.id.gallery_rec_view)
    RecyclerView galleryRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_text)
    TextView emptyText;
    @BindView(R.id.refresher)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YandexGalleryApp.getNetworkComponent(getActivity()).inject(this);
        disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        prepareRecyclerView();

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(() -> loadImages(loadMoreLimit));

        //restore saved state
        if (savedInstanceState != null) {
            Timber.i("Restoring from saved state");
            updateUI();
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

    //init adapter with OnImageClickListener anonymous class
    private void initAdapter() {
        imageAdapter = new GalleryImageAdapter((Image image, int position) -> {
            final Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_POSITION, position);
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_DETAIL, image);
            startActivityForResult(intent, ImageDetailActivity.RCODE_IMAGE_DELETE);
        });
    }

    private void prepareRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);
        galleryRecyclerView.setAdapter(imageAdapter);
        galleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //simple and terrible pagination where no offset query
                if (dy > 0) {
                    int visibleItemCount = gridLayoutManager.getChildCount();
                    int totalItemCount = gridLayoutManager.getItemCount();
                    int pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems + 20) >= totalItemCount) {
                            loadMoreLimit += QUERY_ITEMS_LIMIT;
                            loadImages(loadMoreLimit);
                        }
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                                showToast(getString(R.string.error_no_connection));
                            }
                        }
                    }
                }
            }
        });
    }

    //get last uploaded images from ya.disk
    private void loadImages(final int loadMoreLimit) {
        disposables.add(yandexDiskApi.getLastUploadedImages(QUERY_ITEMS_LIMIT + loadMoreLimit,
                QUERY_MEDIA_TYPE, QUERY_PREVIEW_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe((s) -> isLoading = true)
                .subscribe((ImagesList response) -> {
                    images = new ArrayList<>(response.getImages());
                    updateUI();
                    Timber.i("Images was loaded successful");
                }, t -> {
                    if (t instanceof HttpException) {
                        final HttpException http = (HttpException) t;
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {
                            showToast(String.format(getString(R.string.error), http.code()));
                        }
                    }
                    updateUI();
                    Timber.e("Error while loading main content: %s", t.getMessage());
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageDetailActivity.RCODE_IMAGE_DELETE && resultCode == Activity.RESULT_OK) {
            final int position = data.getExtras().getInt(ImageDetailActivity.EXTRA_IMAGE_POSITION);
            final Image image = imageAdapter.getImage(position);

            //delete current image from adapter and ya.disk (only to trash now)
            if (image != null) {
                disposables.add(yandexDiskApi.deleteImage(image.getImagePath())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            showToast(getString(R.string.delete_success_toast));
                            imageAdapter.deleteImage(position);
                            if (imageAdapter.getItemCount() == 0) {
                                updateUI();
                            }
                            Timber.i("Image %s was deleted. Position: %d", image.getImageName(), position);
                        }, t -> {
                            showToast(getString(R.string.error_delete_image));
                            Timber.e("Image not deleted, cuz: %s", t.getMessage());
                        }));
            }
        }
    }

    private void updateUI() {
        if (isLoading) {
            isLoading = false;
        }

        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (emptyText != null) {
            if (images != null && images.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }
        }

        if (images != null && imageAdapter != null) {
            imageAdapter.setImages(images);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //dispose all Rx Disposable's
        if (disposables != null) {
            disposables.dispose();
        }
    }
}
