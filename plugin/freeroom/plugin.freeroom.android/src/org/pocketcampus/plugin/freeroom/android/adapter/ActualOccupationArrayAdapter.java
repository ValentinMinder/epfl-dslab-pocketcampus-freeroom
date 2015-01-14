package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.FRTimesClient;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;
import org.pocketcampus.plugin.freeroom.shared.FRWhoIsWorkingRequest;

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
		ArrayAdapter<FRPeriodOccupation> {
	private Context context;
	private FRRoomOccupancy occupancy;
	private List<FRPeriodOccupation> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;
	private FreeRoomController mController;
	private FreeRoomHomeView homeView;

	public ActualOccupationArrayAdapter(Context c, FRRoomOccupancy occupancy,
			FreeRoomController mController, FreeRoomHomeView homeView) {
		super(c, R.layout.sdk_list_entry, R.id.sdk_list_entry_text, occupancy
				.getOccupancy());
		this.context = c;
		this.data = occupancy.getOccupancy();
		this.occupancy = occupancy;
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

		final FRPeriodOccupation mActualOccupation = data.get(index);
		TextView tv = vholder.getTextView();
		FRPeriod mFrPeriod = mActualOccupation.getPeriod();
		tv.setText(FRTimesClient.getInstance().formatTimePeriod(mFrPeriod,
				false, false));
		// displayed text is minimal: only hours
		// free/occupied: it's known by color
		// user occupancy: indication by the occupation image

		boolean free = mActualOccupation.isAvailable();

		final ImageView ivpeople = vholder.getImageViewPeople();
		if (free) {
			ivpeople.setImageResource(mModel
					.getImageFromRatioOccupation(mActualOccupation
							.getRatioOccupation()));
			ivpeople.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					homeView.shareDirectWithServer(
							mActualOccupation.getPeriod(), occupancy.getRoom());
				}
			});
		} else {
			ivpeople.setImageResource(R.drawable.freeroom_ic_occupation_occupied);
			ivpeople.setOnClickListener(null);
		}

		final ImageView ivshare = vholder.getImageViewShare();
		if (free) {
			ivshare.setImageResource(R.drawable.freeroom_ic_action_share_enabled);
			OnClickListener ocl = new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FRPeriod mPeriod = mActualOccupation.getPeriod();
					FRRoom mRoom = mModel.getDisplayedOccupancy().getRoom();
					homeView.shareDisplayDialog(mPeriod, mRoom);
				}
			};
			// share icon clickable
			ivshare.setOnClickListener(ocl);

			// TODO: do better!
			final String roomUID = occupancy.getRoom().getUid();
			// TODO: do better

			// TODO: asker whoisworking test to send to controller!
			OnClickListener ocl_line = new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FRWhoIsWorkingRequest req = new FRWhoIsWorkingRequest(roomUID,
							mActualOccupation.getPeriod());
					mController.prepareCheckWhoIsWorking(req);
					mController.checkWhoIsWorking(homeView);
					homeView.whoIsWorkingDisplayDialog(occupancy.getRoom(),
							mActualOccupation.getPeriod());
				}
			};

			if (mActualOccupation.isAvailable() && mActualOccupation.getRatioOccupation() > 0) {
				convertView.setOnClickListener(ocl_line);
				// green, and someone registered
				tv.setCompoundDrawablesWithIntrinsicBounds(
						mModel.getColoredDotDrawable(mActualOccupation), 0,
						R.drawable.freeroom_ic_action_next_item, 0);
			} else {
				// green, but nobody registered
				tv.setCompoundDrawablesWithIntrinsicBounds(
						mModel.getColoredDotDrawable(mActualOccupation), 0, 0,
						0);
				convertView.setOnClickListener(null);
			}

			// TODO: uncomment this
			// whole line clickable
			// convertView.setOnClickListener(ocl);
		} else {
			ivshare.setImageResource(R.drawable.freeroom_ic_action_share_disabled);
			// share icon non clickable
			ivshare.setOnClickListener(null);
			// whole line non clickable
			convertView.setOnClickListener(null);
			tv.setCompoundDrawablesWithIntrinsicBounds(
					mModel.getColoredDotDrawable(mActualOccupation), 0, 0, 0);
		}
		convertView.setBackgroundColor(mModel.getColorLine(mActualOccupation));

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
