package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.running.mobilemenu.R;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.model.MyOrder;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.AsyncBitmapLoader;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className MyOrderActivity
 * @author xiaoma
 * @Description 我的菜单，主要是修改、查看、下单等业务处理
 * @date 2013-3-20 上午10:34:59
 */
public class MyOrderActivity extends ListActivity implements OnClickListener {

	private List<FoodOrderInfo> listFoods;
	private ListView listview;
	private List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
	private Double totalPrice = 0.00;
	private Double totalCard = 0.00;
	private TextView title2;
	private EditText indexFoodCount;
	private String foodCount;
	private AsyncBitmapLoader asyncBitmapLoader;
	private ProgressDialog progressDialog; // 进度条
	// 添加大小份选择
	private RadioGroup selectRadio;
	private RadioButton big;
	private RadioButton small;
	private String isSmall = "N";
	private String type = "";// 订单添加菜品
	private String seatsSid = "";// 订单
	private String reStr = "默认";
	private EditText kwEdit;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		asyncBitmapLoader = new AsyncBitmapLoader();
		type = this.getIntent().getStringExtra("type");
		seatsSid = this.getIntent().getStringExtra("seatsId");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorder_activity);
		listFoods = MyApplication.listFoodInfo;
		Button button = (Button) findViewById(R.id.order_button_send);
		button.setOnClickListener(this);
		// 加载菜品列表listView
		adapter = new MyAdapter(this);
		setListAdapter(adapter);
		upListView();

	}

	/**
	 * @Title: upListView
	 * @Description: 加载视图
	 * @param
	 * @return void
	 * @throws
	 */
	public void upListView() {

		totalPrice = 0.00;
		totalCard = 0.00;
		// 简单加载菜品列表listView
		// listview = (ListView) findViewById(R.id.order_list);
		int count = MyApplication.listFoodInfo.size();
		for (int i = 0; i < count; i++) {
			// HashMap map = new HashMap<String, String>();
			// map.put("orderName", listFoods.get(i).getFoodName());
			// map.put("orderPrice", "×" + listFoods.get(i).getFoodNum());
			//
			// listMap.add(map);
			totalPrice = totalPrice + listFoods.get(i).getFoodPrice()
					* listFoods.get(i).getFoodNum();
			totalCard = totalCard + listFoods.get(i).getCardPrice()
					* listFoods.get(i).getFoodNum();

		}
		// SimpleAdapter adapter = new SimpleAdapter(this, listMap,
		// R.layout.myorder_item2_activity, new String[] { "orderName",
		// "orderPrice" }, new int[] { R.id.order_food_listName,
		// R.id.order_food_listPrice });
		//
		// listview.setAdapter(adapter);
		TextView total = (TextView) findViewById(R.id.order_total_price);
		total.setText("￥ " + String.valueOf(totalPrice));
		TextView cardTotal = (TextView) findViewById(R.id.order_total_crad);
		cardTotal.setText("￥ " + String.valueOf(totalCard));

	}

	final class ViewHolder {
		public ImageView img;
		public TextView foodCount;
		public TextView price;
		public TextView kouwei;
		public Button button;
		private ImageView imgEdit;
		private ImageView imgDelete;
		private TextView isBig;
	}

	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {

			// TODO Auto-generated method stub

			return listFoods.size();

		}

		@Override
		public Object getItem(int arg0) {

			// TODO Auto-generated method stub

			return null;

		}

		@Override
		public long getItemId(int arg0) {

			// TODO Auto-generated method stub

			return 0;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.myorder_item_activity,
						null);

				// holder.img = (ImageView) convertView
				// .findViewById(R.id.order_food_img);

				holder.foodCount = (TextView) convertView
						.findViewById(R.id.order_food_counts);

				holder.price = (TextView) convertView
						.findViewById(R.id.order_food_price);
				holder.kouwei = (TextView) convertView
						.findViewById(R.id.order_food_weidao);
				holder.isBig = (TextView) convertView
						.findViewById(R.id.order_food_big);

				// holder.button = (Button) convertView
				// .findViewById(R.id.order_button);
				holder.imgEdit = (ImageView) convertView
						.findViewById(R.id.myorder_edit);
				holder.imgDelete = (ImageView) convertView
						.findViewById(R.id.myorder_delete);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();

			}

			// holder.img.setBackgroundResource((Integer).get(position).get(
			// "img"));
			FoodOrderInfo food = listFoods.get(position);
			String str = food.getIsSmall();
			String smsalls = "Y".equals(str) ? "小份" : "大份";
			holder.isBig.setText("　(" + smsalls + ")");
			holder.foodCount.setText(food.getFoodName() + " ×"
					+ String.valueOf(food.getFoodNum()));

			holder.price.setText("￥" + String.valueOf(food.getFoodPrice()));
			holder.kouwei.setText("口味：" + food.getNotice());

			// ImageView image = holder.img;
			// String imageURL = food.getPictureURL();

			// // 处理图片加载
			// Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(imageURL);
			// if (bitmap == null) {
			// image.setImageResource(R.drawable.rc2);
			// } else {
			// image.setImageBitmap(bitmap);
			// }
			holder.imgEdit.setOnClickListener(new MyClickListener(food));
			holder.imgDelete.setOnClickListener(new MyClickListener(food));

			return convertView;

		}

	}

	class MyClickListener implements OnClickListener {
		private FoodOrderInfo food;

		public MyClickListener(FoodOrderInfo food) {
			this.food = food;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.myorder_edit:
				selectFoodCount(food);
				break;
			case R.id.myorder_delete:

				Builder builder = new AlertDialog.Builder(MyOrderActivity.this);
				builder.setMessage("确定删除菜品：（" + food.getFoodName() + ")");
				builder.setPositiveButton("确定", new MyInterface(food));
				builder.setNegativeButton("取消", null);
				builder.show();

				break;
			default:
				break;
			}

		}

	};

	class MyInterface implements DialogInterface.OnClickListener {
		FoodOrderInfo food;

		public MyInterface(FoodOrderInfo food) {
			this.food = food;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			MyApplication.listFoodInfo.remove(food);
			adapter.notifyDataSetChanged();
			upListView();
		}
	}

	/**
	 * @Title: selectFoodCount
	 * @Description: 点餐选择份数
	 * @param @param food
	 * @return void
	 * @throws
	 */
	private void selectFoodCount(FoodOrderInfo food) {
		List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
		int listCount = MyApplication.listFoodInfo.size();
		int flag = 1;
		String str = "";
		for (int i = 0; i < listCount; i++) {
			if (food.getFoodSid() == listfood.get(i).getFoodSid()) {
				flag = listfood.get(i).getFoodNum();
				str = listfood.get(i).getNotice();
			}
		}
		isSmall = food.getIsSmall();
		LayoutInflater inflater = (LayoutInflater) MyOrderActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.select_food_items, null);
		indexFoodCount = (EditText) layout.findViewById(R.id.index_food_count);
		selectRadio = (RadioGroup) layout.findViewById(R.id.selete_radio);
		big = (RadioButton) layout.findViewById(R.id.big);
		small = (RadioButton) layout.findViewById(R.id.small);
		kwEdit = (EditText) layout.findViewById(R.id.index_kw);
		kwEdit.setText(str);
		selectRadio.setOnCheckedChangeListener(onCheckChange);
		indexFoodCount.setText(String.valueOf(food.getFoodNum()));
		if (flag == 1) {
			indexFoodCount.setText(String.valueOf(food.getFoodNum()));
		} else {
			indexFoodCount.setText(String.valueOf(flag));
		}
		Spannable spanText = indexFoodCount.getText();
		Selection.setSelection(spanText, spanText.length());
		Builder builder = new AlertDialog.Builder(MyOrderActivity.this);
		builder.setTitle("修改菜品份数：（" + food.getFoodName() + ")");
		builder.setView(layout);
		builder.setPositiveButton("确定", new MyDialogInterface(food));
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	class MyDialogInterface implements DialogInterface.OnClickListener {
		FoodOrderInfo food;

		public MyDialogInterface(FoodOrderInfo food) {
			this.food = food;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
			int listCount = MyApplication.listFoodInfo.size();
			foodCount = indexFoodCount.getText().toString();
			if (!"".equals(kwEdit.getText().toString())) {
				reStr = kwEdit.getText().toString();
			}
			for (int i = 0; i < listCount; i++) {
				if (food.getFoodSid() == listfood.get(i).getFoodSid()) {
					if ("0".equals(foodCount)) {
						listfood.remove(i);
						food.setFoodNum(1);
					} else {
						listfood.get(i).setFoodNum(Integer.parseInt(foodCount));
						listfood.get(i).setIsSmall(isSmall);
						listfood.get(i).setNotice(reStr);
					}
					break;
				}
			}
			// Intent intent = new Intent(MyOrderActivity.this,
			// MyOrderActivity.class);
			// intent.putExtra("seatsId", seatsSid);
			// intent.putExtra("type", type);
			// MyOrderActivity.this.startActivity(intent);
			// MyOrderActivity.this.finish();
			adapter.notifyDataSetChanged();
			upListView();
		}
	};

	int num = 1;

	public void MealsAdd(View view) {
		num = Integer.parseInt(indexFoodCount.getText().toString());
		switch (view.getId()) {
		case R.id.meals_num_add:
			num++;
			indexFoodCount.setText(String.valueOf(num).toString());
			Spannable spanText = indexFoodCount.getText();
			Selection.setSelection(spanText, spanText.length());
			break;
		case R.id.meals_num_sub:
			if (num > 1) {
				num--;
			} else {
				num = 1;
			}

			indexFoodCount.setText(String.valueOf(num).toString());
			Spannable spanText2 = indexFoodCount.getText();
			Selection.setSelection(spanText2, spanText2.length());
			break;

		default:
			break;
		}

	}

	private OnCheckedChangeListener onCheckChange = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == big.getId()) {
				System.out.println("big");
				isSmall = "N";
			}
			if (checkedId == small.getId()) {
				System.out.println("small");
				isSmall = "Y";
			}

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.order_button_send:
			if (MyApplication.orderFlag) {
				Toast.makeText(this, "订单已经提交过了！", Toast.LENGTH_SHORT).show();
			} else {
				putOrder();
			}
			break;

		default:
			break;
		}
	}

	public String jsonString = "";

	private void putOrder() {
		progressDialog = ProgressDialog.show(MyOrderActivity.this, "",
				"正在提交订单...", true);
		Thread thread = new Thread() {
			public void run() {
				Message message = new Message();
				if (MyApplication.listFoodInfo.size() > 0) {
					try {

						String orderInfo = JSON
								.toJSONString(MyApplication.listFoodInfo);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("userName", MyApplication.userName);
						map.put("clientId", MyApplication.clientId);
						System.out.println("................" + type);

						// if ("".equals(type)||null==type||"null".equals(type))
						// {
						if ("".equals(MyApplication.seatsSid)) {
							// //List<Seats> lists = MyApplication.listSeats;
							String id = MyApplication.seats.getSid();
							String name = MyApplication.seats.getSeatName();
							int peopleNum = MyApplication.seats.getPeopleNum();
							// for (int i = 0; i < lists.size(); i++) {
							// id += lists.get(i).getSid() + ",";
							// name += lists.get(i).getSeatName() + ",";
							// peopleNum += lists.get(i).getPeopleNum();
							// }
							// id = id.substring(0, id.lastIndexOf(","));
							// name = name.substring(0, name.lastIndexOf(","));

							MyOrder myorder = new MyOrder();
							myorder.setPersonNum(peopleNum);
							myorder.setResSid(id);
							myorder.setResName(name);
							myorder.setServerName(MyApplication.userName);
							myorder.setOrderState("100");
							String orderString = JSON.toJSONString(myorder);

							map.put("method", "putOrder");
							map.put("order", orderString);
							map.put("orderInfo", orderInfo);

						} else {
							map.put("method", "operateOrder");
							map.put("type", "addFood");
							// map.put("orderId", orderSid);
							map.put("seatsId", seatsSid);
							map.put("data", orderInfo);

						}
						if (netWork()) {
							jsonString = ServiceHttpPost.GetMessageString(map);
							if (!"".equals(jsonString) && null!=jsonString) {

								if ("success".equals(jsonString.trim())) {
									message.what = 1;
								} else {
									message.what = 2;
								}
								MyApplication.seatsSid = "";
							} else {
								message.what = 2;
							}
						} else {
							message.what = 4;
						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				} else {
					message.what = 3;
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
			case 1: {
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss(); // 进度条
				message(1);
				MyApplication.listFoodInfo.clear();
				MyApplication.listSeats.clear();
				MyApplication.seats = null;
				MyApplication.seatsSid = "";
				Intent intent = new Intent(MyOrderActivity.this,
						MySeatsActivity.class);
				MyOrderActivity.this.startActivity(intent);
				MyOrderActivity.this.finish();

				break;
			}
			case 2:
				progressDialog.dismiss();
				message(2);
				break;
			case 3:
				progressDialog.dismiss();
				message(3);
				break;
			case 4:
				progressDialog.dismiss();
				message(4);
				break;

			default:
				break;
			}
		}
	};

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

	private void message(int i) {
		String str = "您的订单已提交！请稍后！";
		if (2 == i)
			str = "客官下单未能成功！";
		if (3 == i)
			str = "客官你还没点菜呢！";
		if (4 == i)
			str = "客官网络出现异常了！";

		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void oBack(View view) {
		Intent intent = new Intent(this, IndexMealsActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, IndexMealsActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		return false;

	}
}
