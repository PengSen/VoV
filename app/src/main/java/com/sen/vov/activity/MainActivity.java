package com.sen.vov.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sen.vov.R;
import com.sen.vov.activity.joke.JokeFragment;
import com.sen.vov.activity.photo.PhotoFragment;
import com.sen.vov.activity.sound.SoundFragment;
import com.sen.vov.request.VolleyHelper;
import com.sen.vov.utils.AnimationsUtil;
import com.sen.vov.utils.SharePreferenceUtil;
import com.sen.vov.widget.CustomViewPager;
import com.sen.vov.widget.PagerSlidingTabStrip;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "Main";
    private static final int JOKE_INDEX = 0;
    private static final int PHOTO_INDEX = 1;
    private static final int SOUND_INDEX = 2;
    private Context mContext;
    private FloatingActionButton mCrossFab;
    private ImageView mJokeItemFab, mPhotoItemFab, mSoundItemFab, mMeItemFab;//mVideoItemFab
    /**
     * 动画进出的判断
     **/
    private boolean isCross = true;
    private JokeFragment jokeFragment;
    private PhotoFragment photoFragment;
    private SoundFragment soundFragment;
    private PagerSlidingTabStrip mTabs;
    private CustomViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
    }
    private void initView() {
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_main);
        mPager = (CustomViewPager) findViewById(R.id.pager_main_content);
        mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPager);
        mPager.setPagingEnabled(false);
        mCrossFab = (FloatingActionButton) findViewById(R.id.fab_main);
        mJokeItemFab = (ImageView) findViewById(R.id.iv_main_fab_joke);
        mPhotoItemFab = (ImageView) findViewById(R.id.iv_main_fab_photo);
        mSoundItemFab = (ImageView) findViewById(R.id.iv_main_fab_sound);
        mMeItemFab = (ImageView) findViewById(R.id.iv_main_fab_me);
//        if(Build.VERSION.SDK_INT >= 21){
//            assert mCrossFab != null;
//            mCrossFab.setOnClickListener(this);
//
//            mJokeItemFab.setOnClickListener(this);
//            mPhotoItemFab.setOnClickListener(this);
//            mSoundItemFab.setOnClickListener(this);
//            mMeItemFab.setOnClickListener(this);
//            mTabs.setVisibility(View.GONE);
//        }else{
//            setTabsValue();
//            mCrossFab.setVisibility(View.GONE);
//            mJokeItemFab.setVisibility(View.GONE);
//            mPhotoItemFab.setVisibility(View.GONE);
//            mSoundItemFab.setVisibility(View.GONE);
//            mMeItemFab.setVisibility(View.GONE);
//        }
        assert mCrossFab != null;
        mCrossFab.setOnClickListener(this);

        mJokeItemFab.setOnClickListener(this);
        mPhotoItemFab.setOnClickListener(this);
        mSoundItemFab.setOnClickListener(this);
        mMeItemFab.setOnClickListener(this);
        setTabsValue();
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        mTabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        mTabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        mTabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        mTabs.setIndicatorColorResource(R.color.colorAccent);
        // 设置选中Tab文字的颜色
        mTabs.setSelectedTextColorResource(R.color.colorAccent);
        // 取消点击Tab时的背景色
        mTabs.setTabBackground(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getInstance(mContext).cancleAllRequest(TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                if (isCross) {
                    isCross = false;
                    AnimationsUtil.xmlFileLoadAnimator(mContext, R.animator.in_cross_anim, mCrossFab).start();
                    AnimationsUtil.inCrossShowChildAnimation(mJokeItemFab, mPhotoItemFab, mSoundItemFab, mMeItemFab);
                } else {
                    crossOutScreenAnima();
                }
                break;
            case R.id.iv_main_fab_joke:
//                Toast.makeText(MainActivity.this, "joke", Toast.LENGTH_SHORT).show();
                mPager.setCurrentItem(JOKE_INDEX);
                crossOutScreenAnima();
                break;
            case R.id.iv_main_fab_photo:
//                Toast.makeText(MainActivity.this, "photo", Toast.LENGTH_SHORT).show();
                mPager.setCurrentItem(PHOTO_INDEX);
                crossOutScreenAnima();
                break;
            case R.id.iv_main_fab_sound:
//                Toast.makeText(MainActivity.this, "sound", Toast.LENGTH_SHORT).show();
                mPager.setCurrentItem(SOUND_INDEX);
                crossOutScreenAnima();
                break;
            case R.id.iv_main_fab_me:
                Toast.makeText(MainActivity.this, "me", Toast.LENGTH_SHORT).show();
                crossOutScreenAnima();
                break;
        }
    }
    class MainPagerAdapter extends FragmentPagerAdapter{
        private final String[] titleText = new String[]{"段子","图片","声音"};
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case JOKE_INDEX:
                    if(jokeFragment == null){
                        jokeFragment = new JokeFragment();
                    }
                    return jokeFragment;
                case PHOTO_INDEX:
                    if(photoFragment == null){
                        photoFragment = new PhotoFragment();
                    }
                    return photoFragment;
                case SOUND_INDEX:
                    if(soundFragment == null){
                        soundFragment = new SoundFragment();
                    }
                    return soundFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return titleText.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleText[position];
        }
    }

    /**
     * fab的item出屏幕的动画
     */
    private void crossOutScreenAnima() {
        isCross = true;
        AnimationsUtil.xmlFileLoadAnimator(mContext, R.animator.out_cross_anim, mCrossFab).start();
        AnimationsUtil.outCrossShowChildAnimation(mJokeItemFab, mPhotoItemFab, mSoundItemFab, mMeItemFab);
    }

    private long exitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(System.currentTimeMillis() - exitTime > 2000){
                Toast.makeText(MainActivity.this, "再按一次退出App~", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                finish();
            }
        }
//        return super.onKeyDown(keyCode, event);
        return false;
    }
}
//view,centerX,centerY动画开始的中心点  startRadius动画开始半径，endRadius动画结束半径
// Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_cross_rl, screenWidth - width, screenHeight -height, width/2,screenWidth);
//                    circularReveal.setInterpolator(new AccelerateInterpolator(2f));//先慢后快，系统默认先快后慢...参数是速度

//Fragment的总布局加载
//1. 如果root为null，attachToRoot将失去作用，设置任何值都没有意义。
//        2. 如果root不为null，attachToRoot设为true，则会给加载的布局文件的指定一个父布局，即root。
//        3. 如果root不为null，attachToRoot设为false，则会将布局文件最外层的所有layout属性进行设置，当该view被添加到父view当中时，这些layout属性会自动生效。
//        4. 在不设置attachToRoot参数的情况下，如果root不为null，attachToRoot参数默认为true。