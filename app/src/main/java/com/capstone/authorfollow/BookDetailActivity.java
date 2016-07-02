package com.capstone.authorfollow;

import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;

/**
 * An activity representing a single UpcomingBook detail screen. This activity is only used narrow width devices. On tablet-size devices,
 * book details are presented side-by-side with a list of items

 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Constants.BOOK_DETAIL, getIntent().getParcelableExtra(Constants.BOOK_DETAIL));
            getSupportFragmentManager().beginTransaction().add(R.id.book_detail_container, BookDetailFragment.newInstance(arguments)).commit();
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
