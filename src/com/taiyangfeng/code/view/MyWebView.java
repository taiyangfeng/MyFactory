package com.taiyangfeng.code.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView {
	private final String TAG = MyWebView.class.getSimpleName();
	String dialogFlag;

	public MyWebView(Context context) {
		this(context, null);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context context) {

		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true); // //设置支持JavaScript脚本
		webSettings.setAllowFileAccess(true); // 设置可以访问文件
		webSettings.setBuiltInZoomControls(true); // 设置支持缩放
		webSettings.setSupportZoom(false); // 支持缩放
		webSettings.setSavePassword(false); // 设置是否保存密码
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		webSettings.setDomStorageEnabled(true); // 使用localStorage则必须打开
		webSettings.setDatabaseEnabled(true);// 启用数据库
		webSettings.setUseWideViewPort(true);// 设置webview推荐使用的窗口
		webSettings.setLoadWithOverviewMode(true); // 设置webview加载的页面的模式，也设置为true
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 自动打开窗口
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSettings.setPluginsEnabled(true);//设置webview支持插件
		webSettings.setPluginState(WebSettings.PluginState.ON);
		webSettings.setAllowContentAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		
		webSettings.setGeolocationEnabled(true);// 启用地理定位
//		webSettings.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");// 设置定位的数据库路径
		
//		setHapticFeedbackEnabled(false);// 触摸的时候没有触感反馈。
		setScrollbarFadingEnabled(true);// 隐藏滚动条
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 去掉垂直滚动条总是显示白色底图

		setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showProcessDialog("加载中...");
				Log.d(TAG, "onPageStarted:" + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				closeDialog();
				Log.d(TAG, "onPageFinished:" + url);
				super.onPageFinished(view, url);
			}
			
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG, "=====WebViewClient===shouldOverrideUrlLoading======");
	            view.loadUrl(url);
	            return true;
	        }
		});
		
	}
	
	ProgressDialog processDialog;
	private void showProcessDialog(String msg){
		processDialog = new ProgressDialog(getContext());
		processDialog.setMessage(msg);
		processDialog.show();
	}
	private void closeDialog(){
		if(processDialog!=null&&processDialog.isShowing()){
			processDialog.dismiss();
		}
	}
	
	public void onDestory(){
		clearView();
	}
	
}
