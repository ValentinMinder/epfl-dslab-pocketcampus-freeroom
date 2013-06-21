package org.pocketcampus.plugin.qaforum.android.activity;

import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import org.pocketcampus.plugin.qaforum.shared.s_report;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * ReportActivity - Report to server.
 * 
 * This class issues the situation where user has problem 
 * using the app or want to report malicious questions or 
 * answers.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class ReportActivity extends PluginView implements IQAforumView {
private QAforumController mController;
private QAforumModel mModel;
private int forwardid;
private int type;
private Button reportButton;
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();

		setContentView(R.layout.qaforum_report);

		if(getIntent().getExtras() !=null){
			forwardid=getIntent().getIntExtra("data",0);
			type=getIntent().getIntExtra("type", -1);
		}
		reportButton = (Button) findViewById(R.id.button1);
	}


  @Override
  public void onResume() {
    super.onResume();
  }
 
public void layoutClick(View view) {
	InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}

public void reportToServer(View view) {
	EditText commentEditText = (EditText)findViewById(R.id.editText1);
	String commString = commentEditText.getText().toString();
	
	 if(commString.trim().length()==0){
		  Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_report_empty), Toast.LENGTH_SHORT).show();
	  } 
	 else if(commString.trim().length()>2000) {
		 Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_report_long), Toast.LENGTH_SHORT).show();
	 }
	 else {
		 s_report dataReport = new s_report(mModel.getSessionid(),forwardid,type,commString);
			mController.report(dataReport);
			reportButton.setEnabled(false);
	}
}

@Override
public void networkErrorHappened() {
	
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
	reportButton.setEnabled(true);
}

@Override
public void gotRequestReturn() {
}

@Override
public void messageDeleted() {
}

@Override
public void loadingFinished() {
	Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_report_rece), Toast.LENGTH_SHORT).show();
	reportButton.setEnabled(true);
	super.onBackPressed();
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