package com.running.mobilemenu.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodImage;
import com.running.mobilemenu.model.FoodType;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.model.Traffics;
import com.running.mobilemenu.model.User;
import com.tgb.lk.ahibernate.util.MyDBHelper;

public class DBHelper extends MyDBHelper {
	private static final String DBNAME = "mobileMenu.db";// 数据库名
	private static final int DBVERSION = 4;
	private static final Class<?>[] clazz = { Food.class, FoodType.class,
			FoodImage.class, User.class,Traffics.class };// 要初始化的表

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}


	// @Override

}
