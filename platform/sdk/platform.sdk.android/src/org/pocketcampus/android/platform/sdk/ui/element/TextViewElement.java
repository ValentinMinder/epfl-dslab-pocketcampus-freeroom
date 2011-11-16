package org.pocketcampus.android.platform.sdk.ui.element;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TextViewElement  extends LinearLayout{

	private LinearLayout mLayout;
	private IRatableViewLabeler mLabeler;
	private TextView mTitleLine;
	private View mConvertView;
	private Object mCurrentObject;
	Context mContext;
	LayoutInflater mInflater;
	private OnItemClickListener mOnElementClickLIstener;
	private OnItemClickListener mOnRatingClickListener;
	private int mPosition;

	public TextViewElement(Object currentObject, Context context,
			IRatableViewLabeler<? extends Object> labeler,
			OnItemClickListener elementListener,
			OnItemClickListener ratingListener, int position) {
		super(context);
		mLabeler = labeler;
		mConvertView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.sdk_list_entry_text_view, null);
		mOnElementClickLIstener = elementListener;
		mOnRatingClickListener = ratingListener;
		mPosition = position;

		// Creates a ViewHolder and store references to the two children
		// views we want to bind data to.
		this.mLayout = (LinearLayout) mConvertView
				.findViewById(R.id.food_menuentry_list);
		this.mTitleLine = (TextView) mConvertView
				.findViewById(R.id.sdk_list_entry_title_description_view_title);
		this.mCurrentObject = currentObject;
		this.mContext = context;

		initializeView();
	}

	public void initializeView() {
		// Bind the data efficiently with the holder.
		mTitleLine.setText(mLabeler.getTitle(mCurrentObject));
		mTitleLine.setClickable(false);
		
		mTitleLine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnElementClickLIstener != null) {
					v.setTag(mLabeler.getRestaurantName(mCurrentObject));
					mOnElementClickLIstener.onItemClick(null, v, mPosition, 0);
				}
			}
		});
		
		addView(mConvertView);
	}
}
