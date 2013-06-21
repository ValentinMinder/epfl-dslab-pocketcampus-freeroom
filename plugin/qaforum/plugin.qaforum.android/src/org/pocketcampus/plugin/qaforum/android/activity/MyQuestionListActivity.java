package org.pocketcampus.plugin.qaforum.android.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * MyQuestionListActivity - show a list of questions
 * 
 * This class shows a list of questions the user has asked.
 * By clicking it, user could see the detailed information 
 * of one particular question.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class MyQuestionListActivity extends PluginView implements IQAforumView {

	private String msg_questions;
	private String msg_answers;
	private QAforumController mController;
	private QAforumModel mModel;
	private JSONArray m_queslistArray;
	private JSONArray m_answerlistArray;
	private boolean lock;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();
		lock = false;
		if (getIntent().getExtras() != null) {
			msg_questions = getIntent().getStringExtra("questions");
			msg_answers = getIntent().getStringExtra("answers");
			try {
				JSONObject dataJsonObject = new JSONObject(msg_questions);
				m_queslistArray = dataJsonObject.getJSONArray("myquestionlist");
				dataJsonObject = new JSONObject(msg_answers);
				m_answerlistArray = dataJsonObject.getJSONArray("myanswerlist");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		setContentView(R.layout.qaforum_myqa_tab);
		Button myquestionButton = (Button) findViewById(R.id.questionButton);
		myquestionButton.setClickable(false);
		myquestionButton.setEnabled(false);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			for (int i = 0; i < m_queslistArray.length(); i++) {
				View customView;
				if (Integer.parseInt(m_queslistArray.getJSONObject(i)
						.getString("closed")) == 1) {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_closed, null);
				} else {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_open, null);
				}
				TextView tv = (TextView) customView
						.findViewById(R.id.TextView01);
				TextView topictags = (TextView) customView
						.findViewById(R.id.textView1);
				TextView datetime = (TextView) customView
						.findViewById(R.id.textView2);
				topictags.setText(m_queslistArray.getJSONObject(i).getString(
						"topicid"));
				String time = m_queslistArray.getJSONObject(i)
						.getString("time");
				
				Date nowDate = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss",Locale.FRANCE);
				format.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
				Date questionDate = format.parse(time);
				format.setTimeZone(TimeZone.getDefault());
				time=format.format(questionDate);
				questionDate=format.parse(time);
				long difference = nowDate.getTime() - questionDate.getTime();
				difference /= 1000;
				if (difference<60) {
					time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference)+getResources().getString(R.string.qaforum_sec_ago);
				} else if (difference<3600) {
					if (difference/60==1) {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_min_ago);
					}else {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_mins_ago);	
					}
				}
				datetime.setText(time);
				tv.setId(i);
				tv.setText((i + 1) + ": "
						+ m_queslistArray.getJSONObject(i).getString("content"));
				tv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (!lock) {
								int quesid = m_queslistArray.getJSONObject(
										v.getId()).getInt("quesid");
								mController.onequestion(quesid);	
								lock=true;
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}
				});

				l.addView(customView);
			}

		} catch (JSONException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			mModel.getListenersToNotify().loadingFinished();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void myquestions(View view) {

		Button myquestionButton = (Button) findViewById(R.id.questionButton);
		myquestionButton.setClickable(false);
		myquestionButton.setEnabled(false);
		Button myanswerButton = (Button) findViewById(R.id.answerButton);
		myanswerButton.setClickable(true);
		myanswerButton.setEnabled(true);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		l.removeAllViews();
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			for (int i = 0; i < m_queslistArray.length(); i++) {
				View customView;
				if (Integer.parseInt(m_queslistArray.getJSONObject(i)
						.getString("closed")) == 1) {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_closed, null);
				} else {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_open, null);
				}
				TextView tv = (TextView) customView
						.findViewById(R.id.TextView01);
				TextView topictags = (TextView) customView
						.findViewById(R.id.textView1);
				TextView datetime = (TextView) customView
						.findViewById(R.id.textView2);
				topictags.setText(m_queslistArray.getJSONObject(i).getString(
						"topicid"));
				String time = m_queslistArray.getJSONObject(i)
						.getString("time");
				Date nowDate = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss",Locale.FRANCE);
				format.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
				Date questionDate = format.parse(time);
				format.setTimeZone(TimeZone.getDefault());
				time=format.format(questionDate);
				questionDate=format.parse(time);
				long difference = nowDate.getTime() - questionDate.getTime();
				difference /= 1000;
				if (difference<60) {
					time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference)+getResources().getString(R.string.qaforum_sec_ago);
				} else if (difference<3600) {
					if (difference/60==1) {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_min_ago);
					}else {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_mins_ago);	
					}
				}
				datetime.setText(time);
				tv.setId(i);
				tv.setText((i + 1) + ": "
						+ m_queslistArray.getJSONObject(i).getString("content"));
				tv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (!lock) {
								int quesid = m_queslistArray.getJSONObject(
									v.getId()).getInt("quesid");
								mController.onequestion(quesid);
								lock=true;
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}
				});

				l.addView(customView);
			}

		} catch (JSONException e) {
			
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}

	public void myanswers(View view) {
		Button myquestionButton = (Button) findViewById(R.id.questionButton);
		myquestionButton.setClickable(true);
		myquestionButton.setEnabled(true);
		Button myanswerButton = (Button) findViewById(R.id.answerButton);
		myanswerButton.setClickable(false);
		myanswerButton.setEnabled(false);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		l.removeAllViews();
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			for (int i = 0; i < m_answerlistArray.length(); i++) {
				View customView;
				if (Integer.parseInt(m_answerlistArray.getJSONObject(i)
						.getString("closed")) == 1) {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_closed, null);
				} else {
					customView = linflater.inflate(
							R.layout.qaforum_list_item_open, null);
				}
				TextView tv = (TextView) customView
						.findViewById(R.id.TextView01);
				TextView answerTextView = (TextView) customView
						.findViewById(R.id.textView1);
				TextView datetime = (TextView) customView
						.findViewById(R.id.textView2);
				answerTextView.setText("A: "
						+ m_answerlistArray.getJSONObject(i)
								.getString("answer"));
				datetime.setText("");
				tv.setId(i);
				tv.setText((i + 1)
						+ " Q: "
						+ m_answerlistArray.getJSONObject(i).getString(
								"content"));
				tv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (!lock) {
								int forwardid = m_answerlistArray.getJSONObject(
									v.getId()).getInt("forwardid");
								mController.oneanswer(forwardid);
								lock=true;
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}
				});
				l.addView(customView);
			}

		} catch (JSONException e) {
			
			e.printStackTrace();
		}
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
		
		lock=false;
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