package org.pocketcampus.platform.android.ui.adapter;

import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.utils.Callback;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LazyAdapter extends BaseAdapter {
    
    private Context activity;
    private List<Map<String, ?>> data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader; 
    public final static String NOT_SELECTABLE = "NOT_SELECTABLE";  
    public final static String LINK_CLICKABLE = "LINK_CLICKABLE";  
    
    private int resourceToInflate;
    private String[] fromKeys;
    private int[] toViews;
    private int noImage;
    private int imageOnLoading;
    private int imageForEmptyUri;
    private int imageOnFail;
    
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
    public LazyAdapter(Context a, List<Map<String, ?>> d, int resource, String[] from, int[] to) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //imageLoader=new ImageLoader(activity.getApplicationContext());
        resourceToInflate = resource;
        fromKeys = from;
        toViews = to;
        
	    //noImage = android.R.drawable.ic_menu_recent_history;
	    //noImage = android.R.drawable.ic_menu_add;
	    noImage = android.R.drawable.ic_menu_gallery;
//	    imageOnLoading = android.R.drawable.ic_popup_sync;
	    imageForEmptyUri = android.R.drawable.ic_menu_gallery;
	    imageOnFail = android.R.drawable.ic_menu_report_image;
		createOptions();
    }
    
    private void createOptions() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageOnLoading)
				.showImageForEmptyUri(imageForEmptyUri)
				.showImageOnFail(imageOnFail)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
    }
    
    public LazyAdapter setStubImage(int resourceId) {
	    imageOnLoading = resourceId;
		createOptions();
    	return this;
    }

    public LazyAdapter setImageForEmptyUri(int resourceId) {
	    imageForEmptyUri = resourceId;
		createOptions();
    	return this;
    }

    public LazyAdapter setImageOnFail(int resourceId) {
	    imageOnFail = resourceId;
		createOptions();
    	return this;
    }

    public LazyAdapter setNoImage(int resourceId) {
    	noImage = resourceId;
    	return this;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public boolean areAllItemsSelectable() {  
        return false;  
    }  
  
    public boolean isEnabled(int position) {  
        Map<String, ?> song = data.get(position);
        return (song.get(NOT_SELECTABLE) == null);
    }  
    
    public static class Actuated {
    	public Actuated(Object original, Actuator callback) {
    		this.original = original;
    		this.callback = callback;
    	}
    	public Object original;
    	public Actuator callback;
    }
    
    public static interface Actuator {
    	void triggered();
    }
    
    public static class Customizer {
		public Customizer(Object original, Callback<View> callback) {
			this.original = original;
			this.callback = callback;
		}

		public Object original;
		public Callback<View> callback;
	}
      
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null){
            vi = inflater.inflate(resourceToInflate, null);
        }
        
        Map<String, ?> song = data.get(position);
        
        for(int i = 0; i < fromKeys.length && i < toViews.length; i++) {
        	View containerView = vi.findViewById(toViews[i]);
        	Object contentData = song.get(fromKeys[i]);
        	
        	if (containerView == null)
				continue;
        	
        	Callback<View> customizer = null;
			if (contentData != null && contentData instanceof Customizer) {
				customizer = ((Customizer) contentData).callback;
				contentData = ((Customizer) contentData).original;
			}

			if (contentData != null && contentData instanceof Actuated) {
				final Actuator actuator = ((Actuated) contentData).callback;
				contentData = ((Actuated) contentData).original;
				containerView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						actuator.triggered();
					}
				});
			}
        	
			if (containerView instanceof TextView) {
        		if(contentData == null) {
        			((TextView) containerView).setVisibility(View.GONE);
        		} else {
           			((TextView) containerView).setText(Html.fromHtml(contentData.toString()));
           			if(song.get(LINK_CLICKABLE) != null)
           				((TextView) containerView).setMovementMethod(LinkMovementMethod.getInstance());
        			((TextView) containerView).setVisibility(View.VISIBLE);
        		}
        	} else if(containerView instanceof ImageView) {
        		String imgUrl = "drawable://" + noImage;
        		((ImageView) containerView).setVisibility(View.VISIBLE);
        		if(contentData == null) {
        		} else if(contentData instanceof Integer) {
        			if(contentData.equals(-1))
        				((ImageView) containerView).setVisibility(View.GONE);
        			imgUrl = "drawable://" + contentData.toString();
        		} else if(contentData instanceof String) {
        			imgUrl = (String) contentData;
        		}
        		if(View.VISIBLE == ((ImageView) containerView).getVisibility())
        			imageLoader.displayImage(imgUrl, (ImageView) containerView, options);
        	}
        	 
			if (customizer != null)
				customizer.callback(containerView);
        	 
        }

        return vi;
    }
}
