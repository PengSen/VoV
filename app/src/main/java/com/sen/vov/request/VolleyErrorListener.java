package com.sen.vov.request;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sen.vov.R;
import com.sen.vov.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 继承volley的错误返回监听，实现对网络层面的错误统一监听。错误释义在最后
 * Created by Sen on 2016/3/18.
 */
public class VolleyErrorListener implements Response.ErrorListener{
    private Activity mActivity;
    public VolleyErrorListener(Activity activity){
        mActivity = activity;
    }
    @Override
    public void onErrorResponse(VolleyError error) {

        mActivity.setContentView(R.layout.activity_error);
        TextView mErrorTxt = (TextView) mActivity.findViewById(R.id.tv_error);
        mErrorTxt.setText(getMessage(error,mActivity));
        mErrorTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
                mActivity.startActivity(new Intent(mActivity, MainActivity.class));
            }
        });
    }

    public static String getMessage(Object error,Activity mActivity) {
        if (error instanceof TimeoutError) {//Socket超时，服务器太忙或网络延迟会产生这个异常
            return mActivity.getResources().getString(R.string.generic_server_down);
        } else if (isServerProblem(error)) {
            return handleServerError(error,mActivity);
        } else if (isNetworkProblem(error)) {
            return mActivity.getResources().getString(R.string.no_internet);
        }
        return mActivity.getResources().getString(R.string.generic_error);
    }

    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError);//Socket关闭，服务器宕机，DNS错误之类的
//                || (error instanceof NoConnectionError);//和NetworkError类似，这个是客户端没有网络连接。
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError)//服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
                || (error instanceof AuthFailureError);//如果在做一个HTTP的身份验证，可能会发生这个错误。
    }

    /**
     *  网络异常返回的状态码处理
     * @param err VolleyError
     * @param mActivity activiy
     * @return 中文释义
     */
    private static String handleServerError(Object err,Activity mActivity) {
        VolleyError error = (VolleyError) err;

        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        // 没有获取相关权限，理论上API会做返回，双重保险
                        HashMap<String, String> result = new Gson().fromJson(
                                new String(response.data),
                                new TypeToken<Map<String, String>>() {
                                }.getType());

                        if (result != null && result.containsKey("error")) {
                            return result.get("error");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return error.getMessage();

                default:
                    return mActivity.getResources().getString(R.string.generic_server_down);
            }
        }
        return mActivity.getResources().getString(R.string.generic_error);
    }

    //在创建一个请求时，需要添加一个错误监听onErrorResponse。如果请求发生异常，会返回一个VolleyError实例。
//以下是Volley的异常列表：
//AuthFailureError：如果在做一个HTTP的身份验证，可能会发生这个错误。
//NetworkError：Socket关闭，服务器宕机，DNS错误都会产生这个错误。
//NoConnectionError：和NetworkError类似，这个是客户端没有网络连接。
//ParseError：在使用JsonObjectRequest或JsonArrayRequest时，如果接收到的JSON是畸形，会产生异常。
//SERVERERROR：服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
//TimeoutError：Socket超时，服务器太忙或网络延迟会产生这个异常。默认情况下，Volley的超时时间为2.5秒，我这里改成了5秒。如果得到这个错误可以使用RetryPolicy。
}
