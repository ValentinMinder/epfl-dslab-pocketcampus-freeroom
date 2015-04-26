package org.pocketcampus.platform.android.ui.adapter;

import java.util.LinkedHashMap;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

public class MultiListAdapter extends BaseAdapter {  
    
    public final Map<String,BaseAdapter> sections = new LinkedHashMap<String,BaseAdapter>();  
      
    public MultiListAdapter() {  
    }  
      
    public void addSection(BaseAdapter adapter) {  
        this.sections.put("section" + this.sections.size(), adapter);  
    }  
      
    public Object getItem(int position) {  
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return adapter.getItem(position);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
    }  
  
    public int getCount() {  
        // total together all sections, plus one for each section header  
        int total = 0;  
        for(Adapter adapter : this.sections.values())  
            total += adapter.getCount();  
        return total;  
    }  
  
    public int getViewTypeCount() {  
        int total = 1; // hack: should be 0: set to 1 so that if we have 0 sub-adapters, we still return >0; if we don't do that the app crashes: a list cannot have 0 cell TYPES.  
        for(Adapter adapter : this.sections.values())  
            total += adapter.getViewTypeCount();  
        return total;  
    }  
      
    public int getItemViewType(int position) {  
        int type = 0;  
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return type + adapter.getItemViewType(position);  
  
            // otherwise jump into next section  
            position -= size;  
            type += adapter.getViewTypeCount();  
        }  
        return IGNORE_ITEM_VIEW_TYPE;  
    }  
      
    public boolean areAllItemsSelectable() {  
        return false;  
    }  
  
    public boolean isEnabled(int position) {  
        for(Object section : this.sections.keySet()) {  
        	BaseAdapter adapter = sections.get(section);  
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
        for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount();  
              
            // check if position inside this section   
            if(position < size) return adapter.getView(position, convertView, parent);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
}
