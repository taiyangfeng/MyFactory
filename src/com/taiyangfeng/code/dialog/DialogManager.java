package com.taiyangfeng.code.dialog;

import java.util.List;
import java.util.WeakHashMap;

import com.taiyangfeng.code.utils.ViewUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * Dialog管理类
 * @author zhengxh
 * @version 1.0, 2015年8月31日 下午3:54:39
 */
public class DialogManager {
	private static final String TAG = DialogManager.class.getSimpleName();
	private static DialogManager dialogManager;
	
	private WeakHashMap<String, Dialog> dialogCache = new WeakHashMap<String, Dialog>();
	
	public static DialogManager getInstance(){
		if(dialogManager==null){
			dialogManager = new DialogManager();
		}
		return dialogManager;
	}
	
	private DialogManager() {
	}
	
	/**
	 * 创建BaseDialog
	 */
	public BaseDialog createDialog(Context context){
		BaseDialog dialog = new BaseDialog(context){
			@Override
			public void dismiss() {
				dialogCache.remove(getDialogFlag());
				super.dismiss();
			}
		};
		dialogCache.put(dialog.getDialogFlag(), dialog);
		return dialog;
	}
	/**
	 * 创建一个空的dialog
	 */
	public EmptyDialog createEmptyDialog(Context context){
		EmptyDialog dialog = new EmptyDialog(context){
			@Override
			public void dismiss() {
				dialogCache.remove(getDialogFlag());
				super.dismiss();
			}
		};
		dialogCache.put(dialog.getDialogFlag(), dialog);
		return dialog;
	}
	
	/**
	 * dialog是否正在显示
	 * @param dialogFlag
	 * @return
	 */
	public boolean isShowing(String dialogFlag){
		if(dialogCache.size()>0){
			if (!TextUtils.isEmpty(dialogFlag) && dialogCache.containsKey(dialogFlag)) {
				Dialog dialog = dialogCache.get(dialogFlag);
				if (dialog != null) {
					return dialog.isShowing();
				}
			}
		}
		return false;
	}
	
	/**
	 * 显示已有的dialog
	 * @param dialogFlag
	 * @return
	 */
	public void show(String dialogFlag){
		if(dialogCache.size()>0){
			if (!TextUtils.isEmpty(dialogFlag) && dialogCache.containsKey(dialogFlag)) {
				Dialog dialog = dialogCache.get(dialogFlag);
				if (dialog != null && !dialog.isShowing()) {
					Context context = dialog.getContext();
					if (context!=null && context instanceof Activity) {
						dialog.show();
					}
				}
			}
		}
	}
	
	/**
	 * 手动关闭Dialog
	 * @param dialogFlag
	 */
	public void closeDialog(String dialogFlag){
		if(dialogCache.size()>0){
			if (!TextUtils.isEmpty(dialogFlag) && dialogCache.containsKey(dialogFlag)) {
				Dialog dialog = dialogCache.get(dialogFlag);
				if (dialog != null && dialog.isShowing()) {
					boolean close = dialog.getContext() != null;
					if (close && dialog.getContext() instanceof Activity) {
						close = !((Activity) dialog.getContext()).isFinishing();
					}
					if (close) {
						dialog.dismiss();
					}
				}
				dialogCache.remove(dialogFlag);
			}
		}
	}

	/**
	 * 创建回显String列表dialog
	 * @param context
	 * @param title 标题
	 * @param strList
	 * @param strListener 列表监听, 根据list顺序(0开始)与View.getId()的int值相对应
	 * @return dialogFlag
	 */
	public String showStringListDialog(Context context, String title, List<SelectTemp> selList, final DialogClickListener strListener){
		return showStringListDialog(context, title, null, selList, null, null, strListener);
	}
	/**
	 * 创建普通String list列表dialog
	 * @param context
	 * @param title
	 * @param strList
	 * @param selStr 选中的String
	 * @param strListener 列表监听, 根据list顺序(0开始)与View.getId()的int值相对应
	 * @return
	 */
	public String showStrListDialog(Context context, String title, List<String> strList, String selStr, DialogClickListener strListener){
		return showStringListDialog(context, title, strList, null, null, selStr, strListener);
	}
	
	public String showStrListDialog(Context context, String title, List<String> strList, DialogClickListener strListener){
		return showStringListDialog(context, title, strList, null, null, null, strListener);
	}
	
	/**
	 * 创建普通String list列表dialog
	 * @param context
	 * @param title
	 * @param strList
	 * @param selStr 选中的String
	 * @param strListener 列表监听, 根据list顺序(0开始)与View.getId()的int值相对应
	 * @return
	 */
	public String showStrListDialog(Context context, String title, String[] strs, String selStr, DialogClickListener strListener){
		return showStringListDialog(context, title, null, null, strs, selStr, strListener);
	}
	
	/**
	 * List弹框
	 * List\String\, String[]与List\SelectTemp\只能存在一个
	 * @param selStr 选中的String
	 */
	private String showStringListDialog(Context context, String title, List<String> strList,  List<SelectTemp> selList, String[] strs, String selStr, final DialogClickListener strListener){
		boolean isStrList = false;
		boolean isSelList = false;
		if(strList!=null){
			isStrList = true;
		}else if(selList!=null){
			isSelList = true;
		}else if(strs==null){
			return null;
		}
		final BaseDialog dialog = createDialog(context);
		dialog.setBtnLayout(View.GONE);
		dialog.setTitle(title);
		
		int listSize = 0;
		if(isStrList){
			listSize = strList.size();
		}else if(isSelList){
			listSize = selList.size();
		}else{
			listSize = strs.length;
		}
		if(listSize>0){
			ScrollView scrollView = new ScrollView(context);
	    	scrollView.setVerticalScrollBarEnabled(false);
			LinearLayout layout = new LinearLayout(context);
			scrollView.addView(layout);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setBackgroundColor(Color.WHITE);
			int textPaddingLeft = ViewUtil.dip2px(context, 24);
			int textPadding = ViewUtil.dip2px(context, 16);
			int textColor = Color.parseColor("#333333");
			int lineColor = Color.parseColor("#e0e0e0");
			int orangeColor = Color.parseColor("#FF6100");//橙色
			for(int i=0; i<listSize; i++){
				TextView text = new TextView(context);
				String name = "";
				boolean isSelect = false;
				if(isStrList){
					name = strList.get(i);
					if(name.equals(selStr)){
						isSelect = true;
					}
				}else if(isSelList){
					SelectTemp temp = selList.get(i);
					name = temp.name;
					isSelect = temp.isSelect;
				}else{
					if(name.equals(selStr)){
						isSelect = true;
					}
					name = strs[i];
				}
				text.setText(name);
				if(isSelect){
					text.setTextColor(orangeColor);
				}else{
					text.setTextColor(Color.BLACK);
				}
				text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				text.setPadding(textPaddingLeft, textPadding, textPadding, textPadding);
				layout.addView(text, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				text.setId(i);
				text.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(strListener!=null&&strListener.onClick(v)){
							dialog.dismiss();
						}
					}
				});
				
				if(i<listSize){
					View line = new View(context);
					line.setBackgroundColor(lineColor);
					layout.addView(line, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				}
			}
			dialog.setContent(scrollView);
			show(context, dialog);
		}
		return dialog.getDialogFlag();
	}
	
	/**
	 * 显示提示框
	 * @param title
	 * @param msg 
	 * @param okString 为null时不显示确认按钮
	 * @param okListener 
	 * @param cancelString 为null时不显示取消按钮
	 * @param cancelListener
	 * @return dialogFlag
	 */
	public String showHintDialog(Context context, CharSequence title, CharSequence msg, String okString, DialogClickListener okListener, CharSequence cancelString, DialogClickListener cancelListener){
		BaseDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setOkBtnText(okString, okListener);
		dialog.setCancleBtnText(cancelString, cancelListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	/**
	 * 显示提示框
	 * @param context
	 * @param title
	 * @param msg
	 * @param msgGravity msg显示位置, 默认靠左. eg.{@link Gravity#CENTER} .. 
	 * @param okString 当null时不显示该按钮
	 * @param okListener 当null时默认触发取消
	 * @param cancelString 当null时不显示该按钮
	 * @param cancelListener 当null时默认触发取消
	 * @return dialogFlag
	 */
	public String showHintDialog(Context context, CharSequence title, CharSequence msg, int msgGravity, String okString, DialogClickListener okListener, CharSequence cancelString, DialogClickListener cancelListener){
		BaseDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setMsgGravity(msgGravity);
		dialog.setOkBtnText(okString, okListener);
		dialog.setCancleBtnText(cancelString, cancelListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	
	/**
	 * 设置自定义视图Dialog
	 * {@link #showViewDialog(Context, String, View, String, DialogClickListener, String, DialogClickListener, OnKeyListener, boolean)}
	 * @return dialogFlag
	 */
	public String showViewDialog(Context context, String title, View contentView, String okString, final DialogClickListener okListener, String cancelString, final DialogClickListener cancelListener){
		BaseDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setContent(contentView);
		dialog.setOkBtnText(okString, okListener);
		dialog.setCancleBtnText(cancelString, cancelListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	
	/**
	 * 
	 * 设置自定义视图Dialog
	 * @param context
	 * @param title 标题 当传null时不显示标题栏
	 * @param contentView 自定义内容View
	 * @param okString 为null时不显示确认按钮
	 * @param okListener 当null时默认触发取消
	 * @param cancelString 为null时不显示取消按钮
	 * @param cancelListener 当null时默认触发取消
	 * @param keyListener 按键监听
	 * @param cancelable 是否点击弹框外的屏幕可取消弹框
	 * @return dialogFlag 弹框唯一标志
	 */
	public String showViewDialog(Context context, String title, View contentView, String okString, final DialogClickListener okListener, String cancelString, final DialogClickListener cancelListener, OnKeyListener keyListener, boolean cancelable){
		BaseDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setContent(contentView);
		dialog.setCancelable(cancelable);
		dialog.setOkBtnText(okString, okListener);
		dialog.setCancleBtnText(cancelString, cancelListener);
		dialog.setOnKeyListener(keyListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	
	/**
	 * 提示框
	 * @param context
	 * @param titleResId 标题前面的修饰图片资源id
	 * @param title
	 * @param msg
	 * @param okString
	 * @param okListener
	 * @param cancelString
	 * @param cancelListener
	 * @param cancelable
	 * @return dialogFlag
	 */
	public String showHintDialog(Context context, int titleResId, String title, CharSequence msg, String okString, DialogClickListener okListener, String cancelString, DialogClickListener cancelListener, OnKeyListener keyListener, boolean cancelable){
		BaseDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setTitleIcon(titleResId);
		dialog.setCancelable(cancelable);
		dialog.setMessage(msg);
		dialog.setOkBtnText(okString, okListener);
		dialog.setCancleBtnText(cancelString, cancelListener);
		dialog.setOnKeyListener(keyListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	
	/**
	 * 设置一个空dialog
	 * @param context
	 * @param rootView
	 * @param keyListener
	 * @param cancelable
	 * @return
	 */
	public String showEmptyDialog(Context context, View rootView, OnKeyListener keyListener, boolean cancelable){
		EmptyDialog dialog = createEmptyDialog(context);
		dialog.setRootView(rootView);
		dialog.setCancelable(cancelable);
		dialog.setOnKeyListener(keyListener);
		show(context, dialog);
		return dialog.getDialogFlag();
	}
	
	private void show(Context context, Dialog dialog){
		if (null != context && context instanceof Activity) {
			Activity currentActivity = (Activity) context;
			if (null != currentActivity && !currentActivity.isFinishing() && !currentActivity.isRestricted()) {
				dialog.show();
			}
		} else {
			Log.d(TAG, "currentActivity has finished when show dialog in DialogManager...");
		}
	}
	
	/**
	 * 简单选项实体
	 */
	public static class SelectTemp{
		public String name;
		public boolean isSelect;
		public String id;
		public SelectTemp(String name, boolean isSelect) {
			this.name = name;
			this.isSelect = isSelect;
		}
		
		public SelectTemp(String id, String name) {
			this.name = name;
			this.id = id;
		}
	}
}
