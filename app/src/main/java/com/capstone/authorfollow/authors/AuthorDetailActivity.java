package com.capstone.authorfollow.authors;

import android.os.Bundle;
import android.view.MenuItem;

import com.capstone.authorfollow.BaseActivity;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.R;

import butterknife.ButterKnife;

/**
 * An activity representing a single UpcomingBook detail screen. This activity is only used narrow width devices. On tablet-size devices,
 * book details are presented side-by-side with a list of items

 * in a {@link AuthorListActivity}.
 */
public class AuthorDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
                arguments.putParcelable(Constants.AUTHOR_DETAIL, getIntent().getParcelableExtra(Constants.AUTHOR_DETAIL));
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, AuthorDetailFragment.newInstance(arguments)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
