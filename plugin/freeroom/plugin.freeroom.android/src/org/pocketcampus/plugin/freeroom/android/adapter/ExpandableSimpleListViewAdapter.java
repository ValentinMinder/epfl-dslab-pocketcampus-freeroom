package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * //TODO: NOT USED as of 2014.04.04, REPLACED BY ExpandableListViewAdapter
 * <p>
 * NOT USED, but need refactoring
 * <p>
 * Simple adapter to use with ExpandableListView, all elements are String.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class ExpandableSimpleListViewAdapter extends
		ExpandableAbstractListViewAdapter<String> {

	public ExpandableSimpleListViewAdapter(Context c, List<String> header,
			Map<String, List<String>> data, FreeRoomModel model) {
		super(c, header, data, model);
	}

	public ExpandableSimpleListViewAdapter(Context c, List<String> header,
			Map<String, List<String>> data) {
		super(c, header, data);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (groupPosition >= headers.size()) {
			return null;
		}

		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.sdk_separated_list_separation_header, null);
			vholder = new ViewHolder();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.sdk_separated_list_header_title));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolder) convertView.getTag();
		}

		String text = (String) this.getChild(groupPosition, childPosition);

		TextView tv = vholder.getTextView();
		tv.setText(text);
		if (mModel != null) {
			// convertView.setBackgroundColor(mModel.getColorOfCheckOccupancyRoom(
			// groupPosition, childPosition));
		}
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupPosition >= headers.size()) {
			return null;
		}

		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.sdk_separated_list_separation_header, null);
			vholder = new ViewHolder();
			vholder.setTextView((TextView) convertView
					.findViewById(R.id.sdk_separated_list_header_title));
			convertView.setTag(vholder);
		} else {
			vholder = (ViewHolder) convertView.getTag();
		}

		String text = (String) headers.get(groupPosition);
		TextView tv = vholder.getTextView();
		tv.setText(text);
		// The color is set only when it's not expanded as a summary of the
		// content
		// Occupied, Free or Free/Occupied
		// Otherwise it's defaut color
		if (mModel != null) {
			ExpandableListView v = ((ExpandableListView) parent);
			if (v.isGroupExpanded(groupPosition)) {
				convertView.setBackgroundColor(mModel.getColorTransparent());
			} else {
				// convertView.setBackgroundColor(mModel
				// .getColorOfCheckOccupancyRoom(groupPosition));
			}
		}
		return convertView;

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

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groupPosition >= headers.size()) {
			return null;
		}
		List<String> groupList = data.get(headers.get(groupPosition));

		if (childPosition >= groupList.size()) {
			return null;
		}
		return data.get(headers.get(groupPosition)).get(childPosition);
	}

	@Override
	public String getChildObject(int groupPosition, int childPosition) {
		return (String) getChild(groupPosition, childPosition);
	}
}
