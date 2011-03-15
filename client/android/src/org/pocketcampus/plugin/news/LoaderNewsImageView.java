package org.pocketcampus.plugin.news;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LoaderNewsImageView extends LinearLayout {
	

	private static final int COMPLETE = 0;
	private static final int FAILED = 1;

	private Context mContext_;
	private Drawable mDrawable_;
	private ProgressBar mSpinner_;
	private ImageView mImage_;
	
	private NewsItem newsItem_;
	
	/**
	 * This is used when creating the view in XML
	 * To have an image load in XML use the tag 'src="http://developer.android.com/images/dialog_buttons.png"'
	 * Replacing the url with your desired image
	 * Once you have instantiated the XML view you can call
	 * setImageDrawable(url) to change the image
	 * @param context
	 * @param attrSet
	 */
	public LoaderNewsImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);
		final String url = attrSet.getAttributeValue(null, "src");
		if(url != null){
			instantiate(context, url);
		} else {
			instantiate(context, null);
		}
	}
	
	/**
	 * This is used when creating the view programatically
	 * Once you have instantiated the view you can call
	 * setImageDrawable(url) to change the image
	 * @param context the Activity context
	 * @param imageUrl the Image URL you wish to load
	 */
	public LoaderNewsImageView(final Context context, final String imageUrl) {
		super(context);
		instantiate(context, imageUrl);		
	}

	/**
	 *  First time loading of the LoaderImageView
	 *  Sets up the LayoutParams of the view, you can change these to
	 *  get the required effects you want
	 */
	private void instantiate(final Context context, final String imageUrl) {
		mContext_ = context;
		
		mImage_ = new ImageView(mContext_);
		mImage_.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mSpinner_ = new ProgressBar(mContext_);
		mSpinner_.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mSpinner_.setPadding(5, 5, 5, 5);
			
		mSpinner_.setIndeterminate(true);
		
		addView(mSpinner_);
		addView(mImage_);
		
		if(imageUrl != null){
			setImageDrawable(imageUrl);
		}
	}


	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * @param imageUrl the url of the image you wish to load
	 */
	private void setImageDrawable(final String imageUrl) {
		mDrawable_ = null;
		mSpinner_.setVisibility(View.VISIBLE);
		mImage_.setVisibility(View.GONE);
		new Thread(){
			public void run() {
				try {
					mDrawable_ = getDrawableFromUrl(imageUrl);
					imageLoadedHandler_.sendEmptyMessage(COMPLETE);
				} catch (MalformedURLException e) {
					imageLoadedHandler_.sendEmptyMessage(FAILED);
				} catch (IOException e) {
					imageLoadedHandler_.sendEmptyMessage(FAILED);
				}
			};
		}.start();
	}

	public void setImageDrawable(NewsItem newsItem) {
		this.newsItem_ = newsItem;
		Drawable draw = newsItem.getImageDrawable();
		
		if(draw != null) {
			setImage(draw);
		} else {
			String imageUri = newsItem.getImageUri();
			if(imageUri != null) {
				this.setImageDrawable(imageUri);
			} else {
				this.setNoImage();
			}
		}
	}


	public void setNoImage() {
		mDrawable_ = null;
		mSpinner_.setVisibility(View.GONE);
		mImage_.setVisibility(View.GONE);
	}
	
	/**
	 * Callback that is received once the image has been downloaded
	 */
	private final Handler imageLoadedHandler_ = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case COMPLETE:
					setImage(mDrawable_);
	
					if(newsItem_ != null) {
						newsItem_.setImageDrawable(mDrawable_);
					}
					
					break;
				case FAILED:
				default:
					// Could change image here to a 'failed' image
					// otherwise will just keep on spinning
					break;
			}
			return true;
		}		
	});
	
	private void setImage(Drawable drawable) {
		mImage_.setImageDrawable(drawable);
		mImage_.setVisibility(View.VISIBLE);
		mSpinner_.setVisibility(View.GONE);
	}

	/**
	 * Pass in an image url to get a drawable object
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), url);
	}

}
