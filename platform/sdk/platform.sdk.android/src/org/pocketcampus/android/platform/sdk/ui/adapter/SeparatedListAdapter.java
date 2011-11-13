package org.pocketcampus.android.platform.sdk.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.ui.element.ElementDimension;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of menus.
 * 
 */
public class SeparatedListAdapter extends BaseAdapter implements Filterable {
	/* App context */
	private Context mContext;
	
	/*Layout Management*/
	private LayoutInflater mInflater;
	private ElementDimension mDimension = ElementDimension.NORMAL;
	private Vector<String> mHeaders;
	private HashMap<String, Vector<Object>> mListContent;
	
	/* View Handler */
	private IRatableViewConstructor mViewConstructor;
	private IRatableViewLabeler<? extends Object> mViewLabeler;

	/*Layout status*/
	private boolean[] mExpanded;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param headers
	 *            restaurant full menu represented in the list section.
	 */
	public SeparatedListAdapter(Vector<String> headers,
			HashMap<String, Vector<Object>> sectionContent,
			IRatableViewConstructor viewConstructor,
			IRatableViewLabeler<? extends Object> viewLabeler, Context context) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mHeaders = headers;
		this.mListContent = sectionContent;
		this.mViewConstructor = viewConstructor;
		this.mViewLabeler = viewLabeler;
		this.mExpanded = new boolean[headers.size()];
		for (int i = 0; i < headers.size(); i++) {
			mExpanded[i] = false;
		}
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*
		 * A ViewHolder keeps references to children views to avoid unnecessary
		 * calls to findViewById() on each row.
		 */
		ViewHolder holder;

		String currentHeader = mHeaders.get(position);

		holder = new ViewHolder(mContext, currentHeader,
				mListContent.get(currentHeader), position, mExpanded[position]);

		holder.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggle(position);
			}
		});

		return holder;
	}

	public void toggle(int position) {
		Log.d("List", "Toggling " + position + ", old: " + mExpanded[position]
				+ ", new: " + !mExpanded[position]);
		mExpanded[position] = !mExpanded[position];
		// mContext.notifyDataSetChanged();
	}

	public void toggleAll(boolean toggle) {
		for (int i = 0; i < mExpanded.length; i++) {
			mExpanded[i] = toggle;
		}
		// mContext.notifyDataSetChanged();
	}

	public Filter getFilter() {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	// Returns the number of meals in that section.
	public int getCount() {
		return mHeaders.size();
	}

	// Returns the meal to be represented at that position.
	public Object getItem(int position) {
		return mHeaders.get(position);
	}

	private class ViewHolder extends LinearLayout {

		View mView;
		TextView mTitle;
		ImageView mImage;

		private List<View> mSectionContents;

		public ViewHolder(Context context, String title, Vector<Object> resto,
				int position, boolean expanded) {
			super(context);

			this.setOrientation(VERTICAL);

			mView = mInflater.inflate(R.layout.sdk_separated_list_header, null);

			mTitle = (TextView) mView
					.findViewById(R.id.sdk_separated_list_header_title);
			mTitle.setText(title);

			mImage = (ImageView) mView
					.findViewById(R.id.sdk_separated_list_header_arrow);
			if (expanded) {
				mImage.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.sdk_separated_list_header_south_arrow));
			} else {
				mImage.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.sdk_separated_list_header_east_arrow));
			}

			addView(mView);

			mSectionContents = new ArrayList<View>();

			Vector<Object> sectionObjects = mListContent.get(title);
			for (Object sectionObject : sectionObjects) {
//				mSectionContents.add(mViewConstructor.getNewView(sectionObject,
//						mContext, mViewLabeler, elementListener, ratingListener, position));
			}

			for (int j = 0; j < mSectionContents.size(); j++) {
				addView(mSectionContents.get(j));
				mSectionContents.get(j)
						.setVisibility(expanded ? VISIBLE : GONE);
			}
		}
	}

	public void setDimension(ElementDimension dimension) {
		mDimension = dimension;
	}
}