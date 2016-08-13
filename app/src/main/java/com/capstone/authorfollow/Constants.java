package com.capstone.authorfollow;

public class Constants {
    public static final String POSTER_IMAGE_KEY = "poster";
    public final static String TWO_PANE_KEY = "two_pane";
    public final static String BOOK_DETAIL = "book_detail";
    public final static String AUTHOR_DETAIL = "author_detail";
    public static final String BOOK_POSTER_IMAGE_VIEW_KEY = "bookPosterImageView";
    public static final String AUTHOR_POSTER_IMAGE_VIEW_KEY = "authorPosterImageView";
    public static final String GENRE_SEARCH = "genre";
    public static final String PREF_USERNAME = "pref.username";
    public static final String PREF_EMAIL = "pref.useremail";
    public static final String PREF_USER_PIC = "pref.userpic";
    public static final String SIGN_OUT_ATTEMPT = "signout";

    public interface TrackScreens {
        public static final String BOOKS = "books";
        public static final String WISHLIST = "wishlist";
        public static final String BOOK_DETAIL = "book_detail";
        public static final String AUTHORLIST = "authors";
        public static final String AUTHOR_DETAIL = "author_detail";
        public static final String SETTINGS = "settings";
    }

    public interface TrackEvents {
        public static final String ADD_BOOK = "add_book";
        public static final String REMOVE_BOOK = "remove_book";
        public static final String ADD_AUTHOR = "add_author";
        public static final String REMOVE_AUTHOR = "remove_author";
    }
}
