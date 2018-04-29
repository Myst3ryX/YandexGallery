package com.myst3ry.yandexgallery.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.network.GlideApp;

import butterknife.BindView;
import timber.log.Timber;


public final class ImageDetailActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_DETAIL = "extra image detail";

    private Image image;
    private String barTitle;

    @BindView(R.id.image_large)
    ImageView imageLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        image = getIntent().getParcelableExtra(EXTRA_IMAGE_DETAIL);

        if (image != null) {
            barTitle = image.getImageName();
            loadImage();
        } else {
            //show error that image can't be shown
        }

        setUpActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_two:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //init load image, thumbnail will be placed before full sized image was loaded
    private void loadImage() {
        GlideApp.with(this)
                .load(image.getImageUrl())
                .thumbnail(GlideApp.with(this).load(image.getImagePreviewUrl()))
                .centerInside()
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //show Toast or Snackbar for image loading/no connection error here
                        Timber.e("Full sized Image loading error %s", e != null ? e.getMessage() : null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Timber.i("Full sized Image was successfully loaded");
                        return false;
                    }
                })
                .into(imageLarge);
    }

    private void setUpActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(barTitle);
            bar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.toolbar_background_translucent));
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }
}
