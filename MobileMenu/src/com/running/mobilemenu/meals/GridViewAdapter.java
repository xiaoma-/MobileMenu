package com.running.mobilemenu.meals;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.running.mobilemenu.R;
import com.running.mobilemenu.model.Seats;
import com.running.mobilemenu.utils.MyApplication;

public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private List<Seats> seats;
	private LayoutInflater mInflater;

	public GridViewAdapter(Context context, List<Seats> seats) {
		this.context = context;
		this.seats = seats;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
    public List<Seats> getList(){
    	return seats;
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
		GridHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.opentab_item_gridview,
					null);
			holder = new GridHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.opentab_icn);

			holder.name = (TextView) convertView.findViewById(R.id.opentab_num);
			holder.rlayour = (RelativeLayout) convertView
					.findViewById(R.id.opentab_layout);
			convertView.setTag(holder);

		} else {
			holder = (GridHolder) convertView.getTag();
		}
		Seats seat = seats.get(position);
		if (null != seat) {
			holder.name.setText(seat.getSeatName());
			if("300".equals(seat.getStatus())){
				holder.imageView.setImageResource(R.drawable.yes);
			}else if("200".equals(seat.getStatus())){
				System.out.println("======="+seat.getStatus());
				holder.imageView.setImageResource(R.drawable.ok);
			}else{
				holder.imageView.setImageResource(R.drawable.no);
			}
		}
		//holder.rlayour.setOnClickListener(new MyOnClickListener(seat));
		return convertView;
	}

	class MyOnClickListener extends Activity implements OnClickListener {
		private Seats seat;

		public MyOnClickListener(Seats seats) {
			this.seat = seats;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
             MyApplication.listSeats.add(seat);
		}

	}

	static class GridHolder {
		public ImageView imageView;
		public TextView goneNum;
		public TextView name;
		public RelativeLayout rlayour;
	}
}
