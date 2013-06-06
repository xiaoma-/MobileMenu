package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.running.mobilemenu.R;
import com.running.mobilemenu.model.MyOrder;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className MySeatsActivity
 * @author xiaoma
 * @Description 显示台位列表
 * @date 2013-4-2 下午5:56:46
 */
public class MySeatsActivity extends ListActivity {

	private ListView listView;// listView控件
	private List<MyOrder> listMyOrder = new ArrayList<MyOrder>();
	View view;
	PopupWindow pop;

	Button btnPress;
	Button btnAdd;
	Button btnPay;
	Button btnMorePay;
	private ProgressDialog progressDialog; // 进度条
	private String orderSid;
	private int reStr;
	private MyAdapter myAdpter;
	private int xWidth;
	private int yHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Display display = getWindowManager().getDefaultDisplay();
		xWidth = display.getWidth();
		yHeight = display.getHeight();
		MyApplication.listFoodInfo.clear();
		MyApplication.listSeats.clear();
		MyApplication.seats = null;
		MyApplication.seatsSid = "";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myseats_activity);
		// upViews();
		getOrderToService("order");
		initPopupWindow();
	}

	private void initPopupWindow() {
		view = this.getLayoutInflater().inflate(R.layout.popup_window, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		btnPress = (Button) view.findViewById(R.id.btnPress);
		btnAdd = (Button) view.findViewById(R.id.btnAddFood);
		btnPay = (Button) view.findViewById(R.id.btnPay);
		btnMorePay = (Button) view.findViewById(R.id.btnPayMore);
	}

	private void upViews() {
		listView = getListView();
		myAdpter = new MyAdapter(this);
		myAdpter.putList(listMyOrder);

		listView.setAdapter(myAdpter);
		listView.setOnItemClickListener(new myOnItemClickListenter());

	}

	class ViewHolder {
		public TextView seatsName;
		public ImageView seatsBtn;
	}

	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<MyOrder> ordersList = new ArrayList<MyOrder>();

		public MyAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);

		}

		public void putList(List<MyOrder> ordersList) {
			this.ordersList = ordersList;
		}

		@Override
		public int getCount() {

			// TODO Auto-generated method stub

			return ordersList.size();

		}

		@Override
		public Object getItem(int position) {

			// TODO Auto-generated method stub

			return ordersList.get(position);

		}

		@Override
		public long getItemId(int position) {

			// TODO Auto-generated method stub

			return 0;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.myseats_item_activity,
						null);

				holder.seatsName = (TextView) convertView
						.findViewById(R.id.myseat_seatname);

				holder.seatsBtn = (ImageView) convertView
						.findViewById(R.id.myseat_seats_btn);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			MyOrder myorder = ordersList.get(position);

			if (null != myorder) {
				String str = "";
				if ("100".equals(myorder.getStatus())) {
					str = "已下单";
				} else if ("200".equals(myorder.getStatus())) {
					str = "已催单";
				} else if ("300".equals(myorder.getStatus())) {
					str = "已上齐";
				} else if ("500".equals(myorder.getStatus())) {
					str = "已结账";
				}
				holder.seatsName.setText(myorder.getResName() + "  ("
						+ myorder.getPersonNum() + "人)    " + str);
			}

			holder.seatsBtn.setOnClickListener(new myOnClickListenter(myorder,
					position));

			return convertView;

		}

	}

	class myOnClickListenter implements OnClickListener {
		private MyOrder myorder;
		int position;

		public myOnClickListenter(MyOrder myorder, int position) {
			this.myorder = myorder;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			int[] location = new int[2];
			v.getLocationOnScreen(location);
			System.out.println("pop.isShowing():" + pop.isShowing());
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				System.out.println(location[0] + "-----" + location[1]);
				if ((yHeight - location[1]) < 250) {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.tophh));
					pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
							(location[1] - pop.getHeight() - 100));
					// pop.showAsDropDown(v, location[0],
					// (location[1]-pop.getHeight()-150));
				} else {
					view.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.hhh));
					pop.showAsDropDown(v);
				}

				btnPress.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						orderSid = String.valueOf(myorder.getOrderSid())
								.toString();
						reStr = position;
						getOrderToService("press");

					}
				});

				btnAdd.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						Intent intent = new Intent(MySeatsActivity.this,
								IndexMealsActivity.class);
						intent.putExtra("seatsId",
								String.valueOf(myorder.getResSid()).toString());
						intent.putExtra("type", "addFood");
						MyApplication.seatsSid = String.valueOf(
								myorder.getResSid()).toString();
						MySeatsActivity.this.startActivity(intent);
					}
				});

				btnPay.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						Intent intent = new Intent(MySeatsActivity.this,
								PayInfoActivity.class);
						intent.putExtra("myOrder",
								String.valueOf(myorder.getResSid()).toString());
						MySeatsActivity.this.startActivity(intent);
						// MySeatsActivity.this.finish();
					}
				});
				btnMorePay.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						Intent intent = new Intent(MySeatsActivity.this,
								MorePayActivity.class);
						intent.putExtra("orderSid",String.valueOf(myorder.getResSid()).toString());
						MySeatsActivity.this.startActivity(intent);
						// MySeatsActivity.this.finish();
					}
				});
			}

		}

	}

	class myOnItemClickListenter implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int location,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MySeatsActivity.this,
					MyServiceOrderActivity.class);
			String orderId = String.valueOf(listMyOrder.get(location)
					.getOrderSid());
			intent.putExtra("orderId", orderId);
			MySeatsActivity.this.startActivity(intent);
			// MySeatsActivity.this.finish();

		}
	}

	public boolean netWork() {
		if (getApplicationContext() != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public List<MyOrder> jsonString;
	private String pressString;
	private String result = "";

	private void getOrderToService(final String str) {
		progressDialog = ProgressDialog.show(MySeatsActivity.this, "",
				"正在请求数据...", true);
		Thread thread = new Thread() {
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					if (netWork()) {
						System.out.println("-----------");
						if ("order".equals(str)) {
							map.put("method", "getOrder");
							map.put("userName", MyApplication.userName);
							map.put("clientId", MyApplication.clientId);
							result = ServiceHttpPost.httpPost(map);
							jsonString = ServiceHttpPost.GetMessageMaps(result,
									MyOrder.class);
							listMyOrder = jsonString;
						}
						if ("press".equals(str)) {
							map.put("method", "operateOrder");
							map.put("userName", MyApplication.userName);
							map.put("clientId", MyApplication.clientId);
							map.put("type", "press");
							map.put("orderId", orderSid);
							result = ServiceHttpPost.httpPost(map);
							pressString = result;
						}
					} else {
						result = "900";
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				Message message = new Message();
				if ("300".equals(result) || "".equals(result) || null == result) {
					message.what = 4;
				} else if ("900".equals(result)) {
					message.what = 5;
				} else {
					if ("order".equals(str)) {
						if (jsonString.size() > 0) {
							message.what = 1;
						} else {
							message.what = 3;
						}
					} else if ("press".equals(str)) {
						if (!"".equals(pressString)) {
							message.what = 2;
						} else {
							message.what = 3;
						}
					} else {
						message.what = 3;
					}
				}
				mHandler.sendMessage(message);
			};
		};
		thread.start();
	}

	/**
	 * @Description: 验证通过业务处理
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				upViews();
				break;
			case 2:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				listMyOrder.get(reStr).setStatus("200");
				System.out.println("------"
						+ listMyOrder.get(reStr).getStatus());
				myAdpter.putList(listMyOrder);
				myAdpter.notifyDataSetChanged();
				message(2);
				break;
			case 3:
				progressDialog.dismiss();
				message(5);
				break;
			case 4:
				progressDialog.dismiss();
				message(3);
				break;
			case 5:
				progressDialog.dismiss();
				message(4);
				break;

			}
		}
	};

	private void message(int i) {
		String str = "";
		if (2 == i)
			str = "催菜成功！";
		if (4 == i)
			str = "网络异常,请检查网络！";
		if (3 == i)
			str = "数据请求失败，请求超时!";
		if (5 == i)
			str = "数据为空，还没添加数据!";
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void seatsBack(View view) {
		Intent intent = new Intent(this, NewOpenTabActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(MySeatsActivity.this,
					NewOpenTabActivity.class);
			MySeatsActivity.this.startActivity(intent);
			MySeatsActivity.this.finish();
		}
		return false;

	}

	public void seatsFlush(View view) {
		if (null != listMyOrder && listMyOrder.size() > 0) {
			listMyOrder.clear();
		} else {
			getOrderToService("order");
			if (null != listMyOrder && listMyOrder.size() > 0) {
				listMyOrder.clear();
				myAdpter.putList(listMyOrder);
				myAdpter.notifyDataSetChanged();
			}
		}
	}
}
