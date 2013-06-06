package com.running.mobilemenu.meals;

import java.util.List;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class OrderMainAdapter extends PagerAdapter {

	public List<View> mListViews;

	public OrderMainAdapter(List<View> mListViews) {
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
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mListViews.get(position));
		// 给每个item的view 就是刚才views存放着的view

		return mListViews.get(position);
	}

	

}
