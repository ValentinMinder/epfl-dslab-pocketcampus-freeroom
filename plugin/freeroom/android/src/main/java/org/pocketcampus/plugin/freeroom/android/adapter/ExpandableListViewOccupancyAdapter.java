package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;
import org.pocketcampus.plugin.freeroom.shared.FRWhoIsWorkingRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * // TODO: NEW INTERFACE as of 2014.04.04
 * <p>
 * Replaces ExpandableAbstractListViewAdapter<T>,
 * ExpandableSimpleListViewAdapter<T> and, ExpandableListViewFavoriteAdapter<T>
 * <p>
 * 
 * <code>ExpandableListViewOccupancyAdapter<T></code> is a simple adapter to use
 * with <code>ExpandableListView</code>.
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

public class ExpandableListViewOccupancyAdapter<T> extends BaseExpandableListAdapter {
	private Context context;
	private OrderMapListFew<String, List<?>, FRRoomOccupancy> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;
	private FreeRoomController mController;
	private FreeRoomHomeView homeView;
	/**
	 * Stores the group index for which it should be highlighted.
	 */
	private int focusedGroup = -1;

	public ExpandableListViewOccupancyAdapter(Context c, OrderMapListFew<String, List<?>, FRRoomOccupancy> data,
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
		FRRoomOccupancy occ = data.getChild(groupPosition, childPosition);
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
	public FRRoomOccupancy getChildObject(int groupPosition, int childPosition) {
		return data.getChild(groupPosition, childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			final ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderChild vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.freeroom_layout_room_home, null);
			vholder = new ViewHolderChild();
			vholder.setTextView((TextView) convertView.findViewById(R.id.freeroom_layout_roomslist_roomname));
			vholder.setImageViewStar((ImageView) convertView.findViewById(R.id.freeroom_layout_roomslist_fav));
			vholder.setImageViewShare((ImageView) convertView.findViewById(R.id.freeroom_layout_roomslist_share));
			vholder.setImageViewPeople((ImageView) convertView.findViewById(R.id.freeroom_layout_roomslist_people));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderChild) convertView.getTag();
		}

		final FRRoomOccupancy occupancy = data.getChild(groupPosition, childPosition);

		final FRRoom mRoom = occupancy.getRoom();
		final String doorCode = mRoom.getDoorCode();
		TextView tv = vholder.getTextView();
		if (mRoom.isSetDoorCodeAlias()) {
			tv.setText(mRoom.getDoorCodeAlias());
		} else {
			tv.setText(doorCode);
		}

		final ImageView star = vholder.getImageViewStar();
		final ImageView share = vholder.getImageViewShare();
		final ImageView people = vholder.getImageViewPeople();

		final String uid = occupancy.getRoom().getUid();
		final boolean isFav = mModel.isFavorite(mRoom);

		if (isFav) {
			star.setImageResource(R.drawable.freeroom_ic_action_favorite_enabled);
		} else {
			star.setImageResource(R.drawable.freeroom_ic_action_favorite_disabled);
		}

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFav) {
					star.setImageResource(android.R.drawable.star_big_off);
					mModel.removeFavorite(mRoom);
				} else {
					star.setImageResource(android.R.drawable.star_big_on);
					mModel.addFavorite(mRoom);
				}
				notifyDataSetChanged();
			}
		});

		// only display if necessary (if it's only free)
		homeView.shareSetClickListener(share, homeView, occupancy);

		// direct share to the server by pressing the +1 button.
		OnClickListener onClickDirectServerShare = new OnClickListener() {
			@Override
			public void onClick(View v) {
				homeView.shareDirectWithServer(occupancy.getTreatedPeriod(), mRoom);
			}
		};

		if (occupancy.isIsFreeAtLeastOnce()) {
			people.setImageResource(mModel.getImageFromRatioOccupation(occupancy.getRatioWorstCaseProbableOccupancy()));
			people.setOnClickListener(onClickDirectServerShare);
		} else if (occupancy.isIsOccupiedAtLeastOnce()) {
			people.setImageResource(R.drawable.freeroom_ic_occupation_occupied);
			people.setOnClickListener(null);
		} else {
			people.setImageResource(R.drawable.freeroom_ic_occupation_unknown);
			people.setOnClickListener(null);
		}

		OnClickListener onClickOpenDetails = new OnClickListener() {
			@Override
			public void onClick(View v) {
				mModel.setDisplayedOccupancy(occupancy);
				homeView.infoDetailsDisplayDialog();
			}
		};
		tv.setOnClickListener(onClickOpenDetails);

		// TODO: asker whoisworking test to send to controller!
		OnClickListener ocl_checkWorking = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FRWhoIsWorkingRequest req = new FRWhoIsWorkingRequest(uid, occupancy.getTreatedPeriod());
				mController.prepareCheckWhoIsWorking(req);
				mController.checkWhoIsWorking(homeView);
			}
		};
		// if you want people image to check who is working for a longer period,
		// uncomment this
		// people.setOnClickListener(ocl_checkWorking);
		convertView.setBackgroundColor(mModel.getColorLine(occupancy));
		tv.setCompoundDrawablesWithIntrinsicBounds(mModel.getColoredDotDrawable(occupancy), 0, 0, 0);

		vholder.setImageViewPeople(people);
		vholder.setImageViewShare(share);
		vholder.setImageViewStar(star);
		vholder.setTextView(tv);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.getChildCount(groupPosition);
	}

	public int getChildrenTotalCount() {
		return data.totalChild();
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
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderGroup vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.freeroom_layout_building_header, null);
			vholder = new ViewHolderGroup();
			vholder.setTextView((TextView) convertView.findViewById(R.id.freeroom_layout_building_header_title));
			vholder.setImageView((ImageView) convertView.findViewById(R.id.freeroom_layout_building_header_show_more));
			vholder.setTextViewMore((TextView) convertView
					.findViewById(R.id.freeroom_layout_building_header_show_more_txt));
			vholder.setRelativeLayoutMore((RelativeLayout) convertView
					.findViewById(R.id.freeroom_layout_building_header_show_more_header));
			vholder.setLinearLayoutLeft((LinearLayout) convertView
					.findViewById(R.id.freeroom_layout_building_header_title_wrapper));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderGroup) convertView.getTag();
		}

		String text = data.getKey(groupPosition);
		TextView tv = vholder.getTextView();
		tv.setText(text);

		final TextView more = vholder.getTextViewMore();
		final ImageView iv = vholder.getImageView();
		final RelativeLayout rel = vholder.getRelativeLayoutMore();
		final ExpandableListView v = ((ExpandableListView) parent);
		final ExpandableListViewOccupancyAdapter<T> adapter = this;

		updateClick(more, iv, v, groupPosition);

		OnClickListener clickLongList = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!v.isGroupExpanded(groupPosition)) {
					v.expandGroup(groupPosition);
				}
				data.switchAvailable(groupPosition);
				if (data.getAvailable(groupPosition)) {
					setGroupFocus(groupPosition);
				} else {
					setGroupFocus(-1);
				}
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
			rel.setOnClickListener(clickLongList);
		} else {
			rel.setOnClickListener(clickShortList);
		}

		if (v.isGroupExpanded(groupPosition)) {
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			// if the group is highlighted.
			if (groupPosition == focusedGroup) {
				convertView.setBackgroundColor(mModel.getColorHighlight());
			} else {
				convertView.setBackgroundColor(mModel.getColorTransparent());
			}
		} else {
			// color of the first child, as it's the less occupied.
			// the get color method handles null values.
			convertView.setBackgroundColor(mModel.getColorLine(getChildObject(groupPosition, 0)));
			tv.setCompoundDrawablesWithIntrinsicBounds(mModel.getColoredDotDrawable(getChildObject(groupPosition, 0)),
					0, 0, 0);
		}

		vholder.setImageView(iv);
		vholder.setTextView(tv);
		vholder.setTextViewMore(more);

		return convertView;
	}

	/**
	 * When clicking on the headers (building name) on the arrow or more/reduce
	 * text, this method updates the text (more/reduce/number of rooms
	 * available) and the image (arrow down or up). It works also as an
	 * auto-reduce when collapsing.
	 * 
	 * @param more
	 *            TextView more/reduce
	 * @param iv
	 *            ImageView for up/down arrow
	 * @param ev
	 *            ExpandableListView of the whole ListView
	 * @param groupPosition
	 *            the index of the header we are interested in
	 */
	private void updateClick(TextView more, ImageView iv, ExpandableListView ev, int groupPosition) {
		// if the group is collapsed, then the rooms available must be reduced
		if (!ev.isGroupExpanded(groupPosition) && data.getAvailable(groupPosition)) {
			data.switchAvailable(groupPosition);
		}

		// if the group is NOT expanded or
		// if the group doesn't exceed the limit
		// we display the number of room available
		// and the arrows works for collapse/expend the listView
		if (!ev.isGroupExpanded(groupPosition) || !data.isOverLimit(groupPosition)) {
			int roomNumber = data.getChildCountTotal(groupPosition);
			more.setText(context.getResources().getQuantityString(R.plurals.freeroom_results_room_header, roomNumber,
					roomNumber));

			if (ev.isGroupExpanded(groupPosition)) {
				iv.setImageResource(R.drawable.freeroom_ic_action_collapse);
			} else {
				iv.setImageResource(R.drawable.freeroom_ic_action_expand);
			}

		} else {
			// else, if the group is expanded and reach the limit
			// if everything available, arrow up to reduce
			if (data.getAvailable(groupPosition)) {
				more.setText(context.getString(R.string.freeroom_results_room_header_reduce));
				iv.setImageResource(R.drawable.freeroom_ic_action_collapse);
			} else {
				// else, if not everything available, arrow down to see more
				more.setText(context.getString(R.string.freeroom_results_room_header_more) + ": "
						+ data.getChildCountNonAvailable(groupPosition));
				iv.setImageResource(R.drawable.freeroom_ic_action_expand);
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
	 * Change the value of {@link #focusedGroup}.
	 * <p>
	 * -1 or any other out of range value: no group highlighted. <br>
	 * only 1 group is highlighted at a time.
	 */
	public void setGroupFocus(int groupPosition) {
		focusedGroup = groupPosition;
	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolderChild {
		private TextView tv = null;
		private ImageView star = null;
		private ImageView share = null;
		private ImageView people = null;

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

		public ImageView getImageViewShare() {
			return share;
		}

		public void setImageViewShare(ImageView iv) {
			this.share = iv;
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
		private RelativeLayout moreRL = null;
		private LinearLayout linear = null;
		private View separator = null;

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

		public void setRelativeLayoutMore(RelativeLayout rl) {
			this.moreRL = rl;
		}

		public RelativeLayout getRelativeLayoutMore() {
			return this.moreRL;
		}

		public void setLinearLayoutLeft(LinearLayout ll) {
			this.linear = ll;
		}

		public void setViewSeparator(View separator) {
			this.separator = separator;
		}
	}
}