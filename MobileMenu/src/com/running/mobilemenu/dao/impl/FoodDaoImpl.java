package com.running.mobilemenu.dao.impl;

import android.content.Context;

import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.utils.DBHelper;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;


public class FoodDaoImpl extends BaseDaoImpl<Food>
{
	public FoodDaoImpl(Context context) {
		super(new DBHelper(context),Food.class);
	}
}
