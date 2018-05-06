package com.myst3ry.yandexgallery.ui.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.YandexGalleryApp;

/*
 * LogoutDialogFragment used to displayed the Authorization LogOut dialog:
 * Clear the Auth token and quit the App
 */

public final class LogoutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme)
                .setTitle(R.string.logout_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.logout_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    ((YandexGalleryApp) getActivity().getApplication()).clearAuthToken();
                    getActivity().finishAffinity();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
    }
}