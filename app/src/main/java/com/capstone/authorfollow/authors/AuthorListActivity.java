package com.capstone.authorfollow.authors;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.capstone.authorfollow.BaseListActivity;
import com.capstone.authorfollow.BookDetailActivity;
import com.capstone.authorfollow.BookDetailFragment;
import com.capstone.authorfollow.BookGridAdaptor;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.R;
import com.capstone.authorfollow.data.types.UpcomingBook;

import butterknife.ButterKnife;

import static com.capstone.authorfollow.BookDetailFragment.newInstance;

public class AuthorListActivity extends BaseListActivity implements BookGridAdaptor.BookSelectionListener {
    private static final String TAG = "MainActivity";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        ButterKnife.bind(this);

        if (findViewById(R.id.author_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
            if (savedInstanceState == null) {
                Bundle bundle = new Bundle();
                getSupportFragmentManager().beginTransaction().add(R.id.author_detail_container, newInstance(bundle), BookDetailFragment.DETAIL_FRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }
        setupDrawerContent();
        syncDrawerState(R.id.nav_author);
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncDrawerState(R.id.nav_author);
    }

        @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onItemSelected(UpcomingBook bookData, Bitmap posterBitmap, View view) {
        Log.d(TAG, "onItemSelected() returned: " + bookData);
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Constants.BOOK_DETAIL, bookData);
            args.putParcelable(Constants.POSTER_IMAGE_KEY, posterBitmap);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment, BookDetailFragment.DETAIL_FRAGMENT_TAG).commit();
        } else {
            ActivityOptions options = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(this, view, Constants.POSTER_IMAGE_VIEW_KEY);
            }
            Intent openDetailIntent = new Intent(this, BookDetailActivity.class);
            openDetailIntent.putExtra(Constants.BOOK_DETAIL, bookData);
            openDetailIntent.putExtra(Constants.POSTER_IMAGE_KEY, posterBitmap);
            if (options != null) {
                startActivity(openDetailIntent, options.toBundle());
            } else {
                startActivity(openDetailIntent);
            }
        }
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
}
