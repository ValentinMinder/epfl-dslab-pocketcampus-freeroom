package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.freeroom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableSimpleListViewAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> headers;
	private Map<String, List<String>> data;

	public ExpandableSimpleListViewAdapter(Context c, List<String> header,
			Map<String, List<String>> data) {
		this.context = c;
		this.headers = header;
		this.data = data;
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
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (groupPosition >= headers.size()) {
			return null;
		}

		String text = (String) this.getChild(groupPosition, childPosition);

		View v = LayoutInflater.from(context).inflate(R.layout.sdk_separated_list_separation_header, null);
		TextView tv = (TextView) v.findViewById(R.id.sdk_separated_list_header_title);
		tv.setText(text);
		return v;
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

		String text = (String) headers.get(groupPosition);
		
		View v = LayoutInflater.from(context).inflate(R.layout.sdk_separated_list_separation_header, null);
		TextView tv = (TextView) v.findViewById(R.id.sdk_separated_list_header_title);
		tv.setText(text);
		return v;
		
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
		this.data.put(value, data.remove(headers.get(id)));
		this.headers.set(id, value);
	}
	
}
