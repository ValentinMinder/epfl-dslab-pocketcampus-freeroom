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
 * MyAnswerListActivity - show a list of answers
 * 
 * This class shows a list of answers the user has given.
 * By clicking it, user could see the detailed information 
 * of one particular answer.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class MyAnswerListActivity extends PluginView implements IQAforumView {
	private String msg;
	private QAforumController mController;
	private JSONArray m_queslistArray;
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (QAforumController) controller;

		if(getIntent().getExtras() !=null){
			msg=getIntent().getStringExtra("data");
			try {
				JSONObject dataJsonObject=new JSONObject(msg);
				m_queslistArray=dataJsonObject.getJSONArray("myanswerlist");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		setContentView(R.layout.qaforum_list_main);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		try {
		 for (int i = 0; i <m_queslistArray.length() ; i++) {
			 View customView;
			 if (Integer.parseInt(m_queslistArray.getJSONObject(i).getString("closed"))==1) {
				 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null);	
			}
			 else {
				 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
			}
		        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
		        TextView answerTextView = (TextView) customView.findViewById(R.id.textView1);
		        TextView datetime = (TextView) customView.findViewById(R.id.textView2);
		        answerTextView.setText("A: "+ m_queslistArray.getJSONObject(i).getString("answer"));
		        datetime.setText("");
		        tv.setId(i);
				tv.setText((i+1)+" Q: "+m_queslistArray.getJSONObject(i).getString("content"));
		        tv.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	try {
		        		int forwardid=m_queslistArray.getJSONObject(v.getId()).getInt("forwardid");
		        		mController.oneanswer(forwardid);
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