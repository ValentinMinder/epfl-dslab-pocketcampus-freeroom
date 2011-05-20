package org.pocketcampus.plugin.mainscreen;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PluginsAdapter implements ListAdapter {
	
	private Vector<PluginBase> plugins_;
	private LayoutInflater li_;
	private Context context_;

	public PluginsAdapter(Context context, Vector<PluginBase> plugins) {
		
		context_ = context;
		
		li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		plugins_ = new Vector<PluginBase>();
		
		for (final PluginBase plugin : plugins) {
			PluginInfo pluginInfo = plugin.getPluginInfo();

			// MENU ICONS
			if(pluginInfo.hasMenuIcon() == true) {
				plugins_.add(plugin);
			}
		}
	}

	@Override
	public int getCount() {
		return plugins_.size();
	}

	@Override
	public PluginBase getItem(int position) {
		return plugins_.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
        if (convertView == null) {
            v = li_.inflate(R.layout.mainscreen_plugin_button, null);
        } else {
        	v = convertView;
        }
        
        PluginBase pb = getItem(position);
        PluginInfo pi = pb.getPluginInfo();
        
        ImageView iv = (ImageView) v.findViewById(R.id.mainscreen_button_image);
        iv.setImageDrawable(pi.getIcon().getDrawable(context_));
        
        TextView tv = (TextView) v.findViewById(R.id.mainscreen_button_text);
        tv.setText(pi.getNameResource());
        
        return v;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return plugins_.size() == 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
