package com.myst3ry.yandexgallery.ui.activity;

import android.content.Intent;
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
import com.myst3ry.yandexgallery.ui.fragment.dialogfragment.DeleteImageDialogFragment;
import com.myst3ry.yandexgallery.ui.fragment.dialogfragment.ImageInfoDialogFragment;
import com.myst3ry.yandexgallery.utils.OnDeleteClickListener;

import butterknife.BindView;
import timber.log.Timber;


public final class ImageDetailActivity extends BaseActivity implements OnDeleteClickListener {

    public static final String EXTRA_IMAGE_DETAIL = "extra image detail";
    public static final String EXTRA_IMAGE_POSITION = "extra image position";
    public static final int RCODE_IMAGE_DELETE = 66;

    private int position;
    private Image image;
    private String barTitle;

    @BindView(R.id.image_large)
    ImageView imageLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        image = getIntent().getParcelableExtra(EXTRA_IMAGE_DETAIL);
        position = getIntent().getExtras().getInt(EXTRA_IMAGE_POSITION);

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
            case R.id.action_share:
                //share image public link
                final String downloadLink = getPublicDownloadLink();
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text), image.getImageName(), downloadLink));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_text)));
                return true;
            case R.id.action_delete:
                //show image delete confirmation dialog
                final DeleteImageDialogFragment deleteFragment = DeleteImageDialogFragment.newInstance(image);
                deleteFragment.setCancelable(false);
                deleteFragment.show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_image_info:
                //show image additional info dialog
                final ImageInfoDialogFragment infoFragment = ImageInfoDialogFragment.newInstance(image);
                infoFragment.setCancelable(false);
                infoFragment.show(getSupportFragmentManager(), null);
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

    private String getPublicDownloadLink() { //stub
        return "[link]";
    }

    //delete current image
    @Override
    public void onDeleteConfirmClicked() {
        final Intent deleteIntent = new Intent();
        deleteIntent.putExtra(EXTRA_IMAGE_DETAIL, image);
        deleteIntent.putExtra(EXTRA_IMAGE_POSITION, position);
        setResult(RESULT_OK, deleteIntent);
        finish();
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(barTitle);
            bar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.toolbar_background_translucent));
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }
}
