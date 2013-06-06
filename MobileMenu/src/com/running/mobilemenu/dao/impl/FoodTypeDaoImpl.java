package com.running.mobilemenu.dao.impl;

import android.content.Context;

import com.running.mobilemenu.model.FoodType;
import com.running.mobilemenu.utils.DBHelper;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;


public class FoodTypeDaoImpl extends BaseDaoImpl<FoodType>
{
	public FoodTypeDaoImpl(Context context) {
		super(new DBHelper(context),FoodType.class);
	}
}
