package com.running.mobilemenu.init;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.running.mobilemenu.R;
import com.running.mobilemenu.dao.impl.TrafficsDaoImpl;
import com.running.mobilemenu.model.Traffics;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

public class SplashActivity extends BaseActivity {

	/*
	 * 该方法中主要是实现了进入界面与主界面的跳转功能 其中这里可以添加用户登录与数据加载操作
	 * 
	 * 闫振伟 2012-12-29
	 */

	private final int TIME_UP = 1;
	private final String SHARE_CLIENTID_TAG = "MAP_SHARE_CLIENTID_TAG";
	private Map<String, Object> map = new HashMap<String, Object>();
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == TIME_UP) {

				// 新建Intent
				Intent intent = new Intent();
				// 从本类的activity跳转到目标activity
				intent.setClass(SplashActivity.this, LoginActivity.class);
				// 开启跳转的activity
				// 注：还要在androidMainfest.xml注册一下目标的activity
				// 还要在 src/空间里新建一个目标的类 res/layout/目录下新建一个对应的布局文件xml
				startActivity(intent);
				// 这个函数有两个参数，一个参数是第一个activity退出时的动画，另外一个参数则是第二个activity进入时的动画
				// 必须紧挨着 startActivity 调用 和 finish() 方法之前
				overridePendingTransition(R.anim.splash_screen_fade,
						R.anim.splash_screen_hold);
				// 退出第一个activity
				SplashActivity.this.finish();
			}
		}
	};

	public void onCreate(Bundle paramBundle) {
	
		super.onCreate(paramBundle);
		trafficTotal(this);
		// 版本检测
		if (isNetworkConnected(this)) {
			getVersion();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// final boolean isNet = HttpUtils.isNetworkConnected(this);
		// 使用一张图的形式加载，已经更改了图片
		setContentView(R.layout.splash_screen_view);
		trafficTotal(this);
		GifView gf1 = (GifView) findViewById(R.id.loadinggif);
		gf1.setGifImageType(GifImageType.COVER);

		gf1.setGifImage(R.drawable.loadings2);

		new Thread() {
			public void run() {
				try {
					sleep(2000);
				} catch (Exception e) {

				}

				Message msg = new Message();
				msg.what = TIME_UP;
				handler.sendMessage(msg);
			}
		}.start();

	}

	

	public void getVersion() {
		Log.d("Version", "版本检测");
		try {
			PackageInfo packageInfo = getApplicationContext()
					.getPackageManager().getPackageInfo(getPackageName(), 0);

			MyApplication.localVersion = packageInfo.versionCode;
			String name = packageInfo.versionName;
		
			Log.i("Version", "本地版本号码：" + MyApplication.localVersion + ",版本名称："
					+ name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		/***
		 * 在这里写一个方法用于请求获取服务器端的serverVersion.
		 */
		new Thread() {
			public void run() {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("method", "version");

				String versionInfo = ServiceHttpPost.GetMessageString(map);
				if (!"".equals(versionInfo) && null != versionInfo
						&& !"null".equals(versionInfo)) {
					String[] strs = versionInfo.split(",");
					// serverVersion = Integer.valueOf(strs[1]);
					MyApplication.APP_URL = strs[0];
					MyApplication.serverVersion = Integer.parseInt(strs[2]);
					Log.i("MyApplication", "url=" + MyApplication.APP_URL
							+ "Version: " + MyApplication.serverVersion);
				}
			}
		}.start();

		// System.out.println("版本信息："+versionInfo);

	}

}