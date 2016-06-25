package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.capstone.authorfollow.service.Services;
import com.capstone.authorfollow.service.Services.Item;

import java.util.Date;

import static com.capstone.authorfollow.CommonUtil.isEmpty;

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
    private float rating;

    @Column(name = "amazonLink")
    private String amazonLink;

    @Column(name = "grLink")
    private String grLink;

    @Column(name = "publishedDate")
    private Date publishedDate;

    @Column(name = "description")
    private String description;

    @Column(name = "noOfPages")
    private int noOfPages;

    @Column(name = "publisher")
    private String publisher;

    public UpcomingBook(){

    }

    public UpcomingBook(String author, Item item, Services.GRBookResponse grBookInfo) {
        this.grApiId = (item.isbn != null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.title = item.title;
        this.author = author;
        this.isbn = (item.isbn != null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.smallImageUrl = (!isEmpty(item.mediumImageUrl) ? item.mediumImageUrl : grBookInfo.grImageUrl);
        this.bigImageUrl = (!isEmpty(item.largeImageUrl) ? item.largeImageUrl : grBookInfo.grImageUrl);
        this.amazonLink = item.amazonUrl;
        this.publishedDate = item.pubDate;
        this.rating = grBookInfo.rating;
        this.grLink = grBookInfo.grLinkUrl;
        this.description = grBookInfo.description;
        //this.noOfPages = grBookInfo.noOfPages;
        this.publisher = grBookInfo.publisher;
    }

    protected UpcomingBook(Parcel in) {
        grApiId = in.readString();
        title = in.readString();
        author = in.readString();
        isbn = in.readString();
        smallImageUrl = in.readString();
        bigImageUrl = in.readString();
        rating = in.readFloat();
        amazonLink = in.readString();
        grLink = in.readString();
        description = in.readString();
        noOfPages = in.readInt();
        publisher = in.readString();
        publishedDate = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(grApiId);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(isbn);
        dest.writeString(smallImageUrl);
        dest.writeString(bigImageUrl);
        dest.writeFloat(rating);
        dest.writeString(amazonLink);
        dest.writeString(grLink);
        dest.writeString(description);
        dest.writeInt(noOfPages);
        dest.writeString(publisher);
        dest.writeLong(publishedDate.getTime());
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

    public float getRating() {
        return rating;
    }

    public String getAmazonLink() {
        return amazonLink;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getGrLink() {
        return grLink;
    }

    public String getDescription() {
        return description;
    }

    public int getNoOfPages() {
        return noOfPages;
    }

    public String getPublisher() {
        return publisher;
    }

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
        sb.append(", grLink='").append(grLink).append('\'');
        sb.append(", publishedDate=").append(publishedDate);
        sb.append(", description='").append(description).append('\'');
        sb.append(", noOfPages=").append(noOfPages);
        sb.append(", publisher='").append(publisher).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
