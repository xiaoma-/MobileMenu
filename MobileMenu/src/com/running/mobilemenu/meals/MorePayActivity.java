package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.BeanUtil;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className MorePayActivity
 * @author xiaoma
 * @Description 并台结账
 * @date 2013-3-29 上午9:42:09
 */
public class MorePayActivity extends BaseActivity {
	private ProgressDialog progressDialog;
	private ArrayList<Seats> lists1 = new ArrayList<Seats>();
	private List<Seats> lists2 = new ArrayList<Seats>();

	private GridView gridView1;
	private GridView gridView2;
	private MoreGridAdapter adpater1;
	private MoreGridTwoAdapter adpater2;
	private Seats seats = new Seats();// 选中的台位
	private String seatsSid;
	private String seatsSid2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		seats = (Seats) this.getIntent().getSerializableExtra("Seats");
		seatsSid = this.getIntent().getStringExtra("orderSid");
		setContentView(R.layout.morepay_activity);
		gridView1 = (GridView) findViewById(R.id.more_grid1);
		gridView2 = (GridView) findViewById(R.id.more_grid2);
		httpService();
	}

	public void upViews() {
		if (null != seats) {
			seatsSid2 = seats.getSid();
		}
		for (Seats seat : lists2) {
			if (null != seats && seatsSid2.equals(seat.getSid())) {
				lists1.add(seat);
				lists2.remove(seat);
				break;
			}
			if (null != seatsSid && seatsSid.equals(seat.getSid())) {
				lists1.add(seat);
				lists2.remove(seat);
				break;
			}
		}
		// lists2.remove(seats);
		adpater1 = new MoreGridAdapter(this);
		adpater1.setList(lists1);
		gridView1.setAdapter(adpater1);
		gridView1.setOnItemClickListener(new MyClick1());

		adpater2 = new MoreGridTwoAdapter(this);
		adpater2.setList(lists2);
		gridView2.setAdapter(adpater2);
		gridView2.setOnItemClickListener(new MyClick2());
	}

	class MyClick1 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			lists2.add(lists1.get(arg2));
			adpater2.setList(lists2);
			adpater2.notifyDataSetChanged();
			lists1.remove(arg2);
			adpater1.setList(lists1);
			adpater1.notifyDataSetChanged();
		}

	}

	class MyClick2 implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			lists1.add(lists2.get(arg2));
			adpater1.setList(lists1);
			adpater1.notifyDataSetChanged();
			lists2.remove(arg2);
			adpater2.setList(lists2);
			adpater2.notifyDataSetChanged();
		}

	}

	String jsonStr;
	Map<String, Object> jsonMap;

	private void httpService() {
		progressDialog = ProgressDialog.show(MorePayActivity.this, "",
				"正在获取台位信息...", true);
		Thread thread = new Thread() {

			public void run() {
				Message msg = new Message();
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "getSeats");
					map.put("type", "useing");// type:all（全部）free（空闲）
					map.put("category", "all");// category:hall(大厅) //
												// box（包厢）all(全部)
					map.put("userName", MyApplication.userName);// 当前用户
					map.put("clientId", MyApplication.clientId);
					if (isNetworkConnected(MorePayActivity.this)) {
						jsonStr = ServiceHttpPost.httpPost(map);
						if ("".equals(jsonStr) || null == jsonStr) {
							msg.what = 3;
						} else {
							jsonMap = ServiceHttpPost.GetMessageMap(map);
							if (null != jsonMap) {
								System.out.println(jsonMap.get("hall")
										.toString());
								System.out.println(jsonMap.get("box")
										.toString());
								if (jsonMap.containsKey("hall")) {

									List<Map<String, Object>> hall = ServiceHttpPost
											.GetListMaps(jsonMap.get("hall")
													.toString());
									if (null != hall) {

										lists2.addAll(BeanUtil.toBeanList(
												Seats.class, hall));
									}

								}
								if (jsonMap.containsKey("box")) {
									List<Map<String, Object>> box = ServiceHttpPost
											.GetListMaps(jsonMap.get("box")
													.toString());
									if (null != box) {
										lists2.addAll(BeanUtil.toBeanList(
												Seats.class, box));
									}

								}
							}
							msg.what = 1;
						}
					} else {
						msg.what = 2;
					}
					// add();
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}

				messageHandler.sendMessage(msg);
			}
		};
		thread.start();
	}

	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1: {
				upViews();
				// mainAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
				break;
			case 2: {
				progressDialog.dismiss();
				toast("网络异常，请检查网络配置！");
				break;
			}
			case 3: {
				progressDialog.dismiss();
				toast("数据请求失败，网络超时！");
				break;
			}

			default:
				break;
			}
		}
	};

	public String jsonString;

	public void morePay(View view) {
		progressDialog = ProgressDialog.show(MorePayActivity.this, "",
				"正在请求数据...", true);
		Thread thread = new Thread() {

			public void run() {
				Message message = new Message();
				try {
					String sid = "";
					for (Seats seats : lists1) {
						sid += seats.getSid() + ",";
					}
					sid = sid.substring(0, sid.lastIndexOf(","));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "morePay");
					map.put("userName", MyApplication.userName);
					map.put("clientId", MyApplication.clientId);
					map.put("seatsId", sid);
					if (isNetworkConnected(MorePayActivity.this)) {
						jsonString = ServiceHttpPost.GetMessageString(map);
						if (null != jsonString && !"".equals(jsonString)) {
							message.what = 1;
						} else if ("fail".equals(jsonString)) {
							message.what = 2;
						} else {
							message.what = 3;
						}
					} else {
						message.what = 4;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}

				messageHandler2.sendMessage(message);
			}
		};
		thread.start();
	}

	private Handler messageHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1: {
				progressDialog.dismiss();
				Intent intent = new Intent(MorePayActivity.this,
						MorePayItemActivity.class);
				intent.putExtra("seatsLists", lists1);
				intent.putExtra("jsonString", jsonString);
				MorePayActivity.this.startActivity(intent);
			}
				break;
			case 2: {
				toast("数据请求失败！");
				progressDialog.dismiss();
				break;
			}
			case 3: {
				toast("数据请求失败，网络超时！");
				adpater1.notifyDataSetChanged();
				adpater2.notifyDataSetChanged();
				progressDialog.dismiss();
				break;
			}
			case 4: {
				toast("网络异常，请检查网络配置！");
				progressDialog.dismiss();
			}
				break;
			default:
				break;
			}
		}
	};

	public void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void morepayBack(View view) {
		// Intent intent = new
		// Intent(MorePayActivity.this,MySeatsActivity.class);
		// MorePayActivity.this.startActivity(intent);
		MorePayActivity.this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new
			// Intent(MorePayActivity.this,MySeatsActivity.class);
			// MorePayActivity.this.startActivity(intent);
			MorePayActivity.this.finish();
		}
		return false;

	}
}
