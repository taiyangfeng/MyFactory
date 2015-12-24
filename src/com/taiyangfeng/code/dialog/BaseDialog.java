package com.taiyangfeng.code.dialog;

import java.util.UUID;

import com.taiyangfeng.code.R;
import com.taiyangfeng.code.utils.ViewUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseDialog extends Dialog{
	private TextView okBtn, cancelBtn;
	private TextView title;//标题
	private View titleLine;
	private LinearLayout contentLayout; //内容区域
	private LinearLayout btnLayout;//按钮区域
	private TextView message;//按钮区域
	private View btnLine;//按钮间隔线
	
	/**dialog标志*/
	private String dialogFlag;
	
	private DialogClickListener okListener, cancelListener;

	public BaseDialog(Context context) {
		super(context);
        init();
	}

	private void init() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_dialog);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
        getWindow().setAttributes(lp);
        initView();
	}

	private void initView() {
		dialogFlag = UUID.randomUUID().toString();
		
		title = (TextView) findViewById(R.id.dialog_title_tv);
		titleLine = findViewById(R.id.dialog_title_line);
		btnLayout = (LinearLayout) findViewById(R.id.dialog_btn_layout);
		message = (TextView) findViewById(R.id.dialog_message_tv);
		
		okBtn = (TextView) findViewById(R.id.dialog_ok_btn);
		cancelBtn = (TextView) findViewById(R.id.dialog_cancel_btn);
		btnLine = findViewById(R.id.dialog_btn_line);
		
        contentLayout = (LinearLayout) findViewById(R.id.dialog_content_layout);
        
        okBtn.setOnClickListener(listener);
        cancelBtn.setOnClickListener(listener);
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}
	
	/**
	 * 获取dialog的唯一标志
	 */
	public String getDialogFlag(){
		return dialogFlag;
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id==R.id.dialog_ok_btn){
				if(okListener!=null){
					boolean click = okListener.onClick(v);
					if(click){
						dismiss();
					}
				}else{
					dismiss();
				}
			}else if(id==R.id.dialog_cancel_btn){
				if(cancelListener!=null){
					boolean click = cancelListener.onClick(v);
					if(click){
						dismiss();
					}
				}else{
					dismiss();
				}
			}
		}
	};

	/**
	 * 设置btn是否显示
	 * @param typw 0:是否显示按钮区域; 1:是否显示显示确认按钮; 2:是否显示显示取消按钮
	 * @param visibility
	 */
	private void setBtnVisibility(int type, int visibility){
		switch (type) {
		case 0:
			btnLayout.setVisibility(visibility);
			okBtn.setVisibility(visibility);
			cancelBtn.setVisibility(visibility);
			break;
		case 1:
			btnLayout.setVisibility(View.VISIBLE);
			okBtn.setVisibility(visibility);
			break;
		case 2:
			btnLayout.setVisibility(View.VISIBLE);
			cancelBtn.setVisibility(visibility);
			break;
		}
		
		if(okBtn.getVisibility()!=View.VISIBLE||cancelBtn.getVisibility()!=View.VISIBLE){
			btnLine.setVisibility(View.GONE);
		}else{
			btnLine.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置标题
	 */
	public void setTitle(CharSequence dialogTitle){
		if(TextUtils.isEmpty(dialogTitle)){
			title.setVisibility(View.GONE);
			titleLine.setVisibility(View.GONE);
		}else{
			title.setVisibility(View.VISIBLE);
			titleLine.setVisibility(View.VISIBLE);
			title.setText(dialogTitle);
		}
	}
	
	/**
	 * 设置Message显示位置
	 * @param gravity
	 */
	public void setMsgGravity(int gravity){
		message.setGravity(gravity);
	}
	
	/**
	 * 设置标题前缀图标
	 * @param resId 图片资源id
	 */
	public void setTitleIcon(int resId){
		if(resId>0){
			title.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
			title.setCompoundDrawablePadding(ViewUtil.dip2px(getContext(), 8));
			title.setBackgroundColor(Color.TRANSPARENT);
			title.setTextColor(Color.parseColor("#2c66a8"));//蓝色字体
			title.setGravity(Gravity.LEFT);
		}
	}
	
	/**
	 * 设置内容
	 */
	public void setContent(View contentView){
		contentLayout.removeAllViews();
		contentLayout.addView(contentView);
	}
	
	/**
	 * 设置Message
	 */
	public void setMessage(CharSequence contentMsg){
		message.setText(contentMsg);
	}
	
	/**
	 * 设置确定按钮内容
	 * @param okListener 确认监听
	 */
	public void setOkBtnText(CharSequence btn, DialogClickListener okListener){
		this.okListener = okListener;
		if(btn==null){
			if(cancelBtn.getVisibility()==View.GONE){
				setBtnVisibility(0, View.GONE);
			}else{
				setBtnVisibility(1, View.GONE);
			}
		}else{
			okBtn.setText(btn);
		}
	}
	/**
	 * 设置取消按钮内容
	 * @param okListener 取消监听
	 */
	public void setCancleBtnText(CharSequence btn, DialogClickListener cancelListener){
		this.cancelListener = cancelListener;
		if(btn==null){
			if(okBtn.getVisibility()==View.GONE){
				setBtnVisibility(0, View.GONE);
			}else{
				setBtnVisibility(2, View.GONE);
			}
		}else{
			cancelBtn.setText(btn);
		}
	}
	
	/**
	 * 设置按钮布局是否显示
	 * @param visibility One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
	 */
	public void setBtnLayout(int visibility){
		btnLayout.setVisibility(visibility);
	}
	
}
