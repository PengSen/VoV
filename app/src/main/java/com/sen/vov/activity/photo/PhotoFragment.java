package com.sen.vov.activity.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.sen.vov.R;
import com.sen.vov.models.ContentModel;
import com.sen.vov.models.SystemModel;
import com.sen.vov.request.CustomRequest;
import com.sen.vov.request.VolleyErrorListener;
import com.sen.vov.request.VolleyHelper;
import com.sen.vov.utils.RequestUtil;
import com.sen.vov.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片主fragment
 * Created by Sen on 2016/4/3.
 */
public class PhotoFragment extends Fragment implements ViewPager.OnPageChangeListener{
    private static final String TAG = "PhotoFragment";
    private int page = 1;//刷新页数 999
    private List<ContentModel> contentList = new ArrayList<>();
    private ImagePagerAdapter mAdapter;
    private String sharePhotoPageKey = "photoPage";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        String sharePhotoPage = SharePreferenceUtil.getSharePage(getContext(), sharePhotoPageKey, "");
        if(sharePhotoPage.isEmpty()){
            SharePreferenceUtil.setSharePage(getContext(), sharePhotoPageKey , "999");
            page = 500;
        }else{
            page = Integer.valueOf(sharePhotoPage) > 1 ? Integer.valueOf(sharePhotoPage) : 999;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_fragment, container, false);
        ViewPager mViewPagr = (ViewPager) view.findViewById(R.id.vp_content);
        mAdapter = new ImagePagerAdapter(getActivity().getSupportFragmentManager());
        mViewPagr.setAdapter(mAdapter);
        mViewPagr.setOffscreenPageLimit(3);//缓存
        mViewPagr.addOnPageChangeListener(this);
        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == contentList.size()-2){
            getData();
            SharePreferenceUtil.setSharePage(getContext(), sharePhotoPageKey, String.valueOf(--page));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoItemFragment.newInstance(contentList.get(position).getImage0(), contentList.get(position).getText());
        }
    }

    public void getData() {
//        Log.e("page",""+page);
        Map<String, String> params = new HashMap<>();
        params.put("type", "10");//查询的类型，10图片 29段子 31声音
        params.put("page", String.valueOf(page));//页数。每页最多返回20条记录
        CustomRequest request = new CustomRequest.RequestBuilder().post().url(RequestUtil.sisterUrl).params(RequestUtil.getRequestParams(params)).clazz(SystemModel.class)
                .successListener(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if(RequestUtil.getModelDataState(getContext(),response)){
                            SystemModel systemModel = (SystemModel) response;
                            contentList.addAll(systemModel.getShowapi_res_body().getPagebean().getContentlist());
                            mAdapter.notifyDataSetChanged();
//                            Log.e("contentlist", "" + contentList.size());
                        }
//                        mRefreshSisterSwipe.setRefreshing(false);//设置为false代表数据刷新成功
                    }
                }).errorListener(new VolleyErrorListener(getActivity())).build();
        VolleyHelper.getInstance(getContext()).addToRequestQueue(request, TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getInstance(getActivity()).cancleAllRequest(TAG);
    }
}
