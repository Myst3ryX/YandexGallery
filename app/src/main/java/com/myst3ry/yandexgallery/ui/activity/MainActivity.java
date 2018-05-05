package com.myst3ry.yandexgallery.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.myst3ry.yandexgallery.R;
import com.myst3ry.yandexgallery.YandexGalleryApp;
import com.myst3ry.yandexgallery.ui.fragment.AuthFragment;
import com.myst3ry.yandexgallery.ui.fragment.GalleryFragment;
import com.myst3ry.yandexgallery.ui.fragment.dialogfragment.AboutDialogFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public final class MainActivity extends BaseActivity {

    private YandexGalleryApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (YandexGalleryApp) getApplication();

        if (getIntent() != null && !app.isLoggedIn()) {
            onFirstLogin();
        }

        if (savedInstanceState == null) {
            showContent();
        }
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
            case R.id.action_about:
                //show about information dialog
                new AboutDialogFragment().show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_logout:
                //clear auth token and quit app
                app.clearAuthToken();
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //parse and save token on the first login
    private void onFirstLogin() {
        Uri data = getIntent().getData();
        if (data != null) {
            Timber.i("Intent received");

            final Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
            final Matcher matcher = pattern.matcher(data.toString());

            if (matcher.find()) {
                final String authToken = matcher.group(1);
                Timber.i("Token received successful");
                app.saveAuthToken(authToken);
            } else {
                //show err
            }
        } else {
            //show err
        }
    }

    private void showContent() {
        final Fragment fragment = app.isLoggedIn() ? new GalleryFragment() : new AuthFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
