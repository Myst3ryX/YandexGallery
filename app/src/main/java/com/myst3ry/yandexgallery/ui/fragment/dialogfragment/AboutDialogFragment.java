package com.myst3ry.yandexgallery.ui.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.myst3ry.yandexgallery.R;

/*
 * AboutDialogFragment used to displayed the about information
 */

public final class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme)
                .setTitle(R.string.about_title)
                .setMessage(String.format(getString(R.string.about_message), getString(R.string.about_developer)))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create();
    }
}