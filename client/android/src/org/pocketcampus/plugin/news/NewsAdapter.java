package org.pocketcampus.plugin.news;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
	

	private LayoutInflater mInflater_;
	private FeedDownloader downloader_;

	public NewsAdapter(Context context, int textViewResourceId, List<NewsItem> items) {
		super(context, textViewResourceId);

		this.setNotifyOnChange(false);
		
		mInflater_ = LayoutInflater.from(context);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		String[] urls = context.getResources().getStringArray(R.array.news_feeds_url);
		
		ArrayList<String> urlsToDownload = new ArrayList<String>();
		
		for(String url: urls) {
			if(prefs.getBoolean("load_rss" + url, true)) {
				urlsToDownload.add(url);
			}
		}
		
		downloader_ = new FeedDownloader(this);
		downloader_.execute(urlsToDownload.toArray(new String[0]));
	}
	
	public void setDebugData() {
		Log.d(NewsAdapter.class.toString(), "Adding debug news data");
		this.add(new NewsItem("Les cristaux liquides se réinventent à l’EPFL", "Un laboratoire de l’EPFL a mis au point une nouvelle technologie, basée sur l’optofluidique, pour améliorer les performances des affichages LCD et des systèmes de traitement de l'information optiques. Les chercheurs visent un taux de rafraîchissement de l’ordre du kilohertz, dix fois plus rapide qu’avec la technologie à l’œuvre dans les téléviseurs.", "http://actu.epfl.ch/news/les-cristaux-liquides-se-reinventent-a-lepfl/", "2011-02-01", "http://actu.epfl.ch/image/2129/324x182.jpg"));
		this.add(new NewsItem("L’informatique, un univers désespérément masculin?", "L’Agence onusienne pour les technologies de l’information et de la communication (ITU) a organisé un débat sur la faible participation féminine dans son domaine. Aussi bien à l’EPFL que dans les autres universités suisses, les étudiantes ne représentent que 13 à 15% des effectifs. Anastasia Ailamaki, professeure responsable du Laboratoire de systèmes et applications de traitement de données massives était invitée. Elle nous donne quelques pistes de réflexion.", "http://actu.epfl.ch/news/linformatique-un-univers-desesperement-masculin", "2011-03-02", "http://actu.epfl.ch/image/2136/324x182.jpg"));
		this.add(new NewsItem("Des anticancéreux pour combattre la malaria?", "Une nouvelle classe de médicaments utilisés dans le cadre de la chimiothérapie contre le cancer est également active contre le paludisme.", "http://actu.epfl.ch/news/des-anticancereux-pour-combattre-la-malaria", "2011-03-01", null));
		
		this.dataSetChanged();
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		//if (v == null) {
			v = mInflater_.inflate(R.layout.news_newsentry, null);
		//}
		
		final NewsItem newsItem = getItem(position);
		
		if (newsItem != null) {
			TextView tv;
			
			tv = (TextView) v.findViewById(R.id.news_item_title);
			tv.setText(newsItem.getTitle());
			
			tv = (TextView) v.findViewById(R.id.news_item_description);
			tv.setText(newsItem.getDescription());
			
			LoaderNewsImageView liv = (LoaderNewsImageView) v.findViewById(R.id.news_item_image);
			liv.setImageDrawable(newsItem);
		}
		
		return v;
		
	}
	
	protected void dataSetChanged() {
		this.sortNews();
		this.notifyDataSetChanged();
	}
	
	private void sortNews() {
		this.sort(new Comparator<NewsItem>() {

			@Override
			public int compare(NewsItem item1, NewsItem item2) {
				try {
					return item1.getPubDate().compareTo(item2.getPubDate());
				} catch(NullPointerException e) {
					return 0;
				}
			}
			
		});
	}

}
