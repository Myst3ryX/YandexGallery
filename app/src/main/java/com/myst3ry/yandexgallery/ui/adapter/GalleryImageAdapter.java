package com.myst3ry.yandexgallery.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.network.GlideApp;
import com.myst3ry.yandexgallery.ui.activity.ImageDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public final class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ImageHolder> {

    private List<Image> images = new ArrayList<>();

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Image image = getImage(position);
        GlideApp.with(holder.itemView.getContext())
                .load(image.getImagePreviewUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.color_image_placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(@NonNull List<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    private Image getImage(int position) {
        return images.get(position);
    }

    final class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_view) ImageView imageView;

        ImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this); //test
        }

        @Override
        public void onClick(View v) {
            final Context context = v.getContext();
            Intent intent = new Intent(context, ImageDetailActivity.class);
            Image imageSelected = getImage(getLayoutPosition());
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE_DETAIL, imageSelected);
            context.startActivity(intent);
        }
    }
}
