package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.android.views.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;
import org.pocketcampus.plugin.freeroom.shared.WorkingOccupancy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * // TODO: NEW INTERFACE as of 2014.04.04
 * <p>
 * Replaces ExpandableAbstractListViewAdapter<T>,
 * ExpandableSimpleListViewAdapter<T> and, ExpandableListViewFavoriteAdapter<T>
 * <p>
 * 
 * <code>ExpandableListViewAdapter<T></code> is a simple adapter to use with
 * <code>ExpandableListView</code>.
 * 
 * Data are stored in user-defined class <code>OrderMapList</code>. You can use
 * <code>OrderMapListFew</code> if you want to limit the number or elements
 * displayed.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 * @param <T>
 *            The type of data is the lists.
 */

public class ExpandableListViewAdapter<T> extends BaseExpandableListAdapter {
	private Context context;
	private OrderMapListFew<String, List<?>, Occupancy> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;
	private FreeRoomController mController;
	private FreeRoomHomeView homeView;

	public ExpandableListViewAdapter(Context c,
			OrderMapListFew<String, List<?>, Occupancy> data,
			FreeRoomController controller, FreeRoomHomeView homeView) {
		this.context = c;
		this.data = data;
		this.mController = controller;
		this.mModel = (FreeRoomModel) mController.getModel();
		this.homeView = homeView;
	}

	/**
	 * Return the corresponding child's doorCode, this method is intented for
	 * the display, thus should not return the door UID, if you want the object
	 * FRRoom, use getChildObject instead
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Occupancy occ = data.getChild(groupPosition, childPosition);
		return occ.getRoom().getDoorCode();
	}

	/**
	 * This method returns the child's object. It is not suitable for display,
	 * use getChild(int, int) instead.
	 * 
	 * @param groupPosition
	 *            The group id
	 * @param childPosition
	 *            The child id
	 * @return The child object FRRoom associated.
	 */
	public Occupancy getChildObject(int groupPosition, int childPosition) {
		return data.getChild(groupPosition, childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, final ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderChild vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_roomslist, null);
			vholder = new ViewHolderChild();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_roomname));
			vholder.setImageViewMap((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_map));
			vholder.setImageViewStar((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_fav));
			vholder.setImageViewInfo((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_info));
			vholder.setImageViewArrow((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_arrow));
			vholder.setImageViewPeople((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_people));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderChild) convertView.getTag();
		}

		final Occupancy occupancy = data.getChild(groupPosition, childPosition);

		final String doorCode = occupancy.getRoom().getDoorCode();
		TextView tv = vholder.getTextView();
		tv.setText(doorCode);

		final ImageView star = vholder.getImageViewStar();
		final ImageView map = vholder.getImageViewMap();
		final ImageView info = vholder.getImageViewInfo();
		final ImageView arrow = vholder.getImageViewArrow();
		final ImageView people = vholder.getImageViewPeople();

		map.setImageResource(R.drawable.map_normal_icon);

		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri mUri = Uri
						.parse("pocketcampus://map.plugin.pocketcampus.org/search");
				Uri.Builder mbuild = mUri.buildUpon().appendQueryParameter("q",
						doorCode);
				Intent i = new Intent(Intent.ACTION_VIEW, mbuild.build());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});

		final String uid = occupancy.getRoom().getUid();
		final boolean isFav = mModel.containRoomFavorites(uid);

		if (isFav) {
			star.setImageResource(android.R.drawable.star_big_on);
		} else {
			star.setImageResource(android.R.drawable.star_big_off);

		}

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFav) {
					star.setImageResource(android.R.drawable.star_big_off);
					mModel.removeRoomFavorites(uid);
				} else {
					star.setImageResource(android.R.drawable.star_big_on);
					mModel.addRoomFavorites(uid, doorCode);
				}
				notifyDataSetChanged();
			}
		});
		vholder.setStarCheck(false);

		info.setImageResource(R.drawable.information);
		info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mModel.setDisplayedOccupancy(occupancy);
				homeView.displayPopupInfo();
			}
		});

		// only display if necessary (if it's only free)
		if (!occupancy.isIsAtLeastOccupiedOnce()
				&& occupancy.isIsAtLeastFreeOnce()) {
			arrow.setImageResource(R.drawable.arrow);
			arrow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					List<ActualOccupation> list = occupancy.getOccupancy();
					long tss = list.get(0).getPeriod().getTimeStampStart();
					long tse = list.get(list.size() - 1).getPeriod()
							.getTimeStampEnd();
					FRPeriod mPeriod = new FRPeriod(tss, tse, false);
					WorkingOccupancy work = new WorkingOccupancy(mPeriod,
							occupancy.getRoom());
					ImWorkingRequest request = new ImWorkingRequest(work,
							mModel.getAnonymID());
					mController.prepareImWorking(request);
					mController.ImWorking(homeView);
				}
			});
		}

		people.setImageResource(mModel.getImageFromRatioOccupation(occupancy
				.getRatioWorstCaseProbableOccupancy()));
		people.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: should we do something ?
			}
		});

		convertView.setBackgroundColor(mModel.getColor(occupancy));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.getChildCount(groupPosition);
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderGroup vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_building_header, null);
			vholder = new ViewHolderGroup();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_building_header_title));
			vholder.setImageView((ImageView) convertView
					.findViewById(R.id.freeroom_layout_building_header_show_more));
			vholder.setTextViewMore((TextView) convertView
					.findViewById(R.id.freeroom_layout_building_header_show_more_txt));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderGroup) convertView.getTag();
		}

		String text = data.getKey(groupPosition);
		TextView tv = vholder.getTextView();
		tv.setText(text);

		final TextView more = vholder.getTextViewMore();

		final ImageView iv = vholder.getImageView();
		final ExpandableListView v = ((ExpandableListView) parent);
		final ExpandableListViewAdapter<T> adapter = this;

		updateClick(more, iv, v, groupPosition);

		OnClickListener clickLongList = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!v.isGroupExpanded(groupPosition)) {
					v.expandGroup(groupPosition);
				}
				data.switchAvailable(groupPosition);
				updateClick(more, iv, v, groupPosition);
				adapter.notifyDataSetChanged();
			}
		};

		OnClickListener clickShortList = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!v.isGroupExpanded(groupPosition)) {
					v.expandGroup(groupPosition);
				} else {
					v.collapseGroup(groupPosition);
				}
				data.switchAvailable(groupPosition);
				updateClick(more, iv, v, groupPosition);
				adapter.notifyDataSetChanged();
			}
		};
		if (data.isOverLimit(groupPosition)) {
			more.setOnClickListener(clickLongList);
			iv.setOnClickListener(clickLongList);
		} else {
			more.setOnClickListener(clickShortList);
			iv.setOnClickListener(clickShortList);
		}

		if (v.isGroupExpanded(groupPosition)) {
			convertView
					.setBackgroundColor(mModel.COLOR_CHECK_OCCUPANCY_DEFAULT);
		} else {
			// color of the first child, as it's the less occupied.
			// the get color method handles null values.
			convertView.setBackgroundColor(mModel.getColor(getChildObject(
					groupPosition, 0)));
		}
		return convertView;
	}

	private void updateClick(TextView more, ImageView iv,
			ExpandableListView ev, int groupPosition) {
		if (!ev.isGroupExpanded(groupPosition)
				|| !data.isOverLimit(groupPosition)) {
			int roomNumber = data.getChildCountTotal(groupPosition);
			String room_s = "";
			if (roomNumber > 1) {
				room_s = context
						.getString(R.string.freeroom_results_room_header_rooms);
			} else {
				room_s = context
						.getString(R.string.freeroom_results_room_header_room);
			}
			more.setText(roomNumber + " " + room_s);

			if (ev.isGroupExpanded(groupPosition)) {
				iv.setImageResource(R.drawable.arrow_up);
			} else {
				iv.setImageResource(R.drawable.arrow_down);
			}

		} else {
			if (data.getAvailable(groupPosition)) {
				more.setText(context
						.getString(R.string.freeroom_results_room_header_reduce));
				iv.setImageResource(R.drawable.arrow_up);
			} else {
				more.setText(context
						.getString(R.string.freeroom_results_room_header_more)
						+ ": " + data.getChildCountNonAvailable(groupPosition));
				iv.setImageResource(R.drawable.arrow_down);
			}
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO: let true, but dont submit imworking everytime!
		// false: this is not used anymore, child is not selectable
		return false;
		// Occupancy mOccupancy = getChildObject(groupPosition, childPosition);
		// return mOccupancy.isIsAtLeastFreeOnce()
		// && !mOccupancy.isIsAtLeastOccupiedOnce();
	}

	public void updateHeader(int id, String value) {
		data.updateKey(id, value);
	}

	/**
	 * Expands all the groups if there are no more than 3 groups or not more than 10 results.
	 * @param ev
	 */
	public void updateCollapse(ExpandableListView ev) {
		System.out.println("check: " + data.size() + "/" + data.totalChild());
		if (data.size() <= 3 || data.totalChild() <= 10) {
			System.out.println("i wanted to expand");
			// TODO: this cause troubles in performance! To delete if not found
			for (int i = 0; i < data.size(); i++) {
				ev.expandGroup(i);
			}
		}
	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolderChild {
		private TextView tv = null;
		private ImageView map = null;
		private ImageView star = null;
		private ImageView info = null;
		private ImageView arrow = null;
		private ImageView people = null;
		private boolean starChecked = false;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}

		public void setImageViewStar(ImageView iv) {
			this.star = iv;
		}

		public ImageView getImageViewStar() {
			return this.star;
		}

		public void setImageViewMap(ImageView iv) {
			this.map = iv;
		}

		public ImageView getImageViewMap() {
			return this.map;
		}

		public boolean isStarChecked() {
			return starChecked;
		}

		public void setStarCheck(boolean check) {
			starChecked = check;
		}

		public ImageView getImageViewInfo() {
			return info;
		}

		public void setImageViewInfo(ImageView iv) {
			this.info = iv;
		}

		public ImageView getImageViewArrow() {
			return arrow;
		}

		public void setImageViewArrow(ImageView iv) {
			this.arrow = iv;
		}

		public ImageView getImageViewPeople() {
			return people;
		}

		public void setImageViewPeople(ImageView iv) {
			this.people = iv;
		}
	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolderGroup {
		private TextView tv = null;
		private ImageView iv = null;
		private TextView more = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}

		public void setImageView(ImageView iv) {
			this.iv = iv;
		}

		public ImageView getImageView() {
			return this.iv;
		}

		public void setTextViewMore(TextView tv) {
			this.more = tv;
		}

		public TextView getTextViewMore() {
			return this.more;
		}

	}
}