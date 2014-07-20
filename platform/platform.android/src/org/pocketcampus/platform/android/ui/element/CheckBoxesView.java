package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An array adapter that contains a <code>TextView</code> and two
 * <code>CheckBox</code> per line (it's supposed to be used with the
 * <code>MultipleCheckBoxesListView</code> or an equivalent).
 * 
 * You can set the listeners and the tags to be displayed in the
 * <code>TextView</code> from the application.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class CheckBoxesView extends LinearLayout {
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The labeler from the application to get the obejct's attributes. */
	@SuppressWarnings("rawtypes")
	private ILabeler mLabeler;
	/** The position of the object in the <code>ListView</code>. */
	private int mPosition;
	/** The object's title. */
	private TextView mTitleLine;
	/** The positive <code>CheckBox</code>. */
	private CheckBox mPositiveCheckBox;
	/** The negative <code>CheckBox</code>. */
	private CheckBox mNegativeCheckBox;
	/** The click listener on the <code>CheckBox</code>. */
	private OnItemClickListener mOnCheckBoxClickListener;

	/**
	 * Class constructor.
	 * 
	 * @param currentObject
	 *            The object to be displayed in the line.
	 * @param context
	 *            The application context.
	 * @param labeler
	 *            The object's labeler.
	 * @param checkBoxListener
	 *            The listener for the <code>CheckBox</code>.
	 * @param ratingListener
	 *            The listener on the rating bar.
	 * @param position
	 *            The position of the object in the list.
	 */
	public CheckBoxesView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler,
			OnItemClickListener checkBoxListener, int position) {
		super(context);
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_checkboxes, null);

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;
		mOnCheckBoxClickListener = checkBoxListener;

		// Creates a ViewHolder and store references to the two children views
		// we want to bind data to.
		mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_checkboxes_text);
		mPositiveCheckBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_entry_positive_checkbox);
		mNegativeCheckBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_entry_negative_checkbox);

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
	public void initializeView() {

		// Positive CheckBox click listener
		mPositiveCheckBox.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the positive
			 * <code>CheckBox</code> is clicked.
			 */
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

		// Negative CheckBox click listener
		mNegativeCheckBox.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the negative
			 * <code>CheckBox</code> is clicked.
			 */
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

		// Title text for the holder
		if (mLabeler.getLabel(mCurrentObject) != null) {
			mTitleLine.setText(mLabeler.getLabel(mCurrentObject));
		}

		addView(mConvertView);
	}

}
