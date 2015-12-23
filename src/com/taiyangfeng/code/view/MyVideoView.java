package com.taiyangfeng.code.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

public class MyVideoView extends VideoView {
	private final String TAG = MyVideoView.class.getSimpleName();

	public MyVideoView(Context context) {
		this(context, null);
	}

	public MyVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d(TAG, "====OnCompletionListener===onCompletion=====");
				onStatus(PlayerStatus.END);
			}
		});
		
		setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.d(TAG, "====OnPreparedListener===onPrepared=====");
				play();
				onStatus(PlayerStatus.STARTED);
			}
		});
		
		setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d(TAG, "====OnErrorListener===onError=====");
				onStatus(PlayerStatus.ERROR);
				return false;
			}
		});
	}
	
	public void play(String path){
		if(TextUtils.isEmpty(path)){
			Log.d(TAG, "play video FAIL! path:"+path);
			return;
		}
		setVideoURI(Uri.parse(path));
	}

	private void play(){
		start();
	}
	
	public void onDestory(){
		stopPlayback();
	}
	
	
	private void onStatus(PlayerStatus status){
		//TODO
	}
	
	public enum PlayerStatus {
		INITIALIZED, STARTED, PLAYERING, STOPED, ERROR, END;
	}
}
