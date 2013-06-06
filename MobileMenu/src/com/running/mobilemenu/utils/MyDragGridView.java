package com.running.mobilemenu.utils;



import com.running.mobilemenu.meals.GridViewAdapter;
import com.running.mobilemenu.model.Seats;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class MyDragGridView extends GridView
{

	private int							dragPosition;	// 开始拖拽的位置
	private int							dropPosition;	// 结束拖拽的位置
	private int							dragPointX;	// 相对于item的x坐标
	private int							dragPointY;	// 相对于item的y坐标
	private int							dragOffsetX;
	private int							dragOffsetY;
	private ImageView					dragImageView;	// 拖动item的preview

	private WindowManager				windowManager;
	private WindowManager.LayoutParams	windowParams;

	private int							itemHeight;
	private Context context;
	public MyDragGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
	}

	boolean	flag	= false;

	public void setLongFlag(boolean temp)
	{
		flag = temp;
	}

	public boolean setOnItemLongClickListener(final MotionEvent ev)
	{
		this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				// onInterceptTouchEvent(ev);
				// TODO Auto-generated method stub

				int x = (int) ev.getX();
				int y = (int) ev.getY();
				dragPosition = dropPosition = arg2;
				System.out.println(dragPosition);
				if (dragPosition == AdapterView.INVALID_POSITION)
				{

				}
				ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());
				// 得到当前点在item内部的偏移量 即相对于item左上角的坐标
				dragPointX = x - itemView.getLeft();
				dragPointY = y - itemView.getTop();

				dragOffsetX = (int) (ev.getRawX() - x);
				dragOffsetY = (int) (ev.getRawY() - y);
				
				itemHeight=itemView.getHeight();


				// 解决问题3
				// 每次都销毁一次cache，重新生成一个bitmap
				itemView.destroyDrawingCache();
				itemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				// 建立item的缩略图
				startDrag(bm, x, y);
				return false;
			};
		});
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			return setOnItemLongClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void startDrag(Bitmap bm, int x, int y)
	{
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		System.out.println("X: " + x + " dragPointX: " + dragPointX
				+ " dragOffsetX: " + dragOffsetX);
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;// 这个必须加
		// 得到preview左上角相对于屏幕的坐标
		windowParams.x = x - dragPointX + dragOffsetX;
		windowParams.y = y - dragPointY + dragOffsetY;
		// L.l("==================windowParams.y==============="+windowParams.y);
		// 设置宽和高
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION)
		{
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction())
			{
				case MotionEvent.ACTION_MOVE:
					onDrag(x, y);
					break;
				case MotionEvent.ACTION_UP:
					stopDrag();
					onDrop(x, y);
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void onDrag(int x, int y)
	{
		if (dragImageView != null)
		{
			windowParams.alpha = 0.6f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			// L.l("=================windowParams.y=====000========"+windowParams.y);
			windowManager.updateViewLayout(dragImageView, windowParams);
		}

		int tempScrollX = x - dragPointX + dragOffsetX;
		int tempScrollY = y - dragPointY + dragOffsetY;

		if (tempScrollY +itemHeight> 600)
		{
			this.scrollTo(0, tempScrollY);
		}
		else
			if (pointToPosition(x, y) > 2)
			{

				this.scrollTo(0, tempScrollY - 300);
			}

	}

	private void onDrop(int x, int y)
	{
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != AdapterView.INVALID_POSITION)
		{
			dropPosition = tempPosition;
		}
		if (dropPosition != dragPosition)
		{
			System.out.println("dragPosition: " + dragPosition
					+ " dropPosition: " + dropPosition);
			GridViewAdapter adapter = (GridViewAdapter) getAdapter();

			Seats dragItem = (Seats) adapter.getItem(dragPosition);

			Builder builder = new AlertDialog.Builder(context);
			// builder.setTitle("填写就餐人数：（" + seats.getSeatName() + ")");
			// builder.setView(layout);
			builder.setMessage("您确定要把  [" + dragItem.getSeatName() + "] 菜单复制到"
					+ "[" + adapter.getList().get(dragPosition).getSeatName()
					+ "]吗？");
			builder.setPositiveButton("确定", null);
			builder.setNegativeButton("取消", null);
			builder.show();
			// 解决问题3
			/*
			 * ViewGroup itemView1 = (ViewGroup)getChildAt(dragPosition - getFirstVisiblePosition()); ViewGroup
			 * itemView2 = (ViewGroup)getChildAt(dropPosition - getFirstVisiblePosition());
			 * itemView1.destroyDrawingCache(); itemView2.destroyDrawingCache();
			 */
		}
	}

	private void stopDrag()
	{
		if (dragImageView != null)
		{
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

}

