package com.taiyangfeng.code.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
/**
 * 手机信息工具类
 * @author zhengxh
 * @version 1.0, 2015年12月23日 下午1:33:59
 */
public class DeviceUtil {
	private static final String TAG = DeviceUtil.class.getSimpleName();

	 /**
     * 获取手机的电子串号 <br/>
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称<br/>
     * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的<br/>
     * 其组成为：<br/>
     * 1. 前6位数(TAC)是”型号核准号码”，一般代表机型<br/>
     * 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地<br/>
     * 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号<br/>
     * 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用<br/>
     *
     * @return 手机电子串号
     */
	public static String getIMEI(Context context) {
		String imei = "";
		TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
		if (tm == null) {
			Log.w(TAG, "can't get device imei");
			return null;
		}
		imei = tm.getDeviceId();

		return imei;
	}

	/**
     * 获取手机的 IMSI 号<br/>
     * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)<br/>
     * IMSI共有15位，其结构如下：<br/>
     * MCC+MNC+MIN<br/>
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;<br/>
     * MNC:Mobile NetworkCode，移动网络码，共2位<br/>
     * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03<br/>
     * 合起来就是（也是Android手机中APN配置文件中的代码）：<br/>
     * 中国移动：46000 46002<br/>
     * 中国联通：46001<br/>
     * 中国电信：46003<br/>
     * 举例，一个典型的IMSI号码为460030912121001<br/>
     * @return IMSI 号
     */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = null;
		try {
			imsi = tm.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (imsi == null || imsi.length() == 0){
            imsi = "00000000000000";
        }
		return imsi;
	}

	public static String getWifiMAC(Context context) {
		String mac = null;

		WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
		if (wifiManager == null) {
			Log.w(TAG, "can't get wifi mac address");
		}
		try {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			mac = wifiInfo.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac;
	}
	
	/**
	 * 获取本地IP地址
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	/**
	 * 获取屏幕分辨率
	 * @param context
	 * @return 0:width; 1:height
	 */
	public static int[] getScreenResolution(Context context) {
		int[] resolution = new int[2];
		DisplayMetrics metrics = new DisplayMetrics();
		
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		WindowManager wm = (WindowManager) context.getSystemService("window");
		wm.getDefaultDisplay().getMetrics(metrics);
		resolution[0] = metrics.widthPixels;
		resolution[1] = metrics.heightPixels;
		return resolution;
	}

	/**
     * 判断是否有SD卡
     *
     * @return
     */
    public static boolean isHaveSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    
    /**
     * 获得手机内存的可用空间大小<br/>
     * 单位：byte
     *
     * @return 内存可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    

	/**
	 * 获取 CPU 信息<br/>
	 * 实际上就是读取 /proc/cpuinfo 文件的内容
	 *
	 * @return 包含 CPU 信息的 Map
	 */
	public static Map<String, String> getCPUInfo() {
		Map<String, String> cpuInfo = new HashMap<String, String>();
		BufferedReader reader = null;
		FileReader fstream = null;
		try {
			fstream = new FileReader("/proc/cpuinfo");
			if (fstream != null) {
				reader = new BufferedReader(fstream, 1024);
				String line;
				while ((line = reader.readLine()) != null) {
					String[] strs = line.split(":");
					if (strs.length == 2) {
						cpuInfo.put(strs[0].trim(), strs[1].trim());
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader = null;
			fstream = null;
		}
		return cpuInfo;
	}

	/**
	 * 获取手机内存信息<br/>
	 * 实际上就是读取 /proc/meminfo 文件的内容<br/>
	 * 返回的 Map 中，Key 是文件中的字段名，Value 为对应的数值，单位是：KB<br/>
	 * 包含的字段及其含义：<br/>
	 * 
	 * <pre>
	 *      MemTotal：所有可用RAM大小
	 *      MemFree：LowFree与HighFree的总和，被系统留着未使用的内存
	 *      Buffers：用来给文件做缓冲大小
	 *      Cached：被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）
	 *      SwapCached：被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口
	 *      Active：在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用
	 *      Inactive：在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径
	 *      SwapTotal：交换空间的总大小
	 *      SwapFree：未被使用交换空间的大小
	 *      Dirty：等待被写回到磁盘的内存大小
	 *      Writeback：正在被写回到磁盘的内存大小
	 *      AnonPages：未映射页的内存大小
	 *      Mapped：设备和文件等映射的大小
	 *      Slab：内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗
	 *      SReclaimable：可收回Slab的大小
	 *      SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）
	 *      PageTables：管理内存分页页面的索引表的大小
	 *      NFS_Unstable：不稳定页表的大小
	 * </pre>
	 *
	 * @return 包含手机内存信息的 Map
	 */
	public static HashMap<String, Long> getMemoryInfo(Context context) {
		HashMap<String, Long> hmMeminfo = new HashMap<String, Long>();
		String meminfoPath = "/proc/meminfo";
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(meminfoPath);
			br = new BufferedReader(fr, 4096);
			String lineStr = null;
			while ((lineStr = br.readLine()) != null) {
				String[] lineItems = lineStr.split("\\s+");
				if (lineItems != null && lineItems.length == 3) {
					String itemName = lineItems[0].substring(0, lineItems[0].length() - 1);
					long itemMemory = Long.valueOf(lineItems[1]);
					hmMeminfo.put(itemName, itemMemory);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return hmMeminfo;
	}
	
	/**
	 * 获取手机号码</br>
	 * 需要添加权限 android.permission.READ_PHONE_STATE
	 * @return
	 */
	public static String getMobile(Context context){
		TelephonyManager tm= (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
}
