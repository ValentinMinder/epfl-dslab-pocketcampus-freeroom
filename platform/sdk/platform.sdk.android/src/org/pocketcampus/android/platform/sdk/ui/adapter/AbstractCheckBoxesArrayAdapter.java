package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * An Array Adapter that contains a TextView and two CheckBoxes per line (it's
 * supposed to be used with the CheckBoxesListView or an equivalent).
 * 
 * You can set the Listeners and the tags to be displayed in the TextViews from
 * the Application.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public abstract class AbstractCheckBoxesArrayAdapter extends
		ArrayAdapter<Object> {
	/** The Application context */
	private Context mContext;
	/** The LayoutInflater to inflate the resources for the layout */
	private LayoutInflater mInflater;
	/** The source for the Layout */
	protected static int mLayoutResourceId = R.layout.sdk_list_entry_checkboxes;
	/** The source for the TextView */
	protected static int mTextViewResourceId = R.id.sdk_list_checkbox_entry_text;
	/** Listener to be set by the Application */
	private OnItemClickListener mListener;

	/**
	 * The constructor, that simply calls the super constructor
	 * 
	 * @param context
	 *            The Application context
	 * @param items
	 *            The items to be displayed in the list
	 */
	public AbstractCheckBoxesArrayAdapter(Context context,
			List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
		this.mContext = context;
	}

	/**
	 * Overrides the getView method and creates the View by initializing the
	 * TextView and both CheckBoxes. Also sets the Listeners on the CheckBoxes
	 * to call the OnItemClick method if the Application has defined a Listener
	 * for the CheckBoxes.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		/** If the View is null, calls the super getView() method */
		if (v == null) {
			v = super.getView(position, convertView, parent);
		}

		/** Creates the TextView and the two CheckBoxes */
		final CheckBox likeB = (CheckBox) v
				.findViewById(R.id.sdk_list_entry_positive_checkbox);
		final CheckBox dislikeB = (CheckBox) v
				.findViewById(R.id.sdk_list_entry_negative_checkbox);

		/** Listener on the positive CheckBox */
		likeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					if (dislikeB.isChecked()) {
						dislikeB.setChecked(false);
					}
				}

				if (mListener != null) {
					mListener.onItemClick(null, (View) buttonView, position,
							(long) 1);
				}

			}

		});

		/** Listener on the negative CheckBox */
		dislikeB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					if (likeB.isChecked()) {
						likeB.setChecked(false);
					}
				}

				if (mListener != null) {
					mListener.onItemClick(null, (View) buttonView, position,
							(long) 0);
				}

			}

		});

		return v;
	}

	/**
	 * Sets the click listener for the CheckBoxes
	 * 
	 * @param clickListener
	 *            The click listener created in the application
	 */
	public void setOnItemClickListener(OnItemClickListener clickListener) {
		mListener = clickListener;
	}

}