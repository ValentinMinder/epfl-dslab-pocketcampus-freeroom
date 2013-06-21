package org.pocketcampus.plugin.qaforum.android.activity;

import java.util.ArrayList;
import org.apache.commons.lang.*;

import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import org.pocketcampus.plugin.qaforum.shared.s_session;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
/**
 * SettingActivity - show seeting options.
 * 
 * This class provides interface where user could
 * set his preferences. e.g. availability, topic, 
 * lanuage, expirytime.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class SettingActivity extends PluginView implements IQAforumView {

private QAforumController mController;
private QAforumModel mModel;
private ToggleButton acceptButton;
private Spinner intervalSpinner;

private CheckBox checkBox1;
private CheckBox checkBox2;
private CheckBox checkBox3;
private CheckBox checkBox4;
private CheckBox checkBox5;
private CheckBox checkBox6;
private CheckBox checkBox7;
private CheckBox checkBox8;

private Button submitButton;
private Button cancelButton;

  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();
		setContentView(R.layout.qaforum_setting);
		
		checkBox1=(CheckBox)findViewById(R.id.checkBox1);
		checkBox2=(CheckBox)findViewById(R.id.checkBox2);
		checkBox3=(CheckBox)findViewById(R.id.checkBox3);
		checkBox4=(CheckBox)findViewById(R.id.checkBox4);
		
		checkBox5=(CheckBox)findViewById(R.id.checkBox5);
		checkBox6=(CheckBox)findViewById(R.id.checkBox6);
		checkBox7=(CheckBox)findViewById(R.id.checkBox7);
		checkBox8=(CheckBox)findViewById(R.id.checkBox8);
		
		acceptButton=(ToggleButton)findViewById(R.id.toggleButton1);
		acceptButton.setChecked(mModel.getAccept()==1);

		
		intervalSpinner=(Spinner)findViewById(R.id.Spinner01);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.qaforum_interval_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		intervalSpinner.setAdapter(adapter);
		int interval=mModel.getResttime();
		switch (interval) {
		case 0:
			interval=0;
			break;
		case 1:
			interval=1;
			break;
		case 5:
			interval=2;
			break;
		case 15:
			interval=3;
			break;
		case 30:
			interval=4;
			break;
		case 60:
			interval=5;
			break;
		case 720:
			interval=6;
			break;
		default:
			interval=0;
			break;
		}
		intervalSpinner.setSelection(interval);

		String languageString=mModel.getLanguage();
		if(languageString.contains("English"))
			checkBox1.setChecked(true);
		
		if(languageString.contains("French"))
			checkBox2.setChecked(true);
		
		if(languageString.contains("Germany"))
			checkBox3.setChecked(true);
		
		if(languageString.contains("Italian"))
			checkBox4.setChecked(true);
		
		String topicString=mModel.getTopic();
		if(topicString.contains("Travel"))
			checkBox5.setChecked(true);
		
		if(topicString.contains("Study"))
			checkBox6.setChecked(true);
		
		if(topicString.contains("Living"))
			checkBox7.setChecked(true);
		
		if(topicString.contains("Others"))
			checkBox8.setChecked(true);
		
		submitButton = (Button) findViewById(R.id.button1);
		cancelButton = (Button) findViewById(R.id.button2);
		
	}

  @Override
  public void onResume() {
    super.onResume();
  }

public void confirm(View view) {
	//confirm the changes of settings
	int interval=intervalSpinner.getSelectedItemPosition();
	switch (interval) {
	case 0:
		interval=0;
		break;
	case 1:
		interval=1;
		break;
	case 2:
		interval=5;
		break;
	case 3:
		interval=15;
		break;
	case 4:
		interval=30;
		break;
	case 5:
		interval=60;
		break;
	case 6:
		interval=12*60;
		break;

	default:
		break;
	}
	
	ArrayList<String> languageArrayList = new ArrayList<String>();
	if (checkBox1.isChecked()) {
		languageArrayList.add("English");
	}
	if (checkBox2.isChecked()) {
		languageArrayList.add("French");
	}
	if (checkBox3.isChecked()) {
		languageArrayList.add("Germany");
	}
	if (checkBox4.isChecked()) {
		languageArrayList.add("Italian");
	}
	
	ArrayList<String> topicsArrayList = new ArrayList<String>();
	if (checkBox5.isChecked()) {
		topicsArrayList.add("Travel");
	}
	if (checkBox6.isChecked()) {
		topicsArrayList.add("Study");
	}
	if (checkBox7.isChecked()) {
		topicsArrayList.add("Living");
	}
	if (checkBox8.isChecked()) {
		topicsArrayList.add("Others");
	}
	String language = StringUtils.join(languageArrayList, ' ');
	String topics= StringUtils.join(topicsArrayList, ' ');
	s_session setting=new s_session(mModel.getSessionid(),acceptButton.isChecked()?1:0,interval, language,topics,0,0,0);
	mModel.updateSettingCookie(setting);
	mController.setting(setting);
	submitButton.setEnabled(false);
	cancelButton.setEnabled(false);
}

public void cancel(View view) {
	//cancel the users' changes, and back to the main menu.
	super.onBackPressed();
}

@Override
public void networkErrorHappened() {
	
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
	submitButton.setEnabled(true);
	cancelButton.setEnabled(true);
}


@Override
public void gotRequestReturn() {
	
	Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_setting_changed), Toast.LENGTH_SHORT).show();
	submitButton.setEnabled(true);
	cancelButton.setEnabled(true);
	super.onBackPressed();
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