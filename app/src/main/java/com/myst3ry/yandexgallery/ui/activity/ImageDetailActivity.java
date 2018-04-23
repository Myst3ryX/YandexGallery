package com.myst3ry.yandexgallery.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.network.GlideApp;

import butterknife.BindView;

public final class ImageDetailActivity extends BaseActivity {

    @BindView(R.id.image_large)
    ImageView imageLarge;

    public static final String EXTRA_IMAGE_DETAIL = "image detail";
    private Image image;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        image = getIntent().getParcelableExtra(EXTRA_IMAGE_DETAIL);
        if (image != null) {
            title = image.getImageName();
            loadImage();
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

    private void loadImage() {
        GlideApp.with(this)
                .load(image.getImageUrl())
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageLarge);
    }

    private void setUpActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }
}
