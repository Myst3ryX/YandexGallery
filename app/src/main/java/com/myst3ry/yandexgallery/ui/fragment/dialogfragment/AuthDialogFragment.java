package com.myst3ry.yandexgallery.ui.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.ui.fragment.AuthFragment;

/*
 * AuthDialogFragment used to displayed the Authorization dialog
 */

public final class AuthDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme)
                .setTitle(R.string.auth_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.auth_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getArguments().getString(AuthFragment.ARG_AUTH_URL)))))
                .setNegativeButton(R.string.auth_exit_btn, (dialog, which) -> getActivity().finishAffinity())
                .create();
    }

}
