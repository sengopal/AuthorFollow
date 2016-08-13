package com.capstone.authorfollow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.Tracker;

public abstract class BaseActivity extends AppCompatActivity {

    private Tracker mTracker;

    public void setToolbar(Toolbar mToolbar, boolean showHomeUp, boolean showTitle) {
        if (mToolbar != null) {
            mToolbar.setTitle(getTitle());
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeUp);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthorFollowApplication application = (AuthorFollowApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    protected Tracker getTracker() {
        return mTracker;
    }
}
