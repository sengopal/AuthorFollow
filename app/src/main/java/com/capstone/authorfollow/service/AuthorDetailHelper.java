package com.capstone.authorfollow.service;

import android.os.AsyncTask;
import android.util.Log;

import com.capstone.authorfollow.BuildConfig;
import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.service.Services.AuthorDetail;
import com.capstone.authorfollow.service.Services.GoodReadsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class AuthorDetailHelper {
    private static final String TAG = AuthorDetailHelper.class.getSimpleName();

    public static void addAuthorToFollowList(String author) {
        new AuthorDetailAsyncTask(author, true).execute();
    }

    public static void refreshAuthorData(List<String> authorList) {
        for (String author : authorList) {
            new AuthorDetailAsyncTask(author, false).execute();
        }
    }

    public static class AuthorDetailAsyncTask extends AsyncTask<Void, Void, AuthorDetail> {
        private final String author;
        private final boolean isNewAuthor;

        public AuthorDetailAsyncTask(String author, boolean isNewAuthor) {
            this.author = author;
            this.isNewAuthor = isNewAuthor;
        }

        @Override
        protected AuthorDetail doInBackground(Void... v) {
            try {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.GR_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
                GoodReadsService goodReadsService = retrofit.create(GoodReadsService.class);
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(author, BuildConfig.GR_API_KEY).execute().body();
                Map<String, String> params = new HashMap<>();
                params.put("format", "xml");
                params.put("key", BuildConfig.GR_API_KEY);
                AuthorDetail authorDetail = goodReadsService.getAuthorDetailById(authorInfo.authorId, params).execute().body();
                return authorDetail;
            } catch (IOException e) {
                Log.e(TAG, "Exception in calling goodreads", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(AuthorDetail authorDetail) {
            AuthorFollow authorFollow = null;
            if (isNewAuthor) {
                authorFollow = new AuthorFollow(authorDetail, true);
            } else {
                authorFollow = DBHelper.getAuthorInfo(author);
                authorFollow.refresh(authorDetail);
            }
            authorFollow.save();
        }
    }

    public static class AuthorSearchAsyncTask extends AsyncTask<Void, Void, AuthorDetail> {

        public interface Callback {
            void searchComplete(AuthorFollow authorFollow);
        }

        private final String query;
        private final Callback callback;

        public AuthorSearchAsyncTask(String query, Callback callback) {
            this.query = query;
            this.callback = callback;
        }

        @Override
        protected AuthorDetail doInBackground(Void... v) {
            AuthorDetail authorDetail = null;
            try {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.GR_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
                GoodReadsService goodReadsService = retrofit.create(GoodReadsService.class);
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(query, BuildConfig.GR_API_KEY).execute().body();

                if (null != authorInfo.authorId && !authorInfo.authorId.isEmpty()) {
                    Map<String, String> params = new HashMap<>();
                    params.put("format", "xml");
                    params.put("key", BuildConfig.GR_API_KEY);
                    authorDetail = goodReadsService.getAuthorDetailById(authorInfo.authorId, params).execute().body();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception in calling aws", e);
            }
            return authorDetail;
        }

        @Override
        protected void onPostExecute(AuthorDetail authorDetail) {
            AuthorFollow authorFollow = null;
            if (null != authorDetail) {
                authorFollow = new AuthorFollow(authorDetail, false);
            }
            callback.searchComplete(authorFollow);
        }
    }
}
