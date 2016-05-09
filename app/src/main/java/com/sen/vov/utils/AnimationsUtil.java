package com.sen.vov.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.sen.vov.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动画实现类
 * Created by Sen on 2016/3/21.
 */
public class AnimationsUtil {
    private final static int radius = 300;//圆半径
    private static int duration = 500;//动画时间
    public static void inCrossShowChildAnimation(View... view){
        AnimatorSet set = new AnimatorSet();
        int length = view.length;
        for (int i=0;i<length;i++){
            View viewChild = view[i];
            int x,y;//位移的圆心坐标
            if(i == 0){
                x = radius;
                y = 0;
            }else if(i == length - 1){
                x = 0;
                y = -radius;
            }else{
                float angle = (-90 / (length - 1)) * i;//计算偏移角度
                x = (int) (radius * Math.cos(Math.toRadians(angle)));
                y = (int) (radius * Math.sin(Math.toRadians(angle)));
//                Log.e("angle:",""+angle);
            }
//            Log.e("x:"+x,"y:"+y);
            ObjectAnimator translationX = ObjectAnimator.ofFloat(viewChild, "translationX", 0, -x);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(viewChild, "translationY", 0, y);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(viewChild, "scaleX", 0, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewChild, "scaleY", 0, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(viewChild, "rotation", 0, 360f);
            set.playTogether(translationX,translationY,scaleX,scaleY);
            set.play(rotation);
        }
            set.setDuration(duration);
            set.start();
    }
    public static void outCrossShowChildAnimation(View... view){
        AnimatorSet set = new AnimatorSet();
        int length = view.length;
        for (int i=0;i<length;i++) {
            View viewChild = view[i];
            ObjectAnimator rotation = ObjectAnimator.ofFloat(viewChild, "rotation", 360f, 0f);
            ObjectAnimator translationX = ObjectAnimator.ofFloat(viewChild, "translationX", viewChild.getTranslationX(),0);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(viewChild, "translationY", viewChild.getTranslationY(),0);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(viewChild, "scaleX", 1f, 0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewChild, "scaleY", 1f, 0f);
            set.playTogether(rotation,translationX,translationY,scaleX,scaleY);
        }
            set.setInterpolator(new DecelerateInterpolator(2f));//先快后慢
            set.setDuration(duration);
            set.start();
    }

    /**
     * 调用animator文件夹下的动画
     * @param context
     * @param id
     * @param view
     * @return animator对象，方便开启动画并对动画的start end做监听
     */
    public static Animator xmlFileLoadAnimator(Context context,int id, View view ){
        Animator animator = AnimatorInflater.loadAnimator(context, id);
        animator.setTarget(view);
        return animator;
    }
}
