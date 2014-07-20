package org.pocketcampus.plugin.edx.android;

import org.pocketcampus.plugin.edx.R;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * EdXMainView - Main view that shows EdX courses.
 * 
 * This is the main view in the EdX Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's EdX data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXLoginView extends PluginView implements IEdXView {

	//private EdXController mController;
	private EdXModel mModel;
	
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return EdXController.class;
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
		//Tracker
		//Tracker.getInstance().trackPageView("edx");
		
		// Get and cast the controller and model
		//mController = (EdXController) controller;
		mModel = (EdXModel) controller.getModel();

		

		// The ActionBar is added automatically when you call setContentView, unless we disable it :-)
		disableActionBar();
		
		
		displayForm();


	}

	
	

	/**
	 * Displays the authentication form.
	 */
	private void displayForm() {
		setContentView(R.layout.edx_customloginpage);
		
		TextView usernameField = (TextView) findViewById(R.id.edx_username);
		usernameField.setText(mModel.getEmail());
		TextView passwordField = (TextView) findViewById(R.id.edx_password);
		passwordField.setText("");
		
		Button loginButton = (Button) findViewById(R.id.edx_loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView usernameField = (TextView) findViewById(R.id.edx_username);
				TextView passwordField = (TextView) findViewById(R.id.edx_password);
				mModel.setCredentials(usernameField.getText().toString(), passwordField.getText().toString());
				finish();
			}
		});
	}

	
	
	
	
	@Override
	public void userCoursesUpdated() {
	}
	@Override
	public void courseSectionsUpdated() {
	}
	@Override
	public void moduleDetailsUpdated() {
	}
	@Override
	public void activeRoomsUpdated() {
	}

	
	
	

	
	
	
	@Override
	public void networkErrorHappened() {
	}
	@Override
	public void networkErrorCacheExists() {
	}
	@Override
	public void upstreamServerFailure() {
	}
	@Override
	public void serverFailure() {
	}

	
	
	
	@Override
	public void userCredentialsUpdated() {
	}
	@Override
	public void loginSucceeded() {
	}
	@Override
	public void loginFailed() {
	}
	@Override
	public void sessionTimedOut() {
	}


	@Override
	protected String screenName() {
		// TODO Auto-generated method stub
		return null;
	}

}
