package com.running.mobilemenu.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.TrafficStats;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @className ServiceHttpPost
 * @author xiaoma
 * @Description 与服务器数据交互数据格式JSON数据，通过POST流的形式，定义泛型接收方法解析的JSON数据直接以实体对象关联。
 *              JSON用的是阿里fastjson工具
 * @date 2013-1-31 下午3:22:20
 */
public class ServiceHttpPost extends BaseActivity {

	private static final String TAG = "ServiceHttpPost";
	// public static final String SERVICEURL =
	// "http://192.168.1.7:8080/InfoPublish/service.do?method=httpPost";
	public static final String SERVICEURL = MyApplication.httpUrl
			+ "toFood.do?method=httpPost";
	public static final int CONNECTION_TIMEOUT_INT = 20000;
	public static final int READ_TIMEOUT_INT = 20000;
	private static Integer rState; // 获取服务器状态
	private TrafficStats trafficStats;

	public static String httpPost(Map<String, Object> map) {
		String result = "";
		String param = JSON.toJSONString(map);
		System.out.println(param);
		if (param != null) {
			URL url = null;
			try {
				url = new URL(SERVICEURL);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setDoInput(true); // 设置输入流采用字节流
				urlConn.setDoOutput(true); // 设置输出流采用字节流
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false); // 设置缓存
				urlConn.setRequestProperty("Charset", "utf-8");
				urlConn.setConnectTimeout(CONNECTION_TIMEOUT_INT);
				urlConn.setReadTimeout(READ_TIMEOUT_INT);
				urlConn.setRequestProperty("Content-Type", "application/json");
				urlConn.setRequestProperty("Accept", "application/json");

				urlConn.connect(); // 连接既往服务端发送消息

				BufferedOutputStream dop = new BufferedOutputStream(
						urlConn.getOutputStream());
				dop.write(param.getBytes("utf-8")); // 发送参数
				dop.flush(); // 发送，清空缓存
				dop.close(); // 关闭

				int status = urlConn.getResponseCode();
				if (status != 200) {
					System.out.println("result： " + status);
					result = String.valueOf(300);
					throw new IOException("Post failed with error code "
							+ status);
				} else {
					// 下面开始做接收工作
					BufferedReader bufferReader = new BufferedReader(
							new InputStreamReader(urlConn.getInputStream()));

					String readLine = null;
					while ((readLine = bufferReader.readLine()) != null) {
						result += readLine;
					}
					bufferReader.close();
					urlConn.disconnect();
					rState = 200;
				}
				System.out.println("rState： " + rState);
				System.out.println("result： " + result);
				return result;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("param is null");
		}
		return result;
	}
	public   void mag(){
	   Toast.makeText(this, "----", Toast.LENGTH_SHORT).show();
   }
	// 定义泛型方法
	public static <T> T GetMessage(Map<String, Object> map, Class<T> cls) {

		String jsonString = httpPost(map);
		Log.d(TAG, "访问 Method: " + map.get("Method"));
		T t = null;
		try {
			t = JSON.parseObject(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

	public static <T> List<T> GetMessageList(Map<String, Object> map,
			Class<T> cls) {

		String jsonString = httpPost(map);
		Log.d(TAG, "访问 Method: " + map.get("method"));
		List<T> t = new ArrayList<T>();
		try {
			t = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return t;
	}

	public static <T> List<Map<String, Object>> GetMessageMaps(
			Map<String, Object> map) {

		String jsonString = httpPost(map);
		Log.d(TAG, "访问 Method: " + map.get("method"));
		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();

		try {
			t = JSON.parseObject(jsonString,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

	public static <T> List<T> GetMessageMaps(Map<String, Object> map,
			Class<T> cls) {

		String jsonString = httpPost(map);
		Log.d(TAG, "访问 Method: " + map.get("method"));
		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
		List<T> list = new ArrayList<T>();
		try {
			t = JSON.parseObject(jsonString,
					new TypeReference<List<Map<String, Object>>>() {
					});
			if (null != t) {
				list = BeanUtil.toBeanList(cls, t);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	public static <T> List<T> GetMessageMaps(String jsonString, Class<T> cls) {

		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
		List<T> list = new ArrayList<T>();
		try {
			t = JSON.parseObject(jsonString,
					new TypeReference<List<Map<String, Object>>>() {
					});
			if (null != t) {
				list = BeanUtil.toBeanList(cls, t);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	public static <T> Map<String, Object> getFoods(Map<String, Object> map,
			Class<T> cls) {
		String jsonString = httpPost(map);
		Map maps = new HashMap<String, Object>();
		List<T> t = new ArrayList<T>();
		try {
			t = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		maps.put("ServerState", rState);
		maps.put("Results", t);
		return maps;
	}

	public static String GetMessageString(Map<String, Object> map) {
		String jsonString = httpPost(map);

		return jsonString;
	}

	public static Map<String, Object> GetMessageMap(Map<String, Object> map) {
		String jsonString = httpPost(map);
		Map maps = new HashMap<String, Object>();
		try {
			if (!"".equals(jsonString))
				maps = JSON.parseObject(jsonString);
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return maps;
	}

	public static Map<String, Object> GetMessageMap(String jsonString) {
		Map maps = new HashMap<String, Object>();
		try {
			if (!"".equals(jsonString))
				maps = JSON.parseObject(jsonString);
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return maps;
	}

	public static List<Map<String, Object>> GetListMaps(String json) {

		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
		try {
			t = JSON.parseObject(json,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

	public static List<Map<String, String>> GetListMapsString(String json) {

		List<Map<String, String>> t = new ArrayList<Map<String, String>>();
		try {
			t = JSON.parseObject(json,
					new TypeReference<List<Map<String, String>>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

}
