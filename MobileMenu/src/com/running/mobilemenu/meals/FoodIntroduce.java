package com.running.mobilemenu.meals;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.running.mobilemenu.R;
import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.utils.BaseActivity;




/**
 * @className FoodIntroduce
 * @author xiaoma
 * @Description 显示每个菜品的详细信息
 * @date 2013-3-18 下午4:20:05
 */
public class FoodIntroduce extends BaseActivity {
	private Food food;
	private String notice;
	private String marking;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		food = (Food) this.getIntent().getSerializableExtra("food");
		notice =  this.getIntent().getStringExtra("notice");
		marking =  this.getIntent().getStringExtra("makingPro");
		setContentView(R.layout.food_introduce);
		upViews();
		
	}
	public void upViews(){
		TextView pl = (TextView)findViewById(R.id.food_introduce_pl);
		TextView js = (TextView)findViewById(R.id.food_introduce_js);
		pl.setText(notice);
		js.setText(marking);
	}
	public void introduceBack(View view){
		FoodIntroduce.this.finish();
	}
}
