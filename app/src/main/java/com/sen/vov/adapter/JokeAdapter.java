package com.sen.vov.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sen.vov.R;
import com.sen.vov.models.ContentModel;
import com.sen.vov.utils.RequestUtil;
import com.sen.vov.utils.ScreenUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sen on 2016/3/28.
 */
public class JokeAdapter extends Adapter<ViewHolder> {
    private int TYPE_ITEM = 0;
    private int TYPE_FOOT_VIEW = 1;
//    private List<ContentModel> data;
    private List<String> textData = new ArrayList<>();
//    private Context mContext;
    private OnItemClickListener onItemClickListener;
    public JokeAdapter(List<String> textData) {
//        this.data = data;
        this.textData = textData;
//        this.mContext = context;
    }
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public void set0nItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建新View，被LayoutManager所调用
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joke_item_fragment, parent, false);
            return new ItemViewHolder(view);
        }else if(viewType == TYPE_FOOT_VIEW){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refresh_foot_view, parent, false);
            return new FootItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){
                ((ItemViewHolder) holder).mJokeTxetItem.setText(textData.get(position));

            if(onItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        //获取数据的数量
            return textData.size() == 0 ? 0 : textData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position+1 == getItemCount()){
            return TYPE_FOOT_VIEW;
        }else{
            return TYPE_ITEM;
        }
    }
//    public void addItem(ContentModel model, int position){
//        data.add(position, model);
//        notifyItemInserted(position);
//    }
    public void addItem(int position, String str){
        textData.add(position, str);
        notifyItemInserted(position);
    }
    private static class ItemViewHolder extends ViewHolder {
        private TextView mJokeTxetItem;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mJokeTxetItem = (TextView) itemView.findViewById(R.id.tv_joke_text_item);
        }
    }
    private static class FootItemViewHolder extends ViewHolder{

        public FootItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
