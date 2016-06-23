package com.capstone.authorfollow;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.BooksDataLoader;
import com.capstone.authorfollow.service.Services;
import com.capstone.authorfollow.service.Services.AmazonService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<UpcomingBook>> {

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public interface Callback {
        public void onItemSelected(UpcomingBook bookData, Bitmap posterBitmap, View view);
    }

    @Bind(R.id.book_list_recycle_view)
    RecyclerView mPopularGridView;

    @Bind(R.id.main_movie_sw_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.main_grid_empty_container)
    LinearLayout mNoMovieContainer;

    private List<UpcomingBook> bookList;
    private BookGridAdaptor bookGridAdaptor;
    private AmazonService mService;

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
        bookList = DBHelper.upcoming();
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
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

        bookGridAdaptor = new BookGridAdaptor(bookList, colorPrimaryLight, (Callback) getActivity());
        //TODO Mladen stage 2
//        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.POSITION_KEY)) {
//            bookGridAdaptor.setSelectedPosition(savedInstanceState.getInt(Constants.POSITION_KEY));
//        }

        mPopularGridView.setAdapter(bookGridAdaptor);
        bookGridAdaptor.addBooks(DBHelper.upcoming());
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity().getApplicationContext(), "Submitted:" + query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getActivity().getApplicationContext(), newText, Toast.LENGTH_LONG).show();
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
    public Loader<List<UpcomingBook>> onCreateLoader(int id, Bundle args) {
        List<String> followList = DBHelper.getFollowList();
        return new BooksDataLoader(getActivity(), mService, followList);
    }

    @Override
    public void onLoadFinished(Loader<List<UpcomingBook>> loader, List<UpcomingBook> data) {
        mSwipeRefreshLayout.setRefreshing(false);
        bookList = data;
        bookGridAdaptor.addBooks(data);
        if (data == null || data.isEmpty()) {
            if (!CommonUtil.isConnected(getActivity())) {
                toggleShowEmptyMovie(false);
            }
        } else {
            toggleShowEmptyMovie(true);
        }
        //TODO Mladen stage 2
        Snackbar.make(getView(), data == null ? R.string.movies_not_found : R.string.movies_data_loaded, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<List<UpcomingBook>> loader) {
        mSwipeRefreshLayout.setRefreshing(false);
        bookGridAdaptor.addBooks(null);
    }

    @Override
    public void onRefresh() {
        restartLoader();
    }

    private void toggleShowEmptyMovie(boolean showMovieGrid) {
        mNoMovieContainer.setVisibility(showMovieGrid ? View.GONE : View.VISIBLE);
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
