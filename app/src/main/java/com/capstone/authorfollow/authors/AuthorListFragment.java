package com.capstone.authorfollow.authors;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.capstone.authorfollow.BaseActivity;
import com.capstone.authorfollow.R;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.service.AuthorDetailHelper.AuthorSearchAsyncTask;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AuthorListFragment extends Fragment implements AuthorListAdapter.Callback {

    @Bind(R.id.author_list_recycle_view)
    RecyclerView authorListView;

    //@Bind(R.id.empty_author_container)
    //LinearLayout emptyAuthorContainer;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    private SearchView mSearchView;
    private List<AuthorFollow> authorFollowList;
    private AuthorListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_list, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.setToolbar(mToolbar, true, true);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listAdapter = new AuthorListAdapter(AuthorListFragment.this);
        authorListView.setLayoutManager(linearLayoutManager);
        authorListView.setAdapter(listAdapter);
        authorListView.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.spacing_small)));
        authorListView.setHasFixedSize(true);
        authorListView.setAdapter(listAdapter);

        authorFollowList = DBHelper.getAuthorsList();
        listAdapter.setAuthors(authorFollowList);

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
        inflater.inflate(R.menu.authorlist_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();

        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        if(null!=v) {
            v.setImageResource(R.drawable.ic_person_add_white_36px);
        }

        mSearchView.setQueryHint(getString(R.string.search_in_upcoming));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Set from the already followed authors
                //listAdapter.setAuthors(DBHelper.getFilteredAuthorsList(query));
                startSearch(query);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listAdapter.setAuthors(DBHelper.getAuthorsList());
                mSearchView.clearFocus();
                return false;
            }
        });
    }

    private void startSearch(String query) {
        //listAdapter.setAuthors(null);
        progressBar.setVisibility(View.VISIBLE);
        new AuthorSearchAsyncTask(query, new AuthorSearchAsyncTask.Callback() {
            @Override
            public void searchComplete(AuthorFollow authorFollow) {
                progressBar.setVisibility(View.GONE);
                if(null==authorFollow){
                    Snackbar.make(getView(),getString(R.string.author_not_found),Snackbar.LENGTH_LONG).show();
                }else {
                    listAdapter.addToAuthors(authorFollow);
                    mSearchView.setIconified(true);
                    mSearchView.clearFocus();
                }
            }
        }).execute();
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

    public void resetSearchBar() {
        //Calls Close on search
        if (null != mSearchView) {
            mSearchView.setIconified(true);
        }
    }

    @Override
    public void onAuthorClick(AuthorFollow author) {

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
