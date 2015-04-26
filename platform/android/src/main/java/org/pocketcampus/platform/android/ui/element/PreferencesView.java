package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.platform.android.ui.labeler.ILabeler;

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
 * A view to display a text and its correlated <code>CheckBox</code> to let the
 * user choose preferences.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch
 */
public class PreferencesView extends LinearLayout {
	/** The application context. */
	private Context mContext;
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object for which we can set the preferences. */
	private Object mCurrentObject;
	/** The labeler from the application, to get the object's attributes. */
	@SuppressWarnings("rawtypes")
	private ILabeler mLabeler;
	/** The position of the object in the <code>ListView</code>. */
	private int mPosition;
	/** The object's title. */
	private TextView mTitle;
	/** The <code>CheckBox</code> to represent the preference for this object. */
	private CheckBox mPrefBox;
	/** The <code>CheckBox</code> listener. */
	private OnItemClickListener mOnCheckBoxClickListener;
	/** The <code>SharedPreferences</code> to retrieve. */
	private SharedPreferences mPrefs;

	/**
	 * Class constructor.
	 * 
	 * @param currentObject
	 *            The object to be displayed.
	 * @param context
	 *            The application context.
	 * @param labeler
	 *            The object's labeler.
	 * @param prefName
	 *            The name of the <code>SharedPreferences</code>.
	 * @param listener
	 *            The object's line listener.
	 * @param position
	 *            The object's position in the list.
	 * @throws IllegalArgumentException
	 *             If the object is null.
	 * @throws IllegalArgumentException
	 *             If the labeler is null.
	 * @throws IllegalArgumentException
	 *             If the listener is null.
	 */
	public PreferencesView(Object currentObject, Context context,
			ILabeler<? extends Object> labeler, String prefName,
			OnItemClickListener listener, int position) {
		super(context);
		mContext = context;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_preferences, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}
		if (labeler == null) {
			new IllegalArgumentException("Labeler cannot be null!");
		}
		if (listener == null) {
			new IllegalArgumentException("Listener cannot be null!");
		}

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
	 * Initializes the view.
	 */
	@SuppressWarnings("unchecked")
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

			/**
			 * Defines what has to be performed when the <code>CheckBox</code>
			 * is clicked.
			 */
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

			/**
			 * Defines what has to be performed when the line is clicked.
			 */
			@Override
			public void onClick(View v) {
				mPrefBox.performClick();
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
					mPrefBox.performClick();
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
					mPrefBox.performClick();
					break;
				}
				return false;
			}
		});
		addView(mConvertView);
	}

}
