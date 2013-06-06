package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.dao.impl.FoodDaoImpl;
import com.running.mobilemenu.dao.impl.FoodImageDaoImpl;
import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.AsyncBitmapLoader;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;

/**
 * @className IndexMealsActivity
 * @author xiaoma
 * @Description  菜品选择首页
 * @date 2013-3-29 上午9:40:16
 */
public class IndexMealsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private GridView gridView;
	private GridAdapter adapter;// 适配器
	private ArrayList<Food> listFoods = new ArrayList<Food>();
	private EditText indexFoodCount;// 菜品份数
	private int pageConut = 0;// 定义索引位置
	private String foodCount;
	private Seats seat;
	private FoodDaoImpl foodDao;// 菜品数据源
	private FoodImageDaoImpl foodImageDao;// 菜品图片数据源
	// 添加大小份选择
	private RadioGroup selectRadio;
	private RadioButton big;
	private RadioButton small;
	private String isSmall = "N";
	private String type = "";// 订单添加菜品
	private String seatsId = "";// 订单
	private String kwStr = "默认";
	private EditText kwEdit;
	private EditText selectName;
	private ImageButton selectBtn;

	// private Seats aSeats;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		type = this.getIntent().getStringExtra("type");
		seatsId = this.getIntent().getStringExtra("seatsId");
		// aSeats = (Seats) this.getIntent().getSerializableExtra("seats");
		// System.out.println("----SeatName: "+aSeats.getSeatName());
		foodDao = new FoodDaoImpl(IndexMealsActivity.this);
		foodImageDao = new FoodImageDaoImpl(IndexMealsActivity.this);
		new Thread(new MyThread("主菜", 1)).start();
		// 订台的数据
		seat = (Seats) this.getIntent().getSerializableExtra("seat");
		setContentView(R.layout.meals_index_activity);

		upViews();
	}

	class MyThread implements Runnable {

		private String name;
		private int num;
        private String spell;
		public MyThread(String name, int num) {
			this.name = name;
			this.num = num;
			this.spell = name;

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String sql = "select * from foods where typeName = ?";
			String sql2 = "select * from foods where encode like ? or spell like ?";
			if (2 == num || 1 == num) {
				
				listFoods = (ArrayList<Food>) foodDao.rawQuery(sql,
						new String[] { name });
			} else {
				
				listFoods = (ArrayList<Food>) foodDao.rawQuery(sql2,
						new String[] { "%"+name+"%","%"+spell+"%" });
				System.out.println("-----"+listFoods.size());
			}

			Message msg = messageHandler.obtainMessage();
			msg.what = num;
			messageHandler.sendMessage(msg);
		}

	};

	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1: {
				gridView();
			}
				break;
			case 2: {
			}
			case 3: {
				adapter.setList(listFoods);
				adapter.notifyDataSetChanged();
			}
				break;

			default:
				break;
			}
		}
	};

	// GridView 加载
	private void gridView() {
		gridView = (GridView) findViewById(R.id.index_grid);
		adapter = new GridAdapter(IndexMealsActivity.this);
		adapter.setList(listFoods);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(adapter);
	}

	/**
	 * @Title: upViews
	 * @Description: 加载页面View和监听
	 * @param
	 * @return void
	 * @throws
	 */
	private void upViews() {
		// TODO Auto-generated method stub
		selectName = (EditText) findViewById(R.id.index_meals_name);
		selectBtn = (ImageButton) findViewById(R.id.index_select);
		// 菜单导航监听
		Button menu1 = (Button) findViewById(R.id.menu1);
		Button menu2 = (Button) findViewById(R.id.menu2);
		Button menu3 = (Button) findViewById(R.id.menu3);
		Button menu4 = (Button) findViewById(R.id.menu4);
		Button menu5 = (Button) findViewById(R.id.menu5);
		Button menu6 = (Button) findViewById(R.id.menu6);
		menu1.setOnClickListener(this);
		menu2.setOnClickListener(this);
		menu3.setOnClickListener(this);
		menu4.setOnClickListener(this);
		menu5.setOnClickListener(this);
		menu6.setOnClickListener(this);
		selectBtn.setOnClickListener(this);
		// 我的菜单监听
		Button mealBtn =(Button) findViewById(R.id.meals_myorder);
		if("addFood".equals(type))mealBtn.setText("已添菜品");
		mealBtn.setOnClickListener(this);
		
		// 返回
		// findViewById(R.id.meals_back).setOnClickListener(this);

	}

	// 定义列表列
	static class GridHolder {
		private ImageView bigImage;
		private TextView mName;
		private TextView mPrice;
		private ImageView shopingImg;

	}

	// 定义GridView数据载体
	class GridAdapter extends BaseAdapter {

		private Context context;

		private List<Food> list;
		private LayoutInflater mInflater;

		public GridAdapter(Context c) {
			super();
			this.context = c;
		}

		public void setList(List<Food> list) {
			this.list = list;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int index) {

			return list.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GridHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.index_item_activity,
						null);
				holder = new GridHolder();
				holder.mName = (TextView) convertView
						.findViewById(R.id.food_name);
				holder.mPrice = (TextView) convertView
						.findViewById(R.id.food_price);
				holder.shopingImg = (ImageView) convertView
						.findViewById(R.id.food_shop_img);

				holder.bigImage = (ImageView) convertView
						.findViewById(R.id.index_food_big);
				convertView.setTag(holder);

			} else {
				holder = (GridHolder) convertView.getTag();

			}
			Food food = list.get(position);
			if (food != null) {
				holder.mName.setText(food.getName());
				holder.mPrice.setText("￥" + String.valueOf(food.getBigPrice()));
				ImageView image = holder.bigImage;
				String imageURL = food.getPictureURL();
				// holder.bigImage.setImageResource(food.getsPicture());
				// loadImage(imageURL,GridAdapter.this, holder);
				// 处理图片加载
				Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(imageURL);
				if (bitmap == null) {
					image.setImageResource(R.drawable.rc2);
				} else {
					image.setImageBitmap(bitmap);
				}
			}
			holder.shopingImg.setOnClickListener(new MyOnClickListener(food));
			return convertView;
		}

	}

	// 点餐监听
	class MyOnClickListener implements OnClickListener {
		private Food food;

		public MyOnClickListener(Food food) {
			this.food = food;
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.food_shop_img:
				selectFoodCount(food);
				break;
			default:
				break;
			}

		}
	}

	// 查看大图监听
	@Override
	public void onItemClick(AdapterView<?> adapterview, View view, int i, long l) {
		// TODO Auto-generated method stub
		// 转向大图浏览
//		Intent intent = new Intent(IndexMealsActivity.this,
//				FoodItemActivity.class);
//		intent.putExtra("foodLists", listFoods);
//		intent.putExtra("pageCount", i);
//		IndexMealsActivity.this.startActivity(intent);

	}

	// 页面监听
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.index_food_big:

			// 转向大图浏览
			// Intent intent = new Intent(IndexMealsActivity.this,
			// FoodItemActivity.class);
			// intent.putExtra("foodLists", listFoods);
			// intent.putExtra("pageCount", pageConut);
			// IndexMealsActivity.this.startActivity(intent);
			break;
		case R.id.menu1:
			new Thread(new MyThread("主菜", 2)).start();
			break;
		case R.id.menu2:
			new Thread(new MyThread("凉菜", 2)).start();
			break;
		case R.id.menu3:
			new Thread(new MyThread("汤类", 2)).start();
			break;
		case R.id.menu4:
			new Thread(new MyThread("主食", 2)).start();
			break;
		case R.id.menu5:
			new Thread(new MyThread("点心", 2)).start();
			break;
		case R.id.menu6:
			new Thread(new MyThread("酒水", 2)).start();
			break;
		case R.id.meals_myorder: {
			if (MyApplication.listFoodInfo.size() <= 0) {
				Toast.makeText(this, "你还没有点餐！请点餐！", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent2 = new Intent(this, MyOrderActivity.class);
				intent2.putExtra("seatsId", seatsId);
				intent2.putExtra("type", type);
				this.startActivity(intent2);
			}
		}
			break;
		case R.id.index_select: {
			String encode = selectName.getText().toString().toUpperCase();
				
			new Thread(new MyThread(encode, 3)).start();
		}
			break;
		case R.id.meals_back: {
			/*
			 * Intent intent3 = new Intent(this,MainActivity.class);
			 * this.startActivity(intent3); this.finish(); break;
			 */
		}

		default:
			break;
		}
	}

	/**
	 * @Title: sendIntent
	 * @Description: 处理菜单分类
	 * @param @param str
	 * @return void
	 * @throws
	 */
	public void sendIntent(String str) {
		listFoods.clear();
		// addLists(str);
		// listFoods = MyApplication.geFoodLists(str);

	}

	/**
	 * @Title: selectFoodCount
	 * @Description: 点餐选择份数
	 * @param @param food
	 * @return void
	 * @throws
	 */
	int flagNum = 1;

	private void selectFoodCount(Food food) {
		List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
		int listCount = MyApplication.listFoodInfo.size();

		String str = "";
		for (int i = 0; i < listCount; i++) {
			if (food.getSid() == listfood.get(i).getFoodSid()) {
				flagNum = listfood.get(i).getFoodNum();
				str = listfood.get(i).getNotice();
			}
		}
		LayoutInflater inflater = (LayoutInflater) IndexMealsActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.select_food_items, null);
		indexFoodCount = (EditText) layout.findViewById(R.id.index_food_count);
		selectRadio = (RadioGroup) layout.findViewById(R.id.selete_radio);
		big = (RadioButton) layout.findViewById(R.id.big);
		small = (RadioButton) layout.findViewById(R.id.small);
		kwEdit = (EditText) layout.findViewById(R.id.index_kw);
		kwEdit.setText(str);
		selectRadio.setOnCheckedChangeListener(onCheckChange);
		indexFoodCount.setText(String.valueOf(food.getOrderCount()));

		if (flagNum == 1) {
			indexFoodCount.setText(String.valueOf(food.getOrderCount()));
		} else {
			indexFoodCount.setText(String.valueOf(flagNum));
		}
		Spannable spanText = indexFoodCount.getText();
		Selection.setSelection(spanText, spanText.length());
		Builder builder = new AlertDialog.Builder(IndexMealsActivity.this);
		builder.setTitle("选择菜品份数：（" + food.getName() + ")");
		builder.setView(layout);
		builder.setPositiveButton("确定", new MyDialogInterface(food));
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	class MyDialogInterface implements DialogInterface.OnClickListener {
		Food food;

		public MyDialogInterface(Food food) {
			this.food = food;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {

			case -1: {
				List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
				int listCount = MyApplication.listFoodInfo.size();
				boolean flag = true;
				foodCount = indexFoodCount.getText().toString();

				if (!"".equals(kwEdit.getText().toString())) {
					kwStr = kwEdit.getText().toString();
				}
				for (int i = 0; i < listCount; i++) {
					if (food.getSid() == listfood.get(i).getFoodSid()) {
						listfood.get(i).setFoodNum(Integer.parseInt(foodCount));
						listfood.get(i).setIsSmall(isSmall);
						listfood.get(i).setNotice(kwStr);
						flag = false;
						break;
					}
				}
				if (flag) {
					FoodOrderInfo info = new FoodOrderInfo();
					info.setFoodSid(food.getSid());
					info.setFoodName(food.getName());
					info.setFoodNum(Integer.parseInt(foodCount));
					if ("Y".equals(isSmall)) {
						info.setCardPrice(food.getSmallCardPrice());
						info.setFoodPrice(food.getSmallPrice());
					} else {
						info.setCardPrice(food.getBigCardPrice());
						info.setFoodPrice(food.getBigPrice());
					}
					info.setIsSmall(isSmall);
					info.setPictureURL(food.getPictureURL());
					info.setNotice(kwStr);
					info.setState("100");
					MyApplication.listFoodInfo.add(info);
				}
				isSmall = "N";
				flagNum = 1;
				break;
			}
			default:
				break;
			}
		}
	};

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

	public void mealsBack(View view) {
		// Intent intent = new
		// Intent(IndexMealsActivity.this,NewOpenTabActivity.class);
		// IndexMealsActivity.this.startActivity(intent);
		// IndexMealsActivity.this.finish();
		getButton();
	}

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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			getButton();

		}

		return false;

	}

	public void getButton() {
		if (MyApplication.listFoodInfo.size() > 0
				|| !"".equals(MyApplication.seatsSid)) {
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
			MyApplication.listFoods.clear();
			Intent intent = new Intent(IndexMealsActivity.this,
					NewOpenTabActivity.class);
			IndexMealsActivity.this.startActivity(intent);
			IndexMealsActivity.this.finish();
		}
	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				MyApplication.listFoodInfo.clear();
				MyApplication.listSeats.clear();
				MyApplication.seats = null;
				MyApplication.addSeatsFood = null;
				MyApplication.seatsSid = "";
				Intent intent = new Intent(IndexMealsActivity.this,
						NewOpenTabActivity.class);
				IndexMealsActivity.this.startActivity(intent);
				IndexMealsActivity.this.finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:

				break;
			}
		}
	};
}
