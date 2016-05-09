package com.sen.vov.activity.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.sen.vov.R;
import com.sen.vov.adapter.SoundAdapter;
import com.sen.vov.models.ContentModel;
import com.sen.vov.models.SystemModel;
import com.sen.vov.request.CustomRequest;
import com.sen.vov.request.VolleyErrorListener;
import com.sen.vov.request.VolleyHelper;
import com.sen.vov.utils.NetworkUtil;
import com.sen.vov.utils.RequestUtil;
import com.sen.vov.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 声音主fragment
 * Created by Sen on 2016/4/16.
 */
public class SoundFragment extends Fragment{
    private static final String TAG = "SoundFragment";
    private SwipeRefreshLayout mRefreshView;
    private RecyclerView mRecyclerView;
    private SoundAdapter mAdapter;
    private List<ContentModel> contentList = new ArrayList<>();
    private boolean isLoading = true;//是否需要加载下一页动画
    private boolean isInit = false;//使初始化数据动画只在初始化时出现
    private int page = 1;
    private WifiBroadcastReceiver receiver;
    private TextView mSuggest;
//    private String shareSoundPageKey = "soundPage";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        isInit = true;
        receiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();//代码注册广播。
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_sound_fragment);
        mRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh_sound_fragment);
        mSuggest = (TextView) view.findViewById(R.id.tv_suggest_sound_fragment);
        initView();
        return view;
    }
    private void initView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SoundAdapter(getContext(),contentList);
//        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //newState三种状态 0=手指离开屏幕，1 = 手指触摸屏幕, 2 = 手指加速滑动并放开，此时滑动状态伴随0
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition+1 == mAdapter.getItemCount()){
                    if(mRefreshView.isRefreshing()){
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                    if (!isLoading) {
                        mRefreshView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = true;
                                getData();
                            }
                        }, 300);//加载数据的动画显示
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRefreshView.setColorSchemeResources(R.color.colorAccent);
        if(isInit){
            mRefreshView.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshView.setRefreshing(true);
                }
            });
        }
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//下拉刷新
            @Override
            public void onRefresh() {
                contentList.clear();
                getData();
            }
        });
    }
    public class WifiBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){//检查wifi是否连接到有效路由器,不检查wifi是否开关
                //在WIFI开启状态下会进入此监听,但是打开wifi不代表有有效wifi
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(parcelableExtra != null){
                    NetworkInfo.State state = ((NetworkInfo) parcelableExtra).getState();
                    if(state == NetworkInfo.State.CONNECTED){
                        mSuggest.setVisibility(View.GONE);
                    }else{
                        mSuggest.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void getData() {
//        Log.e("sound_page",""+page);
        Map<String, String> params = new HashMap<>();
        params.put("type", "31");//查询的类型，10图片 29段子 31声音
        params.put("page", String.valueOf(++page));//页数。每页最多返回20条记录
        CustomRequest request = new CustomRequest.RequestBuilder().post().url(RequestUtil.sisterUrl).params(RequestUtil.getRequestParams(params)).clazz(SystemModel.class)
                .successListener(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if(RequestUtil.getModelDataState(getContext(),response)){
                            SystemModel systemModel = (SystemModel) response;
                            contentList.addAll(systemModel.getShowapi_res_body().getPagebean().getContentlist());
                            mAdapter.notifyDataSetChanged();
                            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                            isLoading = false;
//                            Log.e("contentlist", "" + contentList.size());
                        }
                        mRefreshView.setRefreshing(false);//设置为false代表数据刷新成功
                    }
                }).errorListener(new VolleyErrorListener(getActivity())).build();
        VolleyHelper.getInstance(getContext()).addToRequestQueue(request, TAG);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getInstance(getContext()).cancleAllRequest(TAG);
        mAdapter.destroyPlayer();
        if(receiver != null){
            getActivity().unregisterReceiver(receiver);
        }
    }
}
