package org.pocketcampus.plugin.news.gui;

import java.util.List;

import org.pocketcampus.R;
//import org.pocketcampus.android.platform.sdk.ui.adapter.AbstractFeedAdapter;
import org.pocketcampus.plugin.news.shared.NewsItem;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StandardFeedAdapter {//extends AbstractFeedAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	public StandardFeedAdapter(Context context, List<? extends Object> items) {
//		super(context, items);
		mInflater = LayoutInflater.from(context);

		mContext = context;
	}

//	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.sdk_list_entry_feed, null);

			// if (!showImage_) {
			// v.findViewById(R.id.news_item_image).setVisibility(View.GONE);
			// }
		}

		// The item to display
		final NewsItem newsItem = getItem(position);

		if (newsItem != null) {
			TextView tv;

			tv = (TextView) v.findViewById(R.id.sdk_list_entry_feed_title);
			tv.setText(Html.fromHtml(newsItem.getTitle()));

			tv = (TextView) v
					.findViewById(R.id.sdk_list_entry_feed_description);

			tv.setText(newsItem.getDescription());

			// LoaderNewsImageView liv = (LoaderNewsImageView) v
			// .findViewById(R.id.news_item_image);
			// if (showImage_) {
			// liv.setTag(newsItem); // This view shows this NewsItem
			// liv.setNewItem(newsItem);
			// }

			// "Read more" textview, shown only on the selected item
			// TextView more = (TextView) v.findViewById(R.id.news_readmore);

			// if (selectedItem_ == position) {
			//
			// more.setVisibility(View.VISIBLE);
			//
			// more.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			//
			// Intent i = new Intent(Intent.ACTION_VIEW, Uri
			// .parse(newsItem.getLink()));
			// context_.startActivity(i);
			// }
			// });
			//
			// } else {
			// more.setVisibility(View.GONE);
			// }

		}

		return v;

	}

	public NewsItem getItem(int position) {
		return null;
	}

}
