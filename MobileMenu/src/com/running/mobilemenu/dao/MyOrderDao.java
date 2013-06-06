package com.running.mobilemenu.dao;

import java.util.ArrayList;
import java.util.List;

import com.running.mobilemenu.model.MyOrder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class MyOrderDao {
	private static final String[] COLS = new String[] { MyOrder._ID, MyOrder.STATE };
	private SQLiteDatabase db;
	private DBOpenHelper dbOpenHelper;
	public MyOrderDao(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		// Log.v("aaaa", "aaaa->dbOpenHelper = " + dbOpenHelper);
		establishDb();
	}

	private void establishDb() {
		if (this.db == null) {
			this.db = this.dbOpenHelper.getWritableDatabase();
		}
		// Log.v("aaaa", "aaaa->db = " + db);
	}

	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}

	public long Insert(MyOrder myOrder) {
		ContentValues values = new ContentValues();
		values.put(MyOrder._ID, myOrder.getOrderSid());
		values.put(MyOrder.STATE, myOrder.getOrderState());
		return this.db.insert(DBOpenHelper.TABLE_ORDER, null, values);
	}

	public long update(MyOrder myOrder) {
		ContentValues values = new ContentValues();
		
		values.put(MyOrder.STATE, myOrder.getOrderState());
		return this.db.update(DBOpenHelper.TABLE_ORDER, values, MyOrder._ID + "=" + myOrder.getOrderSid(), null);
	}

	public int delete(long id) {
		return this.db.delete(DBOpenHelper.TABLE_ORDER, MyOrder._ID + "=" + id,	null);
	}


	public MyOrder queryByID(long id) {
		Cursor cursor = null;
		MyOrder myOrder = null;
		try {
			cursor = this.db.query(DBOpenHelper.TABLE_ORDER, COLS, MyOrder._ID + "=" + id, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				myOrder = new MyOrder();
				
				myOrder.setOrderSid(Integer.parseInt(cursor.getString(0)));
				myOrder.setOrderState(cursor.getString(1));
				
			}
		} catch (SQLException e) {
			Log.v("aaaa", "aaaa->queryByID.SQLException");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return myOrder;
	}

	public List<MyOrder> queryByTitleForList(String title) {
		ArrayList<MyOrder> list = new ArrayList<MyOrder>();
		Cursor cursor = null;
		MyOrder myOrder = null;
		try {
			cursor = this.db.query(true, DBOpenHelper.TABLE_ORDER, COLS,null, null, null, null,	null, null);
			int count = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				myOrder = new MyOrder();
				myOrder.setOrderSid(Integer.parseInt(cursor.getString(0)));
				myOrder.setOrderState(cursor.getString(1));
				list.add(myOrder);
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e("aaaa", "aaaa->queryByTitle.SQLException");
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}
		return list;
	}

	public List<MyOrder> queryAllForList() {
		ArrayList<MyOrder> list = new ArrayList<MyOrder>();
		Cursor cursor = null;
		MyOrder myOrder = null;
		try {
			cursor = this.db.query(DBOpenHelper.TABLE_ORDER, COLS, null, null,
					null, null, null);
			int count = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				myOrder = new MyOrder();
				myOrder.setOrderSid(Integer.parseInt(cursor.getString(0)));
				myOrder.setOrderState(cursor.getString(1));
				list.add(myOrder);
				cursor.moveToNext();
			}
		} catch (SQLException e) {
			Log.e("aaaa", "aaaa->queryByTitle.SQLException");
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
		}
		return list;
	}

	public Cursor queryByTitleForCursor(String title) {
		Cursor cursor = null;
		try {
			cursor = this.db.query(DBOpenHelper.TABLE_ORDER, COLS, null, null,
					null, null, null);
		} catch (SQLException e) {
			Log.e("aaaa", "aaaa->queryByTitle.SQLException");
			e.printStackTrace();
		} finally {
			// if(cursor != null && !cursor.isClosed())
			// cursor.close();
		}
		return cursor;
	}

	public Cursor queryAllForCursor() {
		Cursor cursor = null;
		try {
			cursor = this.db.query(DBOpenHelper.TABLE_ORDER, COLS, null, null,
					null, null, null);
		} catch (SQLException e) {
			Log.e("aaaa", "aaaa->queryByTitle.SQLException");
			e.printStackTrace();
		}
		return cursor;
	}

}
