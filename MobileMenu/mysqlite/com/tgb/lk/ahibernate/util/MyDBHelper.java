package com.tgb.lk.ahibernate.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
	private Class<?>[] modelClasses;
	private SQLiteDatabase db;

	public MyDBHelper(Context context, String databaseName,
			SQLiteDatabase.CursorFactory factory, int databaseVersion,
			Class<?>[] modelClasses) {
		super(context, databaseName, factory, databaseVersion);
		this.modelClasses = modelClasses;
	}

	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		TableHelper.createTablesByClasses(db, this.modelClasses);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TableHelper.dropTablesByClasses(db, this.modelClasses);
		onCreate(db);
	}

	public void addTable(java.lang.Class<?>[] classes) {
		TableHelper.createTablesByClasses(db, classes);
	}
}