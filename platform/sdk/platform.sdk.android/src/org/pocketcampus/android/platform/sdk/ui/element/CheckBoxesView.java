package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an Object that the user can rate. It represents a line of a
 * ListView and contains the object's title and description, along with a rating
 * bar and the number of votes that Object got. It's designed to be used with
 * the RatableExpandableListView or an equivalent, and can be created directly
 * in the Application View.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class CheckBoxesView extends LinearLayout {
	/** The Application Context */
	private Context mContext;
	/** The convert view */
	private View mConvertView;
	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Labeler from the Application to get the Obejct's attributes */
	private ILabeler mLabeler;
	/** The position of the Object in the ListView */
	private int mPosition;
	/** The Object's title */
	private TextView mTitleLine;
	/** The positive CheckBox */
	private CheckBox mPositiveCheckBox;
	/** The negative CheckBox */
	private CheckBox mNegativeCheckBox;
	/** The click listener on the CheckBox */
	private OnItemClickListener mOnCheckBoxClickListener;

	/**
	 * The constructor
	 * 
	 * @param currentObject
	 *            The Object to be displayed in the line
	 * @param context
	 *            The Application context
	 * @param labeler
	 *            The Object's labeler
	 * @param checkBoxListener
	 *            the listener for the title and description lines
	 * @param ratingListener
	 *            the listener on the rating bar
	 * @param position
	 *            the position of the Object in the List
	 */
	public CheckBoxesView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler,
			OnItemClickListener checkBoxListener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_checkboxes, null);

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		/**
		 * Creates a ViewHolder and store references to the two children views
		 * we want to bind data to.
		 */
		mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_checkbox_entry_text);
		mPositiveCheckBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_entry_positive_checkbox);
		mNegativeCheckBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_entry_negative_checkbox);

		/** Listeners */
		mOnCheckBoxClickListener = checkBoxListener;

		initializeView();
	}

	/**
	 * Initializes the View
	 */
	public void initializeView() {

		/** positive CheckBox click listener */
		mPositiveCheckBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;

				if (b.isChecked()) {
					if (mNegativeCheckBox.isChecked()) {
						mNegativeCheckBox.setChecked(false);
					}
				}
				if (mOnCheckBoxClickListener != null) {
					mOnCheckBoxClickListener.onItemClick(null, v, mPosition, 1);
				}
			}
		});

		/** negative CheckBox click listener */
		mNegativeCheckBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;

				if (b.isChecked()) {
					if (mPositiveCheckBox.isChecked()) {
						mPositiveCheckBox.setChecked(false);
					}
				}
				if (mOnCheckBoxClickListener != null) {
					mOnCheckBoxClickListener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		/** Title text for the holder */
		mTitleLine.setText(mLabeler.getLabel(mCurrentObject));

		addView(mConvertView);
	}

}
