package com.capstone.authorfollow.service;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.capstone.authorfollow.BuildConfig;
import com.capstone.authorfollow.PreferenceUtil;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.NetworkResponse;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.capstone.authorfollow.service.Services.AmazonService;
import com.capstone.authorfollow.service.Services.GRBookResponse;
import com.capstone.authorfollow.service.Services.Item;
import com.capstone.authorfollow.service.Services.ItemSearchResponse;

import java.io.IOException;
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
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.capstone.authorfollow.CommonUtil.isEmpty;

public class BooksDataLoader extends AsyncTaskLoader<NetworkResponse<List<UpcomingBook>>> {
    private static final DateFormat PUB_DATE_FORMAT = new SimpleDateFormat("MM-yyyy");
    private static final String TAG = "BooksDataLoader";
    private List<String> authors;
    private NetworkResponse<List<UpcomingBook>> bookSvcInfoList;
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
    public NetworkResponse<List<UpcomingBook>> loadInBackground() {
        List<UpcomingBook> booksList = new ArrayList<>();
        //TODO: Temp code for bootstrap
        if (null == this.authors || this.authors.isEmpty()) {
            AuthorDetailHelper.addAuthorToFollowList("James Patterson");
            AuthorDetailHelper.addAuthorToFollowList("James Rollins");
            AuthorDetailHelper.addAuthorToFollowList("J.K.Rowling");
            this.authors = DBHelper.getFollowListNames();
        }else{
            //Refresh the Author Info
            AuthorDetailHelper.refreshAuthorData(this.authors);
        }

        try {
            for (String author : this.authors) {
                booksList.addAll(getBookInfoForAuthor(author));
            }
            DBHelper.updateUpcoming(booksList);
            return new NetworkResponse<>(null, booksList);
        } catch (Exception e) {
            Log.e(TAG, "IOException occurred in loadInBackground()", e);
            return new NetworkResponse<List<UpcomingBook>>(e.getMessage(), booksList);
        }
    }

    private List<UpcomingBook> getBookInfoForAuthor(String author) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        List<UpcomingBook> booksList = new ArrayList<>();
        int upcomingFwdDays = PreferenceUtil.getPrefs(getContext(), PreferenceUtil.PREF_UPCOMING_FWD_DAYS, 120);
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
        return booksList;
    }

    private List<UpcomingBook> convertToDBData(String author, ItemSearchResponse searchResponse) throws IOException {
        List<UpcomingBook> bookList = new ArrayList<>();
        if(null!=searchResponse && null!=searchResponse.resultItems && null!=searchResponse.resultItems.itemList){
            for(Item item : searchResponse.resultItems.itemList) {
                String uniqueId = (null!=item.isbn ? item.isbn : item.asin);
                if(!isEmpty(uniqueId)) {
                    GRBookResponse grBookResponse = getBookInfoFromGR(uniqueId);
                    if(null!=grBookResponse && isImgAvailable(item, grBookResponse)) {
                        bookList.add(new UpcomingBook(author, item, grBookResponse));
                    }
                }
            }
        }
        return bookList;
    }

    private boolean isImgAvailable(Item item, GRBookResponse grBookInfo) {
        //Atleast one image should be available
        return (!isEmpty(item.mediumImageUrl) || !isEmpty(item.mediumImageUrl)
                || (!isEmpty(grBookInfo.grImageUrl) && !grBookInfo.grImageUrl.contains("assets/nophoto/book/")));
    }

    private GRBookResponse getBookInfoFromGR(String uniqueId) throws IOException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Services.GR_URL).addConverterFactory(SimpleXmlConverterFactory.create()).build();
        Services.GoodReadsService goodReadsService = retrofit.create(Services.GoodReadsService.class);
        GRBookResponse grBookInfo = goodReadsService.getBookInfo(uniqueId, BuildConfig.GR_API_KEY).execute().body();
        return grBookInfo;
    }

    private Map<String, String> buildParams(String author, int noOfForwardDays, int pageNum) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SignedRequestsHelper helper = SignedRequestsHelper.getInstance("webservices.amazon.com", BuildConfig.AWS_CUST_KEY, BuildConfig.AWS_API_KEY);
        Map<String, String> params = new HashMap<>();
        params.put("AssociateTag", "sengopalme-20");
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("ResponseGroup", "BrowseNodes,ItemAttributes,Images");
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
    public void deliverResult(NetworkResponse<List<UpcomingBook>> bookSvcInfoList) {
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