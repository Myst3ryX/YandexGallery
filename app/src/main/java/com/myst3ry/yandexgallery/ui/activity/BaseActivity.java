package com.myst3ry.yandexgallery.ui.activity;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.YandexGalleryApp;
import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Abstract Base Activity for all Activities.
 * ButterKnife binds, toolbar setup and watches memory leaks.
 */

abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //watch memory leaks
        RefWatcher refWatcher = YandexGalleryApp.getRefWatcher(this);
        refWatcher.watch(this);
    }
}
