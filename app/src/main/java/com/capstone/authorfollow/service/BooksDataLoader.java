package com.capstone.authorfollow.service;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.capstone.authorfollow.PreferenceUtil;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.Services.AmazonService;
import com.capstone.authorfollow.service.Services.ItemSearchResponse;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class BooksDataLoader extends AsyncTaskLoader<List<UpcomingBook>> {
    private static final DateFormat PUB_DATE_FORMAT = new SimpleDateFormat("MM-yyyy");
    private static final String TAG = "BooksDataLoader";
    private List<String> authors;
    private List<UpcomingBook> bookSvcInfoList;
    private AmazonService service;

    public BooksDataLoader(Context context, AmazonService service, List<String> authors) {
        super(context);
        this.service = service;
        this.authors = authors;
    }

    private static String getPubDate(int forwardDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, forwardDays);
        return PUB_DATE_FORMAT.format(calendar.getTime());
    }

    @Override
    public List<UpcomingBook> loadInBackground() {
        List<UpcomingBook> booksList = new ArrayList<>();
        //TODO: Temp code for bootstrap
        if (null == this.authors || this.authors.isEmpty()) {
            AuthorDetailHelper.addAuthorToFollowList("James Patterson");
            AuthorDetailHelper.addAuthorToFollowList("James Rollins");
            AuthorDetailHelper.addAuthorToFollowList("J.K.Rowling");
            this.authors = DBHelper.getFollowList();
        }

        for(String author : this.authors){
            booksList.addAll(getBookInfoForAuthor(author));
        }
        return booksList;
    }

    private List<UpcomingBook> getBookInfoForAuthor(String author) {
        List<UpcomingBook> booksList = new ArrayList<>();
        int upcomingFwdDays = PreferenceUtil.getPrefs(getContext(), PreferenceUtil.PREF_UPCOMING_FWD_DAYS, 120);
        try {
            Call<ItemSearchResponse> bookSvcInfoCall = service.findBooks(buildParams(author, upcomingFwdDays, 1));
            ItemSearchResponse searchResponse = bookSvcInfoCall.execute().body();
            booksList.addAll(convertToDBData(author, searchResponse));
            if(searchResponse.resultItems.totalPages > 1){
                for(int i = 2; i <= searchResponse.resultItems.totalPages; i++){
                    bookSvcInfoCall = service.findBooks(buildParams(author, upcomingFwdDays, i));
                    searchResponse = bookSvcInfoCall.execute().body();
                    booksList.addAll(convertToDBData(author, searchResponse));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "IOException occurred in loadInBackground()");
        }
        return booksList;
    }

    private List<UpcomingBook> convertToDBData(String author, ItemSearchResponse searchResponse) {
        List<UpcomingBook> bookList = new ArrayList<>();
        if(null!=searchResponse && null!=searchResponse.resultItems && null!=searchResponse.resultItems.itemList){
            for(Services.Item item : searchResponse.resultItems.itemList) {
                if(null!=item.isbn || null!=item.asin) {
                    bookList.add(new UpcomingBook(author, item));
                }
            }
        }
        return bookList;
    }

    private Map<String, String> buildParams(String author, int noOfForwardDays, int pageNum) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SignedRequestsHelper helper = SignedRequestsHelper.getInstance("webservices.amazon.com", "AKIAIV32PE3ER4WKYHOA", "Flxr9aHgX82CfH/W+yKeWsPWW5m6DMMJegDmAIWB");
        Map<String, String> params = new HashMap<>();
        params.put("AssociateTag", "sengopalme-20");
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("ResponseGroup", "ItemAttributes,Images");
        params.put("SearchIndex", "Books");
        params.put("ItemPage", String.valueOf(pageNum));
        //params.put("BrowseNode", "154606011");
        StringBuilder sb = new StringBuilder();
        //Use for recent releases and recent releases as configuration
        //TODO: Make this a configuration too
        sb.append("language:english");
        //author:James Patterson and pubdate: after 05-2016
        sb.append(" and not keywords-begin:kindle");
        sb.append(" and pubdate: after ").append(getPubDate(0));
        sb.append(" and pubdate: before ").append(getPubDate(noOfForwardDays));
        sb.append(" and author:").append(author);
        params.put("Power",sb.toString());
        params.put("Sort","-publication_date");

        String testUrl = helper.sign(params);
        return helper.getSignedParams(params);
    }

    @Override
    public void deliverResult(List<UpcomingBook> bookSvcInfoList) {
        this.bookSvcInfoList = bookSvcInfoList;
        if (isStarted()) {
            super.deliverResult(bookSvcInfoList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (this.bookSvcInfoList != null) {
            deliverResult(this.bookSvcInfoList);
        }

        if (takeContentChanged() || this.bookSvcInfoList == null) {
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