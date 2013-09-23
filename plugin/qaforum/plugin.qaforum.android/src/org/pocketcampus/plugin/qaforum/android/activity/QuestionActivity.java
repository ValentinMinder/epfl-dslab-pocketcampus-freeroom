package org.pocketcampus.plugin.qaforum.android.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import org.pocketcampus.plugin.qaforum.shared.s_answer;
import org.pocketcampus.plugin.qaforum.shared.s_relation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * QuestionActivity - show question received.
 * 
 * This class issues question received.
 * Here user could see the question and give answers.
 * User could also see the personal information, not 
 * private, of the asker
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class QuestionActivity extends PluginView implements IQAforumView {
private JSONObject m_JsonObject;

private QAforumController mController;
private QAforumModel mModel;
private int mforwardid;

private TextView mContentTextView;
private TextView mAskerTextView;
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
		mModel.currentActivity = this;
		setContentView(R.layout.qaforum_question);
		
		if(getIntent().getExtras() !=null)	
		{
			String dataString=getIntent().getExtras().getString("data");
			try {
				m_JsonObject=new JSONObject(dataString);
				mContentTextView=(TextView)findViewById(R.id.TextView01);
				mContentTextView.setText(m_JsonObject.getString("content"));
				mAskerTextView=(TextView)findViewById(R.id.textView5);
				mAskerTextView.setText(getResources().getString(R.string.qaforum_by)+m_JsonObject.getString("askername"));
				mAskerTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				mAskerTextView.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	//see the profile of the user
			        	try {
							mController.relationship(new s_relation(mModel.getSessionid(), m_JsonObject.getString("askername")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			        });
				TextView topicTextView = (TextView)findViewById(R.id.textView3);
				TextView tagsTextView = (TextView)findViewById(R.id.textView4);
				TextView asktimeTextView = (TextView)findViewById(R.id.textView6);
				topicTextView.setText(getResources().getString(R.string.qaforum_detail_topic)+": "+m_JsonObject.getString("topic"));
				tagsTextView.setText(getResources().getString(R.string.qaforum_question_tags)+m_JsonObject.getString("tags"));
				asktimeTextView.setText(m_JsonObject.getString("time"));
				
				mforwardid=m_JsonObject.getInt("forwardid");
				
				//display answers when the question has answers already
				LinearLayout l = (LinearLayout) findViewById(R.id.answersLayout);
				LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				JSONArray answerlistArray = m_JsonObject
						.getJSONArray("answerlist");
				if (answerlistArray.length()!=0) {
					TextView answerTitleTextView = (TextView)findViewById(R.id.textView10);
					answerTitleTextView.setVisibility(View.VISIBLE);
				}
				for (int i = 0; i < answerlistArray.length(); i++) {
					View customView = linflater.inflate(
							R.layout.qaforum_one_item_answer, null);
					TextView answerTextView = (TextView) customView
							.findViewById(R.id.TextView01);
					TextView userTextView = (TextView) customView
							.findViewById(R.id.textView1);
					TextView timeTextView = (TextView) customView
							.findViewById(R.id.textView2);

					answerTextView
							.setText((i + 1)
									+ ": "
									+ answerlistArray.getJSONObject(i).getString(
											"content"));
					userTextView.setText(getResources().getString(R.string.qaforum_by)
							+ answerlistArray.getJSONObject(i).getString("name"));
                    userTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    final String username = answerlistArray.getJSONObject(i).getString("name");
				    userTextView.setOnClickListener(new View.OnClickListener() {
			           @Override
			            public void onClick(View v) {
							    mController.relationship(new s_relation(mModel.getSessionid(), username));
			            }
			        });

					timeTextView.setText(answerlistArray.getJSONObject(i)
							.getString("time"));
					l.addView(customView);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		submitButton = (Button) findViewById(R.id.button1);
		reportButton = (Button) findViewById(R.id.button2);
	}

	public void showRelation(String resultString) throws JSONException {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		JSONObject dataJsonObject=new JSONObject(resultString);
		// set title
		alertDialogBuilder.setTitle(dataJsonObject.getString("name"));
		String message = "";
		String pathString = dataJsonObject.getString("path");
		String questionString =getResources().getString(R.string.qaforum_relation_question)+dataJsonObject.getString("question");
		int count = dataJsonObject.length();
		if (count==3) {
			message+=(questionString+"\n"+pathString);
		}
		else{
			String lanuage = getResources().getString(R.string.qaforum_relation_language)+dataJsonObject.getString("language");
			String answerme = getResources().getString(R.string.qaforum_relation_answer_me)+dataJsonObject.getString("answerme");
			String answerall = getResources().getString(R.string.qaforum_relation_answer_all)+ dataJsonObject.getString("answerall");
			String reputation = getResources().getString(R.string.qaforum_relation_reputation)+dataJsonObject.getString("reputation");
			String onlineString;
			if (dataJsonObject.getInt("online")==1) {
				onlineString=getResources().getString(R.string.qaforum_relation_status_on);
			}else {
				onlineString=getResources().getString(R.string.qaforum_relation_status_off);
			}
			String topic = getResources().getString(R.string.qaforum_relation_topics)+dataJsonObject.getString("topic");
			message +=(reputation+"\n"+questionString+"\n"+answerme+"\n"+answerall+"\n"+lanuage+"\n"+topic+"\n"+onlineString+"\n"+pathString);
		}
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close current activity, so just do noting
				}
			  });
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
	}

	
  @Override
  public void onResume() {
    super.onResume();
  }

public void submitAnswer(View view) {
	EditText answerEditText=(EditText)findViewById(R.id.editText1);
	String answerString = answerEditText.getText().toString().trim();
	if (answerString.length()==0) {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_reply_answer), Toast.LENGTH_SHORT).show();
	}
	else if (answerString.length()>2000) {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_feedback_long), Toast.LENGTH_SHORT).show();
	} 
	else{
		boolean isEnglish = true;
		for (char c : answerString.toCharArray()) {
			if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
				isEnglish = false;
				break;
			}
		}
		if (isEnglish) {
			s_answer myAnswer=new s_answer(mModel.getSessionid(),mforwardid,answerEditText.getText().toString(),0);
			mController.answer(myAnswer);
			submitButton.setEnabled(false);
			reportButton.setEnabled(false);
		}
		else {
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.qaforum_question_english),Toast.LENGTH_LONG).show();
		}
	}
}
public void layoutClick(View view) {
	InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}
public void report(View view) {
	Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
	intent.putExtra("data", mforwardid);
	intent.putExtra("type", 0);
	startActivity(intent);
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
	
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_answer_received), Toast.LENGTH_SHORT).show();
	submitButton.setEnabled(true);
	reportButton.setEnabled(true);
	super.onBackPressed();
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
