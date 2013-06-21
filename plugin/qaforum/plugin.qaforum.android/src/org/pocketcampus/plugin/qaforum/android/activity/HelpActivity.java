package org.pocketcampus.plugin.qaforum.android.activity;

import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;

import android.os.Bundle;
import android.widget.Toast;
/**
 * HelpActivity - introduction to the plugin.
 * 
 * This class shows the introduction page to users. 
 * It includes "What is it?" and "How to use it?"
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class HelpActivity extends PluginView implements IQAforumView {
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		setContentView(R.layout.qaforum_help);
	}

  @Override
  public void onResume() {
    super.onResume();
  }
 
@Override
public void networkErrorHappened() {
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
}

@Override
public void gotRequestReturn() {
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