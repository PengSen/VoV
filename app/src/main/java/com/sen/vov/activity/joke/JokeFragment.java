package com.sen.vov.activity.joke;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.sen.vov.R;
import com.sen.vov.adapter.JokeAdapter;
import com.sen.vov.models.ContentModel;
import com.sen.vov.models.SystemModel;
import com.sen.vov.request.CustomRequest;
import com.sen.vov.request.VolleyErrorListener;
import com.sen.vov.request.VolleyHelper;
import com.sen.vov.utils.RequestUtil;
import com.sen.vov.utils.SharePreferenceUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笑话 主fragment
 * Created by Sen on 2016/4/14.
 */
public class JokeFragment extends Fragment {
    private static final String TAG = "JokeFragment";
    private SwipeRefreshLayout mRefreshListSwipe;
    private RecyclerView mRecyclerListView;
//    private List<ContentModel> contentList = new ArrayList<>();
    private List<String> textData = new ArrayList<>();
    private JokeAdapter mAdapter;
    private boolean isLoading = true;//上拉数据加载的控制
    private boolean isInit = false;
    private String shareJokePageKey = "jokePage";
    private int page = 1;//刷新页数  --500

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        isInit = true;
        String shareJokePage = SharePreferenceUtil.getSharePage(getContext(), shareJokePageKey, "");
        if(shareJokePage.isEmpty()){
            SharePreferenceUtil.setSharePage(getContext(), shareJokePageKey, "500");
            page = 500;
        }else{
            page = Integer.valueOf(shareJokePage) > 1 ? Integer.valueOf(shareJokePage) : 500;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.joke_fragment, container, false);
        mRefreshListSwipe = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh_sister_fragment);
        mRecyclerListView = (RecyclerView) view.findViewById(R.id.rv_list_sister_fragment);

        mRefreshListSwipe.setColorSchemeResources(R.color.colorAccent);
//        mRecyclerListView.setHasFixedSize(true);//如果高度固定，则可以提升性能
        initViewListener();
        return view;
    }

    private void initViewListener() {
        if(isInit){
            mRefreshListSwipe.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshListSwipe.setRefreshing(true);//初始化数据更新
                }
            });
            isInit = false;
        }
        mRefreshListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新
                textData.clear();
                mAdapter.notifyDataSetChanged();
                getData();
                SharePreferenceUtil.setSharePage(getContext(), shareJokePageKey, String.valueOf(--page));
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerListView.setLayoutManager(layoutManager);//初始化布局：线性、格子、瀑布流
        mRecyclerListView.setAdapter(mAdapter = new JokeAdapter(textData));
        mRecyclerListView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //newState三种状态 0=手指离开屏幕，1 = 手指触摸屏幕, 2 = 手指加速滑动并放开，此时滑动状态伴随0
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();//最后一个可见视图的位置
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    if (mRefreshListSwipe.isRefreshing()) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        mRefreshListSwipe.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = true;
                                getData();
                                SharePreferenceUtil.setSharePage(getContext(), shareJokePageKey, String.valueOf(--page));
                            }
                        }, 300);//加载数据的动画显示
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {//滑动时触发
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mAdapter.set0nItemClickListener(new JokeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getData() {
//        Log.e("page", "" + page);
        Map<String, String> params = new HashMap<>();
        params.put("type", "29");//查询的类型，10图片 29段子 31声音
        params.put("page", String.valueOf(page));//页数。每页最多返回20条记录
        CustomRequest request = new CustomRequest.RequestBuilder().post().url(RequestUtil.sisterUrl).params(RequestUtil.getRequestParams(params)).clazz(SystemModel.class)
                .successListener(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        SystemModel systemModel = (SystemModel) response;
                        if(RequestUtil.getModelDataState(getContext(),response)){
                            List<ContentModel> contentlist = systemModel.getShowapi_res_body().getPagebean().getContentlist();
                            for(int i=0;i<contentlist.size();i++){
                                String weixin_url = contentlist.get(i).getWeixin_url();
                                getTextData(weixin_url);
                            }
//                            contentList.addAll(contentlist);
//                            Log.e("contentlist", "" + contentList.size());
                        }
                        mRefreshListSwipe.setRefreshing(false);//设置为false代表数据刷新成功
                    }
                }).errorListener(new VolleyErrorListener(getActivity())).build();
        VolleyHelper.getInstance(getContext()).addToRequestQueue(request, TAG);

    }

    /**
     * API的文字数据有误，根据源url用jsoup自取段子数据
     * @param weixin_url 源url
     */
    private void getTextData(String weixin_url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, weixin_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document parse = Jsoup.parse(response);
                String s = parse.body().select("[class=ui-row-flex]").select("p").text();
                mAdapter.addItem(textData.size(), s);
                mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                isLoading = false;
            }
        },new VolleyErrorListener(getActivity()));
        VolleyHelper.getInstance(getContext()).addToRequestQueue(stringRequest,TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getInstance(getContext()).cancleAllRequest(TAG);
    }
}