package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.dao.impl.FoodImageDaoImpl;
import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodImage;
import com.running.mobilemenu.model.FoodOrderInfo;
import com.running.mobilemenu.utils.AsyncBitmapLoader;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;

/**
 * @className FoodItemActivity
 * @author xiaoma
 * @Description 定义菜单的大图显示
 * @date 2013-3-14 下午3:56:36
 */
public class FoodItemActivity extends BaseActivity implements OnClickListener,
		OnTouchListener {
	private List<Food> foodlists;
	private AsyncBitmapLoader asyncBitmapLoader;
	private ImageView imageView;
	private TextView foodName;
	private TextView foodPrice;
	private TextView foodCategory;
	private int pageCount;
	private int totalCount;
	private EditText indexFoodCount;
	private String foodCount;
	private String isSmall = "N";
	// 添加大小份选择
	private RadioGroup selectRadio;
	private RadioButton big;
	private RadioButton small;
	private String reStr = "默认";
	private EditText kwEdit;
	private List<FoodImage> images = new ArrayList<FoodImage>();
	private FoodImageDaoImpl imageDao;
	private GestureDetector gestureDetector;
	public int imageNum=0;//food的图片个数
	   public int flagNum=0;//图片位置
	   public Food imgFood;//当前菜品
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		gestureDetector = new GestureDetector(this, new GestureListener());
		imageDao = new FoodImageDaoImpl(this);
		asyncBitmapLoader = new AsyncBitmapLoader();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_item_activity);
		foodlists = (List<Food>) this.getIntent().getSerializableExtra(
				"foodLists");
		pageCount = this.getIntent().getIntExtra("pageCount", 0);
		totalCount = foodlists.size();
		getImages();
	}

	private void upViews() {
		// TODO Auto-generated method stub
		Food food = foodlists.get(pageCount);
		imageView = (ImageView) findViewById(R.id.food_item_bimg);
		imageView.setOnTouchListener(this);
		imageView.setLongClickable(true);
		 imgFood = foodlists.get(pageCount);
		imageNum = imgFood.getImages().size();
		System.out.println("+++++++++"+imageNum);
		if (null != food) {
			// 处理图片加载
			if (food.getImages().size() > 0) {
				Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(food
						.getImages().get(0).getImageUrl());
				if (bitmap == null) {
					imageView.setImageResource(R.drawable.rc2);
				} else {
					imageView.setImageBitmap(bitmap);
				}
			}else{
				imageView.setImageResource(R.drawable.rc2);
			} 
			foodName = (TextView) findViewById(R.id.food_item_name);
			foodName.setText(food.getName());
			foodPrice = (TextView) findViewById(R.id.food_item_price);
			System.out.println("------" + food.getBigPicture());
			foodPrice.setText("价格：" + String.valueOf(food.getBigPrice())
					+ "元/份");
			foodCategory = (TextView) findViewById(R.id.food_item_category);
		}

		findViewById(R.id.food_item_next).setOnClickListener(this);
		findViewById(R.id.food_item_back).setOnClickListener(this);
		findViewById(R.id.food_item_message).setOnClickListener(this);
		findViewById(R.id.food_item_previous).setOnClickListener(this);
		findViewById(R.id.food_item_order).setOnClickListener(this);
	}
   
	@Override
	public void onClick(View view) {
		Food food = null;
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.food_item_back: {
			Intent intent5 = new Intent(this, IndexMealsActivity.class);
			this.startActivity(intent5);
			break;
		}
		case R.id.food_item_next: {

			 ++pageCount;
			
			changeView();
			break;
		}
		case R.id.food_item_previous: {
			--pageCount;
			changeView();
			break;
		}
		case R.id.food_item_message: {
			Intent intent = new Intent(this, FoodIntroduce.class);
			intent.putExtra("notice", foodlists.get(pageCount).getNotice());
			intent.putExtra("makingPro", foodlists.get(pageCount).getMakingProcess());
			this.startActivity(intent);
			break;
		}
		case R.id.food_item_order:
			selectFoodCount(foodlists.get(pageCount));
			break;

		default:
			break;
		}
	}

	private void changeView() {
		if (pageCount < totalCount && pageCount >= 0) {
			Food food = foodlists.get(pageCount);
			 imgFood = foodlists.get(pageCount);
				imageNum = imgFood.getImages().size();
			if (null != food) {
				// 处理图片加载
				if (food.getImages().size() > 0) {
					Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(food
							.getImages().get(0).getImageUrl());
					if (bitmap == null) {
						imageView.setImageResource(R.drawable.rc2);
					} else {
						imageView.setImageBitmap(bitmap);
					}
				}else{
					imageView.setImageResource(R.drawable.rc2);
				} 
			}
			foodName.setText(food.getName());
			foodPrice.setText("价格：" + String.valueOf(food.getBigPrice())
					+ "元/份");
		} else {
			if (pageCount < 0)
				pageCount = 0;
			if (pageCount == totalCount)
				pageCount = totalCount - 2;
			Toast.makeText(this, "数据到底了！", Toast.LENGTH_SHORT).show();
		}
	}

	// 点餐操作
	/**
	 * @Title: selectFoodCount
	 * @Description: 点餐选择份数
	 * @param @param food
	 * @return void
	 * @throws
	 */
	private void selectFoodCount(Food food) {
		List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
		int listCount = MyApplication.listFoodInfo.size();
		int flag = 1;
		String str = "";
		for (int i = 0; i < listCount; i++) {
			if (food.getSid() == listfood.get(i).getFoodSid()) {
				flag = listfood.get(i).getFoodNum();
				str = listfood.get(i).getNotice();
			}
		}
		LayoutInflater inflater = (LayoutInflater) FoodItemActivity.this
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
		if (flag == 1) {
			indexFoodCount.setText(String.valueOf(food.getOrderCount()));
		} else {
			indexFoodCount.setText(String.valueOf(flag));
		}
		Spannable spanText = indexFoodCount.getText();
		Selection.setSelection(spanText, spanText.length());
		Builder builder = new AlertDialog.Builder(FoodItemActivity.this);
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
			List<FoodOrderInfo> listfood = MyApplication.listFoodInfo;
			int listCount = MyApplication.listFoodInfo.size();
			boolean flag = true;
			foodCount = indexFoodCount.getText().toString();
			for (int i = 0; i < listCount; i++) {
				if (food.getSid() == listfood.get(i).getFoodSid()) {
					listfood.get(i).setFoodNum(Integer.parseInt(foodCount));
					listfood.get(i).setIsSmall(isSmall);
					listfood.get(i).setNotice(reStr);
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
				info.setNotice(reStr);
				MyApplication.listFoodInfo.add(info);
			}
			isSmall = "N";
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

	private void getImages() {
		class MyThread implements Runnable {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Food food : foodlists) {
					if (null != food) {
						String sql = "select * from food_image where foodSidFk = ?";
						images = (ArrayList<FoodImage>) imageDao.rawQuery(sql,
								new String[] { String.valueOf(food.getSid()) });
						food.setImages(images);
					}
				}
				Message msg = messageHandler.obtainMessage();
				msg.what = 1;
				messageHandler.sendMessage(msg);
			}

		}
		;
		new Thread(new MyThread()).start();
	}

	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1: {
				upViews();
			}
				break;

			default:
				break;
			}
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(FoodItemActivity.this,
					IndexMealsActivity.class);
			FoodItemActivity.this.startActivity(intent);
			FoodItemActivity.this.finish();
		}

		return false;

	}
	
	
	class GestureListener extends SimpleOnGestureListener {
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onDoubleTap");
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onDown");
			return super.onDown(e);
		}
		
		//用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			// 参数解释： 
			        // e1：第1个ACTION_DOWN MotionEvent 
			        // e2：最后一个ACTION_MOVE MotionEvent 
			        // velocityX：X轴上的移动速度，像素/秒 
			        // velocityY：Y轴上的移动速度，像素/秒 
			        // 触发条件 ： 
			        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒 
			
			final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				// 向左滑动
				flagNum++;
				System.out.println("向左滑动"+flagNum+">>>>>"+imageNum);
				if(flagNum<imageNum){
					String url = imgFood.getImages().get(flagNum).getImageUrl();
					if (!"".equals(url)) {
						// 处理图片加载
						Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(url);
						if (bitmap == null) {
							imageView.setImageResource(R.drawable.rc2);
						} else {
							imageView.setImageBitmap(bitmap);
						}
					}
				}else{
					flagNum=imageNum-1;
				}
				
				Log.i("MyGesture", "Fling left");
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				// 向右滑动
				flagNum--;
				System.out.println("向右滑动"+flagNum+">>>>>"+imageNum);
				if(flagNum>=0){
					String url = imgFood.getImages().get(flagNum).getImageUrl();
					if (!"".equals(url)) {
						// 处理图片加载
						Bitmap bitmap = new AsyncBitmapLoader().loadBitmap(url);
						if (bitmap == null) {
							imageView.setImageResource(R.drawable.rc2);
						} else {
							imageView.setImageBitmap(bitmap);
						}
					}
				}else{
					flagNum=0;
				}
				Log.i("MyGesture", "Fling right");
			} else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				// 向上滑动
				Log.i("MyGesture", "Fling down");
			} else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				// 向下滑动
				Log.i("MyGesture", "Fling up");
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onLongPress");
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
					+ distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onSingleTapUp");
			return super.onSingleTapUp(e);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

}
