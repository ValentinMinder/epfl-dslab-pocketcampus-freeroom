package org.pocketcampus.plugin.freeroom.android.adapter;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public abstract class ExpandableAbstractListViewAdapter<T> extends
		BaseExpandableListAdapter {
	protected Context context;
	protected List<String> headers;
	protected Map<String, List<T>> data;
	// hold the caller view for colors updates.
	protected FreeRoomModel mModel;

	public ExpandableAbstractListViewAdapter(Context c, List<String> header,
			Map<String, List<T>> data, FreeRoomModel model) {
		this.context = c;
		this.headers = header;
		this.data = data;
		this.mModel = model;
	}
	
	public ExpandableAbstractListViewAdapter(Context c, List<String> header,
			Map<String, List<T>> data) {
		this.context = c;
		this.headers = header;
		this.data = data;
		this.mModel = null;
	}

	/**
	 * Return the corresponding child's doorCode, this method is intented for
	 * the display, thus should not return the door UID, if you want the object
	 * FRRoom, use getChildObject instead
	 */
	@Override
	abstract public Object getChild(int groupPosition, int childPosition);

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
	abstract public T getChildObject(int groupPosition, int childPosition);

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	abstract public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent);

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition >= data.size()) {
			return 0;
		}
		List<?> list = data.get(headers.get(groupPosition));
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
	abstract public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent);

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

}
