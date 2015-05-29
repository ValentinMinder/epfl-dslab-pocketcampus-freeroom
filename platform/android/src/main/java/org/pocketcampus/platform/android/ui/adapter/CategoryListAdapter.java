package org.pocketcampus.platform.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Displays a ListView with separators.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class CategoryListAdapter extends BaseAdapter {
	/** List of sections, ie sublists separated by headers. */
	protected List<Section> mSections = new ArrayList<Section>();
	
	final private static int TYPE_SECTION_HEADER = 0;
	
	private LayoutInflater mInflater;
	
	/**
	 * Default public constructor.
	 */
	public CategoryListAdapter(Context context) {
		super();
		
		mInflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Adds a new section to this list.
	 * @param caption Caption to display.
	 * @param adapter Adapter for this list
	 */
	public void addSection(String caption, Adapter adapter) {
		mSections.add(new Section(caption, adapter));
	}
	
	/**
	 * Removes all the sections from this list.
	 */
	public void clearSections() {
		mSections.clear();
	}
	
	private View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
		View view = convertView;
		view = mInflater.inflate(R.layout.sdk_list_header, null);
		
		TextView timeTextView = (TextView) view.findViewById(R.id.list_header_title);
		timeTextView.setText(caption);
		
		return view;
	}
	
	@Override
	public Object getItem(int position) {
		for (Section section : this.mSections) {
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

		for (Section section : mSections) {
			total += section.adapter.getCount() + 1; // add one for header
		}

		//total = Math.max(total, 1);
		return total;
	}
	
	@Override
	public int getViewTypeCount() {
		// One for the header, plus those from sections.
		int total = 1;

		for (Section section : this.mSections) {
			total += section.adapter.getViewTypeCount();
		}

		return(total);
	}

	// one for the header, plus those from sections
	public int getItemViewType(int position) {
		int typeOffset = TYPE_SECTION_HEADER + 1;

		for (Section section : this.mSections) {
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
		int sectionIndex = 0;

		for (Section section : this.mSections) {
			if (position == 0) {
				return(getHeaderView(section.caption, sectionIndex, convertView, parent));
			}

			int size=section.adapter.getCount()+1;

			if (position < size) {
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
	private class Section {
		String caption;
		Adapter adapter;

		Section(String caption, Adapter adapter) {
			this.caption = caption;
			this.adapter = adapter;
		}
	}
}
