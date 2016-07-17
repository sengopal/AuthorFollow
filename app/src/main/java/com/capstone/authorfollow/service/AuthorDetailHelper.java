package com.capstone.authorfollow.service;

import android.os.AsyncTask;
import android.util.Log;

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
    public static final String GR_KEY = "We96lMbi0gpn6i9oHKd0dA";

    public static void addAuthorToFollowList(String author) {
        new AuthorDetailAsyncTask(author, GR_KEY, true).execute();
    }

    public static void refreshAuthorData(List<String> authorList) {
        for (String author : authorList) {
            new AuthorDetailAsyncTask(author, GR_KEY, false).execute();
        }
    }

    public static class AuthorDetailAsyncTask extends AsyncTask<Void, Void, AuthorDetail> {
        private final String author;
        private final String grKey;
        private final boolean isNewAuthor;

        public AuthorDetailAsyncTask(String author, String grKey, boolean isNewAuthor) {
            this.author = author;
            this.grKey = grKey;
            this.isNewAuthor = isNewAuthor;
        }

        @Override
        protected AuthorDetail doInBackground(Void... v) {
            try {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.GR_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
                GoodReadsService goodReadsService = retrofit.create(GoodReadsService.class);
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(author, this.grKey).execute().body();
                Map<String, String> params = new HashMap<>();
                params.put("format", "xml");
                params.put("key", this.grKey);
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
                authorFollow = new AuthorFollow(authorDetail);
            } else {
                authorFollow = DBHelper.getAuthorInfo(author);
                authorFollow.refresh(authorDetail);
            }
            authorFollow.save();
        }
    }
}
