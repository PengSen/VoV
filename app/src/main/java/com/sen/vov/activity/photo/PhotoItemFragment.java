package com.sen.vov.activity.photo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sen.vov.R;
import com.sen.vov.utils.ScreenUtil;

/**
 * Created by Sen on 2016/4/12.
 */
public class PhotoItemFragment extends Fragment{
    private static final String IMAGE_DATA_EXTRA = "ImageUrl";
    private static final String IMAGE_DATA_TEXT = "ImageText";
    private String ImageUrl;
    private String ImageText;
    private ImageView mImageView;
    private TextView mTextView;

    public static PhotoItemFragment newInstance(String ImageUrl,String ImageText) {
        final PhotoItemFragment f = new PhotoItemFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, ImageUrl);
        args.putString(IMAGE_DATA_TEXT, ImageText);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        ImageText = getArguments() != null ? getArguments().getString(IMAGE_DATA_TEXT) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.photo_item_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.iv_photo_fragment);
        mTextView = (TextView) v.findViewById(R.id.tv_joke_text_item);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextView.setText(ImageText);
//        Log.e("Url", "" + ImageUrl);
        mImageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.getScreenWidth(getContext()), ViewGroup.LayoutParams.MATCH_PARENT));
       Glide.with(getContext()).load(ImageUrl).placeholder(R.drawable.error_not_text).error(R.drawable.error).crossFade().into(mImageView);
//       Glide.with(getContext()).load(ImageUrl).transform(new PhotoTransform(getContext(), ImageUrl, mImageView)).placeholder(R.drawable.error_not_text).error(R.drawable.error).crossFade().into(mImageView);
    }
}
