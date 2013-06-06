package com.running.mobilemenu.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.meals.GridViewAdapter;
import com.running.mobilemenu.meals.NewOpenTabActivity;
import com.running.mobilemenu.model.Seats;

public class DragGridView extends GridView {

	// 定义基本的成员变量
	private ImageView dragImageView;// 被拖拽项的影像，其实就是一个ImageView

	private int dragSrcPosition;// 手指拖动项原始在列表中的位置

	private int dragPosition;// 手指拖动的时候，当前拖动项在列表中的位置

	// x,y坐标的计算

	private int dragPointX;

	private int dragPointY;

	private int dragOffsetX;

	private int dragOffsetY;

	private WindowManager windowManager;// windows窗口控制类

	private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数

	private int scaledTouchSlop;// 判断滑动的一个距离

	private int upScrollBounce;// 拖动的时候，开始向上滚动的边界

	private int downScrollBounce;// 拖动的时候，开始向下滚动的边界
	private Context context;
	private boolean flag = false;

	public DragGridView(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;

	}

	public boolean setOnItemLongClickListener(final MotionEvent ev,
			final int sx, final int sy) {

		this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				int x;
				int y;
				x = sx;
				y = sy;
				// 选中的数据项位置，使用ListView自带的pointToPosition(x, y)方法

				dragSrcPosition = dragPosition = arg2;
				System.out.println("-------------" + dragPosition);

				// 如果是无效位置(超出边界，分割线等位置)，返回

				if (dragPosition == AdapterView.INVALID_POSITION) {

					// return super.onInterceptTouchEvent(ev);

				}
				// 获取选中项View

				// getChildAt(int position)显示display在界面的position位置的View

				// getFirstVisiblePosition()返回第一个display在界面的view在adapter的位置position，可能是0，也可能是4
				ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());

				System.out.println("getFirstVisiblePosition: "
						+ getFirstVisiblePosition() + "dragPosition: "
						+ dragPosition);
				// dragPointY,dragPointX点击位置在点击View内的相对位置

				// dragOffsetX,dragOffsetY屏幕位置和当前ListView位置的偏移量，

				// 这两个参数用于后面拖动的开始位置和移动位置的计算
				// dragItemView = itemView;
				dragPointX = x - itemView.getLeft();

				dragPointY = y - itemView.getTop();

				dragOffsetX = (int) (ev.getRawX() - x);

				dragOffsetY = (int) (ev.getRawY() - y);

				System.out.println("itemView.getLeft(): " + itemView.getLeft()
						+ " itemView.getTop():" + itemView.getTop()
						+ "ev.getRawX() " + ev.getRawX() + "---" + x
						+ " ev.getRawY()" + ev.getRawY() + "---" + y);

				System.out.println("dragPointX: " + dragPointX + " dragPointY:"
						+ dragPointY + "dragOffsetX " + dragOffsetX
						+ " dragOffsetY" + dragOffsetY);

				// 获取右边的拖动图标，这个对后面分组拖拽有妙用
				View dragger = itemView.findViewById(R.id.opentab_icn);
				// 如果选中拖动图标
				if (dragger != null) {

					upScrollBounce = Math.min(y - scaledTouchSlop,
							getHeight() / 4);

					downScrollBounce = Math.max(y + scaledTouchSlop,
							getHeight() * 3 / 4);

					// 设置Drawingcache为true，获得选中项的影像bm，就是后面我们拖动的哪个头像
					itemView.setDrawingCacheEnabled(true);

					Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());

					// 准备拖动影像(把影像加入到当前窗口，并没有拖动，拖动操作我们放在onTouchEvent()的move中执行)
					startDrag(bm, x, y);

				}

				return false;

			};
		});
		return super.onInterceptTouchEvent(ev);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) ev.getX();

			int y = (int) ev.getY();
			return setOnItemLongClickListener(ev, x, y);
		}
		return super.onInterceptTouchEvent(ev);

	}

	public void startDrag(Bitmap bm, int x, int y) {
		// 释放影像，在准备影像的时候，防止影像没释放，每次都执行一下
		stopDrag();

		windowParams = new WindowManager.LayoutParams();

		windowParams.gravity = Gravity.TOP | Gravity.LEFT;

		windowParams.x = x - dragPointX + dragOffsetX;

		windowParams.y = y - dragPointY + dragOffsetY;

		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		// 下面这些参数能够帮助准确定位到选中项点击位置，照抄即可

		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;

		windowParams.windowAnimations = 0;

		// 把影像ImagView添加到当前视图中
		ImageView imageView = new ImageView(getContext());

		imageView.setImageBitmap(bm);

		windowManager = (WindowManager) getContext().getSystemService("window");

		windowManager.addView(imageView, windowParams);

		// 把影像ImageView引用到变量drawImageView，用于后续操作(拖动，释放等等)
		dragImageView = imageView;

	}

	public void onDrag(int x, int y) {

		if (dragImageView != null) {

			windowParams.alpha = 0.8f;

			windowParams.x = x - dragPointX + dragOffsetX;

			windowParams.y = y - dragPointY + dragOffsetY - 30;

			windowManager.updateViewLayout(dragImageView, windowParams);

		}

		int tempPosition = pointToPosition(x, y);

		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
			ViewGroup itemViews = (ViewGroup) getChildAt(dragPosition
					- getFirstVisiblePosition());
			View image = itemViews.findViewById(R.id.opentab_icn);
		}

		// 滚动

		if (y < upScrollBounce || y > downScrollBounce) {

			// 使用setSelection来实现滚动

			setSelection(dragPosition);

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (dragImageView != null && dragPosition != INVALID_POSITION) {

			int action = ev.getAction();

			switch (action) {

			case MotionEvent.ACTION_UP:

				int upX = (int) ev.getX();

				int upY = (int) ev.getY();

				stopDrag();

				onDrop(upX, upY);

				break;

			case MotionEvent.ACTION_MOVE:

				int moveX = (int) ev.getX();

				int moveY = (int) ev.getY();
				// thisEventTime = ev.getEventTime();
				// if(isLongPressed(lastDownTime,thisEventTime,5000)){
				onDrag(moveX, moveY);
				// }

				break;

			default:
				break;

			}

			return true;

		}

		return super.onTouchEvent(ev);

	}

	public void onDrop(int x, int y) {

		// 为了避免滑动到分割线的时候，返回-1的问题

		int tempPosition = pointToPosition(x, y);

		if (tempPosition != INVALID_POSITION) {

			dragPosition = tempPosition;

		}

		// 超出边界处理

		if (y < getChildAt(0).getTop()) {

			// 超出上边界

			dragPosition = 0;

		} else if (y > getChildAt(getChildCount() - 1).getBottom()
				|| (y > getChildAt(getChildCount() - 1).getTop() && x > getChildAt(
						getChildCount() - 1).getRight())) {

			// 超出下边界

			dragPosition = getAdapter().getCount() - 1;

		}

		// 数据交换

		if (dragPosition != dragSrcPosition && dragPosition > -1
				&& dragPosition < getAdapter().getCount()) {

			GridViewAdapter adapter = (GridViewAdapter) getAdapter();

			Seats dragItem = (Seats) adapter.getItem(dragSrcPosition);
			
			ViewGroup itemViews = (ViewGroup) getChildAt(dragPosition
					- getFirstVisiblePosition());
			ImageView image = (ImageView) itemViews.findViewById(R.id.opentab_icn);
		
			// adapter.remove(dragItem);
			//
			// adapter.insert(dragItem, dragPosition);
			if(!"300".equals(dragItem.getStatus())){
			 Toast.makeText(getContext(),"操作台位无效,请重新选择！",
			 Toast.LENGTH_SHORT).show();
			}else{
				Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("您确定要把  [" + dragItem.getSeatName() + "] 菜单复制到"
						+ "[" + adapter.getList().get(dragPosition).getSeatName()
						+ "]吗？");
				String sid1 = dragItem.getSid();
				
				String sid2 = adapter.getList().get(dragPosition).getSid();
				Seats seats = adapter.getList().get(dragPosition);
				builder.setPositiveButton("确定", new MyListener(sid1,sid2,image,seats));
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		}

	}

	/** 监听对话框里面的button点击事件 */
	// DialogInterface.OnClickListener listener = new
	// DialogInterface.OnClickListener() {
	class MyListener implements DialogInterface.OnClickListener {
		private String strSid1;
		private String strSid2;
        private ImageView image;
        private Seats seats;
		MyListener(String strSid1,String strSid2,ImageView image,Seats seats) {
			this.strSid1 = strSid1;
			this.strSid2 = strSid2;
			this.image = image;
			this.seats = seats;
		}

		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				image.setImageResource(R.drawable.yes);
				seats.setStatus("300");
				httpService(strSid1,strSid2);
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:

				break;
			}
		}
	};

	private ProgressDialog progressDialog; // 进度条
	private String jsonString;

	public void httpService(final String sSid1,final String sSid2){
		progressDialog = ProgressDialog.show(context, "","正在处理...", true);
		Thread thread = new Thread() {

			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "copySeats");
					map.put("clientId", MyApplication.clientId);
					map.put("userName", MyApplication.userName);// 当前用户
					map.put("seatsId1",sSid1);// 复制原台位
					map.put("seatsId2",sSid2);// 被复制台位
					jsonString = ServiceHttpPost.GetMessageString(map);
					
					
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}
				Message msg = messageHandler.obtainMessage();
				if ("success".equals(jsonString)) {
					msg.what = 1;
				} else {
					msg.what = 2;
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
				progressDialog.dismiss();
				
			}
				break;
			case 2:
				progressDialog.dismiss();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 
	 * 停止拖动，去除拖动项的头像
	 */
	public void stopDrag() {

		if (dragImageView != null) {

			windowManager.removeView(dragImageView);

			dragImageView = null;

		}

	}

}
