package org.pocketcampus.plugin.qaforum.android.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.plugin.qaforum.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.android.QAforumController;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;

import android.content.Context;
import android.os.Bundle;
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

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		if (getIntent().getExtras() != null) {
			msg = getIntent().getStringExtra("data");
			System.out.println(msg);
			try {
				dataJsonObject = new JSONObject(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

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
						.setText(getResources().getString(R.string.qaforum_answer)+" "
								+ (i + 1)
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