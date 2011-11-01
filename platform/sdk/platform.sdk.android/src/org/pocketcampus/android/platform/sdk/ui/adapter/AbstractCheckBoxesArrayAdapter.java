package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.R;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author Oriane
 */
public abstract class AbstractCheckBoxesArrayAdapter extends ArrayAdapter<Object> {
	private Context mContext;
	private LayoutInflater mInflater;
	protected static int mLayoutResourceId = R.layout.sdk_list_entry_checkboxes;
	protected static int mTextViewResourceId = R.id.sdk_list_checkbox_entry_text;

	private ElementDimension mDimension = ElementDimension.NORMAL;
	
	private OnCheckedChangeListener mOnPositiveBoxListener;
	private OnCheckedChangeListener mOnNegativeBoxListener;
	private OnItemClickListener mListener;
	
	private List<String> mLikeTags;
	private List<String> mDislikeTags;

	public AbstractCheckBoxesArrayAdapter(Context context, List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
		this.mContext = context;
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if(v == null) {
			v = super.getView(position, convertView, parent);
		}

		mLikeTags = new ArrayList<String>();
		mDislikeTags = new ArrayList<String>();
		
		final CheckBox likeB = (CheckBox)v.findViewById(R.id.sdk_list_entry_checkbox1);
		final CheckBox dislikeB = (CheckBox)v.findViewById(R.id.sdk_list_entry_checkbox2);
		final TextView text = (TextView)v.findViewById(R.id.sdk_list_checkbox_entry_text);
		final String tag = text.getText().toString();
		
		likeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("TAG", "like " + tag);

				if(isChecked) {		
					mLikeTags.add(tag);
					if(dislikeB.isChecked()){
						dislikeB.setChecked(false);
						mDislikeTags.remove(tag);
					} 
				} else {
					mLikeTags.remove(tag);
				}
				
				if(mListener != null){					
					mListener.onItemClick(null, (View)buttonView, position, (long)1);
				}
					
			}
			
		});
		
		dislikeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("TAG", "dislike " + tag);
				
				if(isChecked) {		
					mDislikeTags.add(tag);
					if(likeB.isChecked()){
						likeB.setChecked(false);
						mLikeTags.remove(tag);
					} 
				} else {
					mDislikeTags.remove(tag);
				}
				
				if(mListener != null) {					
					mListener.onItemClick(null, (View)buttonView, position, (long)0);
				}
				
			}
			
		});

		return v;
	}

	public void setOnPositiveBoxClickListener(OnCheckedChangeListener l) {
		mOnPositiveBoxListener = l;
	}

	public void setOnNegativeBoxClickListener(OnCheckedChangeListener l) {
		mOnNegativeBoxListener = l;
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mListener = l;
	}
	
	public List<String> getPositiveTags() {
		return mLikeTags;
	}
	
	public List<String> getNegativeTags() {
		return mDislikeTags;
	}
}
