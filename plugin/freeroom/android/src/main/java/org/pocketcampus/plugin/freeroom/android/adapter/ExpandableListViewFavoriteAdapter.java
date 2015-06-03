package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomFavoritesActivity;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapList;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
 * <p>
 * Used by favorites :)
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class ExpandableListViewFavoriteAdapter<T> extends BaseExpandableListAdapter {

	FreeRoomFavoritesActivity home;
	protected Context context;
	protected OrderMapList<String, List<FRRoom>, FRRoom> data;
	// hold the caller view for colors updates.
	protected FreeRoomModel mModel;

	public ExpandableListViewFavoriteAdapter(Context c, OrderMapList<String, List<FRRoom>, FRRoom> data,
			FreeRoomModel model, FreeRoomFavoritesActivity home) {
		this.context = c;
		this.data = data;
		this.mModel = model;
		this.home = home;
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
	 * use {@link #getChild(int, int)} instead.
	 * 
	 * @param groupPosition
	 *            The group id
	 * @param childPosition
	 *            The child id
	 * @return The child object FRRoom associated.
	 */
	public FRRoom getChildObject(int groupPosition, int childPosition) {
		if (groupPosition >= data.size()) {
			return null;
		}
		List<FRRoom> groupList = data.get(groupPosition);

		if (childPosition >= groupList.size() || groupList == null) {
			return null;
		}
		return groupList.get(childPosition);
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderChild vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.freeroom_layout_room_favorites, null);
			vholder = new ViewHolderChild();
			vholder.setTextView((TextView) convertView.findViewById(R.id.freeroom_layout_favlist_text));
			vholder.setImageViewMap((ImageView) convertView.findViewById(R.id.freeroom_layout_favlist_map));
			vholder.setImageViewStar((ImageView) convertView.findViewById(R.id.freeroom_layout_favlist_fav));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderChild) convertView.getTag();
		}

		final FRRoom room = this.getChildObject(groupPosition, childPosition);

		TextView tv = vholder.getTextView();
		tv.setText(room.getDoorCode());

		final ImageView star = vholder.getImageViewStar();
		ImageView map = vholder.getImageViewMap();

		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri mUri = Uri.parse("pocketcampus://map.plugin.pocketcampus.org/search");
				Uri.Builder mbuild = mUri.buildUpon().appendQueryParameter("q", room.getDoorCode());
				Intent i = new Intent(Intent.ACTION_VIEW, mbuild.build());
				context.startActivity(i);

			}
		});

		final boolean isFav = mModel.isFavorite(room);

		if (isFav) {
			star.setImageResource(R.drawable.sdk_remove);
		} else {
			star.setImageResource(R.drawable.freeroom_ic_action_favorite_disabled);
		}

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFav) {
					star.setImageResource(android.R.drawable.star_big_off);
					mModel.removeFavorite(room);
				} else {
					star.setImageResource(android.R.drawable.star_big_on);
					mModel.addFavorite(room);
				}
				home.favoritesUpdateSummary();
				notifyDataSetChanged();
			}
		});
		vholder.setStarCheck(false);
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (groupPosition >= data.size()) {
			return null;
		}

		ViewHolderGroup vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.freeroom_layout_building_header_fav, null);
			vholder = new ViewHolderGroup();
			vholder.setTextView((TextView) convertView.findViewById(R.id.freeroom_layout_building_header_fav_title));
			vholder.setTextViewExpand((TextView) convertView
					.findViewById(R.id.freeroom_layout_building_header_fav_show_more_txt));
			vholder.setImageViewExpand((ImageView) convertView
					.findViewById(R.id.freeroom_layout_building_header_fav_show_more));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolderGroup) convertView.getTag();
		}

		List<FRRoom> list = data.get(groupPosition);
		String text = "unkwown";
		if (list != null && !list.isEmpty() && list.get(0) != null) {
			FRRoom room = list.get(0);
			if (room.isSetBuilding_name()) {
				text = room.getBuilding_name();
			} else {
				String code = room.getDoorCode();
				text = "";
				while (code.length() > 0 && (code.charAt(0) + "").matches("[A-Za-z]")) {
					text += code.charAt(0);
					code = code.substring(1);
				}
			}
		}
		TextView tv = vholder.getTextView();
		tv.setText(text);

		TextView tv_expand = vholder.getTextViewExpand();
		int size = data.get(text).size();
		tv_expand.setText(home.getResources().getQuantityString(R.plurals.freeroom_results_room_header, size, size));

		ImageView iv_expand = vholder.getImageViewExpand();
		if (isExpanded) {
			iv_expand.setImageResource(R.drawable.freeroom_ic_action_collapse);
		} else {
			iv_expand.setImageResource(R.drawable.freeroom_ic_action_expand);
		}

		return convertView;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition >= data.size()) {
			return 0;
		}
		List<?> list = data.get(groupPosition);
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition >= data.size()) {
			return null;
		}
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
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
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
		private TextView tv_expand = null;
		private ImageView iv_expand = null;

		public void setTextView(TextView tv) {
			this.tv = tv;
		}

		public TextView getTextView() {
			return this.tv;
		}

		public void setTextViewExpand(TextView tv) {
			this.tv_expand = tv;
		}

		public TextView getTextViewExpand() {
			return this.tv_expand;
		}

		public void setImageViewExpand(ImageView iv) {
			this.iv_expand = iv;
		}

		public ImageView getImageViewExpand() {
			return this.iv_expand;
		}

	}

}
