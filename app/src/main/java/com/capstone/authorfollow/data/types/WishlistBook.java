package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by sengopal on 6/13/16.
 */

@Table(name = "WishlistBook", id = BaseColumns._ID)
public class WishlistBook extends Model implements Parcelable {
    @Column(name = "gr_api_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String grApiId;

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "author")
    private String author;

    @Column(name = "gr_author_key")
    private String grAuthorKey;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "smallImageUrl")
    private String smallImageUrl;

    @Column(name = "bigImageUrl")
    private String bigImageUrl;

    @Column(name = "rating")
    private int rating;

    @Column(name = "grLink")
    private String grLink;

    @Column(name = "publishedDate")
    private String publishedDate;

    protected WishlistBook(Parcel in) {
        grApiId = in.readString();
        title = in.readString();
        image = in.readString();
        author = in.readString();
        grAuthorKey = in.readString();
        isbn = in.readString();
        smallImageUrl = in.readString();
        bigImageUrl = in.readString();
        rating = in.readInt();
        grLink = in.readString();
        publishedDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(grApiId);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(author);
        dest.writeString(grAuthorKey);
        dest.writeString(isbn);
        dest.writeString(smallImageUrl);
        dest.writeString(bigImageUrl);
        dest.writeInt(rating);
        dest.writeString(grLink);
        dest.writeString(publishedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WishlistBook> CREATOR = new Creator<WishlistBook>() {
        @Override
        public WishlistBook createFromParcel(Parcel in) {
            return new WishlistBook(in);
        }

        @Override
        public WishlistBook[] newArray(int size) {
            return new WishlistBook[size];
        }
    };
}
