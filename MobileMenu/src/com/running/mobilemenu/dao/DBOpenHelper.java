package com.running.mobilemenu.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.running.mobilemenu.model.MyOrder;

public class DBOpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "db_seals";
	public static final String TABLE_ORDER = "orders";
	public static final String TABLE_FOODS = "foods";
	public static final String TABLE_SEATS = "seats";
	private static final int DB_VERSION = 1;
	private static final String CREATE_TABLE_ORDER = "create table " + TABLE_ORDER + " (" + MyOrder._ID + " integer primary key," + MyOrder.STATE + " text)";
	private static final String CREATE_TABLE_FOODS = "create table " + TABLE_FOODS + " (sid integer primary key,name varchar,price varchar,c_price varchar)";
	private static final String CREATE_TABLE_SEATS = "create table " + TABLE_SEATS + " (" + MyOrder._ID + " integer primary key," + MyOrder.STATE + " text)";
	private static final String DROP_TABLE_ORDER = "drop table if exists " + TABLE_ORDER;
	private static final String DROP_TABLE_FOOD = "drop table if exists " + TABLE_FOODS;
	private static final String DROP_TABLE_SEATS = "drop table if exists " + TABLE_SEATS;

	public DBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public  void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			db.execSQL(CREATE_TABLE_FOODS);
		} catch (SQLException e) {
			// TODO: handle exception
			Log.v("aaaa", "aaaa->Database created failed.");
		}
	}

	@Override
	public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.v("aaaa", "aaaa->onUpgrade, oldVersion = " + oldVersion
				+ ", newVersion = " + newVersion);
		try {
			db.execSQL(CREATE_TABLE_FOODS);
		} catch (SQLException e) {
			// TODO: handle exception
			Log.v("aaaa", "aaaa->Database created failed.");
		}
		onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {

		super.onOpen(db);
		// TODO 每次成功打开数据库后首先被执行
	}
}