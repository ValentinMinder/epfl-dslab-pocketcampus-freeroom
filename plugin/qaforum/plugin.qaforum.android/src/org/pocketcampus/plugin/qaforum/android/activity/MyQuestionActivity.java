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
import org.pocketcampus.plugin.qaforum.shared.s_relation;

import android.content.Context;
import android.os.Bundle;
import android.graphics.Paint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * MyQuestionActivity - show my quetion information.
 * 
 * This class shows the detailed information of 
 * one question.
 *  
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class MyQuestionActivity extends PluginView implements IQAforumView {
	private String msg;
	private JSONObject dataJsonObject;
    private QAforumController mController;
    private QAforumModel mModel;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
        mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();
        mModel.currentActivity = this;

		if (getIntent().getExtras() != null) {
			msg = getIntent().getStringExtra("data");
			System.out.println(msg);
			try {
				dataJsonObject = new JSONObject(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		setContentView(R.layout.qaforum_my_question);
		TextView tvQuestion = (TextView) findViewById(R.id.TextView01);
		TextView tvAnswer = (TextView) findViewById(R.id.textView2);
		TextView tvAuthor = (TextView) findViewById(R.id.textView5);
		TextView tvDate = (TextView) findViewById(R.id.textView6);
		TextView tvTopic = (TextView) findViewById(R.id.textView3);
		TextView tvTags = (TextView) findViewById(R.id.textView4);
		try {
			tvQuestion.setText(dataJsonObject.getString("content"));
			tvAuthor.setText(getResources().getString(R.string.qaforum_by)+dataJsonObject.getString("userid"));
            	tvAuthor.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				tvAuthor.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	//see the profile of the user
			        	try {
							mController.relationship(new s_relation(mModel.getSessionid(), dataJsonObject.getString("userid")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			        });
			tvDate.setText(dataJsonObject.getString("time"));
			tvTopic.setText(getResources().getString(R.string.qaforum_detail_topic)+": "+dataJsonObject.getString("topicid"));
			tvTags.setText(getResources().getString(R.string.qaforum_question_tags)+dataJsonObject.getString("tags"));
			
			LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
			LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			JSONArray answerlistArray = dataJsonObject
					.getJSONArray("answerlist");
			if (answerlistArray.length()==0) {
				tvAnswer.setVisibility(View.GONE);
			}
			for (int i = 0; i < answerlistArray.length(); i++) {
				View customView2 = linflater.inflate(
						R.layout.qaforum_one_item_answer, null);

				TextView answerTextView = (TextView) customView2
						.findViewById(R.id.TextView01);
				TextView userTextView = (TextView) customView2
						.findViewById(R.id.textView1);
				TextView timeTextView = (TextView) customView2
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
				l.addView(customView2);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/*
		setContentView(R.layout.qaforum_list_main);
		TextView subTitleTextView = (TextView) findViewById(R.id.standard_titled_layout_title);
		subTitleTextView.setText(getString(R.string.qaforum_details_title));
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			for (int i = 0; i <= 3; i++) {
				View customView = linflater.inflate(R.layout.qaforum_one_item,
						null);

				TextView titleTextView = (TextView) customView
						.findViewById(R.id.TextView01);
				TextView contentTextView = (TextView) customView
						.findViewById(R.id.textView1);
				switch (i) {
				case 0:
					titleTextView.setText(getResources().getString(R.string.qaforum_question_single));
					contentTextView
							.setText(dataJsonObject.getString("content"));
					break;
				case 1:
					titleTextView.setText(getResources().getString(R.string.qaforum_detail_topic));
					contentTextView
							.setText(dataJsonObject.getString("topicid"));
					break;
				case 2:
					titleTextView.setText(getResources().getString(R.string.qaforum_tags));
					contentTextView.setText(dataJsonObject.getString("tags"));
					break;
				case 3:
					titleTextView.setText(getResources().getString(R.string.qaforum_detail_asktime));
					contentTextView.setText(dataJsonObject.getString("time"));
					break;
				default:
					break;
				}
				l.addView(customView);
			}
			JSONArray answerlistArray = dataJsonObject
					.getJSONArray("answerlist");
			if (answerlistArray.length()>0) {
				View customView = linflater.inflate(R.layout.qaforum_one_item_answertitle,
						null);
				TextView titleTextView = (TextView) customView
						.findViewById(R.id.TextView01);
				titleTextView.setText(getResources().getString(R.string.qaforum_answer));
				l.addView(customView);
			}
			for (int i = 0; i < answerlistArray.length(); i++) {
				View customView2 = linflater.inflate(
						R.layout.qaforum_one_item_answer, null);

				TextView answerTextView = (TextView) customView2
						.findViewById(R.id.TextView01);
				TextView userTextView = (TextView) customView2
						.findViewById(R.id.textView1);
				TextView timeTextView = (TextView) customView2
						.findViewById(R.id.textView2);

				answerTextView
						.setText((i + 1)
								+ ": "
								+ answerlistArray.getJSONObject(i).getString(
										"content"));
				userTextView.setText(getResources().getString(R.string.qaforum_by)
						+ answerlistArray.getJSONObject(i).getString("name"));
				timeTextView.setText(answerlistArray.getJSONObject(i)
						.getString("time"));
				l.addView(customView2);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		*/
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
