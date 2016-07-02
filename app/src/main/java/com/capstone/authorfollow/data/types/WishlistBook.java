package com.capstone.authorfollow.data.types;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.annotation.Table;

/**
 * Created by sengopal on 6/13/16.
 */

@Table(name = "WishlistBook", id = BaseColumns._ID)
public class WishlistBook extends UpcomingBook implements Parcelable {
    public static final String CONTENT_AUTHORITY = "com.capstone.authorfollow/wishlistbook";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public WishlistBook() {
    }

    protected WishlistBook(Parcel in) {
        super(in);
    }

    public WishlistBook(UpcomingBook upcomingBook) {
        super(upcomingBook);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
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
