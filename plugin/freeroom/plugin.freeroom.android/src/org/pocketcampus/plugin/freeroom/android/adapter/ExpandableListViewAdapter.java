package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapList;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

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
	private OrderMapList<String, List<?>, Occupancy> data;
	// hold the caller view for colors updates.
	private FreeRoomModel mModel;

	public ExpandableListViewAdapter(Context c,
			OrderMapList<String, List<?>, Occupancy> data, FreeRoomModel model) {
		this.context = c;
		this.data = data;
		this.mModel = model;
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
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
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
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderChild) convertView.getTag();
		}

		final Occupancy occupancy = data.getChild(groupPosition, childPosition);

		final String doorCode = occupancy.getRoom().getDoorCode();
		TextView tv = vholder.getTextView();
		tv.setText(doorCode);

		final ImageView star = vholder.getImageViewStar();
		ImageView map = vholder.getImageViewMap();

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
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderGroup vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_roomslist, null);
			vholder = new ViewHolderGroup();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_roomname));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderGroup) convertView.getTag();
		}

		String text = data.getKey(groupPosition);
		TextView tv = vholder.getTextView();
		tv.setText(text);

		ExpandableListView v = ((ExpandableListView) parent);
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

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO: let true, but dont submit imworking everytime!
		return true;
//		Occupancy mOccupancy = getChildObject(groupPosition, childPosition);
//		return mOccupancy.isIsAtLeastFreeOnce()
//				&& !mOccupancy.isIsAtLeastOccupiedOnce();
	}

	public void updateHeader(int id, String value) {
		data.updateKey(id, value);
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

	}

	/**
	 * Class used to keep a view, it saves ressources by avoiding multiple
	 * inflate and findViewById operations.
	 * 
	 */
	private class ViewHolderGroup {
		private TextView tv = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}

	}
}
