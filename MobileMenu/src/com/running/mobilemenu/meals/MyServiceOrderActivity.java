package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.running.mobilemenu.R;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className MyOrderActivity
 * @author xiaoma
 * @Description 我的菜单，主要是修改、查看、下单等业务处理
 * @date 2013-3-20 上午10:34:59
 */
public class MyServiceOrderActivity extends ListActivity implements
		OnClickListener {

	private List<FoodOrderInfo> listFoods = new ArrayList<FoodOrderInfo>();
	private ProgressDialog progressDialog; // 进度条

	private ListView listView;// listView控件
	View view;
	PopupWindow pop;

	Button btnPress;
	Button btnAdd;
	Button btnPay;
	private String orderId;
	private String seatsId;
	private String foodSid;
	private MyAdapter myAdpter;
	private int reStr;
	private Button btnDele;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		orderId = this.getIntent().getStringExtra("orderId");
		seatsId = this.getIntent().getStringExtra("seatsId");
		setContentView(R.layout.myserviceorder_activity);
		getFoodOrder("order");

		initPopupWindow();
	}

	private void initPopupWindow() {
		view = this.getLayoutInflater().inflate(R.layout.popup_window2, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		btnPress = (Button) view.findViewById(R.id.btnPress2);
		btnAdd = (Button) view.findViewById(R.id.btnAddFood2);
		btnDele = (Button) view.findViewById(R.id.btnDeleFood2);

	}

	private void upViews() {
		myAdpter = new MyAdapter(this);
		myAdpter.setList(listFoods);
		listView = getListView();
		listView.setAdapter(myAdpter);
		listView.setOnItemClickListener(new myOnItemClickListenter());

	}

	class ViewHolder {
		public TextView seatsName;
		public ImageView seatsBtn;
	}

	class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<FoodOrderInfo> ordersList = new ArrayList<FoodOrderInfo>();

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);

		}

		public void setList(List<FoodOrderInfo> ordersList) {
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
			FoodOrderInfo info = ordersList.get(position);

			if (null != info) {
				holder.seatsName.setText(info.getFoodName() + " x"
						+ info.getFoodNum() + "    （" + info.getStateStr()
						+ "）");
			}

			holder.seatsBtn.setOnClickListener(new myOnClickListenter(info,
					position));

			return convertView;

		}

	}

	class myOnClickListenter implements OnClickListener {
		private FoodOrderInfo myorder;
		int postion;

		public myOnClickListenter(FoodOrderInfo myorder, int position) {
			this.myorder = myorder;
			this.postion = position;

		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				pop.showAsDropDown(v);

				btnPress.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						if ("300".equals(myorder.getState())||"450".equals(myorder.getState())) {
							if("300".equals(myorder.getState())) message("此菜品已上！");
							
							if("450".equals(myorder.getState())) message("此菜品已退菜！");
						} else {

							foodSid = String.valueOf(myorder.getFoodSid())
									.toString();
							reStr = postion;
							getFoodOrder("press");
						}

					}
				});

				btnAdd.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						if ("450".equals(myorder.getState())) {
							message("此菜品已退菜！");
						} else {
							foodSid = String.valueOf(myorder.getFoodSid())
									.toString();
							reStr = postion;
							getFoodOrder("over");
						}
					}
				});
				btnDele.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pop.dismiss();
						if ("300".equals(myorder.getState())||"500".equals(myorder.getState())) {
							message("此菜品已做好，不能退菜！");
						} else {

							foodSid = String.valueOf(myorder.getFoodSid())
									.toString();
							reStr = postion;
							getFoodOrder("delete");
						}
					}
				});
			}

		}

	}

	private void message(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	class myOnItemClickListenter implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int location,
				long arg3) {
			// TODO Auto-generated method stub

		}
	}

	public List<Map<String, Object>> jsonString;
	private String jsonStr;

	private void getFoodOrder(final String str) {

		progressDialog = ProgressDialog.show(MyServiceOrderActivity.this, "",
				"正在请求数据...", true);
		Thread thread = new Thread() {
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("userName", MyApplication.userName);
					map.put("clientId", MyApplication.clientId);
					map.put("orderId", orderId);
					map.put("seatsId", seatsId);
					if ("order".equals(str)) {
						map.put("method", "getFoodOrderInfo");
						jsonString = ServiceHttpPost.GetMessageMaps(map);
						if (null != jsonString) {
							for (Map maps : jsonString) {
								System.out.println(maps.toString());
								FoodOrderInfo info = new FoodOrderInfo();
								info.setOrderInfoSid((Integer) maps
										.get("orderInfoSid"));
								info.setFoodSid((Integer) maps.get("foodSid"));
								info.setFoodName((String) maps.get("foodName"));
								info.setFoodNum((Integer) maps.get("foodNum"));
								info.setFoodPrice(Float.valueOf(maps.get(
										"foodPrice").toString()));
								info.setCardPrice(Float.valueOf(maps.get(
										"cardPrice").toString()));
								info.setNotice((String) maps.get("notice"));
								info.setIsSmall((String) maps.get("isSmall"));
								info.setPictureURL((String) maps
										.get("pictureURL"));
								String state = maps.get("status").toString();
								
								if (!"".equals(state)) {
									if ("100".equals(state)) {
										info.setStateStr("未上菜");
									} else if ("200".equals(state)) {
										info.setStateStr("催菜中");
									} else if ("300".equals(state)) {
										info.setStateStr("菜已上");
									} else if ("500".equals(state)) {
										info.setStateStr("已结账");
									} else if ("450".equals(state)) {
										info.setStateStr("已退菜");
									}
								}
								info.setState(state);
								listFoods.add(info);
							}
						}
					} else if ("press".equals(str)) {
						map.put("method", "operateFoodInfo");
						map.put("type", "press");
						map.put("foodSid", foodSid);
						jsonStr = ServiceHttpPost.GetMessageString(map);
					} else if ("over".equals(str)) {
						map.put("method", "operateFoodInfo");
						map.put("type", "over");
						map.put("foodSid", foodSid);
						jsonStr = ServiceHttpPost.GetMessageString(map);
					} else if ("delete".equals(str)) {
						map.put("method", "operateFoodInfo");
						map.put("type", "delete");
						map.put("foodSid", foodSid);
						jsonStr = ServiceHttpPost.GetMessageString(map);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				Message message = new Message();
				if ("order".equals(str)) {
					if (null != jsonString) {
						message.what = 1;
					} else {
						message.what = 2;
					}
				} else if ("press".equals(str)) {
					if ("success".equals(jsonStr.trim())) {
						message.what = 3;
					} else {
						message.what = 2;
					}
				} else if ("over".equals(str)) {
					if ("success".equals(jsonStr.trim())) {
						message.what = 4;
					} else {
						message.what = 2;
					}
				} else if ("delete".equals(str)) {
					if ("success".equals(jsonStr.trim())) {
						message.what = 5;
					} else {
						message.what = 2;
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
			case 1: {
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss(); // 进度条
				upViews();
				break;
			}
			case 2:
				progressDialog.dismiss();
				message(2);
				break;
			case 3:
				progressDialog.dismiss();
				listFoods.get(reStr).setStateStr("催菜中");
				listFoods.get(reStr).setState("200");
				myAdpter.setList(listFoods);
				myAdpter.notifyDataSetChanged();
				message(3);
				break;
			case 4:
				progressDialog.dismiss();
				listFoods.get(reStr).setStateStr("菜已上");
				listFoods.get(reStr).setState("300");
				myAdpter.setList(listFoods);
				myAdpter.notifyDataSetChanged();
				message(3);
				break;

			case 5:
				progressDialog.dismiss();
				listFoods.get(reStr).setStateStr("已退菜");
				listFoods.get(reStr).setState("450");
				myAdpter.setList(listFoods);
				myAdpter.notifyDataSetChanged();
				message(3);
				break;

			default:
				break;
			}
		}
	};

	private void message(int i) {
		String str = "您的订单已提交！请稍后！";
		if (2 == i) {
			str = "数据请求失败！";
		}
		if (3 == i) {
			str = "操作成功！";
		}

		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void serviceOrderBack(View view) {
		// Intent intent = new Intent(this, MySeatsActivity.class);
		// this.startActivity(intent);
		this.finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent(this, MySeatsActivity.class);
			// this.startActivity(intent);
			this.finish();
		}
		return false;

	}
}
