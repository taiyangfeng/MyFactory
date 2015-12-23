package com.taiyangfeng.code.utils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
/**
 * 文件下载工具类
 * @author zhengxh
 * @version 1.0, 2015年12月23日 下午1:35:48
 */
public class DownloadFileUtil {
	public static final int DOWNLOAD_BEGIN = 0;
	public static final int DOWNLOAD_START = 1;
	public static final int DOWNLOAD_STOP = 2;
	public static final int DOWNLOAD_END = 3;
    public static final int DOWNLOAD_ERROR = 4;
    
    private Handler handler;
    private String downloadUrl;
    private String fileName;
    private DownloadThread thread;
    
    private boolean isExit = false;
    private boolean isDownloading;
    
    private static ConcurrentHashMap<Handler, String> downloadTaskMap = new ConcurrentHashMap<Handler, String>();
    
	 /**
     * @param downloadUrl 下载路径
     * @param fileSaveDir 文件保存目录
     * @param handler 下载状态handler Message.what:DOWNLOAD_*; Message.arg1:当前已下载大小, Message.arg2:文件大小; Message.obj: 保存到本地文件的路径(可能为null)
     */
	public DownloadFileUtil(String downloadUrl, File fileSaveDir, Handler handler) {
		this.handler = handler;
		this.downloadUrl = downloadUrl;
		
		try {
			URL url = new URL(downloadUrl);
			thread = new DownloadThread(url, fileSaveDir);
			downloadTaskMap.put(handler, downloadUrl);
		} catch (MalformedURLException e) {
			sendHanlder(DOWNLOAD_ERROR, 0, 0, null);
			e.printStackTrace();
			new RuntimeException();
		}
	}
	
	private boolean isDownload(){
		if(downloadTaskMap.containsKey(handler)){
			return true;
		}
		return false;
	}
	
	/**
	 * 设置要保存的文件名
	 * @param fileName eg. xxx.apk
	 */
	public void setSaveFileName(String fileName){
		this.fileName = fileName;
	}
	
	/**
	 * 获取保存的文件名
	 * @param fileName eg. xxx.apk
	 */
	public String getSaveFileName(){
		return fileName;
	}
	
	/**
	 * 开始
     */
	public void startDown() {
		if(thread!=null&&isDownload()){
			thread.start();
		}
	}
	/**
	 * 停止下载
	 */
	public void stopDown(){
		isExit = true;
	}
	
	/**
	 * 是否正在下载
	 * @return
	 */
	public boolean isDownloading(){
		return isDownloading;
	}
	
	/**
     * 发送下载进度消息
     * @param state        下载状态
     * @param downloadSize 当前已下载大小
     * @param fileSize     资源总大小
     * @param filePath     文件下载到本地的路径
     */
    private void sendHanlder(int state, int downloadSize, int fileSize, String filePath) {
    	Iterator<Entry<Handler, String>> iterator = downloadTaskMap.entrySet().iterator();
//    	Log.i("AAA", "=====download status========="+state);
    	if(iterator.hasNext()){
    		Handler handler = iterator.next().getKey();
    		handler.removeMessages(state);
        	handler.obtainMessage(state, downloadSize, fileSize, filePath).sendToTarget();
        	if(state==DOWNLOAD_ERROR||state==DOWNLOAD_END){
        		downloadTaskMap.remove(handler);
        	}
    	}
//        if (handler != null) {
//        	handler.removeMessages(state);
//        	handler.obtainMessage(state, downloadSize, fileSize, filePath).sendToTarget();
//        }
    }
    
    /**下载线程*/
    class DownloadThread extends Thread{
    	private File saveFileDir;
    	private URL downUrl;

    	public DownloadThread(URL downUrl, File saveFileDir) {
    		this.downUrl = downUrl;
    		this.saveFileDir = saveFileDir;
    	}
    	@Override
    	public void run() {
    		Log.d(this.getClass().getSimpleName(), "==downUrl=="+downUrl);
    		int downLength=0;//下载长度
    		int fileSize = 0; //文件长度
    		try {
    			sendHanlder(DOWNLOAD_BEGIN, 0, 0, null);
    			isDownloading = true;
    			
    			DefaultHttpClient httpclient = new DefaultHttpClient();
    			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20 * 1000);
    			HttpGet httpget = new HttpGet(downloadUrl);
    			HttpResponse resp = httpclient.execute(httpget); 
    			int code = resp.getStatusLine().getStatusCode();
    			if(HttpStatus.SC_OK == code){
    				HttpEntity entity = resp.getEntity();
    				fileSize = (int) entity.getContentLength();
                    
//    			if (http.getResponseCode()==200) {
//    				fileSize = http.getContentLength();
//    				String filename = getFileName(http);
    				String filename = getFileName();
    				if(!saveFileDir.exists()){
    					saveFileDir.mkdirs();
    				}
    				File saveFile = new File(saveFileDir, filename);//构建保存文件
    				if(!saveFile.exists()){
    					saveFile.createNewFile();
    				}
    				String filePath = saveFile.getAbsolutePath();
    				byte[] buffer = new byte[1024];
    				int offset = 0;
    				RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
//    				InputStream inStream = http.getInputStream();
    				InputStream inStream = entity.getContent();
    				while (!isExit&&(offset = inStream.read(buffer, 0, 1024)) != -1) {
    					threadfile.write(buffer, 0, offset);
    					downLength += offset;
    					sendHanlder(DOWNLOAD_START, downLength, fileSize, filePath);
    				}
    				threadfile.close();
    				inStream.close();
    				if(isExit){
    					sendHanlder(DOWNLOAD_STOP, downLength, fileSize, filePath);
    				}else{
    					sendHanlder(DOWNLOAD_END, downLength, fileSize, filePath);
    				}
    				isDownloading = false;
    			}else{
    				sendHanlder(DOWNLOAD_ERROR, downLength, fileSize, null);
    				Log.e("DownloadFileUtils", "Download file FAILL! responseCode:"+code);
    				isDownloading = false;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			sendHanlder(DOWNLOAD_ERROR, downLength, fileSize, null);
    			isDownloading = false;
    		}
    	}
    	
        /** 获取文件名 */
        private String getFileName() {
        	if(!TextUtils.isEmpty(fileName)){
        		return fileName;
        	}
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
                filename = UUID.randomUUID()+ ".temp";//默认取一个文件名
            }
            fileName = filename;
            return filename;
        } 
    }
    
}
