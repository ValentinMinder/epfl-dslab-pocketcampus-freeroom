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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * FeedbackListActivity - show a list of feedbacks.
 * 
 * This class issues the situation where a few 
 * feedbacks are received at the same time.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class FeedbackListActivity extends PluginView implements IQAforumView {

	private String msg;
	private JSONArray m_queslistArray;

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
				JSONObject dataJsonObject = new JSONObject(msg);
				m_queslistArray = dataJsonObject.getJSONArray("feedbacklist");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		setContentView(R.layout.qaforum_list_main);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			for (int i = 0; i < m_queslistArray.length(); i++) {
				View customView = linflater.inflate(
						R.layout.qaforum_list_item_closed, null);
				TextView tv = (TextView) customView
						.findViewById(R.id.TextView01);
				tv.setId(i);
				tv.setText(getResources().getString(R.string.qaforum_feedback)
						+ i
						+ ": "
						+ m_queslistArray.getJSONObject(i)
								.getString("feedback") + getResources().getString(R.string.qaforum_from)
						+ m_queslistArray.getJSONObject(i).getString("userid"));
				tv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							String dataString = m_queslistArray.getJSONObject(
									v.getId()).toString();
							Intent intent = new Intent(getApplicationContext(),
									FeedbackActivity.class);
							intent.putExtra("data", dataString);
							startActivity(intent);
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