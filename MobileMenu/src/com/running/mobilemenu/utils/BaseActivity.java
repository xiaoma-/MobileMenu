package com.running.mobilemenu.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.running.mobilemenu.dao.impl.TrafficsDaoImpl;
import com.running.mobilemenu.model.Traffics;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @className BaseActivity
 * @author xiaoma
 * @Description Activity超类
 * @date 2013-5-29 下午2:25:09
 */
public class BaseActivity extends Activity {
	public static List<Activity> allaActivities = new ArrayList<Activity>();
	private TrafficsDaoImpl trafficsDao;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	 trafficsDao = new TrafficsDaoImpl(this);
    	super.onCreate(savedInstanceState);
//    	if(!isNetworkConnected(this)){
//			Toast.makeText(this, "网络不可用!请检查网络！", Toast.LENGTH_SHORT).show();
//			
//		}
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {   
	        @Override  
	        public void uncaughtException(Thread thread, Throwable ex) {  
	            ex.printStackTrace();  
	            // 出异常了，退出程序？ 给个提示？ 还是硬着头皮走下去？ 
	            exit();
	        }  
	    });
    }
     @Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			allaActivities.add(this);
		}

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			allaActivities.remove(this);
		}

		public void exit() {
			for (Activity activity : allaActivities) {
				activity.finish();
			}
			allaActivities.clear();
		}
		public  boolean isNetworkConnected(Context context) {  
		      if (context != null) {  
		         ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
		                  .getSystemService(Context.CONNECTIVITY_SERVICE);  
		          NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
		          if (mNetworkInfo != null) { 
		              return mNetworkInfo.isAvailable();  
		          }  
		      }  
		     return false;  
 }  
		public Traffics trafficTotal(Context context) {
			PackageManager packageManager = context.getPackageManager();
			long receive = 0;
			long send = 0;
			try {
				ApplicationInfo applicationInfo = packageManager
						.getApplicationInfo("com.running.mobilemenu",
								ApplicationInfo.FLAG_SYSTEM);
				int uid = applicationInfo.uid;
				receive = TrafficStats.getUidRxBytes(uid);
				send = TrafficStats.getUidTxBytes(uid);

			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR); // 获取当前年份
			int mMonth = c.get(Calendar.MONTH);// 获取当前月份
			int mDay = c.get(Calendar.DAY_OF_MONTH) - 1;// 获取当前月份的日期号码
			int mDay2 = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码

			// int oldreceive;
			// int oldreceive;

			String createDate = mYear + "-" + mMonth + "-" + mDay;
			String createDate2 = mYear + "-" + mMonth + "-" + mDay2;

			String sql = "select * from traffics  where createDate = ?";
			Traffics traffic = trafficsDao.query(sql, new String[] { createDate2 });
			if (null != traffic) {
				traffic.setReceiveDay(receive - traffic.getReceive());
				traffic.setSendDay(send - traffic.getSend());
				trafficsDao.update(traffic);
//				Traffics traffics = new Traffics();
//				traffics.setReceive(receive);
//				traffics.setSend(send);
//				traffics.setCreateDate(createDate2);
//				trafficsDao.insert(traffics);
			}else{
				Traffics traffics = new Traffics();
				traffics.setReceive(receive);
				traffics.setSend(send);
				traffics.setCreateDate(createDate2);
				trafficsDao.insert(traffics);
				traffic = trafficsDao.query(sql, new String[] { createDate2 });
			}
	      return traffic;
		}
		
}
