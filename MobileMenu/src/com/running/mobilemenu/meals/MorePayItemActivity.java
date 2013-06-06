package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.running.mobilemenu.R;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className MorePayItemActivity
 * @author xiaoma
 * @Description 病态结账，每台菜品详情及价格。
 * @date 2013-3-29 上午9:42:46
 */
public class MorePayItemActivity extends BaseActivity {
	private List<Seats> listSeats = new ArrayList<Seats>();
	private String jsonString;
	public Map<String, Object> jsonMap;
	private String price;
	private String cradPrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		listSeats = (List<Seats>) this.getIntent().getSerializableExtra(
				"seatsLists");
		jsonString = this.getIntent().getStringExtra("jsonString");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.morepay_item_activity);
		getLists();
	}

	public void getLists() {
		TextView titlePrice = (TextView) findViewById(R.id.more_pay_total);
		TextView titleCardPrice = (TextView) findViewById(R.id.more_pay_total_crad);
		jsonMap = JSON.parseObject(jsonString);
		if (null != jsonMap) {
			price = jsonMap.get("sumPrice").toString();
			titlePrice.setText("￥"+price);
			cradPrice = jsonMap.get("cardPrice").toString();
			titleCardPrice.setText("￥"+cradPrice);
			for (Seats seat : listSeats) {
				if (jsonMap.containsKey(seat.getSid())) {
					List<Map<String, Object>> listmaps = ServiceHttpPost
							.GetListMaps(jsonMap.get(seat.getSid()).toString());
					if (null != listmaps) {
						upView(seat, listmaps);
					}
				}
			}

		}
	}
     
	/**
	 * @Title: upView 
	 * @Description: 代码创建循环创建空间，显示订台数据。 
	 * @param @param seats
	 * @param @param lists    
	 * @return void    
	 * @throws 
	 */
	public void upView(Seats seats, List<Map<String, Object>> lists) {
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.more_l_id);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(200,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp1.setMargins(5, 0, 0, 0);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(100,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp2.setMargins(0, 0, 20, 0);
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tv3 = new TextView(this);
		tv3.setText(seats.getSeatName() + "("
				+ jsonMap.get(seats.getSid() + "peopleNum").toString() + "人)");
		tv3.setTextSize(18);
		tv3.setTextColor(Color.BLACK);
		mLayout.addView(tv3, lp3);
		for (int i = 0; i < lists.size(); i++) {
			RelativeLayout r = new RelativeLayout(this);
			TextView tv = new TextView(this);
			if (!"".equals(lists.get(i).get("foodName").toString()))
				tv.setText(lists.get(i).get("foodName").toString() + " x"
						+ lists.get(i).get("foodNum"));
			tv.setTextColor(Color.BLACK);
			r.addView(tv, lp1);
			TextView tv2 = new TextView(this);
			if (!"".equals(lists.get(i).get("sumPrice").toString()))
				tv2.setText("￥"
						+ Float.valueOf(lists.get(i).get("sumPrice").toString())
								.toString());
			tv2.setTextColor(Color.BLACK);
			r.addView(tv2, lp2);
			mLayout.addView(r);
		}
	}

	public void payMoreBack(View view) {
		Intent intent = new Intent(MorePayItemActivity.this,
				MorePayActivity.class);
		MorePayItemActivity.this.finish();
	}

	public void goMorePay(View view) {
		Toast.makeText(this, "请前往收银台进行结账！欢迎下次再来！", Toast.LENGTH_SHORT).show();
		Thread thread = new Thread() {
			public void run() {
				try {
					String sid = "";
					for (Seats seats : listSeats) {
						sid += seats.getSid() + ",";
					}
					sid = sid.substring(0, sid.lastIndexOf(","));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "sendPaysSeats");
					map.put("clientId", MyApplication.clientId);
					map.put("userName", MyApplication.userName);// 当前用户
					map.put("seatsId", sid);// 结账台位
					ServiceHttpPost.GetMessageString(map);

				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}

			}
		};
		thread.start();
	}
}
