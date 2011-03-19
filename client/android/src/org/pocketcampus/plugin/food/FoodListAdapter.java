/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]    see "licence"-file in the root directory
 * [   MAINTAINER  ]    ElodieNilane.Triponez@epfl.ch
 * [     STATUS    ]    Usable
 *
 **************************[ C O M M E N T S ]**********************
 *
 * A class to make sections in a listview.  
 *                      
 *******************************************************************
 */
package org.pocketcampus.plugin.food;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pocketcampus.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class FoodListAdapter extends BaseAdapter {

	public final Map<String, Adapter> sections_;
	public final ArrayAdapter<String> headers_;
	public final static int TYPE_SECTION_HEADER = 0;

	public FoodListAdapter(Context context) {
		sections_ = new LinkedHashMap<String, Adapter>();
		headers_ = new ArrayAdapter<String>(context,
				R.layout.food_list_header);
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
		this.headers_.add(section);
		this.sections_.put(section, adapter);
	}
	
	/**
	 * Method to remove sections from the list view.
	 */
	public void removeSections(){
		this.headers_.clear();
		this.sections_.clear();
	}

	/**
	 * Returns the position of a click
	 */
	public Object getItem(int position) {
		for (Object section : this.sections_.keySet()) {
			Adapter adapter = sections_.get(section);
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
		for (Adapter adapter : this.sections_.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		int total = 1;
		for (Adapter adapter : this.sections_.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections_.keySet()) {
			Adapter adapter = sections_.get(section);
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
		for (Object section : this.sections_.keySet()) {
			Adapter adapter = sections_.get(section);
			int size = adapter.getCount() + 1;

			// Gets the position in the section
			if (position == 0)
				return headers_.getView(sectionnum, convertView, parent);
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