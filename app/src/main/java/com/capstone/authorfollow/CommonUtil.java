package com.capstone.authorfollow;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class CommonUtil {

    public static boolean isConnected(Activity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected());
    }

    public static boolean isEmpty(String s) {
        return (null == s || s.length() == 0);
    }

    public static class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addTransition(new ChangeBounds()).
                        addTransition(new ChangeTransform()).
                        addTransition(new ChangeImageTransform());
            }
        }
    }

    public static void setupFragAnimation(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new CommonUtil.DetailsTransition());
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new CommonUtil.DetailsTransition());
        }
    }

    public static Date parse(String s, DateFormat dateFormat) {
        try {
            if (s != null) {
                return dateFormat.parse(s);
            }
        } catch (Exception e) {
            Log.e("CommonUtil", "Exception in date parsing", e);
        }
        return null;
    }
}
