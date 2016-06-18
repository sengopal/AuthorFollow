package com.capstone.authorfollow.service;

import android.os.AsyncTask;
import android.util.Log;

import com.capstone.authorfollow.data.types.AuthorFollow;
import com.capstone.authorfollow.service.Services.AuthorDetail;
import com.capstone.authorfollow.service.Services.GoodReadsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class AuthorDetailHelper {
    private static final String TAG = AuthorDetailHelper.class.getSimpleName();

    public static AuthorFollow addAuthorToFollowList(String author) {
        return new AuthorDetailAsyncTask("We96lMbi0gpn6i9oHKd0dA").doInBackground(author);
    }

    public static class AuthorDetailAsyncTask extends AsyncTask<String, Void, AuthorFollow> {
        private String grKey;

        public AuthorDetailAsyncTask(String grKey) {
            this.grKey = grKey;
        }

        @Override
        protected AuthorFollow doInBackground(String... authors) {
            try {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.GR_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
                GoodReadsService goodReadsService = retrofit.create(GoodReadsService.class);
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(authors[0], this.grKey).execute().body();
                Map<String, String> params = new HashMap<>();
                params.put("format", "xml");
                params.put("key", this.grKey);
                AuthorDetail authorDetail = goodReadsService.getAuthorDetailById(authorInfo.authorId, params).execute().body();
                AuthorFollow authorFollow = new AuthorFollow(authorInfo.authorId, authorDetail);
                authorFollow.save();
                return authorFollow;
            } catch (IOException e) {
                Log.e(TAG, "Exception in calling goodreads", e);
            }
            return null;
        }
    }
}
