package com.running.mobilemenu.meals;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MainAdapter extends PagerAdapter {

	public List<View> mListViews;

	public MainAdapter() {
	}
    public void putList(List<View> mListViews){
    	this.mListViews = mListViews;
    }
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mListViews.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;
	}

	/**
	 * 跳转到每个页面都要执行的方法
	 */

	public void setPrimaryItem(View view, int position, Object obj) {
//		Food food = listFood.get(position);
//		Food food2 = listFood2.get(position);
//		((TextView) views.get(position).findViewById(R.id.food_name))
//				.setText(food.getName());
//		// ((TextView)v.findViewById(R.id.food_price)).setText("价格"+i);
//		((TextView) views.get(position).findViewById(R.id.food_name2))
//				.setText(food2.getName());
//
//		ImageView imageView = ((ImageView) views.get(position)
//				.findViewById(R.id.food_shop_img));
//		ImageView imageView2 = ((ImageView) views.get(position)
//				.findViewById(R.id.food_shop_img2));
//
//		ImageView img = ((ImageView) views.get(position).findViewById(
//				R.id.index_food_1));
//		img.setImageResource(R.drawable.dx_1_1);
//		ImageView img2 = ((ImageView) views.get(position).findViewById(
//				R.id.index_food_2));
//		img2.setImageResource(R.drawable.dx_1_2);
//
//		String imageURL = MyApplication.httpUrl + food.getPictureSmall();
//		String imageURL2 = MyApplication.httpUrl + food2.getPictureSmall();
//
//		// 处理图片加载
//		Bitmap bitmap1 = getBitmap(img, imageURL);
//		if (bitmap1 == null) {
//			img.setImageResource(R.drawable.ic_launcher);
//		} else {
//			img.setImageBitmap(bitmap1);
//		}
//		Bitmap bitmap2 = getBitmap(img2, imageURL2);
//		if (bitmap2 == null) {
//			img2.setImageResource(R.drawable.ic_launcher);
//		} else {
//			img2.setImageBitmap(bitmap2);
//		}
//
//		imageView.setOnClickListener(new MyOnClickListener(food));
//		imageView2.setOnClickListener(new MyOnClickListener(food2));
//	};

//	// 处理图片加载
//	private Bitmap getBitmap(ImageView image, String imageURL) {
//		Bitmap bitmap = asyncBitmapLoader.loadBitmap(image, imageURL,
//				new ImageCallBack() {
//					@Override
//					public void imageLoad(ImageView imageView, Bitmap bitmap) {
//						imageView.setImageBitmap(bitmap);
//					}
//				});
	//	return bitmap;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mListViews.get(position));
		// 给每个item的view 就是刚才views存放着的view

		return mListViews.get(position);
	}

}
