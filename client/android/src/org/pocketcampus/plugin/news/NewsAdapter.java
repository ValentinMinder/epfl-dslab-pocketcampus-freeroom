package org.pocketcampus.plugin.news;

import java.util.Comparator;
import java.util.List;

import org.pocketcampus.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
	

	private LayoutInflater mInflater_;
	private FeedDownloader downloader;

	public NewsAdapter(Context context, int textViewResourceId, List<NewsItem> items) {
		super(context, textViewResourceId);

		this.setNotifyOnChange(false);
		
		mInflater_ = LayoutInflater.from(context);
		
		downloader = new FeedDownloader(this);
		downloader.execute("http://actu.epfl.ch/feeds/rss/mediacom/fr/", "http://feeds.nytimes.com/nyt/rss/HomePage");
	}
	
	public void setDebugData() {
		Log.d(NewsAdapter.class.toString(), "Adding debug news data");
		this.add(new NewsItem("Les cristaux liquides se r�inventent � l�EPFL", "Un laboratoire de l�EPFL a mis au point une nouvelle technologie, bas�e sur l�optofluidique, pour am�liorer les performances des affichages LCD et des syst�mes de traitement de l'information optiques. Les chercheurs visent un taux de rafra�chissement de l�ordre du kilohertz, dix fois plus rapide qu�avec la technologie � l��uvre dans les t�l�viseurs.", "http://actu.epfl.ch/news/les-cristaux-liquides-se-reinventent-a-lepfl/", "2011-02-01", "http://actu.epfl.ch/image/2129/324x182.jpg"));
		this.add(new NewsItem("L�informatique, un univers d�sesp�r�ment masculin?", "L�Agence onusienne pour les technologies de l�information et de la communication (ITU) a organis� un d�bat sur la faible participation f�minine dans son domaine. Aussi bien � l�EPFL que dans les autres universit�s suisses, les �tudiantes ne repr�sentent que 13 � 15% des effectifs. Anastasia Ailamaki, professeure responsable du Laboratoire de syst�mes et applications de traitement de donn�es massives �tait invit�e. Elle nous donne quelques pistes de r�flexion.", "http://actu.epfl.ch/news/linformatique-un-univers-desesperement-masculin", "2011-03-02", "http://actu.epfl.ch/image/2136/324x182.jpg"));
		this.add(new NewsItem("Des anticanc�reux pour combattre la malaria?", "Une nouvelle classe de m�dicaments utilis�s dans le cadre de la chimioth�rapie contre le cancer est �galement active contre le paludisme.", "http://actu.epfl.ch/news/des-anticancereux-pour-combattre-la-malaria", "2011-03-01", null));
		
		this.dataSetChanged();
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater_.inflate(R.layout.news_newsentry, null);
		}
		
		final NewsItem r = getItem(position);
		
		if (r != null) {
			TextView tv;
			
			tv = (TextView) v.findViewById(R.id.news_item_title);
			tv.setText(r.getTitle());
			
			tv = (TextView) v.findViewById(R.id.news_item_description);
			tv.setText(r.getDescription());
			
			LoaderImageView liv = (LoaderImageView) v.findViewById(R.id.news_item_image);
			String imageUri = r.getImage();
			if(imageUri != null) {
				liv.setImageDrawable(imageUri);
			} else {
				liv.setNoImage();
			}
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
