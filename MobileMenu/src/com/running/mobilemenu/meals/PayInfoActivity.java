package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className PayInfoActivity
 * @author xiaoma
 * @Description 账单详情
 * @date 2013-4-13 上午10:40:29
 */
public class PayInfoActivity extends BaseActivity {
	private ProgressDialog progressDialog; // 进度条
	private List<FoodOrderInfo> listFoods = new ArrayList<FoodOrderInfo>();// 菜品
	private List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
	private ListView listview;
	private String totalPrice = "0.00";
	private String totalCardPrice = "0.00";
	private TextView price;
	private TextView cardPrice;
	private String seatsId = "";// 订单
	private String personNum;
	private String reName;
	private String seatsSid;
	private TextView seatsName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		seatsSid = this.getIntent().getStringExtra("myOrder");
		System.out.println("seatsId: " + seatsSid);
		setContentView(R.layout.payinfo_activity);
		toService();
	}

	public void upListView() {

		// 简单加载菜品列表listView
		listview = (ListView) findViewById(R.id.pay_list);

		// SimpleAdapter adapter = new SimpleAdapter(this, listMap,
		// R.layout.myorder_item2_activity, new String[] { "foodName",
		// "foodPrice" }, new int[] { R.id.order_food_listName,
		// R.id.order_food_listPrice });
		MyAdapter adapter = new MyAdapter(this, listFoods);

		listview.setAdapter(adapter);
		TextView total = (TextView) findViewById(R.id.pay_total);
		TextView totalCard = (TextView) findViewById(R.id.pay_total_crad);
		total.setText("￥ " + totalPrice);
		totalCard.setText("￥ " + totalCardPrice);
		seatsName = (TextView) findViewById(R.id.seatsName);
		seatsName.setText(reName);
	}

	class ViewHolder {
		TextView name;
		TextView price;
	}

	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<FoodOrderInfo> lists;

		public MyAdapter(Context context, List<FoodOrderInfo> lists) {
			this.lists = lists;
			this.mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {

			// TODO Auto-generated method stub

			return lists.size();

		}

		@Override
		public Object getItem(int arg0) {

			// TODO Auto-generated method stub

			return lists.get(arg0);

		}

		@Override
		public long getItemId(int arg0) {

			// TODO Auto-generated method stub

			return arg0;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.pay_item_activity,
						null);

				holder.name = (TextView) convertView
						.findViewById(R.id.food_listName);
				holder.price = (TextView) convertView
						.findViewById(R.id.food_num);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();

			}

			FoodOrderInfo food = lists.get(position);
			if (null != food) {
				holder.name.setText(food.getFoodName() + " ×"
						+ String.valueOf(food.getFoodNum()));
				float total = food.getFoodNum() * food.getFoodPrice();
				holder.price.setText("￥" + String.valueOf(total));
			}
			return convertView;

		}

	}

	public String jsonStr;
	public Map<String, Object> jsonString;
	public List<Map<String, Object>> listMaps;

	private void toService() {
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(PayInfoActivity.this, "",
				"正在请求账单数据...", true);
		Thread thread = new Thread() {
			public void run() {
				Message message = new Message();
				try {

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "operateOrder");
					map.put("userName", MyApplication.userName);
					map.put("clientId", MyApplication.clientId);
					map.put("type", "pay");
					map.put("seatsId", seatsSid);
					if (isNetworkConnected(PayInfoActivity.this)) {
						jsonStr = ServiceHttpPost.httpPost(map);
						if ("".equals(jsonStr) || null == jsonStr) {
								message.what = 3;
						}else{
							jsonString = ServiceHttpPost.GetMessageMap(jsonStr);
							if (null != jsonString) {
								totalPrice = jsonString.get("price").toString();
								totalCardPrice = jsonString.get("cardPrice")
										.toString();
								reName = jsonString.get("resName").toString();
								personNum = jsonString.get("personNum")
										.toString();
								reName = reName + "(" + personNum + "人)";
								listMap = ServiceHttpPost
										.GetListMaps(jsonString.get("data")
												.toString());
								for (Map maps : listMap) {
									FoodOrderInfo info = new FoodOrderInfo();
									info.setFoodName((String) maps
											.get("foodName"));
									info.setFoodNum((Integer) maps
											.get("foodNum"));
									info.setFoodPrice(Float.valueOf(maps.get(
											"foodPrice").toString()));
									listFoods.add(info);
								}
								if (null != jsonString) {
									message.what = 1;
								} else {
									message.what = 2;
								}
							}
						}

					} else {
						message.what = 4;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
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
				upListView();
				break;
			case 2:
				progressDialog.dismiss();
				message("数据请求失败！");
				break;
			case 3:
				progressDialog.dismiss();
				message("数据请求失败，网络超时！");
				break;
			case 4:
				progressDialog.dismiss();
				message("网络异常，请检查配置！");
				break;
			default:
				break;
			}
		}
	};

	private void message(String str) {

		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void payBack(View view) {
		// Intent intent = new
		// Intent(PayInfoActivity.this,MySeatsActivity.class);
		// PayInfoActivity.this.startActivity(intent);
		PayInfoActivity.this.finish();
	}

	public void goPay(View view) {
		Toast.makeText(this, "请前往收银台进行结账！欢迎下次再来！", Toast.LENGTH_SHORT).show();
		Thread thread = new Thread() {
			public void run() {
				try {

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "sendPaysSeats");
					map.put("clientId", MyApplication.clientId);
					map.put("userName", MyApplication.userName);// 当前用户
					map.put("seatsId", seatsSid);// 结账台位
					ServiceHttpPost.GetMessageString(map);

				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}

			}
		};
		thread.start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new
			// Intent(PayInfoActivity.this,MySeatsActivity.class);
			// PayInfoActivity.this.startActivity(intent);
			PayInfoActivity.this.finish();
		}
		return false;

	}
}
