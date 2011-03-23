///*
// ********************* [ P O C K E T C A M P U S ] *****************
// * [    LICENCE    ]    see "licence"-file in the root directory
// * [   MAINTAINER  ]    jonathan.baeriswyl@epfl.ch
// * [     STATUS    ]    used
// *
// **************************[ C O M M E N T S ]**********************
// *
// * Activity that get the pictures corresponding to a meal from the server
// * It get all the URL-Images corresponding to a meal or a queue
// * then pass it to the adapter class, which will
// * add the picture to the gallery
// *                 
// *******************************************************************
// */
//package org.pocketcampus.plugin.food.pictures;
//
//import java.util.Collection;
//import java.util.Vector;
//
//import org.pocketcampus.R;
//import org.pocketcampus.serverapi.ServerAPI;
//import org.pocketcampus.shared.exceptions.ServerException;
//import org.pocketcampus.shared.restaurant.Meal;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Gallery;
//import android.widget.AdapterView.OnItemClickListener;
//
//public class SlidingImageActivity extends Activity {
//
//	// put the downloaded file here
//	private Gallery myGal;
//	private Collection<String> imagesURLcol;
//	private boolean wantToSeeMeal;
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.picture_sliding_images);
//
//		/*
//		 * The gallery used to print the pictures, we'll add the adapter when we
//		 * get all url string
//		 */
//		myGal = (Gallery) findViewById(R.id.pictureGallery_gallery);
//		myGal.setSpacing(20);
//
//		// set the adapter with the corresponding url pictures
//		final BaseAdapter myGalAdapter = new PicturesGalleryAdapter(this,
//				imagesURLcol);
//		myGal.setAdapter(myGalAdapter);
//
//		myGal.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int position,
//					long id) {
//				String selectedImage = (String) ((PicturesGalleryAdapter) myGalAdapter)
//						.getSelectedImage(position);
//				PictureSelectedDialog dialog = new PictureSelectedDialog(v
//						.getContext(), selectedImage);
//				dialog.show();
//			}
//		});
//
//	}
//}
