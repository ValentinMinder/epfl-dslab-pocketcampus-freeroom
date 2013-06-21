package org.pocketcampus.plugin.qaforum.android.activity;


import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import org.pocketcampus.plugin.qaforum.shared.s_ask;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * AskActivity - Ask a question.
 * 
 * This class called when user wants to ask a question.
 * After asking a question, the server will give back a list
 * of similar questions for reference. But now question must be 
 * given in English
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class AskActivity extends PluginView implements IQAforumView {

	private QAforumController mController;
	private QAforumModel mModel;
	private s_ask tempAsk;
	private int quesKeyid;
	private Button submitButton;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();
		setContentView(R.layout.qaforum_ask);
		submitButton = (Button) findViewById(R.id.button1);
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.qaforum_topics_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setSelection(mModel.getAskTopic());
		
		Spinner spinnertime = (Spinner) findViewById(R.id.Spinner01);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.qaforum_expirytime_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnertime.setAdapter(adapter1);
		spinnertime.setSelection(mModel.getAskExpiryTime());
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void ShowMatchingDialog(String resultString) {
		submitButton.setEnabled(true);
		if (resultString.equals("")) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_no_meaning), Toast.LENGTH_SHORT).show();
			return;
		}
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.qaforum_matching);
		dialog.setTitle(getResources().getString(R.string.qaforum_matching_title));
		
		// some settings
		ArrayList<TextView> questions = new ArrayList<TextView>();
		questions.add((TextView) dialog.findViewById(R.id.textView1));
		questions.add((TextView) dialog.findViewById(R.id.textView2));
		questions.add((TextView) dialog.findViewById(R.id.textView3));
		questions.add((TextView) dialog.findViewById(R.id.textView4));
		questions.add((TextView) dialog.findViewById(R.id.textView5));
		try {
			JSONObject dataJsonObject = new JSONObject(resultString);
			Iterator<?> keys = dataJsonObject.keys();
			int i = 0;
			while (keys.hasNext()) {
				final String key = (String) keys.next();
				if (key.equals("key")) {
					quesKeyid = dataJsonObject.getInt(key);
					continue;
				}
				questions.get(i).setText(
						(i + 1) + ": " + dataJsonObject.get(key).toString());

				questions.get(i).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mController.onequestion(Integer.parseInt(key));
					}
				});
				i++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Button dialogButton = (Button) dialog.findViewById(R.id.button2);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// filter according to topics
				tempAsk.setQuesid(quesKeyid);
				mController.ask(tempAsk);
				dialog.dismiss();
			}
		});
		Button cancelbutton = (Button) dialog.findViewById(R.id.button1);
		// if button is clicked, close the custom dialog
		cancelbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void submitQuestion(View view) {
		TextView questionTextView = (TextView) findViewById(R.id.questionText);
		Spinner expirytimeSpinner = (Spinner) findViewById(R.id.Spinner01);
		TextView tagsTextView = (TextView) findViewById(R.id.tagsText);
		Spinner topicSpinner = (Spinner) findViewById(R.id.spinner1);

		String ques = questionTextView.getText().toString();
		if (ques.trim().length() == 0) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.qaforum_question_hint), Toast.LENGTH_SHORT).show();
		} else {
			if (ques.trim().length() > 2000) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.qaforum_question_long), Toast.LENGTH_SHORT)
						.show();
			} else {
				boolean isEnglish = true;
				for (char c : ques.trim().toCharArray()) {
					if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
						isEnglish = false;
						break;
					}
				}
				if (isEnglish) {
					int topic = topicSpinner.getSelectedItemPosition() + 1;
					String tags = tagsTextView.getText().toString();
					int expirytimeid = (Integer) expirytimeSpinner
							.getSelectedItemPosition();
					int expritytime = 0;
					switch (expirytimeid) {
					case 0:
						expritytime = 5;
						break;
					case 1:
						expritytime = 10;
						break;
					case 2:
						expritytime = 30;
						break;
					case 3:
						expritytime = 60;
						break;
					case 4:
						expritytime = 360;
						break;
					case 5:
						expritytime = 720;
						break;
					case 6:
						expritytime = 1440;
						break;
					case 7:
						expritytime = 4320;
						break;
					case 8:
						expritytime = 10080;
						break;
					default:
						break;
					}

					s_ask dataAsk = new s_ask(mModel.getSessionid(), ques,
							topic, tags, expritytime, 0);
					tempAsk = dataAsk;
					submitButton.setEnabled(false);
					mController.matchingQuestion(ques);
					mModel.currentActivity = this;
				} else {
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.qaforum_question_english),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public void layoutClick(View view) {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
		submitButton.setEnabled(true);
	}

	@Override
	public void gotRequestReturn() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_question_received),
				Toast.LENGTH_SHORT).show();
		super.onBackPressed();
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