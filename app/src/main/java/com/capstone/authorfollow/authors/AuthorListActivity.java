package com.capstone.authorfollow.authors;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.capstone.authorfollow.BaseListActivity;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.Constants.TrackScreens;
import com.capstone.authorfollow.R;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.google.android.gms.analytics.HitBuilders;

import butterknife.ButterKnife;

public class AuthorListActivity extends BaseListActivity implements AuthorListAdapter.Callback {
    private static final String TAG = "MainActivity";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        ButterKnife.bind(this);

        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
            if (savedInstanceState == null) {
                Bundle bundle = new Bundle();
                Fragment fragment = AuthorDetailFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.detail_container, fragment, AuthorDetailFragment.AUTHOR_DETAIL_FRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }
        setupDrawerContent();
        syncDrawerState(R.id.nav_author);
        getTracker().setScreenName(TrackScreens.AUTHORLIST);
        getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncDrawerState(R.id.nav_author);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        syncDrawerState(R.id.nav_author);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthorClick(AuthorFollow author, View view) {
        Log.d(TAG, "onAuthorClick() returned: " + author);
        Fragment listFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_author_list);
        if (null != listFragment && listFragment instanceof AuthorListFragment) {
            ((AuthorListFragment) listFragment).resetSearchBar();
        }

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Constants.AUTHOR_DETAIL, author);
            AuthorDetailFragment fragment = new AuthorDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, fragment, AuthorDetailFragment.AUTHOR_DETAIL_FRAGMENT_TAG).commit();
        } else {
            ActivityOptions options = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(this, view, Constants.AUTHOR_POSTER_IMAGE_VIEW_KEY);
            }
            Intent openDetailIntent = new Intent(this, AuthorDetailActivity.class);
            openDetailIntent.putExtra(Constants.AUTHOR_DETAIL, author);
            if (options != null) {
                startActivity(openDetailIntent, options.toBundle());
            } else {
                startActivity(openDetailIntent);
            }
        }
    }
}
