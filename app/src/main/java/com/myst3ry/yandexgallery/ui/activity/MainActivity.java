package com.myst3ry.yandexgallery.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.myst3ry.yandexgallery.R;

import timber.log.Timber;

public final class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_one:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
