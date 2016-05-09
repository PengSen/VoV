package com.sen.vov.activity.sound;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.sen.vov.widget.CusotmProgressView;

public class Player implements OnBufferingUpdateListener, OnCompletionListener {

	public MediaPlayer mediaPlayer; // 媒体播放器
	private CusotmProgressView view; //
	private int playPosition = 0;
	private String url;
	// 初始化播放器
	public Player() {
		super();
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
			mediaPlayer.setOnBufferingUpdateListener(this);
		} catch (Exception e) {
			Log.e("e",""+e.toString());
			e.printStackTrace();
		}
	}

	public void setView(CusotmProgressView view){
		this.view = view;
	}
	public void play() {
		mediaPlayer.start();
	}
	/**
	 * @param url url地址
	 */
	public void playUrl(String url, int playPosition) {
		try {
			mediaPlayer.reset();// 把各项参数恢复到初始状态
			this.url = url;
			if(url != null){
				mediaPlayer.setDataSource(url); // 设置数据源
			}
//			mediaPlayer.prepare(); // prepare自动播放,缓存
			mediaPlayer.prepareAsync();//异步缓冲
			mediaPlayer.setLooping(true);//循环播放
			mediaPlayer.setOnPreparedListener(new PreparedListener(playPosition));
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	private class PreparedListener implements OnPreparedListener{
		private int playPosition;
		private PreparedListener(int playPosition){
			this.playPosition = playPosition;
		}
		@Override
		public void onPrepared(MediaPlayer mp) {
			mp.start();
			if(playPosition > 0){
				mp.seekTo(playPosition);
			}
		}
	}
	// 暂停
	public void pause() {
		mediaPlayer.pause();
	}
	//来电
	public void callIsComing(){
		if(mediaPlayer.isPlaying()){
			playPosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
	}
	//通话结束
	public void callIsDown(){
		if(playPosition > 0 && url != null){
			playUrl(this.url, playPosition);
			playPosition = 0;
		}
	}
	// 停止
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	public void replay(String url){
		if(mediaPlayer.isPlaying()){
			mediaPlayer.seekTo(0);
		}else{
			playUrl(url, 0);
		}
	}
//	public boolean isPlaying(){
//		return mediaPlayer.isPlaying();
//	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.e("mediaPlayer", "onCompletion");
	}

	/**
	 * 缓冲更新
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if(view != null){
			view.setProgress(percent);
		}
		Log.e("% play", percent + " buffer");
	}

}
