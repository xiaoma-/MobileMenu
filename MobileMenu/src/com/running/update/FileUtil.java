package com.running.update;

import java.io.File;
import java.io.IOException;

import com.running.mobilemenu.utils.MyApplication;

import android.os.Environment;

public class FileUtil {
	public static File updateDir = null;
	public static File updateFile = null;

	/***
	 * 
	 * 创建文件
	 * @author xiaoma
	 */
	public static void createFile(String name) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + MyApplication.downloadDir);
			updateFile = new File(updateDir + "/" + name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
