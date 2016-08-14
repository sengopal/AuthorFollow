/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package com.capstone.authorfollow.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.capstone.authorfollow.R;
import com.capstone.authorfollow.data.types.DBHelper;
import com.capstone.authorfollow.data.types.UpcomingBook;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class WishlistWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context ctxt = null;
    private int appWidgetId;
    private List<UpcomingBook> wishlist;

    public WishlistWidgetViewFactory(Context ctxt, Intent intent) {
        this.ctxt = ctxt;
        this.wishlist = DBHelper.wishlist();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return (null != wishlist && !wishlist.isEmpty() ? wishlist.size() : 1);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_row);

        if (null == wishlist || wishlist.isEmpty()) {
            row.setTextViewText(R.id.book_title, ctxt.getString(R.string.no_wishlist_books));
            row.setTextViewText(R.id.author_title, "");
        } else {
            UpcomingBook wishlistBook = wishlist.get(position);
            row.setTextViewText(R.id.book_title, wishlistBook.getTitle());
            row.setTextViewText(R.id.author_title, wishlistBook.getAuthor());

            try {
                Bitmap b = Picasso.with(ctxt).load(wishlistBook.getSmallImageUrl()).get();
                row.setImageViewBitmap(R.id.book_image, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent();
        Bundle extras = new Bundle();

        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.widget_row_layout, intent);
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        wishlist = DBHelper.wishlist();
    }
}