package com.capstone.authorfollow.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.capstone.authorfollow.BaseActivity;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.R;
import com.google.android.gms.analytics.HitBuilders;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
Settings are managing
    - Show Home Page as Authors or Books
    - Show recent
    - upcoming days

    Reference for below code:
    http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
    http://stackoverflow.com/questions/27639786/android-preferencefragment-setting-status-bar-as-per-tool-bar-color
 */
public class SettingsActivity extends BaseActivity {

    @Bind(R.id.settings_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTracker().setScreenName(Constants.TrackScreens.SETTINGS);
        getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        }
    }
}