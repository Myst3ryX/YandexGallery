package com.myst3ry.yandexgallery.ui.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/*
 * ImageInfoDialogFragment used to displayed the image detail information
 */

public final class ImageInfoDialogFragment extends DialogFragment {

    @BindView(R.id.info_image_name)
    TextView imageName;
    @BindView(R.id.info_image_created_date)
    TextView imageCreatedDate;
    @BindView(R.id.info_image_modified_date)
    TextView imageModifiedDate;
    @BindView(R.id.info_image_is_public)
    TextView imageIsPublic;
    @BindView(R.id.info_image_size)
    TextView imageSize;

    private static final String ARG_CURRENT_IMAGE_INFO = "current image info argument";
    private Unbinder unbinder;

    public static ImageInfoDialogFragment newInstance(final Image image) {
        final ImageInfoDialogFragment infoFragment = new ImageInfoDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_IMAGE_INFO, image);
        infoFragment.setArguments(args);
        return infoFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_image_info, null);
        final Image currentImage = getArguments() != null ? getArguments().getParcelable(ARG_CURRENT_IMAGE_INFO) : null;
        unbinder = ButterKnife.bind(this, view);

        if (currentImage != null) {
            //convert image size from bytes to megabytes and prepare strings
            final double sizeInMegabytes = (currentImage.getImageSize() / 1024d) / 1024d;
            final String name = currentImage.getImageName();
            final String created = currentImage.getImageCreatedDate();
            final String modified = currentImage.getImageModifiedDate();
            final String isPublic = getString(currentImage.getImagePublicUrl() != null
                    ? R.string.info_image_public_yes
                    : R.string.info_image_public_no);

            imageName.setText(String.format(getString(R.string.info_image_name), name));
            imageCreatedDate.setText(String.format(getString(R.string.info_image_created_date), reformatDate(created)));
            imageModifiedDate.setText(String.format(getString(R.string.info_image_modified_date), reformatDate(modified)));
            imageSize.setText(String.format(getString(R.string.info_image_size), sizeInMegabytes));
            imageIsPublic.setText(isPublic);
        }

        return new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme)
                .setTitle(getString(R.string.info_image_title))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .create();
    }

    //parse date to another output format
    private String reformatDate(final String dateToFormat) {
        try {
            SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
            return outputDate.format(inputDate.parse(dateToFormat));
        } catch (Exception e) {
            Timber.e("Error while parsing date: %s", e.getMessage());
            return dateToFormat;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}