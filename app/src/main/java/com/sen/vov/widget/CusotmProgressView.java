package com.sen.vov.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sen.vov.R;

public class CusotmProgressView extends ImageView{
	
	private Paint mPaint;
	private RectF f;
	private int roundColor;
	private int roundColorProgress;
	public CusotmProgressView(Context context) {
		this(context, null);
	}
	public CusotmProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public CusotmProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgess, defStyleAttr, 0);
		roundColor = a.getColor(R.styleable.CustomProgess_roundColor, 0xFF727272);
		roundColorProgress = a.getInt(R.styleable.CustomProgess_roundColorProgress, 0);
		a.recycle();
		mPaint = new Paint();
		f = new RectF();//圆弧的大小和界限
		mPaint.setColor(roundColor);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {

		f.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		// 画弧，第一个参数是RectF,第二个参数是角度的开始，第三个参数是多少度，第四个参数是true的时候画扇形，是false的时候画弧线  
		canvas.drawArc(f, -90, (int)(roundColorProgress*3.6), true, mPaint);
		super.onDraw(canvas);
		
	}
	/**
	 * 下载的进度
	 * @param progress 0-100%
	 */
	public void setProgress(int progress){
		this.roundColorProgress = progress;
		if(progress == 100){
			roundColor = Color.TRANSPARENT;
		}
		invalidate();
	}
}
