package org.pocketcampus.plugin.food.pictures;

/**
 * In charge of taking a picture and sending it out to the server.
 * Activity won't actually be displaying anything, just waiting to handle 
 * the Android camera activity result.
 *  
 * @author Elodie
 * @status working
 */
import java.io.File;

import org.pocketcampus.core.communication.FileUpload;
import org.pocketcampus.core.communication.FileUploadParameters;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.food.pictures.CameraCapture.PictureType;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class PictureTaker {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;
	private static Uri imageUri;
	private static Meal meal_;
	private static Activity context_;
	private static PictureType type_;

	public PictureTaker(Activity callingActivity, Meal meal, PictureType type) {
		context_ = callingActivity;
		meal_ = meal;
		type_ = type;
	}

	public void takePicture() {
		Intent intent = new Intent(context_, CameraCapture.class);
		context_.startActivity(intent);
	}

	public static void uploadImage(File image) {
		FileUpload request = new FileUpload() {

			@Override
			protected void doInUiThread(String result) {
				if (result != null) {
					// Do something in the UI
					Toast.makeText(context_, "Submitted", Toast.LENGTH_SHORT)
							.show();
				} else {
					// An error occured, and the field 'exception_' should
					// contain something
				}
			}

			@Override
			protected void cancelled() {
				// Generally thrown when a timeout occured
			}
		};

		// We put the file into a FileUploadParameters
		FileUploadParameters params = new FileUploadParameters();
		params.addParameter("image", image);

		FoodPlugin.getFoodRequestHandler().execute(request, "uploadimage",
				params);
		// 'uploadimage' is a method in the 'Test' class on the server (check it
		// out!)
	}


}
