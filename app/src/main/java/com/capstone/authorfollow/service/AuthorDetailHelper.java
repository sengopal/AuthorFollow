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
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(author, GR_KEY).execute().body();
                Map<String, String> params = new HashMap<>();
                params.put("format", "xml");
                params.put("key", GR_KEY);
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
                Services.AuthorInfo authorInfo = goodReadsService.getAuthorId(query, GR_KEY).execute().body();

                if (null != authorInfo.authorId && !authorInfo.authorId.isEmpty()) {
                    Map<String, String> params = new HashMap<>();
                    params.put("format", "xml");
                    params.put("key", GR_KEY);
                    authorDetail = goodReadsService.getAuthorDetailById(authorInfo.authorId, params).execute().body();
                }

                /*
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.AMAZON_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
                Services.AmazonService service = retrofit.create(Services.AmazonService.class);
                List<String> authorList = new ArrayList<>();
                Call<Services.ItemSearchResponse> bookSvcInfoCall = service.findBooks(buildParams(query, 1));
                Services.ItemSearchResponse searchResponse = bookSvcInfoCall.execute().body();
                authorList.addAll(extractAuthorList(searchResponse));
                if (searchResponse.resultItems.totalPages > 1) {
                    for (int i = 2; i <= searchResponse.resultItems.totalPages; i++) {
                        bookSvcInfoCall = service.findBooks(buildParams(query, i));
                        searchResponse = bookSvcInfoCall.execute().body();
                        authorList.addAll(extractAuthorList(searchResponse));
                    }
                }
                */
            } catch (Exception e) {
                Log.e(TAG, "Exception in calling aws", e);
            }
            return authorDetail;
        }

        //Using a Set to get uniqueness
        /*
        private Set<String> extractAuthorList(Services.ItemSearchResponse searchResponse) {
            Set<String> authorList = new HashSet<>();
            if (null != searchResponse && null != searchResponse.resultItems && null != searchResponse.resultItems.itemList) {
                for (Services.Item item : searchResponse.resultItems.itemList) {
                    if (null!=item.authorNameList && !item.authorNameList.isEmpty()) {
                        authorList.addAll(item.authorNameList);
                    }
                }
            }
            return authorList;
        }

        private Map<String, String> buildParams(String author, int pageNum) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
            SignedRequestsHelper helper = SignedRequestsHelper.getInstance("webservices.amazon.com", "AKIAIV32PE3ER4WKYHOA", "Flxr9aHgX82CfH/W+yKeWsPWW5m6DMMJegDmAIWB");
            Map<String, String> params = new HashMap<>();
            params.put("AssociateTag", "sengopalme-20");
            params.put("Service", "AWSECommerceService");
            params.put("Operation", "ItemSearch");
            params.put("ResponseGroup", "ItemAttributes");
            params.put("SearchIndex", "Books");
            params.put("ItemPage", String.valueOf(pageNum));
            StringBuilder sb = new StringBuilder();
            sb.append("language:english");
            sb.append(" and not keywords-begin:kindle");
            sb.append(" and author-exact:").append(author);
            params.put("Power", sb.toString());
            params.put("Sort", "-publication_date");
            return helper.getSignedParams(params);
        }*/

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
