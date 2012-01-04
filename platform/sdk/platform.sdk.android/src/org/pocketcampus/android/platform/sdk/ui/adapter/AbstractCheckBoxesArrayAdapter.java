package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

/**
 * An array adapter that contains a <code>TextView</code> and two
 * <code>CheckBox</code> per line (it's supposed to be used with the
 * <code>CheckBoxesListViewElement</code> or an equivalent).
 * 
 * You can set the Listeners and the tags to be displayed in the
 * <code>TextView</code> from the application.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public abstract class AbstractCheckBoxesArrayAdapter extends
		ArrayAdapter<Object> {
	/** The resource for the layout. */
	protected static int mLayoutResourceId = R.layout.sdk_list_entry_checkboxes;
	/** The resource for the <code>TextView</code>. */
	protected static int mTextViewResourceId = R.id.sdk_list_entry_checkboxes_text;
	/** The click listener for the object's line. */
	private OnItemClickListener mListener;

	/**
	 * Class constructor calling the super constructor.
	 * 
	 * @param context
	 *            The application context.
	 * @param items
	 *            The items to be displayed in the list.
	 */
	public AbstractCheckBoxesArrayAdapter(Context context,
			List<? extends Object> items) {
		super(context, mLayoutResourceId, mTextViewResourceId, items.toArray());
	}

	/**
	 * Overrides the <code>getView</code> method and creates the view by
	 * initializing the <code>TextView</code> and both <code>CheckBox</code>.
	 * Also sets the listeners on the <code>CheckBox</code> to call the
	 * <code>OnItemClick</code> method if the application has defined a listener
	 * for the <code>CheckBox</code>.
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;

		// If the View is null, calls the super getView() method
		if (v == null) {
			v = super.getView(position, convertView, parent);
		}

		// Creates the TextView and the two CheckBoxes
		final CheckBox likeB = (CheckBox) v
				.findViewById(R.id.sdk_list_entry_positive_checkbox);
		final CheckBox dislikeB = (CheckBox) v
				.findViewById(R.id.sdk_list_entry_negative_checkbox);

		// Listener on the positive CheckBox
		likeB.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the positive
			 * <code>CheckBox</code> is clicked.
			 */
			@Override
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;

				if (b.isChecked()) {
					if (dislikeB.isChecked()) {
						dislikeB.setChecked(false);
					}
				}

				if (mListener != null) {
					mListener.onItemClick(null, (View) v, position, (long) 1);
				}
			}
		});

		// Listener on the negative CheckBox
		dislikeB.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the negative
			 * <code>CheckBox</code> is clicked.
			 */
			@Override
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;

				if (b.isChecked()) {
					if (likeB.isChecked()) {
						likeB.setChecked(false);
					}
				}

				if (mListener != null) {
					mListener.onItemClick(null, (View) v, position, (long) 0);
				}
			}
		});

		return v;
	}

	/**
	 * Sets the listener for the <code>CheckBox</code>.
	 * 
	 * @param clickListener
	 *            The listener defined in the application.
	 */
	public void setOnItemClickListener(OnItemClickListener clickListener) {
		mListener = clickListener;
	}

}