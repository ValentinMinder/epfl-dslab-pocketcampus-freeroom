package org.pocketcampus.platform.android.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SeparatedListAdapter2 extends StickyHeaderListAdapter {
	
	private ArrayAdapter<String> titles;
	
    public SeparatedListAdapter2(Context context, int headerResourceId) {  
    	titles = new ArrayAdapter<String>(context, headerResourceId);
    	headers = titles;
    }  

    public void addSection(String title, BaseAdapter adapter) {  
    	titles.add(title); 
    	addSection(adapter);
    }  

}
