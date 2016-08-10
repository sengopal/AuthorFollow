package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.capstone.authorfollow.service.Services;
import com.capstone.authorfollow.service.Services.BrowseNode;
import com.capstone.authorfollow.service.Services.Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.capstone.authorfollow.CommonUtil.isEmpty;
import static com.capstone.authorfollow.CommonUtil.parse;

@Table(name = "UpcomingBook", id = BaseColumns._ID)
public class UpcomingBook extends Model implements Parcelable {
    private static final String TAG = UpcomingBook.class.getSimpleName();
    private static final DateFormat AWS_ALT_DF = new SimpleDateFormat("yyyy-MM");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
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

    @Column(name = "aspects")
    private String genres;

    public UpcomingBook() {

    }

    public UpcomingBook(String author, Item item, Services.GRBookResponse grBookInfo) {
        this.grApiId = (item.isbn != null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.title = item.title;
        this.author = author;
        this.isbn = (item.isbn != null && !item.isbn.isEmpty() ? item.isbn : item.asin);
        this.smallImageUrl = (!isEmpty(item.mediumImageUrl) ? item.mediumImageUrl : grBookInfo.grImageUrl);
        this.bigImageUrl = (!isEmpty(item.largeImageUrl) ? item.largeImageUrl : grBookInfo.grImageUrl);
        this.amazonLink = item.amazonUrl;
        this.publishedDate = convertToDate(item.pubDate);
        this.rating = grBookInfo.rating;
        this.grLink = grBookInfo.grLinkUrl;
        this.description = grBookInfo.description;
        //this.noOfPages = grBookInfo.noOfPages;
        this.publisher = grBookInfo.publisher;
        this.genres = extractCSGenres(item.browseNodeList);
    }

    private Date convertToDate(String s) {
        Date date = parse(s, DATE_FORMAT);
        if (null == date) {
            date = parse(s, AWS_ALT_DF);
        }
        return (null == s ? new Date() : date);
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
        genres = in.readString();
        try {
            publishedDate = DATE_FORMAT.parse(in.readString());
        } catch (ParseException e) {
            Log.e(UpcomingBook.class.getSimpleName(), "ParseException", e);
        }
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
        dest.writeString(genres);
        dest.writeString(DATE_FORMAT.format(publishedDate));
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

    private String extractCSGenres(List<BrowseNode> browseNodeList) {
        StringBuilder sb = new StringBuilder();
        if (null != browseNodeList) {
            for (BrowseNode node : browseNodeList) {
                sb.append(node.name).append(",");
            }
        }
        return sb.toString();
    }

    public UpcomingBook(UpcomingBook upcomingBook) {
        this.grApiId = upcomingBook.grApiId;
        this.title = upcomingBook.title;
        this.author = upcomingBook.author;
        this.isbn = upcomingBook.isbn;
        this.smallImageUrl = upcomingBook.smallImageUrl;
        this.bigImageUrl = upcomingBook.bigImageUrl;
        this.amazonLink = upcomingBook.amazonLink;
        this.publishedDate = upcomingBook.publishedDate;
        this.rating = upcomingBook.rating;
        this.grLink = upcomingBook.grLink;
        this.description = upcomingBook.description;
        //this.noOfPages = grBookInfo.noOfPages;
        this.publisher = upcomingBook.publisher;
        this.genres = upcomingBook.genres;
    }

    public String getGenres() {
        return genres;
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
        sb.append(", genres='").append(genres).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
