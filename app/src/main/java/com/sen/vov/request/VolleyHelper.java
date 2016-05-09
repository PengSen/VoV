package com.sen.vov.request;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

/**
 * 单例模式初始化volley，线程安全
 * Created by 森 on 2016/3/1.
 */
public class VolleyHelper {

    private static Context mContext;
    private static VolleyHelper getInstance;
    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;
    private VolleyHelper(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized VolleyHelper getInstance(Context context){
        if(getInstance == null){
            getInstance = new VolleyHelper(context);
        }
        return getInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            //获得应用对象避免内存泄漏
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),new OkHttpStack(new OkHttpClient()));
        }
        return mRequestQueue;
    }

    /**
     * 向网络请求队列中添加Http请求
     * @param request
     * @param tag
     * @param <T>
     */
    public <T>void addToRequestQueue(Request<T> request,String tag){
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * 取消掉当前页的所有请求
     * @param tag 默认传入当前页的标志
     */
    public void cancleAllRequest(String tag){
        if(getRequestQueue() != null){
            getRequestQueue().cancelAll(tag);
        }
    }

}
