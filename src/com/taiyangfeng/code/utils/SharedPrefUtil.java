package com.taiyangfeng.code.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * 可扩展的SharedPreferences, key值必须在枚举ConfigKey中配置
 * <p>
 * 初始化 {@link #onCreate(Context)}
 * </p>
 */
public class SharedPrefUtil {
	/**
	 * 配置文件名
	 */
	public static String CONFIG_NAME = "common_share";
	
	private SharedPreferences share;
	
	static enum INSTANCE {
		SINGLETON;
		SharedPrefUtil instance = new SharedPrefUtil();
	}
	
	public static void onCreate(Context context) {
		if (INSTANCE.SINGLETON.instance.share == null) {
			INSTANCE.SINGLETON.instance.share = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		}
	}
	
	public static SharedPrefUtil get() {
		return INSTANCE.SINGLETON.instance;
	}
	
	/**
	 * 设置值
	 * @param configKey 设置配置文件的key
	 * @param configValue 设置配置文件的value, 只能是这几种类型:{@link Integer},{@link String},{@link Boolean},{@link Float}或{@link Long}
	 */
	public void setConfig(String configKey, Object configValue) {
		SharedPreferences.Editor shareEdit = share.edit();
		if (configValue instanceof Integer) {
			shareEdit.putInt(configKey, (Integer) configValue);
        } else if (configValue instanceof String) {
        	shareEdit.putString(configKey, (String) configValue);
        } else if (configValue instanceof Boolean) {
        	shareEdit.putBoolean(configKey, (Boolean) configValue);
        } else if (configValue instanceof Float) {
        	shareEdit.putFloat(configKey, (Float) configValue);
        } else if (configValue instanceof Long) {
        	shareEdit.putLong(configKey, (Long) configValue);
        }
		if (VERSION.SDK_INT < VERSION_CODES.GINGERBREAD){
			shareEdit.commit();
		} else{
			shareEdit.apply();
		}
    }

	/**
	 * 移除配置文件中对应的key
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 */
	public void removeConfig(String configKey) {
    	try {
    		share.edit().remove(configKey);
    		share.edit().commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
	/**
	 * 获取int值的配置信息
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 * @param defValue 如果未获取到返回该默认值
	 * @return
	 */
    public int getIntConfig(String configKey, int defValue) {
        return share.getInt(configKey, defValue);
    }
    /**
	 * 获取boolean值的配置信息
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 * @param defValue 如果未获取到返回该默认值
	 * @return
	 */
    public boolean getBooleanConfig(String configKey, boolean defValue) {
        return share.getBoolean(configKey, defValue);
    }
    /**
	 * 获取long值的配置信息
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 * @param defValue 如果未获取到返回该默认值
	 * @return
	 */
    public long getLongConfig(String configKey, Long defValue) {
        return share.getLong(configKey, defValue);
    }
    /**
	 * 获取Float值的配置信息
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 * @param defValue 如果未获取到返回该默认值
	 * @return
	 */
    public Float getFloatConfig(String configKey, Float defValue) {
    	return share.getFloat(configKey, defValue);
    }
    /**
	 * 获取String值的配置信息
	 * @param configKey {@link com.aten.dwt.utils.ConfigKey}
	 * @param defValue 如果未获取到返回该默认值
	 * @return
	 */
    public String getStringConfig(String configKey, String defValue) {
        return share.getString(configKey, defValue);
    }
    
    
    /**
     * 为{@link com.aten.dwt.utils.SharedPrefUtils}提供key
     * 
     * <p>
     *  需要自行添加对应的key
     * </p>
     */
    public enum ConfigKey {
    	/** App在sd卡的根目录 */
    	RootPath("root_parth"),
    	/** app下载地址 */
    	AppDownUrl("app_download_url"),
    	/** app升级包名 */
    	UpdateAppName("update_app_name");

    	/**
    	 * 获取对应的key
    	 * @return
    	 */
    	public String key;
    	private ConfigKey(String key) {
    		this.key = key;
    	}
    }
}
