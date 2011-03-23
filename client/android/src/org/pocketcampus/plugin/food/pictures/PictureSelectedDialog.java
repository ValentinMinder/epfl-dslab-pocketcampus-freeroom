/**
 * 
 */
package org.pocketcampus.plugin.food.pictures;


//public class PictureSelectedDialog extends Dialog{
/*String urlSelectedPic;
	public PictureSelectedDialog(Context context, String urlSelectedPicture) {
		super(context);
		urlSelectedPic = urlSelectedPicture;
		
		// Dialog box will have no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.picture_selected_dialog);
		setCanceledOnTouchOutside(true);
		
		ImageView myImageView = (ImageView) findViewById(R.id.PictureSelected_ImageView);
		TextView myTextDateView = (TextView) findViewById(R.id.PictureSelected_TextOfDateView);
		
		/* Open a new URL and get the InputStream to load data from it. */
/*		URL aURL;
		try {
			aURL = new URL(urlSelectedPic);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream iStream = conn.getInputStream();
			
			long lastModified = conn.getLastModified();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(lastModified);
			
			/* Buffered is always good for a performance plus. */
			/*BufferedInputStream bis = new BufferedInputStream(iStream);
			
			Bitmap myBitmap = BitmapFactory.decodeStream(bis);

			myImageView.setImageBitmap(myBitmap);
			
			String taken = context.getResources().getString(R.string.resto_picture_taken);
			
			myTextDateView.setText(taken + cal.get(Calendar.DAY_OF_MONTH)+"/"
														+(cal.get(Calendar.MONTH)+1)+"/"
														+cal.get(Calendar.YEAR)+"\n"
														+"at "+cal.get(Calendar.HOUR_OF_DAY)+":"
														+cal.get(Calendar.MINUTE)+":"
														+cal.get(Calendar.SECOND));
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}*/
//}
