package com.running.mobilemenu.dao.impl;

import android.content.Context;

import com.running.mobilemenu.model.FoodImage;
import com.running.mobilemenu.utils.DBHelper;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;


public class FoodImageDaoImpl extends BaseDaoImpl<FoodImage>
{
	public FoodImageDaoImpl(Context context) {
		super(new DBHelper(context),FoodImage.class);
	}
}
