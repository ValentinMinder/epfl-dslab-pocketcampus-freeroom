package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.android.platform.sdk.R;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

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

		final CheckBox likeB = (CheckBox)v.findViewById(R.id.sdk_list_entry_checkbox1);
		final CheckBox dislikeB = (CheckBox)v.findViewById(R.id.sdk_list_entry_checkbox2);
		TextView text = (TextView)v.findViewById(R.id.sdk_list_checkbox_entry_text);
		
		likeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(dislikeB.isChecked()){
					if(isChecked) {						
						dislikeB.setChecked(false);
					}
				}
				if(mListener != null)
					mListener.onItemClick(null, (View)buttonView, position, (long)0);
//				if(mOnPositiveBoxListener != null)
//					mOnPositiveBoxListener.onCheckedChanged(buttonView, isChecked);
			}
			
		});
		
		dislikeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(likeB.isChecked()) {
					if(isChecked) {						
						likeB.setChecked(false);
					}
				}
				if(mListener != null)
					mListener.onItemClick(null, (View)buttonView, position, (long)0);
//				if(mOnNegativeBoxListener != null)
//					mOnNegativeBoxListener.onCheckedChanged(buttonView, isChecked);
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
}
