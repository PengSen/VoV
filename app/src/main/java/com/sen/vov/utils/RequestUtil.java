package com.sen.vov.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Adapter;
import android.widget.Toast;

import com.sen.vov.models.SystemModel;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * API的url
 * 请求参数组合
 * Created by Sen on 2016/3/22.
 */
public class RequestUtil {

    private final static String AppKey = "c11323c473614bf9a88131a509c2838a";
    public final static String sisterUrl = "http://route.showapi.com/255-1";//type=10 图片  type=29 段子 type=31 声音 type=41 视频

    /**
     * 接收非系统级参数的Map
     * @return 完整的系统请求参数
     */
    public static Map<String,String> getRequestParams(Map<String,String> params){
        params.put("showapi_appid","16390");
        params.put("showapi_timestamp",getFormatSystemDate());
        params.put("showapi_res_gzip","1");//是否支持gzip压缩，1true0false
        String apiSign = getApiSign(params);
        params.put("showapi_sign",apiSign);
        return params;
    }
    public static boolean getModelDataState(Context context,Object response){
        SystemModel systemModel = (SystemModel) response;
        if(systemModel.getShowapi_res_code() == 0){
            return true;
        }else{
            Toast.makeText(context, systemModel.getShowapi_res_error(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 得到系统时间并格式化
     *
     * @return yyyyMMddHHmmss
     */
    @SuppressLint("SimpleDateFormat")
    private static String getFormatSystemDate() {
        return  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
    }
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };
    private static String toHexString(byte[] b) {
        if(b!=null && b.length>0) {
            int length = b.length;
            //String to  byte
            StringBuilder sb = new StringBuilder(length * 2);
            for (int i = 0; i < length; i++) {
                sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[b[i] & 0x0f]);
            }
            return sb.toString();
        }
        return "";
    }
    private static String mmd5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes("UTF-8"));
            byte messageDigest[] = digest.digest();

            return toHexString(messageDigest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param map 未排序map
     * @return api的加密标志
     */
    private static String getApiSign(Map<String,String> map){
        String[] sequenceArray = getSequenceArray(map);
        StringBuilder buffer = new StringBuilder();
        for (String key:
        sequenceArray) {
            String value = map.get(key);
            buffer.append(key).append(value);
        }
        //可优化成foreach
//        for(int i=0;i<sequenceArray.length;i++){
//            String key = sequenceArray[i];
//            String value = map.get(key);
//            buffer.append(key).append(value);
//        }
        return  mmd5(buffer.append(AppKey).toString());
    }

    /**
     * 字母排序请求参数
     * @param map 未排序map
     * @return 排序之后的数组
     */
    private static String[] getSequenceArray(Map<String,String> map){
        int size = map.size();
        Iterator<String> iterator = map.keySet().iterator();
        String[] str = new String[size];
        for(int i=0;i<size;i++){
            String next = iterator.next();
            str[i] = next;
        }
        Arrays.sort(str);
        return str;
    }
}
