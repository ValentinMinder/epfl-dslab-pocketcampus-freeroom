package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an object along with a <code>CheckBox</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class CheckBoxView extends LinearLayout {
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The labeler from the application to get the object's attributes. */
	@SuppressWarnings("rawtypes")
	private ILabeler mLabeler;
	/** The position of the object in the <code>ListView</code>. */
	private int mPosition;
	/** The object's title. */
	private TextView mTitleLine;
	/** The <code>CheckBox</code>. */
	private CheckBox mCheckBox;
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
	 *            The click listener for the <code>CheckBox</code>.
	 * @param position
	 *            The position of the object in the list.
	 * @throws IllegalArgumentException
	 *             Thrown if the object is null.
	 * @throws IllegalArgumentException
	 *             Thrown if the labeler is null.
	 */
	public CheckBoxView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler,
			OnItemClickListener checkBoxListener, int position) {
		super(context);
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_checkbox, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}

		mCurrentObject = currentObject;
		mLabeler = labeler;
		mPosition = position;

		// Creates a ViewHolder and store references to the two children views
		// we want to bind data to.
		mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_checkbox_text);
		mCheckBox = (CheckBox) mConvertView
				.findViewById(R.id.sdk_list_entry_box);

		// Listeners
		mOnCheckBoxClickListener = checkBoxListener;

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
	public void initializeView() {

		// CheckBox click listener
		mCheckBox.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the <code>CheckBox</code>
			 * is clicked.
			 */
			public void onClick(View v) {
				if (mOnCheckBoxClickListener != null) {
					mOnCheckBoxClickListener.onItemClick(null, v, mPosition, 0);
				}
			}
		});

		mConvertView.setOnClickListener(new OnClickListener() {

			/**
			 * Defines what has to be performed when the line is clicked.
			 */
			@Override
			public void onClick(View v) {
				mCheckBox.performClick();
			}
		});

		mConvertView.setOnKeyListener(new OnKeyListener() {

			/**
			 * Defines what has to be performed when the line is clicked.
			 */
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.ACTION_DOWN:
					mCheckBox.performClick();
					break;
				}
				return false;
			}
		});

		this.setOnKeyListener(new OnKeyListener() {

			/**
			 * Defines what has to be performed when the line is clicked.
			 */
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.ACTION_DOWN:
					mCheckBox.performClick();
					break;
				}
				return false;
			}
		});

		// Title text for the holder
		if (mLabeler.getLabel(mCurrentObject) != null) {
			mTitleLine.setText(mLabeler.getLabel(mCurrentObject));
		}

		addView(mConvertView);
	}

}
