package com.running.mobilemenu.meals;

import java.util.ArrayList;
import java.util.List;

import com.running.mobilemenu.R;
import com.running.mobilemenu.meals.GridViewAdapter.GridHolder;
import com.running.mobilemenu.model.Seats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreGridTwoAdapter extends BaseAdapter {
	private Context context;
	private List<Seats> seats = new ArrayList<Seats>();
	private LayoutInflater mInflater;

	public MoreGridTwoAdapter(Context context) {
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	public void setList(List<Seats> seats) {
		this.seats = seats;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return seats.size();
	}

	@Override
	public Object getItem(int index) {

		return seats.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.opentab_item_gridview,
					null);
			holder = new GridHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.opentab_icn);

			holder.name = (TextView) convertView.findViewById(R.id.opentab_num);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();
		}
		Seats seat = seats.get(position);
		if (null != seat) {
			holder.name.setText(seat.getSeatName());
			holder.imageView.setImageResource(R.drawable.yes);
		}
		// holder.rlayour.setOnClickListener(new MyOnClickListener(seat));
		return convertView;
	}

	static class GridHolder {
		public ImageView imageView;
		public TextView name;
	}

}
