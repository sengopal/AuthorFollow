package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.capstone.authorfollow.Constants;
import com.capstone.authorfollow.service.Services.Item;

/**
 * This is the Homescreen list - the upcoming Booklist
 */

@Table(name = "UpcomingBook", id = BaseColumns._ID)
public class UpcomingBook extends Model implements Parcelable {
    @Column(name = "gr_api_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String grApiId;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "smallImageUrl")
    private String smallImageUrl;

    @Column(name = "bigImageUrl")
    private String bigImageUrl;

    @Column(name = "rating")
    private int rating;

    @Column(name = "amazonLink")
    private String amazonLink;

    @Column(name = "publishedDate")
    private String publishedDate;

    public UpcomingBook() {
    }

    public UpcomingBook(String author, Item item){
        this.grApiId = (item.isbn!=null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.title = item.title;
        this.author = author;
        this.isbn = (item.isbn!=null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.smallImageUrl = item.mediumImageUrl;
        this.bigImageUrl = item.largeImageUrl;
        this.amazonLink = item.amazonUrl;
        this.publishedDate = Constants.STD_DATE_FORMAT.format(item.pubDate);
        //populated dynamically on detail page
        //this.rating =
        //this.grLink =
    }

    protected UpcomingBook(Parcel in) {
        grApiId = in.readString();
        title = in.readString();
        author = in.readString();
        isbn = in.readString();
        smallImageUrl = in.readString();
        bigImageUrl = in.readString();
        rating = in.readInt();
        amazonLink = in.readString();
        publishedDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(grApiId);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(isbn);
        dest.writeString(smallImageUrl);
        dest.writeString(bigImageUrl);
        dest.writeInt(rating);
        dest.writeString(amazonLink);
        dest.writeString(publishedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpcomingBook> CREATOR = new Creator<UpcomingBook>() {
        @Override
        public UpcomingBook createFromParcel(Parcel in) {
            return new UpcomingBook(in);
        }

        @Override
        public UpcomingBook[] newArray(int size) {
            return new UpcomingBook[size];
        }
    };
}
