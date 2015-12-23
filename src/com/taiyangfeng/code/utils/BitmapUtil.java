package com.taiyangfeng.code.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
/**
 * 图片压缩工具
 * @author zhengxh
 * @version 1.0, 2015年12月23日 下午1:33:13
 */
public class BitmapUtil {


	/**
	 * 动态计算opts.inSampleSize的压缩比例方法
	 * 
	 * @param options
	 * @param minSideLength
	 *            : used to specify that minimal width or height of a bitmap.
	 * @param maxNumOfPixels
	 *            : is used to specify the maximal size in pixels that is tolerable in terms of memory usage.
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 *            需要压缩的 Bitmap
	 * @param minSideLength
	 *            used to specify that minimal width or height of a bitmap. 单位-px
	 * @param maxNumOfPixels
	 *            is used to specify the maximal size in pixels that is tolerable in terms of memory usage. 单位-px
	 * @return Bitmap
	 */
	public static Bitmap comppressBitmap(Context context, Bitmap image, int minSideLength, int maxNumOfPixels) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		int size = baos.toByteArray().length / 1024;
		int maxSize = 1024; // 最大size
		if (size > maxSize) {// 如果图片大于maxSize kb,
			baos.reset();// 重置baos即清空baos
			int quality = 100 - (100 * maxSize) / size;
			Log.i("BitmapUtil", "comppressBitmap reduce bitmap.. " + quality + "%");
			image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩到imgSize大小，把压缩后的数据存放到baos中
		}

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = computeSampleSize(newOpts, minSideLength, maxNumOfPixels);
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 *            需要压缩的资源id
	 * @param minSideLength
	 *            used to specify that minimal width or height of a bitmap. 单位-px
	 * @param maxNumOfPixels
	 *            is used to specify the maximal size in pixels that is tolerable in terms of memory usage. 单位-px
	 * @return Bitmap
	 */
	public static Bitmap comppressBitmap(Context context, int resId, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
		bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param readbytes
	 *            需要压缩的资源字节
	 * @param minSideLength
	 *            used to specify that minimal width or height of a bitmap. 单位-px
	 * @param maxNumOfPixels
	 *            is used to specify the maximal size in pixels that is tolerable in terms of memory usage. 单位-px
	 * @return Bitmap
	 */
	public static Bitmap comppressBitmap(byte[] readbytes, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(readbytes, 0, readbytes.length, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
		bitmap = BitmapFactory.decodeByteArray(readbytes, 0, readbytes.length, opts);
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param imgPath
	 *            需要压缩的资源路径
	 * @param minSideLength
	 *            used to specify that minimal width or height of a bitmap. 单位-px
	 * @param maxNumOfPixels
	 *            is used to specify the maximal size in pixels that is tolerable in terms of memory usage. 单位-px
	 * @return Bitmap
	 */
	public static Bitmap comppressBitmap(String imgPath, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
		bitmap = BitmapFactory.decodeFile(imgPath, opts);
		return bitmap;
	}

	/**
	 * 保存图片到指定目录
	 * 
	 * @param bitmap
	 *            需要保存的图片
	 * @param savePath
	 *            保存地址
	 * @param maxSize
	 *            最大尺寸(kb)
	 */
	public void saveBitmap(Bitmap bitmap, String savePath, int maxSize) {
		File f = new File(savePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int size = baos.toByteArray().length / 1024;
			if (size > maxSize) {// 如果图片大于imgSize kb,
				baos.reset();// 重置baos即清空baos
				int quality = 100 - (100 * maxSize) / size;
				Log.i("BitmapUtil", "comppressBitmap reduce bitmap.. " + quality + "%");
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩到imgSize大小，把压缩后的数据存放到baos中
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩图片
	 * 
	 * @param imgSize
	 *            压缩到多大(单位kb)
	 * @param imagePath
	 *            需要压缩的图片地址
	 * @param targetPath
	 *            压缩后的图片地址
	 * @return 是否成功
	 */
	public static boolean comppressPicture(int imgSize, String imagePath, String targetPath) {
		boolean flag = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int size = baos.toByteArray().length / 1024;
		Log.i("BitmapUtil", "comppressBitmap before size:" + size);
		if (size > imgSize) {// 如果图片大于imgSize kb,
			baos.reset();// 重置baos即清空baos
			int quality = 100 - (100 * imgSize) / size;
			Log.i("BitmapUtil", "comppressBitmap reduce bitmap.. " + quality + "%");
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩到imgSize大小，把压缩后的数据存放到baos中
		}

		byte[] b = baos.toByteArray();
		Log.i("BitmapUtil", "comppressBitmap after size:" + b.length / 1024);
		ByteArrayInputStream isBm = new ByteArrayInputStream(b);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(targetPath);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			// 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
			// 将一次读取的字节数赋给byteread
			while ((byteread = isBm.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			out.flush();
			out.close();
			isBm.close();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			try {
				isBm.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
