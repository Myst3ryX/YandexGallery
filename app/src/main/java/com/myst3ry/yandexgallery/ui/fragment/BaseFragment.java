package com.myst3ry.yandexgallery.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.myst3ry.yandexgallery.YandexGalleryApp;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
 * Abstract Base Fragment for all Fragments.
 * ButterKnife binds and watches memory leaks.
 */

abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //watch memory leaks
        RefWatcher refWatcher = YandexGalleryApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
