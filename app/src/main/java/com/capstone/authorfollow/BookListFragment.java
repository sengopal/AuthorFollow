package com.capstone.authorfollow;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.NetworkResponse;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.BooksDataLoader;
import com.capstone.authorfollow.service.Services;
import com.capstone.authorfollow.service.Services.AmazonService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<NetworkResponse<List<UpcomingBook>>> {

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Bind(R.id.book_list_recycle_view)
    RecyclerView mPopularGridView;

    @Bind(R.id.main_movie_sw_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.main_grid_empty_container)
    LinearLayout mNoMovieContainer;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private List<UpcomingBook> bookList;
    private BookGridAdaptor bookGridAdaptor;
    private AmazonService mService;
    private SearchView mSearchView;

    private int selectedPosition;

    public static BookListFragment newInstance() {
        BookListFragment fragment = new BookListFragment();
        return fragment;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.AMAZON_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
        mService = retrofit.create(AmazonService.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initRetrofit();
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String preLoadedSearch = getActivity().getIntent().getStringExtra(Constants.GENRE_SEARCH);
        if (null != mSearchView) {
            //To ensure that the search text is shown
            mSearchView.setIconified(false);
            mSearchView.setQuery(preLoadedSearch, true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO Mladen stage 2
        //outState.putInt(Constants.POSITION_KEY, bookGridAdaptor.getSelectedPosition());
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

        bookList = DBHelper.upcoming();

        bookGridAdaptor = new BookGridAdaptor((BookSelectionListener) getActivity(), bookList, colorPrimaryLight);
        //TODO Mladen stage 2
//        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.POSITION_KEY)) {
//            bookGridAdaptor.setSelectedPosition(savedInstanceState.getInt(Constants.POSITION_KEY));
//        }

        mPopularGridView.setAdapter(bookGridAdaptor);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);
        return view;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String sortType;
        boolean result;

        switch (item.getItemId()) {
            case R.id.sort_by_popularity_desc:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = true;
                break;
            case R.id.sort_by_rates_desc:
                sortType = Constants.SORT_BY_RATING_DESC;
                result = true;
                break;
            default:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = super.onOptionsItemSelected(item);
                break;
        }
        PreferenceUtil.savePrefs(getActivity(), Constants.SORT_BY_KEY, sortType);
        restartLoader();
        return result;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<NetworkResponse<List<UpcomingBook>>> onCreateLoader(int id, Bundle args) {
        List<String> followList = DBHelper.getFollowList();
        return new BooksDataLoader(getActivity(), mService, followList);
    }

    @Override
    public void onLoadFinished(Loader<NetworkResponse<List<UpcomingBook>>> loader, NetworkResponse<List<UpcomingBook>> response) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (response.isSuccess()) {
            bookList = response.getResponse();
            bookGridAdaptor.addBooks(bookList);
            Snackbar.make(getView(), R.string.movies_data_loaded, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(getView(), response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
            if (!CommonUtil.isConnected(getActivity())) {
                //toggleShowEmptyMovie(false);
            }
        }
        //If there are no upcoming books for selected Authors
        mNoMovieContainer.setVisibility((bookList == null || bookList.isEmpty()) ? View.VISIBLE : View.GONE);
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
