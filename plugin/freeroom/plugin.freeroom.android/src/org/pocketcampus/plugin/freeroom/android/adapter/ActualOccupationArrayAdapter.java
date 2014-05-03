package org.pocketcampus.plugin.freeroom.android.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.views.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
	private FreeRoomController mController;
	private FreeRoomHomeView homeView;

	public ActualOccupationArrayAdapter(Context c, List<ActualOccupation> data,
			FreeRoomController mController, FreeRoomHomeView homeView) {
		super(c, R.layout.sdk_list_entry, R.id.sdk_list_entry_text, data);
		this.context = c;
		this.data = data;
		this.mController = mController;
		this.homeView = homeView;
		this.mModel = (FreeRoomModel) mController.getModel();
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_room_details, null);
			vholder = new ViewHolder();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_details_text));
			vholder.setImageViewShare((ImageView) convertView
					.findViewById(R.id.freeroom_layout_details_share));
			vholder.setImageViewPeople((ImageView) convertView
					.findViewById(R.id.freeroom_layout_details_people));
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
		tv.setText(s);
		// displayed text is minimal: only hours
		// free/occupied: it's known by color
		// user occupancy: indication by the occupation image

		boolean free = mActualOccupation.isAvailable();

		final ImageView ivpeople = vholder.getImageViewPeople();
		if (free) {
			ivpeople.setImageResource(mModel
					.getImageFromRatioOccupation(mActualOccupation
							.getRatioOccupation()));
		} else {
			ivpeople.setImageResource(R.drawable.occupation_occupied);
		}

		final ImageView ivshare = vholder.getImageViewShare();
		if (free) {
			ivshare.setImageResource(R.drawable.share);
			OnClickListener ocl = new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FRPeriod mPeriod = mActualOccupation.getPeriod();
					FRRoom mRoom = mModel.getDisplayedOccupancy().getRoom();
					homeView.showPopupShare(mPeriod, mRoom);
				}
			};
			ivshare.setOnClickListener(ocl);
			convertView.setOnClickListener(ocl);
		} else {
			ivshare.setImageResource(R.drawable.share_disabled);
		}

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
		private ImageView ivshare = null;
		private ImageView ivpeople = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}

		public void setImageViewShare(ImageView iv) {
			this.ivshare = iv;
		}

		public ImageView getImageViewShare() {
			return this.ivshare;
		}

		public void setImageViewPeople(ImageView iv) {
			this.ivpeople = iv;
		}

		public ImageView getImageViewPeople() {
			return this.ivpeople;
		}
	}

}
