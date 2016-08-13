package com.capstone.authorfollow;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.capstone.authorfollow.BookGridAdaptor.BookSelectionListener;
import com.capstone.authorfollow.authors.AuthorListActivity;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.NetworkResponse;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.AuthorDetailHelper;
import com.capstone.authorfollow.service.BooksDataLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<NetworkResponse<List<UpcomingBook>>> {

    @Bind(R.id.book_list_recycle_view)
    RecyclerView mPopularGridView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.no_author_container)
    LinearLayout noAuthorContainer;

    @Bind(R.id.no_connection_container)
    LinearLayout noConnectionContainer;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private BookGridAdaptor bookGridAdaptor;
    private SearchView mSearchView;

    private Handler handler;
    private ContentObserver contentObserver;

    public static BookListFragment newInstance() {
        BookListFragment fragment = new BookListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        List<String> followListNames = DBHelper.getFollowListNames();
        if (null != followListNames && !followListNames.isEmpty()) {
            AuthorDetailHelper.refreshAuthorData(followListNames);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String preLoadedSearch = getActivity().getIntent().getStringExtra(Constants.GENRE_SEARCH);
        if (null != mSearchView && !CommonUtil.isEmpty(preLoadedSearch)) {
            //To ensure that the search text is shown
            mSearchView.setIconified(false);
            mSearchView.setQuery(preLoadedSearch, true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setToolbar(mToolbar, true, true);
        }

        int gridColumns = getResources().getInteger(R.integer.grid_columns);
        int progressViewOffsetStart = getResources().getInteger(R.integer.progress_view_offset_start);
        int progressViewOffsetEnd = getResources().getInteger(R.integer.progress_view_offset_end);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setProgressViewOffset(true, progressViewOffsetStart, progressViewOffsetEnd);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), gridColumns);

        mPopularGridView.setLayoutManager(mLayoutManager);
        mPopularGridView.setHasFixedSize(true);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        int colorPrimaryLight = ContextCompat.getColor(getActivity(), (R.color.colorPrimaryTransparent));
        mPopularGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        List<UpcomingBook> upcomingBookList = DBHelper.upcoming();
        bookGridAdaptor = new BookGridAdaptor((BookSelectionListener) getActivity(), upcomingBookList, colorPrimaryLight);

        setupEmptyContainers(upcomingBookList);

        noAuthorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseListActivity) getActivity()).openActivity(AuthorListActivity.class);
            }
        });

        mPopularGridView.setAdapter(bookGridAdaptor);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);

        registerObserver();

        return view;
    }

    private void setupEmptyContainers(List<UpcomingBook> upcomingBookList) {
        noAuthorContainer.setVisibility(View.GONE);
        noConnectionContainer.setVisibility(View.GONE);
        if (null == upcomingBookList || upcomingBookList.isEmpty()) {
            if (!CommonUtil.isConnected(getActivity())) {
                noConnectionContainer.setVisibility(View.VISIBLE);
            } else {
                noAuthorContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void registerObserver() {
        handler = new Handler();
        contentObserver = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                List<UpcomingBook> upcomingBookList = DBHelper.upcoming();
                bookGridAdaptor.addBooks(upcomingBookList);
                setupEmptyContainers(upcomingBookList);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                onChange(selfChange);
            }
        };
        getContext().getContentResolver().registerContentObserver(UpcomingBook.CONTENT_URI, true, contentObserver);
        getContext().getContentResolver().registerContentObserver(AuthorFollow.CONTENT_URI, true, contentObserver);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.booklist_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_in_upcoming));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookGridAdaptor.addBooks(DBHelper.filterUpcoming(query));
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                bookGridAdaptor.addBooks(DBHelper.upcoming());
                mSearchView.clearFocus();
                return false;
            }
        });
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<NetworkResponse<List<UpcomingBook>>> onCreateLoader(int id, Bundle args) {
        List<String> followList = DBHelper.getFollowListNames();
        return new BooksDataLoader(getActivity(), followList);
    }

    @Override
    public void onLoadFinished(Loader<NetworkResponse<List<UpcomingBook>>> loader, NetworkResponse<List<UpcomingBook>> response) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (response.isSuccess()) {
            List<UpcomingBook> booksList = response.getResponse();
            bookGridAdaptor.addBooks(booksList);
            if (null != booksList && !booksList.isEmpty()) {
                Snackbar.make(getView(), R.string.books_data_loaded, Snackbar.LENGTH_LONG).show();
            }
            setupEmptyContainers(booksList);
        } else {
            Snackbar.make(getView(), getString(R.string.error_message_in_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<NetworkResponse<List<UpcomingBook>>> loader) {
        mSwipeRefreshLayout.setRefreshing(false);
        bookGridAdaptor.addBooks(null);
    }

    @Override
    public void onRefresh() {
        restartLoader();
    }

    public void resetSearchBar() {
        if (null != mSearchView) {
            bookGridAdaptor.addBooks(DBHelper.upcoming());
            mSearchView.clearFocus();
            mSearchView.setIconified(true);
        }
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