package com.capstone.authorfollow.data.types;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    public static List<WishlistBook> wishlist(){
        List<WishlistBook> list = new Select().from(WishlistBook.class).execute();
        return (null!= list ? list : new ArrayList<WishlistBook>());
    }

    public static List<UpcomingBook> upcoming(){
        List<UpcomingBook> list = new Select().from(UpcomingBook.class).execute();
        return (null!= list ? list : new ArrayList<UpcomingBook>());
    }

    public static AuthorFollow getAuthorInfo(long authorId){
        List<AuthorFollow> list = new Select().from(AuthorFollow.class).where("_Id = ?", authorId).execute();
        return (null!=list && !list.isEmpty()) ? list.get(0) : null;
    }

    public static List<String> getFollowList(){
        List<AuthorFollow> authorList = new Select().from(AuthorFollow.class).execute();
        List<String> nameList = new ArrayList<>();
        if(null!=authorList && !authorList.isEmpty()){
            for(AuthorFollow author : authorList){
                nameList.add(author.getName());
            }
        }
        return nameList;
    }

    // For Author Detail screen. To show the list of books in wishlist for that author
    public static List<WishlistBook> getAuthorBooksInWishlist(int authorId){
        return new Select().from(WishlistBook.class).where("author_id = ?", authorId).execute();
    }
}
