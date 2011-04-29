package org.pocketcampus.plugin.transport;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a ListView with separators.
 * @author Florian
 * @status working
 */
abstract public class SeparatedListAdapter extends BaseAdapter {
	/** List of sections, ie sublists separated by headers. */
	protected List<Section> sections_ = new ArrayList<Section>();
	
	final private static int TYPE_SECTION_HEADER = 0;
	
	/**
	 * Gets the header view for this list.
	 * @param caption Caption to display.
	 * @param index Index of the header.
	 * @param convertView
	 * @param parent
	 * @return
	 */
	abstract protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent);
	
	/**
	 * Gets the empty view used if the section is empty.
	 * @param convertView
	 * @param parent
	 * @return
	 */
	abstract protected View getEmptyListView(View convertView, ViewGroup parent);
	
	/**
	 * Default public constructor.
	 */
	public SeparatedListAdapter() {
		super();
	}

	/**
	 * Adds a new section to this list.
	 * @param caption Caption to display.
	 * @param adapter Adapter for this list
	 */
	public void addSection(String caption, Adapter adapter) {
		sections_.add(new Section(caption, adapter));
	}
	
	/**
	 * Removes all the sections from this list.
	 */
	public void clearSections() {
		sections_.clear();
	}
	
	@Override
	public Object getItem(int position) {
		for (Section section : this.sections_) {
			if (position==0) {
				return(section);
			}

			int size=section.adapter.getCount()+1;

			if (position<size) {
				return(section.adapter.getItem(position-1));
			}

			position-=size;
		}

		return(null);
	}

	@Override
	public int getCount() {
		int total = 0;

		for (Section section : sections_) {
			total += section.adapter.getCount()+1; // add one for header
		}

		total = Math.max(total, 1);
		return total;
	}
	
	@Override
	public int getViewTypeCount() {
		// One for the header, plus those from sections.
		int total = 1;

		for (Section section : this.sections_) {
			total += section.adapter.getViewTypeCount();
		}

		return(total);
	}

	// one for the header, plus those from sections
	public int getItemViewType(int position) {
		int typeOffset = TYPE_SECTION_HEADER + 1;

		for (Section section : this.sections_) {
			if (position==0) {
				return(TYPE_SECTION_HEADER);
			}

			int size=section.adapter.getCount() + 1;

			if (position<size) {
				return(typeOffset+section.adapter.getItemViewType(position-1));
			}

			position-=size;
			typeOffset+=section.adapter.getViewTypeCount();
		}

		return(-1);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return(false);
	}
	
	@Override
	public boolean isEnabled(int position) {
		return(getItemViewType(position)!=TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(sections_.size() == 0) {
			return getEmptyListView(convertView, parent);
		}
		
		int sectionIndex = 0;

		for (Section section : this.sections_) {
			if (position == 0) {
				return(getHeaderView(section.caption, sectionIndex, convertView, parent));
			}

			int size=section.adapter.getCount()+1;

			if (position<size) {
				return(section.adapter.getView(position-1, convertView, parent));
			}

			position -= size;
			sectionIndex++;
		}

		return null;
	}

	@Override
	public long getItemId(int position) {
		return(position);
	}

	/**
	 * Inner class representing a section of the list. 
	 */
	class Section {
		String caption;
		Adapter adapter;

		Section(String caption, Adapter adapter) {
			this.caption=caption;
			this.adapter=adapter;
		}
	}
}