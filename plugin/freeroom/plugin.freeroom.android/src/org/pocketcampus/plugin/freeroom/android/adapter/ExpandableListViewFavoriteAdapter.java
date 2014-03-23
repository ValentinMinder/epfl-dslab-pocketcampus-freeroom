package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple adapter to use with ExpandableListView, Headers are Strings, Childs
 * are FRRooms.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class ExpandableListViewFavoriteAdapter extends
		BaseExpandableListAdapter {

	private Context context;
	private List<String> headers;
	private Map<String, List<FRRoom>> data;
	private FreeRoomModel model;

	public ExpandableListViewFavoriteAdapter(Context c, List<String> header,
			Map<String, List<FRRoom>> data, FreeRoomModel model) {
		this.context = c;
		this.headers = header;
		this.data = data;
		this.model = model;
	}

	/**
	 * Return the corresponding child's doorCode, this method is intented for
	 * the display, thus should not return the door UID, if you want the object
	 * FRRoom, use getChildObject instead
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		FRRoom child = this.getChildObject(groupPosition, childPosition);
		if (child != null) {
			return child.getDoorCode();
		}
		return null;
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
	public FRRoom getChildObject(int groupPosition, int childPosition) {
		if (groupPosition >= headers.size()) {
			return null;
		}
		List<FRRoom> groupList = data.get(headers.get(groupPosition));

		if (childPosition >= groupList.size() || groupList == null) {
			return null;
		}
		return groupList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (groupPosition >= headers.size()) {
			return null;
		}

		ViewHolderChild vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.freeroom_layout_roomslist, null);
			vholder = new ViewHolderChild();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_roomname));
			vholder.setImageViewStar((ImageView) convertView
					.findViewById(R.id.freeroom_layout_roomslist_fav));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderChild) convertView.getTag();
		}

		final FRRoom room = this.getChildObject(groupPosition, childPosition);

		TextView tv = vholder.getTextView();
		tv.setText(room.getDoorCode());
		ImageView map = vholder.getImageViewMap();
		map.setImageResource(android.R.drawable.btn_plus);
		final ImageView star = vholder.getImageViewStar();

		final String isFav = model.getRoomFavorites(room.getUid());

		if (isFav != null) {
			star.setImageResource(android.R.drawable.star_big_on);
		} else {
			star.setImageResource(android.R.drawable.star_big_off);

		}

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFav != null) {
					star.setImageResource(android.R.drawable.star_big_off);
					model.removeRoomFavorites(room.getUid());
				} else {
					star.setImageResource(android.R.drawable.star_big_on);
					model.addRoomFavorites(room.getUid(), room.getDoorCode());
				}
				notifyDataSetChanged();
			}
		});
		vholder.setStarCheck(false);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition >= data.size()) {
			return 0;
		}

		return data.get(headers.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition >= data.size()) {
			return null;
		}
		return data.get(headers.get(groupPosition));
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
		if (groupPosition >= headers.size()) {
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

		String text = (String) headers.get(groupPosition);
		TextView tv = vholder.getTextView();
		tv.setText(text);
		return convertView;

	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void updateHeader(int id, String value) {
		if (id >= headers.size()) {
			return;
		}

		this.data.put(value, data.remove(headers.get(id)));
		this.headers.set(id, value);
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

		public boolean isStarChecked() {
			return starChecked;
		}

		public void setStarCheck(boolean check) {
			starChecked = check;
		}

		public void setImageViewMap(ImageView iv) {
			this.map = iv;
		}

		public ImageView getImageViewMap() {
			return this.map;
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
