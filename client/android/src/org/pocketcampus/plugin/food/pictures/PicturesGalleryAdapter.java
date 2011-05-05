/*
 ********************* [ P O C K E T C A M P U S ] *****************
 * [    LICENCE    ]    see "licence"-file in the root directory
 * [   MAINTAINER  ]    jonathan.baeriswyl@epfl.ch
 * [     STATUS    ]    used 
 *
 **************************[ C O M M E N T S ]**********************
 *
 * Adapter used for the pictureGallery_gallery 
 * get a collection of string url to pictures
 * convert it in a tab
 *                 
 *******************************************************************
 */
package org.pocketcampus.plugin.food.pictures;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

import org.pocketcampus.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


public class PicturesGalleryAdapter extends BaseAdapter{
	/** The parent context */
	private Context myContext;
	/** URL-Strings to some remote images. */
	private String[] myRemotePictures;

	/** Constructor. */
	public PicturesGalleryAdapter(Context c, Collection<String> imagesURLcol){ 
		this.myContext = c;
		this.myRemotePictures = new String[imagesURLcol.size()];
		Iterator<String> iter = imagesURLcol.iterator();
		int index = 0;
		while(iter.hasNext()){
			myRemotePictures[index] = iter.next();
			index++;
		}
	}

	/** Returns the amount of images we have defined. */
	public int getCount(){ 
		return this.myRemotePictures.length; 
	}

	/* Use the array-Positions as unique IDs */
	public Object getItem(int position){ 
		return position; 
	}
	
	public long getItemId(int position){ 
		return position; 
	}
	
	public String getSelectedImage(int position){
		return myRemotePictures[position];
	}

	/** Returns a new ImageView to
	 * be displayed, depending on
	 * the position passed. */
	public View getView(int position, View convertView, ViewGroup parent){
		ImageView myImageView = new ImageView(this.myContext);
		try {
			/* Open a new URL and get the InputStream to load data from it. */
			URL aURL = new URL(myRemotePictures[position]);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream iStream = conn.getInputStream();
			/* Buffered is always good for a performance plus. */
			BufferedInputStream bis = new BufferedInputStream(iStream);
			
			/*Create options, to get the pictures, 
			 * inSampleSize is used to avoid the VM out of memory problem*/
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			options.inDensity = 1024;
			/* Decode url-data to a bitmap. */
			Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
			bis.close();
			iStream.close();
			/* Apply the Bitmap to the ImageView that will be returned. */
			myImageView.setImageBitmap(bm);
		} catch (IOException e) {
//			myImageView.setImageResource(R.drawable.picturegallery_loaderrorpicture);
			Log.e("PICTURE GALLERY", "LOADING PICTURE EXCEPTION", e);
		}

		/* Image should be scaled as width/height are set. */
		myImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		/* Set the Width/Height of the ImageView. */
		myImageView.setLayoutParams(new Gallery.LayoutParams(500, 500));
		return myImageView;
	}

	/** Returns the size (0.0f to 1.0f) of the views
	 * depending on the 'offset' to the center. */
	public float getScale(boolean focused, int offset) {
		/* Formula: 1 / (2 ^ offset) */
		return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
	}
}
