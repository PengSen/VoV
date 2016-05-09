package com.sen.vov.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 简化网络请求代码操作类
 * 模拟OkHttp代码使用方式
 * Created by Sen on 2016/3/16.
 */
public class CustomRequest<T> extends Request<T>{
    private Response.Listener<T> mListener;
    private Class<T> mClazz;
    private Gson mGson = new Gson();
    private Map<String,String> mParams;
    private Map<String,String> mHeaders;

    public CustomRequest(int method ,String url ,Class<T> clazz ,Map<String,String> params,Response.Listener<T> listener ,Response.ErrorListener errorListener){
        super(method,url,errorListener);
        mListener = listener;
        mClazz = clazz;
        mParams = params;
        mHeaders = null;
    }
    public CustomRequest(String url ,Class<T> clazz ,Map<String,String> params ,Map<String,String> headers,Response.Listener<T> listener ,Response.ErrorListener errorListener){
        super(Method.GET,url,errorListener);
        mListener = listener;
        mClazz = clazz;
        mParams = params;
        mHeaders = headers;
    }
    public CustomRequest(RequestBuilder builder){
        super(builder.method, builder.url,builder.errorListener);
        mListener = builder.listener;
        mClazz = builder.clazz;
        mParams = builder.mParams;
        mHeaders = builder.mHeaders;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams != null ? mParams : super.getParams();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
                return Response.success(mGson.fromJson(jsonString, mClazz),
                        HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    public static class RequestBuilder{
        private int method = Method.GET;
        private String url;
        private Class clazz;
        private Map<String,String> mParams;
        private Map<String,String> mHeaders;
        private Response.Listener listener;
        private Response.ErrorListener errorListener;

        public RequestBuilder post(){
            this.method = Method.POST;
            return this;
        }
        public RequestBuilder method(int method){
            this.method = method;
            return this;
        }
        public RequestBuilder url(String url){
            this.url = url;
            return this;
        }
        public RequestBuilder clazz(Class clazz){
            this.clazz = clazz;
            return this;
        }
        public RequestBuilder addParams(String key,String value){
            if(mParams == null){
                mParams = new HashMap<>();
                post();
            }
            mParams.put(key,value);
            return this;
        }
        public RequestBuilder params(Map<String,String> params){
            post();
            this.mParams = params;
            return this;
        }
        public RequestBuilder addHeaders(String key,String value){
            if(mHeaders == null)
                mHeaders = new HashMap<>();
            mHeaders.put(key,value);
            return this;
        }
        public RequestBuilder headers(Map<String,String> headers){
            this.mHeaders = headers;
            return this;
        }
        public RequestBuilder successListener(Response.Listener listener){
            this.listener = listener;
            return this;
        }
        public RequestBuilder errorListener(Response.ErrorListener errorListener){
            this.errorListener = errorListener;
            return this;
        }
        public CustomRequest build(){
            return new CustomRequest(this);
        }
    }
}
