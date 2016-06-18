package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.capstone.authorfollow.service.Services.AuthorDetail;

/**
 * Created by sengopal on 5/8/16.
 */
@Table(name = "AuthorFollow", id = BaseColumns._ID)
public class AuthorFollow extends Model implements Parcelable {
    @Column(name = "gr_author_key", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String grAuthorKey;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "gr_page_link")
    public String grPageLink;

    @Column(name = "home_town")
    public String homeTown;

    @Column(name = "fan_count")
    public String fanCount;

    @Column(name = "desc")
    public String desc;


    public AuthorFollow() {
    }

    protected AuthorFollow(Parcel in) {
        grAuthorKey = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    public AuthorFollow(String grAuthorKey, AuthorDetail detail) {
        this.grAuthorKey = grAuthorKey;
        this.name = detail.name;
        this.imageUrl = detail.imageUrl;
        this.grPageLink = detail.grPageLink;
        this.homeTown = detail.homeTown;
        this.fanCount = detail.fanCount;
        this.desc = detail.desc;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(grAuthorKey);
        dest.writeString(name);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthorFollow> CREATOR = new Creator<AuthorFollow>() {
        @Override
        public AuthorFollow createFromParcel(Parcel in) {
            return new AuthorFollow(in);
        }

        @Override
        public AuthorFollow[] newArray(int size) {
            return new AuthorFollow[size];
        }
    };

    public String getGrAuthorKey() {
        return grAuthorKey;
    }

    public void setGrAuthorKey(String grAuthorKey) {
        this.grAuthorKey = grAuthorKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
