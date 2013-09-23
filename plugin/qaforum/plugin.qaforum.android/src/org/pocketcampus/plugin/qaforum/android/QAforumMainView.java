package org.pocketcampus.plugin.qaforum.android;

import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.plugin.qaforum.android.activity.AskActivity;
import org.pocketcampus.plugin.qaforum.android.activity.HelpActivity;
import org.pocketcampus.plugin.qaforum.android.activity.SettingActivity;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * QAforumMainView - Main view of QAforum .
 * 
 * This is the main view in the QAforum Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's QAforum data.
 * 
 * It has four functions: Ask, Record, Forum and Pending
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class QAforumMainView extends PluginView implements IQAforumView {

	private QAforumController mController;
	private QAforumModel mModel;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;

	private TextView titleView;

	
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("qaforum");
		
		// Get and cast the controller and model
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();

		setContentView(R.layout.qaforum_main);
		setTitle(getResources().getString(R.string.qaforum_plugin_title));
		
		button1=(Button)findViewById(R.id.button1);
		button2=(Button)findViewById(R.id.Button01);
		button3=(Button)findViewById(R.id.button2);
		button4=(Button)findViewById(R.id.button4);
		textView1=(TextView)findViewById(R.id.textView1);
		textView2=(TextView)findViewById(R.id.textView2);
		textView3=(TextView)findViewById(R.id.textView3);
		textView4=(TextView)findViewById(R.id.textView4);
		textView5=(TextView)findViewById(R.id.textView5);
		
		titleView=(TextView)findViewById(R.id.standard_titled_layout_title);
		
		textView5.setVisibility(View.GONE);
		
		titleView.setVisibility(View.GONE);
		if (mModel.getSessionid()==null) {
			loadingSession();
		}
	}
	
	
	
	private void normalState() {
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		textView1.setVisibility(View.VISIBLE);
		textView2.setVisibility(View.VISIBLE);
		textView3.setVisibility(View.VISIBLE);
		textView4.setVisibility(View.VISIBLE);
		textView5.setVisibility(View.GONE);
		titleView.setVisibility(View.VISIBLE);
		
	}
	
	private void loadingState() {
		
		button1.setVisibility(View.GONE);
		button2.setVisibility(View.GONE);
		button3.setVisibility(View.GONE);
		button4.setVisibility(View.GONE);
		textView1.setVisibility(View.GONE);
		textView2.setVisibility(View.GONE);
		textView3.setVisibility(View.GONE);
		textView4.setVisibility(View.GONE);
		textView5.setText(getResources().getString(R.string.qaforum_loading));
		textView5.setVisibility(View.VISIBLE);
		titleView.setVisibility(View.GONE);
	}
	
	private void loadingSession() {
		loadingState();
		mController.getTequilaToken();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.qaforum_menu_main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.setting:
	        	if (mModel.getSessionid()==null) {
	        			loadingSession();
				}
	        	else {					
	        		Intent intent=new Intent(getApplicationContext(), SettingActivity.class);
	        		startActivity(intent);
				}
	            return true;
	        case R.id.help:
		        	Intent intent=new Intent(getApplicationContext(), HelpActivity.class);
		    		startActivity(intent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	
	public QAforumController getController() {
		return mController;
	}
	
	public void ask(View view){
		if (mModel.getSessionid()==null)
			loadingSession();
		Intent intent = new Intent(getApplicationContext(), AskActivity.class);
	    startActivity(intent);
	}
	
	public void request(View view){
		if (mModel.getSessionid()==null)
			loadingSession();
		loadingState();
		mController.latestquestions(mModel.getSessionid());	
	}
	
	public void myquestions(View view){
		if (mModel.getSessionid()==null) 
			loadingSession();
		loadingState();
		mController.myquestion(mModel.getSessionid());
	}
	
	public void myAnswers(View view) {
		if (mModel.getSessionid()==null)
			loadingSession();
		loadingState();
		mController.pendingNotification(mModel.getSessionid());
	}
	
	@Override
	public void networkErrorHappened() {
		textView5.setText(getResources().getString(R.string.qaforum_connection_error_happened));
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void gotRequestReturn() {
		normalState();
	}

	@Override
	public void loadingFinished() {
		normalState();
	}
	
	@Override
	public void messageDeleted() {
	}
	
	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_authentication_failed), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void userCancelledAuthentication() {
		finish();
	}
}
