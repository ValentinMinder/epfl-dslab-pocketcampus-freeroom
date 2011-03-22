package org.pocketcampus.plugin.social;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import org.pocketcampus.R;

public class ListSeparator extends BaseAdapter {

	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;

	public ListSeparator(Context context) {
		// For the headers, we use the list_header.xml file
		headers = new ArrayAdapter<String>(context,
				R.layout.social_list_header);
	}

	/**
	 * Method to add the name of the category in the header and the content of the
	 * adapter in the section
	 * @param section
	 * 			the string title of the section to add
	 * @param adapter
	 * 			the adapter of the list view content
	 */
	public void addSection(String section, Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);
	}
	
	/**
	 * Method to remove sections from the list view.
	 */
	public void removeSections(){
		this.headers.clear();
		this.sections.clear();
	}

	/**
	 * Returns the position of a click
	 */
	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// Gets the position inside the section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);

			// Goes to the next section
			position -= size;
		}
		return null;
	}

	/**
	 * Returns the number of items.
	 */
	public int getCount() {
		// Total over all the sections + 1 for the header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		int total = 1;
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// Get position inside the section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// Goes to the next section -1 for the header
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// Gets the position in the section
			if (position == 0)
				return headers.getView(sectionnum, convertView, parent);
			if (position < size)
				return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
}