package com.sen.vov.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sen.vov.R;
import com.sen.vov.activity.sound.Player;
import com.sen.vov.models.ContentModel;
import com.sen.vov.utils.ScreenUtil;
import com.sen.vov.widget.CusotmProgressView;

import java.util.List;

/**
 * 声音里有广播监听wifi
 * Created by Sen on 2016/4/18.
 */
public class SoundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int TYPE_ITEM = 0;
    private int TYPE_FOOT_VIEW = 1;
    private Context mContext;
    private List<ContentModel> data;
    private int screenWidth;
    private final Player player;
    private boolean isPause;//是否暂停状态
    private boolean isSingleLine;//文字显示是否是一行
    private boolean isVersion;//6.0使用位图会导致图片损坏 Called reconfigure on a bitmap that is in use! This may cause graphical corruption!
    private int clickPosition = -1;
    public SoundAdapter(Context context, List<ContentModel> data) {
        this.mContext = context;
        this.data = data;
        screenWidth = ScreenUtil.getScreenWidth(context);
        if(Build.VERSION.SDK_INT >= 23){
            isVersion = true;
        }
        player = new Player();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListen(), PhoneStateListener.LISTEN_CALL_STATE);
    }
    private class PhoneListen extends PhoneStateListener{//监听电话状态，来电之后暂停音乐播放

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    player.callIsComing();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    player.callIsDown();
                    break;
            }
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.sound_item_fragment, parent, false));
        } else if (viewType == TYPE_FOOT_VIEW) {
            return new FootItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.refresh_foot_view, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).mImageItem.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, (int) (screenWidth * 0.7)));
            String imageUrl = data.get(position).getImage3();
            if (isVersion){//mata8 sdk23上placeholder占位符会导致图片fitXY样式加载图片失效
                Glide.with(mContext).load(imageUrl).crossFade().error(R.drawable.error).diskCacheStrategy(DiskCacheStrategy.ALL).into(((ItemViewHolder) holder).mImageItem);
            }else{
                Glide.with(mContext).load(imageUrl).crossFade().placeholder(R.drawable.error_not_text).error(R.drawable.error).diskCacheStrategy(DiskCacheStrategy.ALL).into(((ItemViewHolder) holder).mImageItem);
            }
            ((ItemViewHolder) holder).mTextItem.setText(data.get(position).getText().trim());
            ((ItemViewHolder) holder).mPlayClickItem.setImageResource(R.drawable.sound_click_play);
//            Log.e("url", "=====================" + data.get(position).getVoice_uri());
            ((ItemViewHolder) holder).mPlayClickItem.setVisibility(View.VISIBLE);
            ((ItemViewHolder) holder).mPuaseClickItem.setVisibility(View.GONE);
            ((ItemViewHolder) holder).mReplayClickItem.setVisibility(View.GONE);
            ((ItemViewHolder) holder).mPlayClickItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//播放
                    if (data.get(position).getVoice_uri().isEmpty()) {
                        Toast.makeText(mContext, "音频出错...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    player.setView(((ItemViewHolder) holder).mPuaseClickItem);
                    if (clickPosition == -1) {//首次点击
                        clickPosition = position;
                    }
                    if (clickPosition != position) {
                        notifyDataSetChanged();
//                        player.stop();
                        isPause = false;
                        clickPosition = position;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (((ItemViewHolder) holder).mPuaseClickItem.getVisibility() != View.VISIBLE) {
                                ((ItemViewHolder) holder).mPlayClickItem.setVisibility(View.GONE);
                                soundSelectInAnim(((ItemViewHolder) holder).mPuaseClickItem, ((ItemViewHolder) holder).mReplayClickItem);
                            }
                        }
                    }, 150);
                    if (isPause) {
                        player.play();
                        isPause = false;
                    } else {
                        player.playUrl(data.get(position).getVoice_uri(), 0);
                    }
                }
            });
            ((ItemViewHolder) holder).mPuaseClickItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//暂停
                        player.pause();
                        isPause = true;
                        soundSelectOutAnim(((ItemViewHolder) holder).mPuaseClickItem, ((ItemViewHolder) holder).mReplayClickItem);
                        ((ItemViewHolder) holder).mPlayClickItem.setVisibility(View.VISIBLE);
                }
            });
            ((ItemViewHolder) holder).mReplayClickItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//重播
                    player.replay(data.get(position).getVoice_uri());
                }
            });
            ((ItemViewHolder) holder).mTextItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//放高文本描述
                    if(isSingleLine){
                        ((ItemViewHolder) holder).mTextItem.setSingleLine(true);
                        isSingleLine = false;
                    }else{
                        ((ItemViewHolder) holder).mTextItem.setSingleLine(false);
                        isSingleLine = true;
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT_VIEW;
        } else {
            return TYPE_ITEM;
        }
    }
    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageItem, mPlayClickItem, mReplayClickItem;
        CusotmProgressView mPuaseClickItem;
        TextView mTextItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageItem = (ImageView) itemView.findViewById(R.id.iv_sound_image_item);
            mPlayClickItem = (ImageView) itemView.findViewById(R.id.iv_play_sound_item);
            mPuaseClickItem = (CusotmProgressView) itemView.findViewById(R.id.iv_pause_sound_item);
            mReplayClickItem = (ImageView) itemView.findViewById(R.id.iv_replay_sound_item);
            mTextItem = (TextView) itemView.findViewById(R.id.tv_sound_text_item);
        }
    }

    private static class FootItemViewHolder extends RecyclerView.ViewHolder {

        public FootItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void soundSelectInAnim(View viewLeft, View viewRight){
        int duration = 500;
        viewLeft.setVisibility(View.VISIBLE);
        viewRight.setVisibility(View.VISIBLE);
        ObjectAnimator transAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "translationX",0,-114-30);//计算了图片的宽度，在不同密度的屏幕上可能有差别，不过不大
        ObjectAnimator alphaAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "alpha", 0.2f, 1);
        ObjectAnimator scalexAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "scaleX", 0.2f, 1);
        ObjectAnimator scaleyAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "scaleY", 0.2f, 1);
        ObjectAnimator transAnimaRight = ObjectAnimator.ofFloat(viewRight, "translationX",0,114+30);
        ObjectAnimator alphaAnimaRight = ObjectAnimator.ofFloat(viewRight, "alpha", 0.2f, 1);
        ObjectAnimator scalexAnimaRight = ObjectAnimator.ofFloat(viewRight, "scaleX", 0.2f, 1);
        ObjectAnimator scaleyAnimaRight = ObjectAnimator.ofFloat(viewRight, "scaleY", 0.2f, 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(transAnimaLeft, alphaAnimaLeft, scalexAnimaLeft, scaleyAnimaLeft, transAnimaRight, alphaAnimaRight, scalexAnimaRight, scaleyAnimaRight);
        set.setDuration(duration);
        set.start();
    }
    public void soundSelectOutAnim(final ImageView viewLeft, final ImageView viewRight){
        int duration = 500;
        ObjectAnimator transAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "translationX",-viewLeft.getWidth()-30,0);
        ObjectAnimator alphaAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "alpha", 1, 0.2f);
        ObjectAnimator scalexAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "scaleX", 1, 0.2f);
        ObjectAnimator scaleyAnimaLeft = ObjectAnimator.ofFloat(viewLeft, "scaleY", 1, 0.2f);
        ObjectAnimator transAnimaRight = ObjectAnimator.ofFloat(viewRight, "translationX", viewLeft.getWidth()+30, 0);
        ObjectAnimator alphaAnimaRight = ObjectAnimator.ofFloat(viewRight, "alpha", 1, 0.2f);
        ObjectAnimator scalexAnimaRight = ObjectAnimator.ofFloat(viewRight, "scaleX", 1, 0.2f);
        ObjectAnimator scaleyAnimaRight = ObjectAnimator.ofFloat(viewRight, "scaleY", 1, 0.2f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(transAnimaLeft, alphaAnimaLeft, scalexAnimaLeft, scaleyAnimaLeft, transAnimaRight, alphaAnimaRight, scalexAnimaRight, scaleyAnimaRight);
        set.setDuration(duration);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewLeft.setVisibility(View.GONE);
                viewRight.setVisibility(View.GONE);
            }
        });
    }
    public void destroyPlayer(){
        player.stop();
    }

//    public interface UrlDataModel{
//        public String buildUrl(int width, int height);
//    }
//    public class UrlLoader extends BaseGlideUrlLoader<UrlDataModel>{
//
//        public UrlLoader(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected String getUrl(UrlDataModel model, int width, int height) {
//            return model.buildUrl(width,height);
//        }
//    }
}
