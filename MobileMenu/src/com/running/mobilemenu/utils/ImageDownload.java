package com.running.mobilemenu.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.conn.HttpHostConnectException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * @className ImageDownload
 * @author xiaoma
 * @Description 图片下载工具类
 * @date 2013-3-29 上午9:24:08
 */
public class ImageDownload {
	private static String TAG = "ImageDownload";
	private final String SDCARD_URL = Environment.getExternalStorageDirectory()
			+ "/mobilemenu/download/"; // SDCard路径 保存图片
	private Bitmap mBitmap;
	private String mFileName;
	private String mSaveMessage;

	public Bitmap setImagePath(String filePath) {
		// 以下是取得图片的两种方法 方法1：取得的是byte数组, 从byte数组生成bitmap
		try {

			byte[] data = getImage(filePath);
			if (data != null) {
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
				return mBitmap;
			} else {
				Log.e(TAG, "图片Bitmap对象为空");
			}

			// 方法2：取得的是InputStream，直接从InputStream生成bitmap
			// mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getImage(String path) {
		Log.e(TAG, "获取图片资源");
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			InputStream inStream = conn.getInputStream();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return readStream(inStream);
			}
		} catch (HttpHostConnectException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	public void saveFile(String fileName) throws IOException {

		File myCaptureFile = new File(SDCARD_URL
				+ fileName.substring(fileName.lastIndexOf("/") + 1));
		if (!myCaptureFile.exists()) {
			Log.e(TAG, "新图片");
			try {
				Bitmap bm = setImagePath(fileName);
				File dirFile = new File(SDCARD_URL);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				myCaptureFile.createNewFile();
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "图片已存在");
		}

	}
}
