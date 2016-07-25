package com.capstone.authorfollow;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.authorfollow.data.types.UpcomingBook;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookListActivity extends BaseListActivity implements BookGridAdaptor.BookSelectionListener {
    private static final String TAG = "BookListActivity";

    private boolean mTwoPane;
    private BookListFragment upcomingListFragment;
    private WishlistFragment wishlistFragment;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
            if (savedInstanceState == null) {
                Bundle bundle = new Bundle();
                getSupportFragmentManager().beginTransaction().add(R.id.book_detail_container, BookDetailFragment.newInstance(bundle), BookDetailFragment.DETAIL_FRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }

        initFragments(savedInstanceState);
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setupDrawerContent();
        syncDrawerState(R.id.nav_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncDrawerState(R.id.nav_home);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
*/
    private void initFragments(Bundle savedInstanceState) {
        upcomingListFragment = new BookListFragment();
        upcomingListFragment.resetSearchBar();
        wishlistFragment = new WishlistFragment();
    }

    private void setupViewPager() {
        FragmentTabsAdapter adapter = new FragmentTabsAdapter(getSupportFragmentManager());
        adapter.addFragment(upcomingListFragment, getString(R.string.upcoming));
        adapter.addFragment(wishlistFragment, getString(R.string.wishlist));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                upcomingListFragment.resetSearchBar();
            }
        });
    }

    /*
    private void setupDrawerContent(NavigationView navigationView) {
        mToolbar.setTitle(getTitle());
        setSupportActionBar(mToolbar);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
*/
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_author_add:
                Toast.makeText(getApplicationContext(), "Add Author", Toast.LENGTH_LONG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class FragmentTabsAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> fragmentTitles;

        public FragmentTabsAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments = new ArrayList<>();
            fragmentTitles = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
