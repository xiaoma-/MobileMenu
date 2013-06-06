package com.running.mobilemenu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.model.MyOrder;
import com.running.mobilemenu.model.Seats;

/**
 * @className MyApplication
 * @author xiaoma
 * @Description 初始化加载数据
 * @date 2013-5-29 上午9:07:43
 */
public class MyApplication extends Application {
	public static String clientId = "";// 设备标识
	public static String clientName = "";// 设备名
	public static String clientSystemName = "";// 设备系统
	public static List<Food> listFoods = new ArrayList<Food>();// 点的菜品
	public static List<FoodOrderInfo> listFoodInfo = new ArrayList<FoodOrderInfo>();// 点的菜品
	public static List<Seats> listSeats = new ArrayList<Seats>();// 选择的座位
	public static List<MyOrder> listMyOrders = new ArrayList<MyOrder>();// 订单
	public static List<Map<String, String>> listMapFoods = new ArrayList<Map<String, String>>();
	public static boolean orderFlag = false;
//	 public static String httpUrl = "http://192.168.1.6:8080/AdcMobile/";
	// public static String ImageUrl
	// ="http://192.168.1.6:8080/AdcMobile/foodImages/";
	public static String httpUrl = "http://211.142.99.22/AdcMobile/";
	public static String ImageUrl = "http://211.142.99.22/AdcMobile/foodImages/";
	public static String userName = "";
	public static Seats seats = null;
	public static Seats addSeatsFood = null;
	public static String seatsSid = "";
	public static String APP_URL;
	public static String downloadDir = "mobilemenu/apk/";// 安装目录
	public static int localVersion = 0;// 本地安装版本
	public static String localName;// 本地版本名字

	public static int serverVersion = 0;// 服务器版本
	public static boolean updateState = true;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		listFoods.clear();
		listSeats.clear();

		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		clientId = tm.getDeviceId();
		clientName = Build.MODEL;
		clientSystemName = Build.VERSION.RELEASE;
		Log.e("系统信息", clientId + "-----" + clientName + "----"
				+ clientSystemName);
		
	}

	@Override
	// 建议在您app的退出之前调;
	public void onTerminate() {
		// TODO Auto-generated method stub
		listFoods.clear();
		super.onTerminate();
	}
	
}
