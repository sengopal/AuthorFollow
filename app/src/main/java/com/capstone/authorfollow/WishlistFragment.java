package com.capstone.authorfollow;

import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.capstone.authorfollow.BookGridAdaptor.BookSelectionListener;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.data.types.WishlistBook;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WishlistFragment extends Fragment {

    @Bind(R.id.book_list_recycle_view)
    RecyclerView mPopularGridView;

    @Bind(R.id.no_book_container)
    LinearLayout noBookContainer;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<UpcomingBook> bookList;
    private BookGridAdaptor bookGridAdaptor;

    private Handler handler;
    private ContentObserver contentObserver;

    public static WishlistFragment newInstance() {
        WishlistFragment fragment = new WishlistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void registerObserver() {
        handler = new Handler();
        contentObserver = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                bookList.clear();
                List<UpcomingBook> wishlist = DBHelper.wishlist();
                bookList.addAll(wishlist);
                setupEmptyContainers(wishlist);
                bookGridAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                onChange(selfChange);
            }
        };
        getContext().getContentResolver().registerContentObserver(WishlistBook.CONTENT_URI, true, contentObserver);
    }

    private void setupEmptyContainers(List<UpcomingBook> bookList) {
        noBookContainer.setVisibility(View.GONE);
        if (null == bookList || bookList.isEmpty()) {
            noBookContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterObserver();
    }

    private void unregisterObserver() {
        getContext().getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
        handler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);

        int gridColumns = getResources().getInteger(R.integer.grid_columns);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), gridColumns);

        mPopularGridView.setLayoutManager(mLayoutManager);
        mPopularGridView.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        int colorPrimaryLight = ContextCompat.getColor(getActivity(), (R.color.colorPrimaryTransparent));
        mPopularGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        bookList = DBHelper.wishlist();

        bookGridAdaptor = new BookGridAdaptor((BookSelectionListener) getActivity(), bookList, colorPrimaryLight);
        mPopularGridView.setAdapter(bookGridAdaptor);

        setupEmptyContainers(bookList);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookList = DBHelper.wishlist();
                setupEmptyContainers(bookList);
                bookGridAdaptor.addBooks(bookList);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        registerObserver();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(space, space, space, space);
        }
    }
}
