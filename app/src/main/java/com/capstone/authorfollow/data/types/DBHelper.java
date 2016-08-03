package com.capstone.authorfollow.data.types;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    public static List<UpcomingBook> wishlist() {
        List<WishlistBook> list = new Select().from(WishlistBook.class).execute();
        if (null != list) {
            ArrayList<UpcomingBook> booksList = new ArrayList<>();
            for (WishlistBook wishlistBook : list) {
                booksList.add(wishlistBook);
            }
            return booksList;
        }
        return new ArrayList<>();
    }

    public static List<UpcomingBook> upcoming() {
        List<UpcomingBook> list = new Select().from(UpcomingBook.class).execute();
        return (null != list ? list : new ArrayList<UpcomingBook>());
    }

    public static AuthorFollow getAuthorInfo(String author) {
        List<AuthorFollow> list = new Select().from(AuthorFollow.class).where("name = ?", author).execute();
        return (null != list && !list.isEmpty()) ? list.get(0) : null;
    }

    public static List<AuthorFollow> getAuthorsList() {
        List<AuthorFollow> authorList = new Select().from(AuthorFollow.class).execute();
        return authorList;
    }

    public static List<String> getFollowListNames() {
        List<AuthorFollow> authorList = getAuthorsList();
        List<String> nameList = new ArrayList<>();
        if (null != authorList && !authorList.isEmpty()) {
            for (AuthorFollow author : authorList) {
                nameList.add(author.getName());
            }
        }
        return nameList;
    }

    // For Author Detail screen. To show the list of books in wishlist for that author
    public static List<WishlistBook> getAuthorBooksInWishlist(int authorId) {
        return new Select().from(WishlistBook.class).where("author_id = ?", authorId).execute();
    }

    public static void updateUpcoming(List<UpcomingBook> booksList) {
        ActiveAndroid.beginTransaction();
        new Delete().from(UpcomingBook.class).execute();
        for (UpcomingBook book : booksList) {
            book.save();
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
    }

    public static boolean checkInWishlist(String grApiId) {
        List<WishlistBook> list = new Select().from(WishlistBook.class).where("gr_api_id = ?", grApiId).execute();
        return (null != list && !list.isEmpty());
    }

    public static List<UpcomingBook> getBooksFromAuthor(String author) {
        List<UpcomingBook> list = new Select().from(UpcomingBook.class).where("author = ?", author).execute();
        return list;
    }

    public static void addToWishlist(UpcomingBook upcomingBook) {
        WishlistBook wishlistBook = new WishlistBook(upcomingBook);
        wishlistBook.save();
    }

    public static List<UpcomingBook> filterUpcoming(String query) {
        List<UpcomingBook> list = new ArrayList<>();
        List<UpcomingBook> booksByAuthor = new Select().from(UpcomingBook.class).where("author like ?", "%" + query + "%").execute();
        list.addAll(booksByAuthor);
        List<UpcomingBook> booksByTitle = new Select().from(UpcomingBook.class).where("title like ?", "%" + query + "%").execute();
        list.addAll(booksByTitle);
        List<UpcomingBook> booksByGenre = new Select().from(UpcomingBook.class).where("aspects like ?", "%" + query + "%").execute();
        list.addAll(booksByGenre);
        return (null != list ? list : new ArrayList<UpcomingBook>());
    }

    public static List<UpcomingBook> filterWishlist(String query) {
        List<WishlistBook> list = new ArrayList<>();
        List<WishlistBook> booksByAuthor = new Select().from(WishlistBook.class).where("author like ?", "%" + query + "%").execute();
        list.addAll(booksByAuthor);
        List<WishlistBook> booksByTitle = new Select().from(WishlistBook.class).where("title like ?", "%" + query + "%").execute();
        list.addAll(booksByTitle);

        ArrayList<UpcomingBook> booksList = new ArrayList<>();
        for (WishlistBook wishlistBook : list) {
            booksList.add(wishlistBook);
        }

        return booksList;
    }

    public static void removeFromWishlist(String grApiId) {
        new Delete().from(WishlistBook.class).where("gr_api_id = ?", grApiId).execute();
    }

    public static List<AuthorFollow> getFilteredAuthorsList(String query) {
        List<AuthorFollow> list = new Select().from(AuthorFollow.class).where("name like ?", "%" + query + "%").execute();
        return (null != list ? list : new ArrayList<AuthorFollow>());
    }
}