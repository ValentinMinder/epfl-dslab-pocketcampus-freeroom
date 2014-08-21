package org.pocketcampus.platform.android.ui.element;

import org.pocketcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view to display an object consisting of a text along with an icon.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class IconTextView extends LinearLayout {
	/** The <code>ConvertView</code>. */
	private View mConvertView;
	/** The object to be displayed in the view. */
	private Object mCurrentObject;
	/** The object's title. */
	private TextView mTextLine;
	/** The <code>ImageView</code> resource value. */
	private int mImageResource;
	/** The <code>ImageView</code>. */
	private ImageView mImage;

	/**
	 * Class constructor.
	 * 
	 * @param currentObject
	 *            The object to be displayed in the view.
	 * @param context
	 *            The application context.
	 * @param items
	 *            The list of items to be displayed.
	 * @param iconResourceId
	 *            The resource value of the icon.
	 * @throws IllegalArgumentException
	 *             Thrown if the object is null.
	 */
	public IconTextView(Object currentObject, Context context,
			int iconResourceId) {
		super(context);

		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry, null);

		if (currentObject == null) {
			new IllegalArgumentException("Object cannot be null!");
		}

		mCurrentObject = currentObject;
		mImageResource = iconResourceId;

		mTextLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_text);
		mImage = (ImageView) mConvertView
				.findViewById(R.id.sdk_list_entry_icon);

		initializeView();
	}

	/**
	 * Initializes the view.
	 */
	private void initializeView() {
		mTextLine.setText(mCurrentObject.toString());
		mImage.setImageResource(mImageResource);
		addView(mConvertView);
	}
}
