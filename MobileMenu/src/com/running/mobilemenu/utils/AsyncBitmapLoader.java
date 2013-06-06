package com.running.mobilemenu.utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

/**
 * @className AsyncBitmapLoader
 * @author xiaoma
 * @Description 异步加载图片
 * @date 2013-3-30 下午12:45:30
 */
public class AsyncBitmapLoader {
	/**
	 * 内存图片软引用缓冲
	 */
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;
    private final static String PATH_URL = Environment.getExternalStorageDirectory()+ "/mobilemenu/download/";
	public AsyncBitmapLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public  Bitmap loadBitmap(final String imageURL) {
		// 在内存缓存中，则返回Bitmap对象
		if (imageCache.containsKey(imageURL)) {
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			Bitmap bitmap = reference.get();
			if (bitmap != null) {
				return bitmap;
			}
		} else {
			/**
			 * 加上一个对本地缓存的查找
			 */
			//String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
			File cacheDir = new File(PATH_URL);
			File[] cacheFiles = cacheDir.listFiles();
			int i = 0;
			if (null != cacheFiles) {
				for (; i < cacheFiles.length; i++) {
					if (imageURL.equals(cacheFiles[i].getName())) {
						break;
					}
				}
				if (i < cacheFiles.length) {
					Bitmap bitmap= BitmapFactory.decodeFile(PATH_URL+ imageURL);
					imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));
					return bitmap;
				}
			}
		}
		return null;
	}

	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}
}
