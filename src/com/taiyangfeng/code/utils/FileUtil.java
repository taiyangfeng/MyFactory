package com.taiyangfeng.code.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
/**
 * 文件管理工具类
 * <p>
 * 必须先初始化{@link com.aten.dwt.utils.SharedPrefUtils#onCreate(Context)}
 * 初始化 {@link #onCreate(Context)}
 * </p>
 */
public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	private Context context;
	
	private FileUtil() {
	}

	enum Instance {
		SINGLETON;
		FileUtil instance = new FileUtil();
	}
	
	public static void onCreate(Context context) {
		if (Instance.SINGLETON.instance.context == null) {
			Instance.SINGLETON.instance.context = context.getApplicationContext();
		}
	}
	
	public static FileUtil get() {
		return Instance.SINGLETON.instance;
	}
	
	/**
	 * 获取app在sd卡跟目录下的路径
	 * @return
	 */
	public String getAppRootPath() {
		String storagePath = getStoragePath()+"TEST";//TODO改为相应的名字
		return storagePath;
	}
	
	public String getStoragePath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return path;
	}
	
	/**
	 * 获取缓存图片路径
	 * @return
	 */
	public String getImgCachePath(){
		return getPath(SDPath.Image_Cache.path);
	}
	
	/**
	 * 获取下载路径
	 * @return
	 */
	public String getDownloadPath(){
		return getPath(SDPath.Download.path);
	}
	
	/**
	 * 获取视频路径
	 * @return
	 */
	public String getVideoPath(){
		return getPath(SDPath.Video.path);
	}
	
	/**
	 * 获取日志路径
	 * @return
	 */
	public String getLogPath(){
		return getPath(SDPath.Log.path);
	}
	
	/**
	 * 获取网络配置信息路径
	 * @return
	 */
	public String getNetConfigPath(){
		return getPath(SDPath.NetConfig.path);
	}
	
	private String getPath(String dirName){
		String path;
		String rootPath = getAppRootPath();
		if(rootPath.endsWith("/")){
			path = rootPath+dirName;
		}else{
			path = rootPath+"/"+dirName;
		}
		return path;
	}
	
	/**
	 * 创建文件夹
	 * @param path
	 */
	public File createDir(String path) {
		if (TextUtils.isEmpty(path)) {
			Log.e(TAG, "createDir FAIL, path can not Null");
			return null;
		}
		File f = new File(path);
		try {
			// 获得文件对象
			if (!f.exists() && !f.isDirectory()) {
				// 如果路径不存在,则创建
				f.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
	
	/**
	 * 创建文件
	 * @param fileAbsoluteName 文件全名
	 * @return
	 */
	public File createFile(String fileAbsoluteName) {
		if (TextUtils.isEmpty(fileAbsoluteName)) {
			Log.e(TAG, "createDirectory FAIL, path can not Null");
			return null;
		}
		File file = null;
		try {
			file = new File(fileAbsoluteName);
			File path = new File(file.getParent());
			if (!path.exists()) {
				createDir(path.toString());
			}
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     */
    public void  deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return ;
        }
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath().toString());
            } //删除子目录
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        //删除当前目录
        dirFile.delete();
        dirFile = null;
    }
    
    public int deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }
    
    public void write(String path, byte[] b){
    	FileOutputStream fos = null;
    	File filePath = createFile(path);
    	try {
			fos = new FileOutputStream(filePath);
			fos.write(b);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos!=null){
				try {
					fos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos=null;
			}
		}
    }
	
	
	/**文件存储目录*/
	enum SDPath{
		/**文件缓存目录*/
		Image_Cache("cache/image"),
		/**插件目录*/
		NetConfig("netConfig"),
		/**下载目录*/
		Download("download"),
		/**视频目录*/
		Video("video"),
		/**日志文件目录*/
		Log("log");
		
		String path;
		private SDPath(String path) {
			this.path = path;
		}
	}
}
