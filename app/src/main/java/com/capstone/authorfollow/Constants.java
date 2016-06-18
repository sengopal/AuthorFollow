package com.capstone.authorfollow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Mladen Babic <email>info@mladenbabic.com</email> on 11/1/2015.
 */
public class Constants {
    public final static String MOVIE_DATE_FORMAT = "yyyy-MM-dd";
    public final static String MOVIES_KEY = "movies";
    public static final String POSTER_IMAGE_KEY = "poster";
    public final static String TWO_PANE_KEY = "two_pane";
    public final static String MOVIE_DETAIL_KEY = "movie_detail";
    public static final String POSITION_KEY = "position";
    public final static String SORT_BY_KEY = "sort_by";
    public final static String MOVIE_URL = "http://api.themoviedb.org";
    public final static String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/";
    public final static String IMAGE_SIZE_W185 = "w185/";
    public final static String IMAGE_SIZE_W342 = "w342/";
    public final static String IMAGE_SIZE_W500 = "w500/";
    public final static String IMAGE_SIZE_W780 = "w780/";
    public static final String SORT_BY_POPULARITY_DESC = "popularity.desc";
    public static final String SORT_BY_RATING_DESC = "vote_average.desc";
    public static final String POSTER_IMAGE_VIEW_KEY = "posterImageView";

    public static final DateFormat STD_DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
}
