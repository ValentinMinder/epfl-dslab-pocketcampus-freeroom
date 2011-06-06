package org.pocketcampus.plugin.mainscreen;

import java.util.SortedSet;

import org.pocketcampus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainscreenAdapter extends BaseAdapter {
	

		// Misc
		private LayoutInflater mInflater_;
		
		private SortedSet<MainscreenNews> news_;
		
		private Context ctx_;

		/**
		 * Adapter constructor
		 * @param context Context of the application
		 * @param items Items that have to be on the list
		 */
		public MainscreenAdapter(Context context, SortedSet<MainscreenNews> list) {
			super();
			
			this.ctx_ = context;
			
			this.news_ =  list;
						
			mInflater_ = LayoutInflater.from(context);
						
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = mInflater_.inflate(R.layout.mainscreen_news, null);
			}

			// The item to display
			final MainscreenNews item = (MainscreenNews) getItem(position);

			if (item != null) {
				TextView tv;

				tv = (TextView) v.findViewById(R.id.mainscreen_news_title);
				tv.setText((item.getTitle_()));

				tv = (TextView) v.findViewById(R.id.mainscreen_news_description);
				
				tv.setText(item.getContent_());
				tv.setMaxLines(2);

				ImageView iv = (ImageView) v.findViewById(R.id.mainscree_news_image);
				
				iv.setImageDrawable(item.getPlugin_().getPluginInfo().getIcon().getDrawable(ctx_));
				
			}

			return v;

		}


		@Override
		public int getCount() {
			return news_.size();
		}

		@Override
		public Object getItem(int position) {
			return news_.toArray()[position];
		}

		@Override
		public long getItemId(int position) {
			return ((MainscreenNews)getItem(position)).getId_();
		}


}
