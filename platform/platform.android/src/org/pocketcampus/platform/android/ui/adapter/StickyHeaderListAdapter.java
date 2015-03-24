package org.pocketcampus.platform.android.ui.adapter;

import java.util.LinkedList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class StickyHeaderListAdapter extends BaseAdapter implements StickyListHeadersAdapter {  
    
	private final List<BaseAdapter> sections = new LinkedList<BaseAdapter>();
	private BaseAdapter headers;
      
    public StickyHeaderListAdapter() {
    }  
    
    private void audit() {
    	if(headers == null)
    		throw new IllegalStateException("headers adapter not set");
    	if(headers.getCount() != sections.size())
    		throw new IllegalStateException("number of sections doesn't match number of headers");
    }
      
    public void setHeaders(BaseAdapter adapter) {  
        this.headers = adapter;  
    }  
    
    public void addSection(BaseAdapter adapter) {  
        this.sections.add(adapter);  
    }  
      
    public Object getItem(int position) {  
    	audit();
        for(BaseAdapter adapter : this.sections) { 
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return adapter.getItem(position);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
    }  
  
    public int getCount() {  
    	audit();
        // total together all sections, plus one for each section header  
        int total = 0;  
        for(BaseAdapter adapter : this.sections)  
            total += adapter.getCount();  
        return total;  
    }  
  
    public int getViewTypeCount() {  
    	audit();
        int total = 1; // hack: should be 0: set to 1 so that if we have 0 sub-adapters, we still return >0; if we don't do that the app crashes: a list cannot have 0 cell TYPES.  
        for(BaseAdapter adapter : this.sections)  
            total += adapter.getViewTypeCount();  
        return total;  
    }  
      
    public int getItemViewType(int position) { 
    	audit();
        int type = 0;  
        for(BaseAdapter adapter : this.sections) {  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return type + adapter.getItemViewType(position);  
  
            // otherwise jump into next section  
            position -= size;  
            type += adapter.getViewTypeCount();  
        }  
        return IGNORE_ITEM_VIEW_TYPE;  
    }  
        
    public boolean isEnabled(int position) {  
    	audit();
        for(BaseAdapter adapter : this.sections) {  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return adapter.isEnabled(position);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return false;  
    }  
      
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) { 
    	audit();
        for(BaseAdapter adapter : this.sections) {  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return adapter.getView(position, convertView, parent);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
    }  
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
    	audit();
    	return headers.getView((int) getHeaderId(position), convertView, parent);
	}

  
    @Override  
    public long getItemId(int position) {  
    	audit();
        return position;  
    }

	@Override
	public long getHeaderId(int position) {
    	audit();
    	int index = 0;
        for(BaseAdapter adapter : this.sections) {  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return index;  
  
            // otherwise jump into next section  
            position -= size;
            index++;
        }  
        return -1;  
	}  
  
}
