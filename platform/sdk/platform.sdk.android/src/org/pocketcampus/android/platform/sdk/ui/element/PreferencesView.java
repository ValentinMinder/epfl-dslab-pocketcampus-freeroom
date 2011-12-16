package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A Labeled view to display a text and its correlated CheckBox to let the user
 * choose preferences.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch
 */
public class PreferencesView extends LinearLayout {
	/** The Application context */
	private Context mContext;
	/** The convert view */
	private View mConvertView;
	/** The LayoutInlfater to inflate the resources for the layout */
	private LayoutInflater mInflater;
	/** The Object for which we can set the preferences */
	private Object mCurrentObject;
	/** The Labeler from the Application, to get the Object's attributes */
	private ILabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitle;
	/** The CheckBox to represent the preference for this Object */
	private CheckBox mPrefBox;
	/** The CheckBox listener */
	private OnItemClickListener mOnCheckBoxClickListener;
	/** The SharedPreferences to retrieve */
	private SharedPreferences mPrefs;

	public PreferencesView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, String prefName,
			OnItemClickListener listener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_preferences, null);

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		mOnCheckBoxClickListener = listener;

		mPrefs = mContext.getSharedPreferences(prefName, 0);

		// Creates the TextView and the CheckBox
		mTitle = (TextView) mConvertView
				.findViewById(R.id.sdk_list_preferences_entry_text);
		mPrefBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_preferences_entry_prefBox);

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		// Binds the data efficiently with the holder.

		// TextView
		mTitle.setText(mLabeler.getLabel(mCurrentObject));

		// CheckBox
		if (mPrefs.getBoolean(mTitle.getText().toString(), false)) {
			mPrefBox.setChecked(true);
		}

		// CheckBox Listener
		mPrefBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;

				if (mOnCheckBoxClickListener != null) {

					if (b.isChecked()) {
						mOnCheckBoxClickListener.onItemClick(null, (View) b,
								mPosition, (long) 1);
					} else {
						mOnCheckBoxClickListener.onItemClick(null, (View) b,
								mPosition, (long) 0);
					}

				}
			}
		});

		mConvertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPrefBox.performClick();
			}
		});

		mConvertView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.ACTION_DOWN:
					mPrefBox.performClick();
					break;
				}
				return false;
			}
		});

		
		this.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.ACTION_DOWN:
					mPrefBox.performClick();
					break;
				}
				return false;
			}
		});
		addView(mConvertView);
	}

}
