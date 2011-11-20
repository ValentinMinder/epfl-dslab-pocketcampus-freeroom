//package org.pocketcampus.plugin.news.android;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//
///**
// * Displays an image coming from a NewsItem. Show a spinner while the image is
// * downloading. Once the image is downloaded, the Drawable object is set to the
// * NewsItem to prevent it to redownload.
// * 
// * @status complete
// * 
// * @author Jonas
// * 
// */
//public class LoaderNewsImageView extends LinearLayout {
//
//	// Status
//	private static final int COMPLETE = 0;
//	private static final int FAILED = 1;
//
//	private Context mContext_;
//	private Drawable mDrawable_;
//	private ProgressBar mSpinner_;
//	private ImageView mImage_;
//
//	private NewsItemWithImage newsItem_;
//
//	/**
//	 * This is used when creating the view in XML To have an image load in XML
//	 * use the attribute
//	 * 'src="http://developer.android.com/images/dialog_buttons.png"'
//	 * 
//	 * @param context
//	 * @param attrSet
//	 */
//	public LoaderNewsImageView(final Context context, final AttributeSet attrSet) {
//		super(context, attrSet);
//		final String url = attrSet.getAttributeValue(null, "src");
//		if (url != null) {
//			instantiate(context, url);
//		} else {
//			instantiate(context, null);
//		}
//	}
//
//	/**
//	 * This is used when creating the view programatically
//	 * 
//	 * @param context
//	 *            the Activity context
//	 * @param imageUrl
//	 *            the Image URL you wish to load
//	 */
//	public LoaderNewsImageView(final Context context, final String imageUrl, NewsItemWithImage newsItem) {
//		super(context);
//		instantiate(context, imageUrl);
//		this.newsItem_^
//	}
//
//	/**
//	 * First time loading of the LoaderImageView Sets up the LayoutParams of the
//	 * view
//	 * 
//	 * @param context
//	 * @param imageUrl
//	 */
//	private void instantiate(final Context context, final String imageUrl) {
//		mContext_ = context;
//
//		mImage_ = new ImageView(mContext_);
//		mImage_.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT));
//
//		mSpinner_ = new ProgressBar(mContext_);
//		mSpinner_.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT));
//		mSpinner_.setPadding(15, 5, 5, 5);
//		mSpinner_.setIndeterminate(true);
//
//		addView(mSpinner_);
//		addView(mImage_);
//
//		// Already set the image to download
//		if (imageUrl != null) {
//			if (newsItem_.getDrawable() == null) {
//				setImageDrawable(imageUrl);
//			} else {
//				mDrawable_ = newsItem_.getDrawable();
//				mSpinner_.setVisibility(View.VISIBLE);
//				mImage_.setVisibility(View.GONE);
//			}
//		}
//	}
//
//	/**
//	 * Sets the view's drawable
//	 * 
//	 * @param imageUrl
//	 *            the url of the image to load
//	 */
//	private void setImageDrawable(final String imageUrl) {
//		mDrawable_ = null;
//		mSpinner_.setVisibility(View.VISIBLE);
//		mImage_.setVisibility(View.GONE);
//
//		// Download the image on a new thread
//		new Thread() {
//			public void run() {
//				try {
//					mDrawable_ = getDrawableFromUrl(imageUrl);
//					imageLoadedHandler_.sendEmptyMessage(COMPLETE);
//				} catch (MalformedURLException e) {
//					imageLoadedHandler_.sendEmptyMessage(FAILED);
//				} catch (IOException e) {
//					imageLoadedHandler_.sendEmptyMessage(FAILED);
//				}
//			};
//		}.start();
//	}
//
//	/**
//	 * Set the NewsItem from where to get the image URL
//	 * 
//	 * @param newsItem
//	 *            The NewsItem to use
//	 */
//	public void setNewItem(NewsItemWithImage newsItem) {
//		this.newsItem_ = newsItem;
//
//		// Check if the image is not already downloaded
//		Drawable draw = newsItem.getDrawable();
//		if (draw != null) {
//			setImage(draw);
//		} else {
//			// Start the download
//			String imageUri = newsItem.getNewsItem().getImageUrl();
//			if (imageUri != null) {
//				this.setImageDrawable(imageUri);
//			} else {
//				this.setNoImage();
//			}
//		}
//	}
//
//	/**
//	 * To show no image
//	 */
//	public void setNoImage() {
//		mDrawable_ = null;
//		mSpinner_.setVisibility(View.GONE);
//		mImage_.setVisibility(View.GONE);
//		this.setVisibility(View.GONE);
//		// XXX Try to fix this. The view should be gone when there is no image
//		// to display
//	}
//
//	/**
//	 * Callback that is received once the image has been downloaded
//	 */
//	private final Handler imageLoadedHandler_ = new Handler(new Callback() {
//		@Override
//		public boolean handleMessage(Message msg) {
//			switch (msg.what) {
//			case COMPLETE:
//				setImage(mDrawable_);
//
//				if (newsItem_ != null) {
//					newsItem_.setDrawable(mDrawable_);
//				}
//
//				break;
//			case FAILED:
//			default:
//				setNoImage();
//				break;
//			}
//			return true;
//		}
//	});
//
//	/**
//	 * Set the image into the view
//	 * 
//	 * @param drawable
//	 *            The image to set
//	 */
//	private void setImage(Drawable drawable) {
//
//		// Check if the view shows the current NewsItem
//		if (this.getTag() != null && this.getTag().equals(newsItem_)) {
//			mImage_.setImageDrawable(drawable);
//			mImage_.setVisibility(View.VISIBLE);
//			mSpinner_.setVisibility(View.GONE);
//			this.setVisibility(View.VISIBLE);
//		}
//	}
//
//	/**
//	 * Pass in an image url to get a drawable object
//	 * 
//	 * @return a drawable object
//	 * @throws IOException
//	 * @throws MalformedURLException
//	 */
//	private static Drawable getDrawableFromUrl(final String url)
//			throws IOException, MalformedURLException {
//		return Drawable
//				.createFromStream(((java.io.InputStream) new java.net.URL(url)
//						.getContent()), url);
//	}
//
//	public Drawable resizeDrawable(Drawable d) {
//		Bitmap bitmapOrg = ((BitmapDrawable) d).getBitmap();
//
//		int width = bitmapOrg.getWidth();
//		int height = bitmapOrg.getHeight();
//		int newWidth = 50;
//		int newHeight = 50;
//
//		// calculate the scale - in this case = 0.4f
//		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
//
//		// create a matrix for the manipulation
//		Matrix matrix = new Matrix();
//		// resize the bit map
//		matrix.postScale(scaleWidth, scaleHeight);
//		// rotate the Bitmap
//		matrix.postRotate(45);
//
//		// recreate the new Bitmap
//		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
//				height, matrix, true);
//
//		// make a Drawable from Bitmap to allow to set the BitMap
//		// to the ImageView, ImageButton or what ever
//		BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
//
//		return bmd;
//	}
//
//}
