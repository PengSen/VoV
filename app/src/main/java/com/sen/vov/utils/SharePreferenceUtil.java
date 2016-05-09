package com.sen.vov.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sen on 2016/5/4.
 */
public class SharePreferenceUtil {

//    public SharePreferenceUtil(){
//        PreferenceManager.
//    }
    public static void setSharePage(Context context, String key, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).apply();
    }
    public static String getSharePage(Context context, String key, String defValue){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defValue);
    }
}
