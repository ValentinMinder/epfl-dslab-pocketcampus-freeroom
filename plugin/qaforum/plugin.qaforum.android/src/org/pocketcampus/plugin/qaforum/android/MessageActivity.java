package org.pocketcampus.plugin.qaforum.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MessageActivity - show notifications.
 * 
 * This class issues notifications and ask 
 * users whether or not to receive messages
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */

public class MessageActivity extends PluginView implements IQAforumView {
	private TextView mMessageView;
	private int notificationid;
	private String msg;
	private QAforumController mController;
	private QAforumModel mModel;
	private Button acceptButton;
	private Button declineButton;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Get and cast the controller and model
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();
		
		// Setup the layout
		setContentView(R.layout.qaforum_message);
		mMessageView = (TextView) findViewById(R.id.message);
		acceptButton = (Button) findViewById(R.id.accept);
		declineButton = (Button) findViewById(R.id.decline);
		
		if (getIntent().getExtras() != null) {
			msg = getIntent().getStringExtra("message");
			notificationid = Integer.parseInt(getIntent().getStringExtra("notificationid"));
			mMessageView.setText(msg);
		}
		if (mModel.getSessionid()==null) {
			acceptButton.setVisibility(View.GONE);
			declineButton.setVisibility(View.GONE);
			mMessageView.setText(getResources().getString(R.string.qaforum_notif_login));
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	public void notifAccept(View view) {
		acceptButton.setEnabled(false);
		declineButton.setEnabled(false);
		mController.acceptNotif(notificationid, 1);
	}

	public void notifDecline(View view) {
		acceptButton.setEnabled(false);
		declineButton.setEnabled(false);
		mController.acceptNotif(notificationid, 0);
		super.onBackPressed();
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.qaforum_connection_error_happened),
				Toast.LENGTH_SHORT).show();
		acceptButton.setEnabled(true);
		declineButton.setEnabled(true);
	}

	@Override
	public void gotRequestReturn() {
	}

	@Override
	public void messageDeleted() {
	}
	
	@Override
	public void loadingFinished() {
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