package org.pocketcampus.plugin.cloudprint.android;

import java.io.File;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.cloudprint.R;
import org.pocketcampus.plugin.cloudprint.android.iface.ICloudPrintView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CloudPrintMainView - Main view that shows CloudPrint courses.
 * 
 * This is the main view in the CloudPrint Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * It uploads a file to print
 * and prints it
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class CloudPrintMainView extends PluginView implements ICloudPrintView {

	public static final String EXTRA_JOB_ID = "JOB_ID";
	
	
	private CloudPrintController mController;
	private CloudPrintModel mModel;
	
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return CloudPrintController.class;
	}

	/**
	 * Disables the Activity Title.
	 */
	@Override
	protected void onPreCreate() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (CloudPrintController) controller;
		mModel = (CloudPrintModel) controller.getModel();


		// The ActionBar is added automatically when you call setContentView, unless we disable it :-)
		disableActionBar();

	}

	@Override
	protected void handleIntent(Intent aIntent) {
		
		
	    // Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
//	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) /*&& type != null*/) {
//	        if (type.startsWith("application/pdf")) {
//	        }
        	Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
            	mModel.setFileToPrint(imageUri);
            }
	    } else if (intent.hasExtra(EXTRA_JOB_ID)) {
	    	mModel.setPrintJobId(intent.getLongExtra(EXTRA_JOB_ID, 0));
	    	
	    	
	    } else {
	    	finish();
	        // Handle other intents, such as being started from the home screen
	    }
	    
	    
		if(CloudPrintController.sessionExists(this)) { // I think this is no longer necessary, since the auth plugin doesnt blindly redo auth (well, this saves the one call that the auth plugin does to check if the session is valid)
			updateDisplay();
		} else {
			showLoading();
			CloudPrintController.pingAuthPlugin(this);
		}
		
		
		
	}

	@Override
	protected String screenName() {
		return "/cloudprint";
	}



	/**
	 * Displays the waiting screen.
	 */
	private void showLoading() {
		setContentView(R.layout.cloudprint_loading);
	}

	/**
	 * Displays the authentication form.
	 */
	private void updateDisplay() {
		
		if(mModel.getPrintJobId() != null) {
			setContentView(R.layout.cloudprint_print);

//			Spinner sel = (Spinner) findViewById(R.id.cloudprint_select_pageselection);
//			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);

			
			TextView tv = (TextView) findViewById(R.id.cloudprint_preview_print);
			tv.setText(getString(R.string.cloudprint_dialog_text_print, mModel.getPrintJobId()));
			
			Button b = (Button) findViewById(R.id.cloudprint_print_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					showLoading();
					mController.printFileJob(CloudPrintMainView.this, mModel.getPrintJobId());
				}
			});
			
		} else if (mModel.getFileToPrint() != null) {
			setContentView(R.layout.cloudprint_upload);
			
			TextView tv = (TextView) findViewById(R.id.cloudprint_preview_upload);
			tv.setText(getString(R.string.cloudprint_dialog_text_upload, mModel.getFileToPrint().getLastPathSegment()));
			
			Button b = (Button) findViewById(R.id.cloudprint_upload_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					showLoading();
					mController.uploadFileToPrint(CloudPrintMainView.this, new File(mModel.getFileToPrint().getPath()));
				}
			});
			
		} else {
			finish();
		}
		
	}

	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
		updateDisplay();
	}

	@Override
	public void printServerError() {
		Toast.makeText(getApplicationContext(), getString(
				R.string.cloudprint_string_print_server_error), Toast.LENGTH_SHORT).show();
		updateDisplay();
		
	}

	@Override
	public void uploadComplete(long jobId) {
		mModel.setPrintJobId(jobId);
		updateDisplay();
		
	}

	@Override
	public void printedSuccessfully() {
		mModel.setPrintJobId(null);
		mModel.setFileToPrint(null);
		Toast.makeText(getApplicationContext(), getString(
				R.string.cloudprint_string_document_sent_to_printer), Toast.LENGTH_SHORT).show();
		finish();
		
	}

	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(), getString(
				R.string.sdk_authentication_failed), Toast.LENGTH_SHORT).show();
		finish();
	}
	
	@Override
	public void userCancelledAuthentication() {
		finish();
	}
	
	@Override
	public void notLoggedIn() {
		CloudPrintController.pingAuthPlugin(this);
		showLoading();
		
	}

	@Override
	public void authenticationFinished() {
		updateDisplay();
		
	}

	
}
