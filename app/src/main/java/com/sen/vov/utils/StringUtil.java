package com.sen.vov.utils;

/**
 * Created by Sen on 2016/4/25.
 */
public class StringUtil {
    /**
     * 加载的图片是否是gif图片
     * @param url 图片的url
     * @return true为gif
     */
    public static boolean isGifImage(String url){
        int length = url.length();
        return url.substring(length-3,length).equals("gif");
    }
}
