//package com.sen.vov.activity.photo.transform;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//import com.sen.vov.utils.ScreenUtil;
//import com.sen.vov.utils.StringUtil;
//
///**
// * glide图片压缩显示，废弃
// * Created by Sen on 2016/4/11.
// */
//public class PhotoTransform extends BitmapTransformation {
//
//    private int imageheight;
//    private int screenWidth;
//    private ImageView view;
//    private String url;
//    public PhotoTransform(Context context, String url, ImageView view) {
//        super(context);
//        imageheight = ScreenUtil.getStatusHeight(context) - ScreenUtil.getStatusHeight(context) - 20;
//        screenWidth = ScreenUtil.getScreenWidth(context);
//        this.url = url;
//        this.view = view;
//    }
//    @Override
//    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
////        int width = toTransform.getWidth();
//        int height = toTransform.getHeight();
////        Log.e("width", String.valueOf(width));
////        Log.e("height", String.valueOf(height));
////        Log.e("outWidth", String.valueOf(outWidth));
////        Log.e("outHeight", String.valueOf(outHeight));
//        if(StringUtil.isGifImage(url)){//GIF
//            view.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth));
//            view.setScaleType(ImageView.ScaleType.FIT_XY);
//            return toTransform;
//        }else if(height > imageheight){//长图,按照宽度比放大
////            int max = Math.max(width, screenWidth);
//            view.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT));
//            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
////            return Bitmap.createBitmap(toTransform, 0, 0, screenWidth, height * (screenWidth / width));
//            return toTransform;
//        }else{//正常图，按照宽度比放大
//            view.setScaleType(ImageView.ScaleType.FIT_XY);
//            view.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth));
//            return toTransform;
//        }
//    }
//
//    @Override
//    public String getId() {
//        return "photo";
//    }
//}
