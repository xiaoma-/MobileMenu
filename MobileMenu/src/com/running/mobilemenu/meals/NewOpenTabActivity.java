package com.running.mobilemenu.meals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.running.mobilemenu.R;
import com.running.mobilemenu.dao.impl.FoodDaoImpl;
import com.running.mobilemenu.dao.impl.FoodImageDaoImpl;
import com.running.mobilemenu.dao.impl.FoodTypeDaoImpl;
import com.running.mobilemenu.init.LoginActivity;
import com.running.mobilemenu.init.SplashActivity;
import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodImage;
import com.running.mobilemenu.model.MyOrder;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.model.Traffics;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.BeanUtil;
import com.running.mobilemenu.utils.DragGridView;
import com.running.mobilemenu.utils.DragViewPager;
import com.running.mobilemenu.utils.ImageDownload;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;
import com.running.update.UpdateService;

/**
 * @className OpenTabActivity
 * @author xiaoma
 * @Description 开台首页 选择餐厅的剩余台位
 * @date 2013-4-2 上午11:31:50
 */
public class NewOpenTabActivity extends BaseActivity {
	private static final String TAG = "OpenTabActivity";
	private DragViewPager vPager;
	// DATA
	private MainAdapter mainAdapter = new MainAdapter();// 加载pagerView配置器
	private List<RadioButton> rb_pages = new ArrayList<RadioButton>();
	private List<View> listViews = new ArrayList<View>();// 动态添加pagerView view集合
	private LayoutInflater mInflater;
	private TextView tvTabActivity, tvTabGroups, tvTabFriends, tvTabChat;
	private List<Seats> lists = new ArrayList<Seats>();
	private List<Seats> lists2 = new ArrayList<Seats>();
	private EditText indexFoodCount;// 选择人数
	private String foodCount;// 显示人数
	private ProgressDialog progressDialog;
	private ImageView ivBottomLine;
	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	private Resources resources;
	private MyOrder myOrder;// 订单
	private ImageView image;
	public int pageNum = 0;
	private FoodDaoImpl foodsDao;// 菜品数据数据源
	private FoodImageDaoImpl foodImageDao;// 菜品数据数据源
	private FoodTypeDaoImpl foodTypeDao;// 菜品数据数据源
	private ProgressDialog mSaveDialog = null;// 更新进度显示

	@Override
	public void onCreate(Bundle savedInstanceState) {

		foodsDao = new FoodDaoImpl(NewOpenTabActivity.this);
		foodImageDao = new FoodImageDaoImpl(NewOpenTabActivity.this);
		foodTypeDao = new FoodTypeDaoImpl(NewOpenTabActivity.this);
		isNetworkConnected(this);
		super.onCreate(savedInstanceState);
		// add();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newopentab_activity);
		mInflater = LayoutInflater.from(NewOpenTabActivity.this);
		vPager = (DragViewPager) findViewById(R.id.newvPager);
		resources = getResources();
		httpService(1);
		listenter();
		InitWidth();
		if (MyApplication.updateState) {
			checkVersion();
		}
	}

	private void init() {
		
		mainAdapter.putList(listViews);
		vPager.setAdapter(mainAdapter);
		vPager.setOnPageChangeListener(new MyOnPageChangeListener());
		vPager.setCurrentItem(pageNum);
	}

	private void listenter() {
		tvTabActivity = (TextView) findViewById(R.id.tv_tab_activity);
		tvTabGroups = (TextView) findViewById(R.id.tv_tab_groups);

		tvTabActivity.setOnClickListener(new MyOnClickListener(0));
		tvTabGroups.setOnClickListener(new MyOnClickListener(1));
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			vPager.setCurrentItem(index);
		}
	};

	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
		Log.i("MainActivity", "offset=" + offset);

		position_one = (int) (screenW / 2.0);
		position_two = position_one * 2;
		position_three = position_one * 3;
	}

	private void upView(List<Seats> seats) {

		View view = mInflater.inflate(R.layout.newopentab_gridview, null);
		DragGridView gridView = (DragGridView) view
				.findViewById(R.id.newopentab_grid);
		GridViewAdapter adapter = new GridViewAdapter(this, seats);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new MyItemClick(seats));
		listViews.add(view);
	}

	// 添加GirdView事件监听事件
	class MyItemClick implements OnItemClickListener {
		List<Seats> seatsList;

		public MyItemClick(List<Seats> seatsList) {
			this.seatsList = seatsList;
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long arg3) {
			// TODO Auto-generated method stub
			// 改变选中的台位图片
			// int num = MyApplication.listSeats.size();
			// String sid = seatsList.get(position).getSid();
			image = (ImageView) view.findViewById(R.id.opentab_icn);

			// boolean flags = true;// 判断是否存在
			// for (int i = 0; i < num; i++) {
			// if (sid.equals(MyApplication.listSeats.get(i).getSid())) {
			// flags = false;
			// deleteDialog(i, image);
			// break;
			// }
			// }
			// if (flags) {
			//
			// selectFoodCount(seatsList.get(position));
			// }
			Seats seats = seatsList.get(position);
			Log.e("--------", "-----" + seats.getStatus());
			if ("300".equals(seats.getStatus())) {
				payDialog(position, image, seats);
			} else if ("200".equals(seats.getStatus())) {
				deleteDialog(position, image, seats);
			} else {

				selectFoodCount(seats);

			}

		}
	}

	public void setSeatsStatus(final Seats seats, final String sid) {
		Thread thread = new Thread() {
			String jsonMap;

			public void run() {
				try {
					String str = seats.getSid();
					Map<String, Object> map = new HashMap<String, Object>();

					map.put("method", "putSeatsStatus");
					map.put("status", sid);
					map.put("seatsId", str);
					map.put("peopleNum", seats.getPeopleNum());
					jsonMap = ServiceHttpPost.GetMessageString(map);

				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}
				Message message = new Message();

				if ("success".equals(jsonMap)) {
					message.what = 1;
				} else {
					message.what = 2;
				}
				mHandlers.sendMessage(message);
			}
		};
		thread.start();
	}

	Handler mHandlers = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showStr("操作成功！");
				break;
			case 2:
				showStr("操作失败！");
				break;
			}
		}
	};

	public void showStr(String str) {
		Toast.makeText(NewOpenTabActivity.this, str, Toast.LENGTH_SHORT).show();
	}

	private String[] selectItem = { "菜单", "添餐", "结账", "并台结账" };

	public void payDialog(int i, ImageView image, Seats seats) {
		Builder builder = new AlertDialog.Builder(NewOpenTabActivity.this);
		builder.setTitle("选择操作类型：（" + seats.getSeatName() + "）");
		builder.setPositiveButton("取 消", new MyPayDialogOnClick(seats, image));
		// builder.setNeutralButton("结账", new MyPayDialogOnClick(seats, image));
		// builder.setNegativeButton("并台结账", new MyPayDialogOnClick(seats,
		// image));
		builder.setItems(selectItem, new MyPayDialogOnClick(seats, image));
		builder.show();
	}

	class MyPayDialogOnClick implements DialogInterface.OnClickListener {
		private Seats seats;
		private ImageView image;

		public MyPayDialogOnClick(Seats seats, ImageView image) {
			this.seats = seats;
			this.image = image;

		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub

			switch (arg1) {
			case 0: {
				// Toast.makeText(NewOpenTabActivity.this, selectItem[0],
				// Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(NewOpenTabActivity.this,
						MyServiceOrderActivity.class);
				intent.putExtra("seatsId", seats.getSid());
				NewOpenTabActivity.this.startActivity(intent);
				// NewOpenTabActivity.this.finish();
				break;
			}
			case 1: {
				Intent intent = new Intent(NewOpenTabActivity.this,
						IndexMealsActivity.class);
				intent.putExtra("seatsId", seats.getSid());
				intent.putExtra("type", "addFood");
				MyApplication.seatsSid = seats.getSid();
				NewOpenTabActivity.this.startActivity(intent);
				// NewOpenTabActivity.this.finish();
				break;
			}

			case 2: {
				Intent intent = new Intent(NewOpenTabActivity.this,
						PayInfoActivity.class);
				intent.putExtra("myOrder", String.valueOf(seats.getSid())
						.toString());
				NewOpenTabActivity.this.startActivity(intent);
				break;
			}
			case 3: {
				Intent intent = new Intent(NewOpenTabActivity.this,
						MorePayActivity.class);
				intent.putExtra("Seats", seats);
				NewOpenTabActivity.this.startActivity(intent);
				break;
			}
			case -1: {
				// Intent intent = new Intent(NewOpenTabActivity.this,
				// IndexMealsActivity.class);
				// intent.putExtra("seatsId", seats.getSid());
				// intent.putExtra("type", "addFood");
				// MyApplication.seatsSid = seats.getSid();
				// NewOpenTabActivity.this.startActivity(intent);
				// NewOpenTabActivity.this.finish();

				break;
			}

			case -2: {
				Intent intent = new Intent(NewOpenTabActivity.this,
						MorePayActivity.class);
				intent.putExtra("Seats", seats);
				NewOpenTabActivity.this.startActivity(intent);
				break;
			}
			case -3: {
				// NewOpenTabActivity.this.setSeatsStatus(seats, "100");
				// seats.setStatus("100");
				// image.setImageResource(R.drawable.no);
				Intent intent = new Intent(NewOpenTabActivity.this,
						PayInfoActivity.class);
				intent.putExtra("myOrder", String.valueOf(seats.getSid())
						.toString());
				NewOpenTabActivity.this.startActivity(intent);
				break;
			}

			}

		}

	}

	private String[] selectItem2 = { "点餐", "退台" };

	public void deleteDialog(int i, ImageView image, Seats seats) {
		// Seats seats = MyApplication.listSeats.get(i);

		Builder builder = new AlertDialog.Builder(NewOpenTabActivity.this);
		builder.setTitle("选择操作类型：（" + seats.getSeatName() + "）");
		builder.setPositiveButton("取 消", new MyDialogOnClick(seats, image));
		// builder.setNegativeButton("点餐", new MyDialogOnClick(seats, image));
		builder.setItems(selectItem2, new MyDialogOnClick(seats, image));
		builder.show();
	}

	class MyDialogOnClick implements DialogInterface.OnClickListener {
		private Seats seats;
		private ImageView image;

		public MyDialogOnClick(Seats seats, ImageView image) {
			this.seats = seats;
			this.image = image;

		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub

			switch (arg1) {
			case 0: {
				Intent intent = new Intent(NewOpenTabActivity.this,
						IndexMealsActivity.class);
				// intent.putExtra("seats", seats);
				MyApplication.seats = seats;
				NewOpenTabActivity.this.startActivity(intent);
				NewOpenTabActivity.this.finish();
				break;
			}

			case 1: {
				NewOpenTabActivity.this.setSeatsStatus(seats, "100");
				seats.setStatus("100");
				image.setImageResource(R.drawable.no);

			}
			// case -1: {
			// // boolean flag = MyApplication.listSeats.remove(seats);
			// // if (flag) {
			// NewOpenTabActivity.this.setSeatsStatus(seats, "100");
			// seats.setStatus("100");
			// image.setImageResource(R.drawable.no);
			// // }
			// break;
			// }
			//
			// case -2: {
			// Intent intent = new Intent(NewOpenTabActivity.this,
			// IndexMealsActivity.class);
			// // intent.putExtra("seats", seats);
			// MyApplication.seats = seats;
			// NewOpenTabActivity.this.startActivity(intent);
			// NewOpenTabActivity.this.finish();
			//
			// }

			}

		}

	}

	// 页卡切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					tvTabGroups.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					tvTabFriends.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three, 0, 0, 0);
					tvTabChat.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				tvTabActivity.setTextColor(resources.getColor(R.color.white));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_one, 0,
							0);
					tvTabActivity.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					tvTabFriends.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_one, 0, 0);
					tvTabChat.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				tvTabGroups.setTextColor(resources.getColor(R.color.white));
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_two, 0,
							0);
					tvTabActivity.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					tvTabGroups.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_two, 0, 0);
					tvTabChat.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				tvTabFriends.setTextColor(resources.getColor(R.color.white));
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_three,
							0, 0);
					tvTabActivity.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_three, 0, 0);
					tvTabGroups.setTextColor(resources
							.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_three, 0, 0);
					tvTabFriends.setTextColor(resources
							.getColor(R.color.lightwhite));
				}
				tvTabChat.setTextColor(resources.getColor(R.color.white));
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
		}

	}

	public void goMeals(View view) {

		if (MyApplication.listSeats.size() > 0) {
			Intent intent = new Intent(NewOpenTabActivity.this,
					IndexMealsActivity.class);
			NewOpenTabActivity.this.startActivity(intent);
		} else {
			new AlertDialog.Builder(NewOpenTabActivity.this)
					.setIcon(
							getResources().getDrawable(
									R.drawable.login_error_icon))
					.setTitle("温馨提示").setMessage("客官！您没有选择台位小二不知道送到哪！")
					.create().show();
		}
	}

	public void goSelectMeals(View view) {

		Intent intent = new Intent(NewOpenTabActivity.this,
				MySeatsActivity.class);
		NewOpenTabActivity.this.startActivity(intent);
		NewOpenTabActivity.this.finish();

	}

	public void gobox(View view) {
		pageNum = 1;
		vPager.setCurrentItem(1);

	}

	public void gohall(View view) {
		pageNum = 0;
		vPager.setCurrentItem(0);

	}

	private void add() {
		for (int i = 0; i < 30; i++) {
			Seats s = new Seats();
			s.setSid(String.valueOf(i));
			s.setSeatName("桌位" + i);
			lists.add(s);
			Seats s2 = new Seats();
			s2.setSid(String.valueOf(i));
			s2.setSeatName("包厢" + i);
			lists2.add(s2);
		}
	}

	private void selectFoodCount(Seats seats) {

		LayoutInflater inflater = (LayoutInflater) NewOpenTabActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.select_seats_items, null);
		indexFoodCount = (EditText) layout.findViewById(R.id.index_food_count);
		indexFoodCount.setText("1");
		Spannable spanText = indexFoodCount.getText();

		Selection.setSelection(spanText, spanText.length());
		Builder builder = new AlertDialog.Builder(NewOpenTabActivity.this);
		builder.setTitle("填写就餐人数：（" + seats.getSeatName() + ")");
		builder.setView(layout);
		builder.setPositiveButton("确定", new MyDialogInterface(seats));
		builder.setNegativeButton("取消", new MyDialogInterface(seats));
		builder.show();
	}

	class onclick implements OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			// image.setImageResource(R.drawable.ic_launcher);
		}
	};

	class MyDialogInterface implements DialogInterface.OnClickListener {
		Seats seats;

		public MyDialogInterface(Seats seats) {
			this.seats = seats;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case -1: {
				List<Seats> listSeats = MyApplication.listSeats;
				int listCount = MyApplication.listSeats.size();
				boolean flag = true;
				foodCount = indexFoodCount.getText().toString();
				for (int i = 0; i < listCount; i++) {
					if (seats.getSid() == listSeats.get(i).getSid()) {
						listSeats.get(i).setPeopleNum(
								Integer.parseInt(foodCount));
						seats.setStatus("200");
						setSeatsStatus(listSeats.get(i), "200");
						image.setImageResource(R.drawable.ok);
						flag = false;
						break;
					}
				}
				if (flag) {

					seats.setPeopleNum(Integer.parseInt(foodCount));
					seats.setStatus("200");
					setSeatsStatus(seats, "200");
					MyApplication.listSeats.add(seats);
					image.setImageResource(R.drawable.ok);
				}
			}
				num = 1;
				break;
			case -2: {

			}

				break;

			default:
				break;
			}

		}
	};

	String jsonStrs;
	Map<String, Object> jsonMap;

	private void httpService(final int flagNum) {
		progressDialog = ProgressDialog.show(NewOpenTabActivity.this, "",
				"正在获取台位信息...", true);
		Thread thread = new Thread() {

			public void run() {
				Message msg = new Message();
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "getSeats");
					map.put("type", "all");// type:all（全部）free（空闲）
					map.put("category", "all");// category:hall(大厅)
					// box（包厢）all(全部)
					map.put("userName", MyApplication.userName);// 当前用户

					if (isNetworkConnected(NewOpenTabActivity.this)) {
						jsonStrs = ServiceHttpPost.httpPost(map);
						jsonMap = ServiceHttpPost.GetMessageMap(jsonStrs);
						if ("".equals(jsonStrs) || null == jsonStrs) {
							msg.what = 3;
						} else {
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
										lists = BeanUtil.toBeanList(
												Seats.class, hall);
									}

								}
								if (jsonMap.containsKey("box")) {
									List<Map<String, Object>> hall = ServiceHttpPost
											.GetListMaps(jsonMap.get("box")
													.toString());
									if (null != hall) {
										lists2 = BeanUtil.toBeanList(
												Seats.class, hall);
									}

								}
							}
							if (flagNum == 1) {
								msg.what = 1;
							} else {
								msg.what = 2;
							}
						}
					} else {
						msg.what = 4;
					}
					// add();
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
					msg.what = 3;
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
				for (int i = 0; i < 2; i++) {
					if (i == 0)
						upView(lists);
					else
						upView(lists2);
				}
				init();
				// mainAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
				break;
			case 2:
				for (int i = 0; i < 2; i++) {
					if (i == 0)
						upView(lists);
					else
						upView(lists2);
				}

				mainAdapter.putList(listViews);
				mainAdapter.notifyDataSetChanged();
				vPager.setCurrentItem(pageNum);
				init();
				progressDialog.dismiss();
				break;
			case 3:
				progressDialog.dismiss();
				message("数据请求失败，网络超时！");
				break;
			case 4:
				progressDialog.dismiss();
				message("无网络，请检查网络设置！");
				break;
			default:
				break;
			}
		}
	};
   private void message(String str){
	   Toast.makeText(NewOpenTabActivity.this, str, Toast.LENGTH_SHORT).show();
   }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MyApplication.listSeats.size() > 0) {
				// 创建退出对话框
				AlertDialog isExit = new AlertDialog.Builder(this).create();
				// 设置对话框标题
				isExit.setTitle("系统提示");
				// 设置对话框消息
				isExit.setMessage("你确定要取消当前菜单吗？");
				// 添加选择按钮并注册监听
				isExit.setButton("确定", listener);
				isExit.setButton2("取消", listener);
				// 显示对话框
				isExit.show();
			} else {
				AlertDialog isExit = new AlertDialog.Builder(this).create();
				// 设置对话框标题
				isExit.setTitle("系统提示");
				// 设置对话框消息
				isExit.setMessage("你确定退出登录吗？");
				// 添加选择按钮并注册监听
				isExit.setButton("确定", listener2);
				isExit.setButton2("取消", listener2);
				// 显示对话框
				isExit.show();
			}
		}

		return false;

	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				MyApplication.listFoods.clear();
				exit();
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:

				break;
			}
		}
	};
	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				MyApplication.listFoods.clear();
				MyApplication.listSeats.clear();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:

				break;
			}
		}
	};

	public void opentBack(View view) {
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// 设置对话框标题
		isExit.setTitle("系统提示");
		// 设置对话框消息
		isExit.setMessage("你确定退出登录吗？");
		// 添加选择按钮并注册监听
		isExit.setButton("确定", listener2);
		isExit.setButton2("取消", listener2);
		// 显示对话框
		isExit.show();

	}

	public void opentFlush(View view) {
		lists.clear();
		lists2.clear();
		listViews.clear();
		httpService(2);
		vPager.setCurrentItem(pageNum);
		// Intent intent = new
		// Intent(NewOpenTabActivity.this,NewOpenTabActivity.class);
		// NewOpenTabActivity.this.startActivity(intent);
		// NewOpenTabActivity.this.finish();

	}

	int num = 1;

	public void TabAdd(View view) {
		num = Integer.parseInt(indexFoodCount.getText().toString());
		switch (view.getId()) {
		case R.id.tab_add:
			num++;
			indexFoodCount.setText(String.valueOf(num).toString());
			Spannable spanText = indexFoodCount.getText();
			Selection.setSelection(spanText, spanText.length());
			break;
		case R.id.tab_sub:
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();// 先清掉菜单
		MenuItem item = menu.add(0, 400, 401, "数据同步");
		item.setIcon(R.drawable.download);
		// MenuItem item4 = menu.add(0, 401, 402, "流量查看");
		// item4.setIcon(R.drawable.traffics);
		MenuItem item2 = menu.add(0, 402, 403, "软件更新");
		item2.setIcon(R.drawable.update);
		MenuItem item3 = menu.add(0, 403, 404, "退出");
		item3.setIcon(R.drawable.exit);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 400:
			updateDate();
			break;
		case 401:
			initTraffic(this);
			break;
		case 402:
			if(checkVersion()){
			  Toast.makeText(NewOpenTabActivity.this, "已经是最新版本了！", Toast.LENGTH_SHORT).show();
			};
			break;
		case 403:
			exit();
			break;
		default: // 对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);
		} // 返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
	}

	/**
	 * 更新菜品数据
	 * */
	String result;
	Map<String, Object> jsonString;

	public void updateDate() {

		/*
		 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
		 * available.
		 */
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		final String clientId = tm.getDeviceId();
		progressDialog = ProgressDialog.show(NewOpenTabActivity.this, "",
				"数据检验中...", true);
		Thread thread = new Thread() {
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "update");
					map.put("userName", MyApplication.userName);
					map.put("clientId", clientId);
					if (isNetworkConnected(getApplicationContext())) {
						result = ServiceHttpPost.httpPost(map);
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
					jsonString = ServiceHttpPost.GetMessageMap(result);
					if (null != jsonString) {
						String type = (String) jsonString.get("type");
						System.out.println("type: " + type);
						if ("success".equals(type)) {
							message.what = 1;
						} else if ("update".equals(type)) {
							message.what = 2;
						}
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
				message(1);
				break;
			case 2:
				// 菜品数据有更新
				progressDialog.dismiss();
				getlist();
				break;
			case 3:
				// 菜品数据有更新
				mSaveDialog.dismiss();
				break;
			case 4:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				message(2);
				break;
			case 5:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				message(3);
				break;
			}
		}
	};

	private void message(int i) {

		String str = "";
		if (1 == i)
			str = "数据已经是最新的了！";
		if (2 == i)
			str = "数据请求失败，网络异常请求超时！";
		if (3 == i)
			str = "网络异常请检查网络！";
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	// 更新菜品数据
	public void getlist() {
		mSaveDialog = new ProgressDialog(NewOpenTabActivity.this);
		mSaveDialog.setTitle("更新菜品数据");
		mSaveDialog.setMessage("数据正在下载中，请稍等...");
		// 进度条是否不明确
		mSaveDialog.setIndeterminate(false);
		mSaveDialog.setCancelable(true);
		mSaveDialog.show();
		new Thread(contentRun).start();
	}

	// public List<FoodImage> images = new ArrayList<FoodImage>();
	// 访问服务器下载数据
	private Runnable contentRun = new Runnable() {

		@Override
		public void run() {
			if (jsonString.containsKey("append")) {
				List<Food> lists = JSON.parseArray(jsonString.get("append")
						.toString(), Food.class);

				for (Food food : lists) {
					if (null != food) {
						new Mypic(food.getPictureURL()).start();
						System.out.println("-----edit" + food.getPictureURL());
						foodsDao.insert(food, false);
					}
				}
			}
			if (jsonString.containsKey("edit")) {
				List<Food> lists = JSON.parseArray(jsonString.get("edit")
						.toString(), Food.class);

				for (Food food : lists) {
					if (null != food) {
						new Mypic(food.getPictureURL()).start();
						System.out.println("-----edit" + food.getPictureURL());
						foodsDao.delete(food.getSid());
						foodsDao.insert(food, false);
					}
				}
			}
			if (jsonString.containsKey("delete")) {
				List<Food> lists = JSON.parseArray(jsonString.get("delete")
						.toString(), Food.class);
				for (Food food : lists) {
					if (null != food) {
						foodsDao.delete(food.getSid());
					}
				}
			}
			if (jsonString.containsKey("image")) {
				List<FoodImage> images = JSON.parseArray(jsonString
						.get("image").toString(), FoodImage.class);
				new Thread(new PictureRun(images)).start();
				for (FoodImage image : images) {
					if (null != image) {
						foodImageDao.insert(image, false);
					}
				}

			}
			Message message = new Message();
			message.what = 3;

			mHandler.sendMessage(message);

		}

	};

	class Mypic extends Thread {
		private String imageStr;

		public Mypic(String imageStr) {
			this.imageStr = imageStr;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				if (!"".equals(imageStr)) {
					try {
						new ImageDownload().saveFile(MyApplication.ImageUrl
								+ imageStr);
						Log.d("ImageDownload", "单个图片下载图片成功[" + imageStr + " , "
								+ MyApplication.ImageUrl + imageStr + "]");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("ImageDownload", "单个图片下载图片失败[" + imageStr + " , "
								+ MyApplication.ImageUrl + imageStr + "]");
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	}

	// 线程下载图片
	class PictureRun implements Runnable {
		private List<FoodImage> images;

		public PictureRun(List<FoodImage> images) {
			this.images = images;
		}

		@Override
		public void run() {
			try {
				if (images.size() > 0) {
					for (FoodImage foodImage : images) {
						if (null != foodImage) {
							try {
								new ImageDownload()
										.saveFile(MyApplication.ImageUrl
												+ foodImage.getImageUrl());
								Log.d("ImageDownload",
										"下载图片成功[" + foodImage.getId() + " , "
												+ MyApplication.ImageUrl
												+ foodImage.getImageUrl() + "]");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Log.e("ImageDownload",
										"下载图片失败[" + foodImage.getId() + " , "
												+ MyApplication.ImageUrl
												+ foodImage.getImageUrl() + "]");
								e.printStackTrace();
							}
						}
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadHistory.start();
		}

	};

	Thread threadHistory = new Thread() {
		public void run() {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("method", "updateHistory");
				map.put("userName", MyApplication.userName);
				map.put("clientId", MyApplication.clientId);
				map.put("type", "success");
				if (isNetworkConnected(NewOpenTabActivity.this)) {
					result = ServiceHttpPost.httpPost(map);
				} else {
					result = "900";
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	/***
	 * 检查是否更新版本
	 */
	public boolean checkVersion() {
		Log.d("Version", "检测有版本升级！");
		// new MyApplication().getVersion();
		if (MyApplication.localVersion < MyApplication.serverVersion) {

			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级")
					.setMessage("发现新版本,建议立即更新使用.")
					.setPositiveButton("立即更新",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Log.d("Version", "检测有新版本升级！");
									// 开启更新服务UpdateService
									// 这里为了把update更好模块化，可以传一些updateService依赖的值
									// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
									Intent updateIntent = new Intent(
											NewOpenTabActivity.this,
											UpdateService.class);
									updateIntent.putExtra("app_name",
											"shanghuguanjia");
									NewOpenTabActivity.this
											.startService(updateIntent);
								}
							})
					.setNegativeButton("稍后再说",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
			MyApplication.updateState = false;
			return false;
		}else{
			return true;
		}
	}

	private String getDeviceId() {
		String android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		return android_id;
	}

	public void initTraffic(Context context) {
		Traffics traffic = trafficTotal(context);
		long sendT = traffic.getSend() + traffic.getSendDay();
		long receiveT = traffic.getReceiveDay() + traffic.getReceive();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("流量统计");
		alert.setMessage("接收：" + traffic.getReceiveDay() + "/KB \n发送："
				+ traffic.getSendDay() + "/KB \n总接收：" + receiveT / 1024
				+ "/M \n总发送：" + sendT / 1024 + "/M");
		// alert.setPositiveButton("更新", null);
		alert.setNegativeButton("取消", null);
		alert.show();

	}
}
