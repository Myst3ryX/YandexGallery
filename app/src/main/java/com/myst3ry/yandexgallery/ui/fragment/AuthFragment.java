package com.myst3ry.yandexgallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myst3ry.yandexgallery.BuildConfig;
import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.ui.fragment.dialogfragment.AuthDialogFragment;

/*
 * AuthFragment used to displayed the Authorization screen before User login
 */

public final class AuthFragment extends BaseFragment {

    public static final String ARG_AUTH_URL = BuildConfig.APPLICATION_ID + "arg.auth_url";

    private static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=";
    private static final String AUTH_QUERY_CLIENT_ID = "36b72804b7b341f5b0301b22325f7c66";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle args = new Bundle();
        args.putString(ARG_AUTH_URL, getAuthUrl());

        //show Auth dialog
        final AuthDialogFragment authDialogFragment = new AuthDialogFragment();
        authDialogFragment.setArguments(args);
        authDialogFragment.setCancelable(false);
        authDialogFragment.show(getFragmentManager(), null);
    }

    private String getAuthUrl() {
        return AUTH_URL + AUTH_QUERY_CLIENT_ID;
    }
}
