package com.myst3ry.yandexgallery.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myst3ry.yandexgallery.BuildConfig;
import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.ui.fragment.dialogfragment.LoginDialogFragment;

/*
 * AuthFragment used to displayed the Authorization screen before user LogIn
 */

public final class AuthFragment extends BaseFragment {

    private static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=";
    private static final String AUTH_QUERY_CLIENT_ID = "36b72804b7b341f5b0301b22325f7c66";

    public static final String ARG_AUTH_URL = BuildConfig.APPLICATION_ID + "arg.auth_url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Bundle args = new Bundle();
        args.putString(ARG_AUTH_URL, getAuthUrl());

        //show login dialog
        final LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.setArguments(args);
        loginDialogFragment.setCancelable(false);
        loginDialogFragment.show(getFragmentManager(), null);
    }

    private String getAuthUrl() {
        return AUTH_URL + AUTH_QUERY_CLIENT_ID;
    }
}
