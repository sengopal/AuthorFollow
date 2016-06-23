package com.capstone.authorfollow;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.authorfollow.data.types.UpcomingBook;

/**
 * A fragment representing a single UpcomingBook detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    public static final String DETAIL_FRAGMENT_TAG = "BKFRAGTAG";
    public static final String BOOK_ID = "book_id";
    private UpcomingBook upcomingBook;

    public BookDetailFragment() {
    }

    public static Fragment newInstance(Bundle bundle) {
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null!=getArguments() && getArguments().containsKey(BOOK_ID)) {
            // use a Loader to load content from a content provider.
            //upcomingBook = DummyContent.ITEM_MAP.get(getArguments().getString(BOOK_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("dummy");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (upcomingBook != null) {
            ((TextView) rootView.findViewById(R.id.movie_detail_synopsys_title_text_view)).setText("dummytext");
        }

        return rootView;
    }
}
