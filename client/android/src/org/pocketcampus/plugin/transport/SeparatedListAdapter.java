package org.pocketcampus.plugin.transport;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

abstract public class SeparatedListAdapter extends BaseAdapter {
	protected List<Section> sections_ = new ArrayList<Section>();
	private static int TYPE_SECTION_HEADER = 0;
	
	abstract protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent);
	
	abstract protected View getEmptyListView(View convertView, ViewGroup parent);
	
	public SeparatedListAdapter() {
		super();
	}

	public void addSection(Adapter adapter) {
		sections_.add(new Section(((TransportSummaryAdapter) adapter).getCaption(), adapter));
	}
	
	public void addSection(String caption, Adapter adapter) {
		sections_.add(new Section(caption, adapter));
	}
	
	public void clearSections() {
		sections_.clear();
	}
	
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

	public int getCount() {
		int total = 0;

		for (Section section : sections_) {
			total += section.adapter.getCount()+1; // add one for header
		}

		total = Math.max(total, 1);
		return total;
	}

	public int getViewTypeCount() {
		int total=1;	// one for the header, plus those from sections

		for (Section section : this.sections_) {
			total+=section.adapter.getViewTypeCount();
		}

		return(total);
	}

	public int getItemViewType(int position) {
		int typeOffset=TYPE_SECTION_HEADER+1;	// start counting from here

		for (Section section : this.sections_) {
			if (position==0) {
				return(TYPE_SECTION_HEADER);
			}

			int size=section.adapter.getCount()+1;

			if (position<size) {
				return(typeOffset+section.adapter.getItemViewType(position-1));
			}

			position-=size;
			typeOffset+=section.adapter.getViewTypeCount();
		}

		return(-1);
	}

	public boolean areAllItemsSelectable() {
		return(false);
	}

	public boolean isEnabled(int position) {
		return(getItemViewType(position)!=TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(sections_.size() == 0) {
			return getEmptyListView(convertView, parent);
		}
		
		int sectionIndex=0;

		for (Section section : this.sections_) {
			if (position==0) {
				return(getHeaderView(section.caption, sectionIndex, convertView, parent));
			}

			int size=section.adapter.getCount()+1;

			if (position<size) {
				return(section.adapter.getView(position-1, convertView, parent));
			}

			position-=size;
			sectionIndex++;
		}

		return null;
	}

	@Override
	public long getItemId(int position) {
		return(position);
	}

	class Section {
		String caption;
		Adapter adapter;

		Section(String caption, Adapter adapter) {
			this.caption=caption;
			this.adapter=adapter;
		}
	}
}