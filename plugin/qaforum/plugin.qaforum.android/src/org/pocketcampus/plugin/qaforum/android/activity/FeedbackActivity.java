package org.pocketcampus.plugin.qaforum.android.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * FeedbackActivity - show feedback received.
 * 
 * This class issues feedback received.
 * Here user could see feedback to his answer,
 * including feedback message and rating.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class FeedbackActivity extends PluginView implements IQAforumView {

	private String msg;
	private int forwardid;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		setContentView(R.layout.qaforum_feedback);

		if (getIntent().getExtras() != null) {
			msg = getIntent().getStringExtra("data");
			try {
				JSONObject feedbackJsonObject = new JSONObject(msg);
				TextView questionView = (TextView) findViewById(R.id.textView1);
				TextView answerView = (TextView) findViewById(R.id.textView5);
				TextView feedbackView = (TextView) findViewById(R.id.textView4);
				TextView askernameTextView = (TextView) findViewById(R.id.textView6);
				RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
				ratingBar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// do nothing
					}
				});
				askernameTextView.setText(getResources().getString(R.string.qaforum_by)
						+ feedbackJsonObject.getString("userid"));
				forwardid = feedbackJsonObject.getInt("forwardid");
				questionView.setText(getResources().getString(R.string.qaforum_question_question)
						+ feedbackJsonObject.getString("question"));
				answerView.setText(getResources().getString(R.string.qaforum_answer)+": "
						+ feedbackJsonObject.getString("answer"));
				feedbackView.setText(feedbackJsonObject.getString("feedback"));
				ratingBar.setRating((float) feedbackJsonObject
						.getDouble("rate"));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void layoutClick(View view) {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void report(View view) {
		Intent intent = new Intent(getApplicationContext(),
				ReportActivity.class);
		intent.putExtra("data", forwardid);
		intent.putExtra("type", 2);
		startActivity(intent);
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.qaforum_connection_error_happened),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void gotRequestReturn() {
	}

	@Override
	public void loadingFinished() {
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