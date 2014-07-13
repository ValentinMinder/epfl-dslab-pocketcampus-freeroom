package org.pocketcampus.platform.android.ui.PCSectionedList;

import java.util.ArrayList;

import org.pocketcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class PCEntryAdapter extends ArrayAdapter<PCItem> {

	private Context context;
	private ArrayList<PCItem> items;
	private LayoutInflater vi;

	public PCEntryAdapter(Context context,ArrayList<PCItem> items) {
		super(context,0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final PCItem i = items.get(position);
		if (i != null) {
			if(!i.isEmptyLayout()){
				if(i.isSection()){
					PCSectionItem si = (PCSectionItem)i;
					
					v = vi.inflate(R.layout.sdk_sectioned_list_item_section, null);
					v.setOnClickListener(null);
					//v.setOnLongClickListener(null);
					v.setLongClickable(true);
					
					final TextView sectionView = (TextView) v.findViewById(R.id.PCSectioned_list_item_section_text);
					sectionView.setText(si.getTitle());
					
					final TextView sectionDescriptionView = (TextView) v.findViewById(R.id.PCSectioned_list_item_section_description);
					sectionDescriptionView.setText(si.getDescription());
					
				}else{
					PCEntryItem ei = (PCEntryItem)i;
					
					v = vi.inflate(R.layout.sdk_sectioned_list_item_entry, null);
					v.setLongClickable(false);
					
					final TextView title = (TextView)v.findViewById(R.id.PCSectioned_list_item_entry_place);
					final TextView subtitle = (TextView)v.findViewById(R.id.PCSectioned_list_item_entry_nb);
					
					if (title != null) 
						title.setText(ei.title);
					if(subtitle != null)
						subtitle.setText(ei.subtitle);
				}
			}else{
				PCEmptyLayoutItem eli = (PCEmptyLayoutItem)i;
				v = eli.getLayout();
				
			}
		}
		return v;
	}

}
