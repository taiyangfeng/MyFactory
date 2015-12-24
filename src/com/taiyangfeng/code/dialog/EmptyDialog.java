package com.taiyangfeng.code.dialog;

import java.util.UUID;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class EmptyDialog extends Dialog{
	
	/**dialog标志*/
	private String dialogFlag;
	private LinearLayout view;
	

	public EmptyDialog(Context context) {
		super(context);
        init();
	}

	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new LinearLayout(getContext());
        setContentView(view);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setAttributes(lp);
        dialogFlag = UUID.randomUUID().toString();
	}
	
	/**
	 * 给空dialog设置一个view
	 */
	public void setRootView(View rootView){
		view.removeAllViews();
		view.addView(rootView,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
	}
	
	/**
	 * 获取dialog的唯一标志
	 */
	public String getDialogFlag(){
		return dialogFlag;
	}
}
