package com.capstone.authorfollow.data.types;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.capstone.authorfollow.service.Services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.capstone.authorfollow.CommonUtil.parse;

@Table(name = "AuthorFollow", id = BaseColumns._ID)
public class AuthorFollow extends Model implements Parcelable {
    private static final DateFormat GR_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

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

    @Column(name = "bday")
    public Date dateOfBirth;

    @Column(name = "fan_count")
    public String fanCount;

    @Column(name = "desc")
    public String desc;

    @Column(name = "follow_status")
    public boolean followStatus;

    public AuthorFollow(Services.AuthorDetail detail, boolean followStatus) {
        this.grAuthorKey = detail.id;
        this.name = detail.name;
        this.imageUrl = detail.imageUrl;
        this.grPageLink = detail.grPageLink;
        this.homeTown = detail.homeTown;
        this.fanCount = detail.fanCount;
        this.desc = detail.desc;
        this.followStatus = followStatus;
        this.dateOfBirth = convertToDate(detail.bornAt);
    }

    private Date convertToDate(String s) {
        Date date = parse(s, GR_DATE_FORMAT);
        return date;
    }

    public AuthorFollow() {

    }

    public void refresh(Services.AuthorDetail detail) {
        if(null!=detail) {
            this.imageUrl = detail.imageUrl;
            this.grPageLink = detail.grPageLink;
            this.homeTown = detail.homeTown;
            this.fanCount = detail.fanCount;
            this.desc = detail.desc;
            this.dateOfBirth = convertToDate(detail.bornAt);
        }
    }

    protected AuthorFollow(Parcel in) {
        grAuthorKey = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        grPageLink = in.readString();
        homeTown = in.readString();
        fanCount = in.readString();
        desc = in.readString();
        followStatus = in.readByte() != 0;
        long l = in.readLong();
        if(l > 0) {
            dateOfBirth = new Date(l);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(grAuthorKey);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(grPageLink);
        dest.writeString(homeTown);
        dest.writeString(fanCount);
        dest.writeString(desc);
        dest.writeByte((byte) (followStatus ? 1 : 0));
        dest.writeLong(null!=dateOfBirth ? dateOfBirth.getTime() : -1);
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

    public String getGrPageLink() {
        return grPageLink;
    }

    public void setGrPageLink(String grPageLink) {
        this.grPageLink = grPageLink;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getFanCount() {
        return fanCount;
    }

    public void setFanCount(String fanCount) {
        this.fanCount = fanCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isFollowStatus() {
        return followStatus;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setFollowStatus(boolean followStatus) {
        this.followStatus = followStatus;
    }
}
