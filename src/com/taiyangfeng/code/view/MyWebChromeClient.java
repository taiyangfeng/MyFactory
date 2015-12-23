package com.taiyangfeng.code.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient{
	private final String TAG = MyWebChromeClient.class.getSimpleName();
	private WebView mWebView;
	private View customView;
	private CustomViewCallback mCustomViewCallback;
	private boolean isScreenFull = false;
	
	public MyWebChromeClient(WebView webView) {
		mWebView = webView;
	}
	
	public boolean isFullScreen(){
		return isScreenFull;
	}
	
	@Override
	public void onHideCustomView() {
		Log.d(TAG, "=====WebChromeClient===onHideCustomView======");
		super.onHideCustomView();
		if (customView == null)//不是全屏播放状态
            return;
		
		ViewGroup rootView = (ViewGroup) customView.getParent();
		rootView.removeAllViews();
		rootView.addView(mWebView);
//		rootView.removeView(customView);
//		mWebView.setVisibility(View.VISIBLE);
		if (mCustomViewCallback != null) {
			mCustomViewCallback.onCustomViewHidden();
			mCustomViewCallback = null;
		}
		customView = null;
		isScreenFull = false;
	}
	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		Log.d(TAG, "=====WebChromeClient===onShowCustomView======"+callback);
		super.onShowCustomView(view, callback);
		if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
		ViewGroup rootView = (ViewGroup) mWebView.getParent();
//		mWebView.setVisibility(View.GONE);
		rootView.removeAllViews();
		rootView.addView(view);
        customView = view;
        mCustomViewCallback = callback;
        isScreenFull = true;
	}
	
}
