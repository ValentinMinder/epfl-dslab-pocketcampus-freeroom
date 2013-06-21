package org.pocketcampus.plugin.qaforum.android.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import org.pocketcampus.plugin.qaforum.shared.s_feedback;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * AnswerActivity - show answer received.
 * 
 * This class issues answer received.
 * Here user could see answer to his question.
 * User could also give feedback and rating to 
 * the answer.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */

public class AnswerActivity extends PluginView implements IQAforumView {
private String msg;
private QAforumController mController;
private QAforumModel mModel;
private int forwardid;
private Button submitButton;
private Button reportButton;
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();

		setContentView(R.layout.qaforum_answer);

		if(getIntent().getExtras() !=null){
			msg=getIntent().getStringExtra("data");
			try {
				JSONObject dataJsonObject=new JSONObject(msg);
				TextView question=(TextView)findViewById(R.id.textView4);
				question.setText(getResources().getString(R.string.qaforum_question_question)+dataJsonObject.getString("content"));
				TextView answerTextView=(TextView)findViewById(R.id.textView1);
				answerTextView.setText(getResources().getString(R.string.qaforum_answer)+": "+dataJsonObject.getString("answer"));	
				forwardid=dataJsonObject.getInt("forwardid");
				TextView answername=(TextView)findViewById(R.id.textView5);
				answername.setText("by "+dataJsonObject.getString("replierid"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		submitButton = (Button) findViewById(R.id.button1);
		reportButton = (Button) findViewById(R.id.button2);
	}


  @Override
  public void onResume() {
    super.onResume();
  }
 
public void report(View view) {
	Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
	intent.putExtra("data", forwardid);
	intent.putExtra("type", 1);
	startActivity(intent);
}

public void layoutClick(View view) {
	InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}

public void submitFeedback(View view){
	EditText feedbackEditText=(EditText)findViewById(R.id.editText1);
	RatingBar ratingBar=(RatingBar)findViewById(R.id.ratingBar1);
	if (feedbackEditText.getText().toString().trim().length()==0) {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_answer_feedback), Toast.LENGTH_SHORT).show();
	}
	else  if(feedbackEditText.getText().toString().trim().length()>2000) {
		 Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_feedback_long), Toast.LENGTH_SHORT).show();
	 }
	else {
		boolean isEnglish = true;
		for (char c : feedbackEditText.getText().toString().trim().toCharArray()) {
			if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
				isEnglish = false;
				break;
			}
		}
		if (isEnglish) {
			s_feedback feedback=new s_feedback(mModel.getSessionid(),forwardid,feedbackEditText.getText().toString(),ratingBar.getRating());
			mController.feedback(feedback);
			submitButton.setEnabled(false);
			reportButton.setEnabled(false);
		}
		else {
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.qaforum_question_english),Toast.LENGTH_LONG).show();
		}
	}
}

@Override
public void networkErrorHappened() {
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
	submitButton.setEnabled(true);
	reportButton.setEnabled(true);
}

@Override
public void gotRequestReturn() {
	submitButton.setEnabled(true);
	reportButton.setEnabled(true);
	super.onBackPressed();
	Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_answer_received),
			Toast.LENGTH_SHORT).show();
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