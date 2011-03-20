package org.pocketcampus.plugin.news;

import org.pocketcampus.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Adapter used by the ListView to display the feeds
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class NewsAdapter extends BaseAdapter {

	private NewsProvider newsProvider_;

	// Selected item, will be shown bigger than the others
	private int selectedItem_ = -1;

	// Misc
	private LayoutInflater mInflater_;
	private Context context_;

	/**
	 * Adapter constructor
	 * @param context Context of the application
	 * @param items Items that have to be on the list
	 */
	public NewsAdapter(Context context, NewsProvider newsProvider) {
		super();
		this.context_ = context;

		mInflater_ = LayoutInflater.from(context);
		
		newsProvider_ = newsProvider;
		newsProvider_.setAdapter(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater_.inflate(R.layout.news_newsentry, null);
		}

		// The item to display
		final NewsItem newsItem = getItem(position);

		if (newsItem != null) {
			TextView tv;

			tv = (TextView) v.findViewById(R.id.news_item_title);
			tv.setText(newsItem.getTitle());

			tv = (TextView) v.findViewById(R.id.news_item_description);
			tv.setText(newsItem.getDescriptionNoHtml());
			tv.setMaxLines(selectedItem_ == position ? 15 : 2); // Bigger if the item is selected

			LoaderNewsImageView liv = (LoaderNewsImageView) v.findViewById(R.id.news_item_image);
			liv.setNewItem(newsItem);

			// "View more" button, shown only on the selected item
			Button b = (Button) v.findViewById(R.id.news_view_more);
			
			if(selectedItem_ == position) {
				b.setVisibility(View.VISIBLE);

				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
						context_.startActivity(i);
					}
				});

			} else {
				b.setVisibility(View.GONE);
			}

		}

		return v;

	}

	/**
	 * Sets the item that has been selected, to show it bigger
	 * @param parent 
	 * @param view
	 * @param position
	 * @param id
	 */
	public void setClickedItem(AdapterView<?> parent, View view, int position, long id) {
		selectedItem_ = position;

		// Recompute the view
		this.notifyDataSetChanged();
	}


	@Override
	public int getCount() {
		return newsProvider_.getCount();
	}

	@Override
	public NewsItem getItem(int position) {
		return newsProvider_.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return newsProvider_.getItemId(position);
	}


}
