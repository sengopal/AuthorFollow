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

    public String getGrApiId() {
        return grApiId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public int getRating() {
        return rating;
    }

    public String getAmazonLink() {
        return amazonLink;
    }

    public String getPublishedDate() {
        return publishedDate;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UpcomingBook{");
        sb.append("grApiId='").append(grApiId).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", isbn='").append(isbn).append('\'');
        sb.append(", smallImageUrl='").append(smallImageUrl).append('\'');
        sb.append(", bigImageUrl='").append(bigImageUrl).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", amazonLink='").append(amazonLink).append('\'');
        sb.append(", publishedDate='").append(publishedDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
