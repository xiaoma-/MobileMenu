package com.running.mobilemenu.dao.impl;

import android.content.Context;

import com.running.mobilemenu.model.Traffics;
import com.running.mobilemenu.utils.DBHelper;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;


public class TrafficsDaoImpl extends BaseDaoImpl< Traffics>
{
	public TrafficsDaoImpl(Context context) {
		super(new DBHelper(context), Traffics.class);
	}
}
