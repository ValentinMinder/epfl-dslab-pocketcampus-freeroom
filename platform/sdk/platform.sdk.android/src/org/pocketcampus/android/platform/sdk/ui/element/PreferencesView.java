package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PreferencesView extends LinearLayout {
	// private LinearLayout mLayout;
	private ILabeler mLabeler;
	private TextView mTitleLine;
	private CheckBox mBox;
	private View mConvertView;
	private Object mCurrentObject;
	Context mContext;
	LayoutInflater mInflater;
	private OnItemClickListener mOnCheckedChangedListener;
	private int mPosition;

	public PreferencesView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, OnItemClickListener listener,
			int position) {
		super(context);
		mLabeler = labeler;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_preferences, null);
		mOnCheckedChangedListener = listener;
		mPosition = position;

		// Creates a ViewHolder and store references to the two children
		// views we want to bind data to.
		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_preferences_entry_text);
		this.mBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_preferences_entry_prefBox);
		this.mCurrentObject = currentObject;
		this.mContext = context;

		initializeView();
	}

	public void initializeView() {

		// Bind the data efficiently with the holder.
		mTitleLine.setText(mLabeler.getLabel(mCurrentObject));

		mBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (mOnCheckedChangedListener != null) {

					if (isChecked) {
						Log.d("PREFERENCES",
								"OnCheckedChanged "
										+ mLabeler.getLabel(mCurrentObject)
										+ " -> true (Adapter)");
						mOnCheckedChangedListener.onItemClick(null,
								(View) buttonView, mPosition, (long) 1);
					} else {
						Log.d("PREFERENCES",
								"OnCheckedChanged "
										+ mLabeler.getLabel(mCurrentObject)
										+ " -> false (Adapter)");
						mOnCheckedChangedListener.onItemClick(null,
								(View) buttonView, mPosition, (long) 0);
					}

				}
			}
		});

		addView(mConvertView);
	}

}
