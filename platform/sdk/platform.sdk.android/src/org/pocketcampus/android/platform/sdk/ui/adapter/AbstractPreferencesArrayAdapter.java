package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.R;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
public abstract class AbstractPreferencesArrayAdapter extends ArrayAdapter<Object> {
	private Context mContext;
	private LayoutInflater mInflater;
	protected static int mLayoutResourceId = R.layout.sdk_list_entry_preferences;
	protected static int mTextViewResourceId = R.id.sdk_list_preferences_entry_text;

	private ElementDimension mDimension = ElementDimension.NORMAL;
	
	private CheckBox prefBox;
	private TextView text;
	
	private OnItemClickListener mListener;
	
	/*Preferences*/
	private SharedPreferences mRestoPrefs;
	private Editor mRestoPrefsEditor;

	private static final String RESTO_PREFS_NAME = "RestoPrefs";

	public AbstractPreferencesArrayAdapter(Context context, List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
		this.mContext = context;
		mRestoPrefs = mContext.getSharedPreferences(RESTO_PREFS_NAME, 0);
		mRestoPrefsEditor = mRestoPrefs.edit();
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
		
		prefBox = (CheckBox)v.findViewById(R.id.sdk_list_preferences_entry_prefBox);
		text = (TextView)v.findViewById(R.id.sdk_list_preferences_entry_text);
		final String resto = text.getText().toString();
		
		prefBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					Log.d("PREFERENCES", "want to display " + resto);
					mRestoPrefsEditor.putBoolean(resto, true);
					mRestoPrefsEditor.commit();
				} else {
					Log.d("PREFERENCES", "doesn't want to display " + resto);
					mRestoPrefsEditor.putBoolean(resto, false);
					mRestoPrefsEditor.commit();
				}

				if(mListener != null){
					
					if(isChecked) {
						mListener.onItemClick(null, (View)buttonView, position, (long)1);
					} else {
						mListener.onItemClick(null, (View)buttonView, position, (long)0);
					}
					
				}
					
			}
			
		});

		return v;
	}
	
	public void setOnItemClickListener(OnItemClickListener l) {
		mListener = l;
	}

}
