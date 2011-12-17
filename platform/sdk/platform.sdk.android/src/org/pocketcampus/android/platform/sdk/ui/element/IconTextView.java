package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconTextView extends LinearLayout {
	/** The convert view */
	private View mConvertView;
	/** The Object to be displayed in the View */
	private Object mCurrentObject;
	/** The Object's title */
	private TextView mTextLine;
	/** The image view resource id */
	private int mImageResource;
	/** The ImageView */
	private ImageView mImage;

	/**
	 * The constructor
	 * 
	 * @param currentObject
	 * @param context
	 * @param items
	 * @param iconResourceId
	 */
	public IconTextView(Object currentObject, Context context,
			int iconResourceId) {
		super(context);

		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry, null);

		mCurrentObject = currentObject;
		mImageResource = iconResourceId;

		mTextLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_text);

		mImage = (ImageView) mConvertView
				.findViewById(R.id.sdk_list_entry_icon);
		initializeView();
	}

	/**
	 * 
	 */
	private void initializeView() {
		mTextLine.setText(mCurrentObject.toString());
		mImage.setImageResource(mImageResource);
		addView(mConvertView);
	}
}
