package com.capstone.authorfollow.service;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sengopal on 5/27/16.
 */
public class AuthorListLoader extends AsyncTaskLoader<List<String>> {
    private static final String TAG = "AuthorListLoader";
    private List<String> authorList;
    private AuthorListService service;
    private String searchParam;

    public AuthorListLoader(Context context, AuthorListService service, String param) {
        super(context);
        this.service = service;
        this.searchParam = param;
    }

    @Override
    public List<String> loadInBackground() {
        Call<List<String>> authorListSvcCall = service.findAuthors(this.searchParam);
        try {
            Response<List<String>> authorListResponse = authorListSvcCall.execute();
            return authorListResponse.body();
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred in loadInBackground()");
        }
        return null;
    }

    @Override
    public void deliverResult(List<String> authorList) {
        this.authorList = authorList;
        if (isStarted()) {
            super.deliverResult(authorList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (this.authorList != null) {
            deliverResult(this.authorList);
        }

        if (takeContentChanged() || this.authorList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}