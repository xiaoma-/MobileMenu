/**
 * Program  : ViewPagerActivity.java
 * Author   : qianj
 * Create   : 2012-5-31 涓嬪�?:02:15
 */

package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.List;

import com.running.mobilemenu.R;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
/**
 * 
 */
public class ViewPagerActivity extends Activity {

	List<View> listViews;

	Context context = null;

	LocalActivityManager manager = null;

	TabHost tabHost = null;

	private ViewPager pager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.viewpager);
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setup(manager);
		
		context = ViewPagerActivity.this;
		
		pager  = (ViewPager) findViewById(R.id.viewpager);
		
		listViews = new ArrayList<View>();
		
		
//		Intent i1 = new Intent(context, T1Activity.class);
//		listViews.add(getView("T1Activity", i1));
//		Intent i2 = new Intent(context, T2Activity.class);
//		listViews.add(getView("T2Activity", i2));
//		Intent i3 = new Intent(context, T3Activity.class);
//		listViews.add(getView("T3Activity", i3));
		
		RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget, null);  
		TextView tvTab1 = (TextView)tabIndicator1.findViewById(R.id.tv_title);
		tvTab1.setText("Tab1");
		
		RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);  
		TextView tvTab2 = (TextView)tabIndicator2.findViewById(R.id.tv_title);
		tvTab2.setText("Tab2");
		
		RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);  
		TextView tvTab3 = (TextView)tabIndicator3.findViewById(R.id.tv_title);
		tvTab3.setText("Tab3");

//		tabHost.addTab(tabHost.newTabSpec("A").setIndicator(tabIndicator1).setContent());
//		tabHost.addTab(tabHost.newTabSpec("B").setIndicator(tabIndicator2).setContent());
//		tabHost.addTab(tabHost.newTabSpec("C").setIndicator(tabIndicator3).);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				 tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			            @Override
			            public void onTabChanged(String tabId) {
			                if ("A".equals(tabId)) {
			                    pager.setCurrentItem(0);
			                } 
			                if ("B".equals(tabId)) {
			                    pager.setCurrentItem(1);
			                } 
			                if ("C".equals(tabId)) {
			                    pager.setCurrentItem(2);
			                } 
			            }
			        });
				
			}
		});
		

		pager.setAdapter(new MyPageAdapter(listViews));
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tabHost.setCurrentTab(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	

	}

	private View getView(String id, Intent intent) {
		Log.d("EyeAndroid", "getView() called! id = " + id);
		return manager.startActivity(id, intent).getDecorView();
	}

	private class MyPageAdapter extends PagerAdapter {
		
		private List<View> list;

		private MyPageAdapter(List<View> list) {
			this.list = list;
		}

		@Override
        public void destroyItem(ViewGroup view, int position, Object arg2) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

	}

}
