package org.pocketcampus.plugin.freeroom.android.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * <code>ActualOccupationArrayAdapter</code> is a simple
 * <code>ArrayAdapter</code>, which only redefines getView in order to change
 * the display without changing the data.
 * 
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            The type of data is the lists.
 */

public class ActualOccupationArrayAdapter<T> extends
		ArrayAdapter<ActualOccupation> {
	private Context context;
	private List<ActualOccupation> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;

	public ActualOccupationArrayAdapter(Context c, List<ActualOccupation> data,
			FreeRoomModel model) {
		super(c, R.layout.sdk_list_entry, R.id.sdk_list_entry_text, data);
		this.context = c;
		this.data = data;
		this.mModel = model;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_roomslist, null);
			vholder = new ViewHolder();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_roomname));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolder) convertView.getTag();
		}

		final ActualOccupation mActualOccupation = data.get(index);
		TextView tv = vholder.getTextView();
		String s = "";
		FRPeriod mFrPeriod = mActualOccupation.getPeriod();
		Date start = new Date(mFrPeriod.getTimeStampStart());
		Date end = new Date(mFrPeriod.getTimeStampEnd());

		SimpleDateFormat sdf = new SimpleDateFormat(
				context.getString(R.string.freeroom_pattern_hour_format));
		s += sdf.format(start) + "-" + sdf.format(end) + " ";

		boolean free = mActualOccupation.isAvailable();
		// TODO: string
		s += free ? "free" : "occupied";

		if (free) {
			if (mActualOccupation.isSetProbableOccupation()) {
				s += mActualOccupation.getProbableOccupation() + " p.";
			}
			if (mActualOccupation.isSetRatioOccupation()) {
				s += "(" + mActualOccupation.getRatioOccupation() + " %)";
			}
		}

		tv.setText(s);

		int color = free ? mModel.COLOR_CHECK_OCCUPANCY_FREE
				: mModel.COLOR_CHECK_OCCUPANCY_OCCUPIED;
		convertView.setBackgroundColor(color);
		return convertView;
	}

	// TODO: verify that it's working correctly (right method)
	@Override
	public boolean isEnabled(int index) {
		return data.get(index).isAvailable();
	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolder {
		private TextView tv = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}
	}

}
