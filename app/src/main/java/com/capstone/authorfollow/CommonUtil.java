package com.capstone.authorfollow;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class CommonUtil {

    public static boolean isConnected(Activity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected());
    }

    public static boolean isEmpty(String s){
        return (null==s || s.length()==0);
    }
}
