package com.capstone.authorfollow;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.capstone.authorfollow.authors.AuthorListActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

public class BaseListActivity extends AppCompatActivity {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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

    protected void setupDrawerContent() {
        mToolbar.setTitle(getTitle());
        setSupportActionBar(mToolbar);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                openActivity(BookListActivity.class);
                                break;
                            case R.id.nav_author:
                                openActivity(AuthorListActivity.class);
                                break;
                            case R.id.nav_signout:
                                //TODO: Open signin activity with Intent of Sign out
                                openActivity(AuthorListActivity.class);
                                break;

                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        setupUserDetails();
    }

    private void setupUserDetails() {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView userImgView = (CircleImageView) headerView.findViewById(R.id.profile_image);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.drawer_user_name);
        TextView userEmailTextView = (TextView) headerView.findViewById(R.id.drawer_user_email);
        userNameTextView.setText(PreferenceUtil.getPrefs(getApplicationContext(), Constants.PREF_USERNAME, ""));
        userEmailTextView.setText(PreferenceUtil.getPrefs(getApplicationContext(), Constants.PREF_EMAIL, ""));
        String userPicUrl = PreferenceUtil.getPrefs(getApplicationContext(), Constants.PREF_USER_PIC, "");
        Picasso.with(userImgView.getContext()).load(userPicUrl).placeholder(R.drawable.ic_movie_placeholder).into(userImgView);
    }

    protected void syncDrawerState(int checkedItem) {
        navigationView.setCheckedItem(checkedItem);
    }

    private void openActivity(Class<?> activityClz) {
        Intent intent = new Intent(getApplicationContext(), activityClz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return;
    }

    protected void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
