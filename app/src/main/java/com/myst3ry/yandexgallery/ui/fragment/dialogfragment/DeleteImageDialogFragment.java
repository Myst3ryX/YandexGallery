package com.myst3ry.yandexgallery.ui.fragment.dialogfragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.model.Image;
import com.myst3ry.yandexgallery.utils.OnDeleteClickListener;

/*
 * DeleteImageDialogFragment used to displayed the delete confirmation
 */

public final class DeleteImageDialogFragment extends DialogFragment {

    private static final String ARG_CURRENT_IMAGE_DELETE = "current image delete argument";
    private OnDeleteClickListener onDeleteClickListener;

    public static DeleteImageDialogFragment newInstance(final Image image) {
        final DeleteImageDialogFragment deleteFragment = new DeleteImageDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_IMAGE_DELETE, image);
        deleteFragment.setArguments(args);
        return deleteFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = getActivity();
        if (activity instanceof OnDeleteClickListener) {
            this.onDeleteClickListener = (OnDeleteClickListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Image currentImage = getArguments() != null ? getArguments().getParcelable(ARG_CURRENT_IMAGE_DELETE) : null;
        String title = currentImage != null ? currentImage.getImageName() : "";

        //try to reformat dialog title with "middle" ellipsize...
        if (title.length() > 21) {
            title = title.substring(0, 10) +
                    "..." + title.substring(title.length() - 8, title.length());
        }

        return new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme)
                .setTitle(String.format(getString(R.string.delete_image_title), title))
                .setMessage(R.string.delete_image_message)
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.yes, (dialog, which) -> onDeleteClickListener.onDeleteConfirmClicked())
                .create();
    }
}
