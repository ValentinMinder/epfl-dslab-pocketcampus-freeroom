/**
 * Sandwich List Adapter
 * 
 * @author Oriane
 * 
 */

package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.Sandwich;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SandwichListAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater_;			/* the layout context */
	private Vector<Vector<Sandwich>> storeList_;	/* list of all sandwich store */
	private Context context_;					/* context of the application the list view is in */
	
	/** Constructor with tow arguments (context and collection of sandwich store) */
	public SandwichListAdapter(Context context, Vector<Vector<Sandwich>> storeList){
		this.storeList_ = storeList;
		this.mInflater_ = LayoutInflater.from(context);
		this.context_ = context;
		valid();
	}
	
	private void valid(){
		if(mInflater_ == null) throw new IllegalArgumentException("LayoutInflater cannot be null");
		if(storeList_ == null) throw new IllegalArgumentException("storeList cannot be null");
		if(context_ == null) throw new IllegalArgumentException("context cannot be null");
	}

	/** Make a view to hold each row */
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder; /* keeps references to children views */
		
		if(convertView == null) {
						
			/* inflate the .xml */
			convertView = mInflater_.inflate(R.layout.food_sandwich_list_item, null);
			
			/* Creates a ViewHolder and store references to the two children view*/
			holder = new ViewHolder();
			holder.sandwichInfoLigne = (LinearLayout) convertView.findViewById(R.id.food_sandwich_lign_list);
			holder.sandwichPlace = (TextView) convertView.findViewById(R.id.food_sandwich_place);
			holder.sandwichLeft = (CheckBox) convertView.findViewById(R.id.food_sandwich_left_checkbox);
			
			convertView.setTag(holder);
		} else {			
			holder = (ViewHolder) convertView.getTag();
		}
		
		/* click listener */
		holder.sandwichInfoLigne.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				checkBoxDialog(position);
			}
		});
		
//		holder.sandwichLeft.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				checkBoxDialog(position);
//			}
//		});
		
		holder.sandwichPlace.setText(storeList_.get(position).get(0).getRestaurant());
		holder.sandwichLeft.setChecked(sandwichLeft(storeList_.get(position)));
		
		return convertView;
	}
	
	/* return true if there are at least on Sandwich */
	private boolean sandwichLeft(Vector<Sandwich> store){
		for(Sandwich i: store){
			if(i.isAvailable()){
				/* we have at least one sandwich available */
				return true;
			}
			else { /* this sandwich is not available */ }
		}
		return false;
	}
	
	/* show the sandwich's list of the store */
	private void checkBoxDialog(int position){
		SandwichCheckBoxDialog checkboxDialog = new SandwichCheckBoxDialog(storeList_.get(position), context_);
		checkboxDialog.show();
	}
	
	/** ViewHolder (element we have on the .xml we have inflate */
	static class ViewHolder {
		TextView sandwichPlace;
		CheckBox sandwichLeft;
		LinearLayout sandwichInfoLigne;
	}

	public int getCount() {
		return storeList_.size();
	}

	public Object getItem(int position) {
		return storeList_.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
