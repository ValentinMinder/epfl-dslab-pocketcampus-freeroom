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
 * QuestionListActivity - show a list of questions.
 * 
 * This class issues the situation where a few 
 * questions are received at the same time.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class QuestionListActivity extends PluginView implements IQAforumView {
	private String msg;
	private JSONArray m_queslistArray;
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		if(getIntent().getExtras() !=null){
			msg=getIntent().getStringExtra("data");
			System.out.println(msg);
			try {
				JSONObject dataJsonObject=new JSONObject(msg);	
				m_queslistArray=dataJsonObject.getJSONArray("questionlist");
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
		
		setContentView(R.layout.qaforum_list_main);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		try {
		 for (int i = 0; i <m_queslistArray.length() ; i++) {
			 View customView = linflater.inflate(R.layout.qaforum_list_item_closed, null);
		        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
		        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
		        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
		        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+getResources().getString(R.string.qaforum_by)+m_queslistArray.getJSONObject(i).getString("askername"));
		        String time = m_queslistArray.getJSONObject(i).getString("time");
		        Date nowDate = new Date();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss",Locale.FRANCE);
				format.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
				Date questionDate = format.parse(time);
				format.setTimeZone(TimeZone.getDefault());
				time=format.format(questionDate);
				questionDate=format.parse(time);
		        long difference = nowDate.getTime() - questionDate.getTime();
		        difference /=1000;
		        if (difference<60) {
					time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference)+getResources().getString(R.string.qaforum_sec_ago);
				} else if (difference<3600) {
					if (difference/60==1) {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_min_ago);
					}else {
						time = getResources().getString(R.string.qaforum_time_pre)+Long.toString(difference/60) + getResources().getString(R.string.qaforum_mins_ago);	
					}
				}
		        timetext.setText(time);
		        tv.setId(i);
				tv.setText("Q" + (i+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
		        tv.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	try {
						String dataString=m_queslistArray.getJSONObject(v.getId()).toString();
						Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
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
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}

  @Override
  public void onResume() {
    super.onResume();
  }
 
  
@Override
public void networkErrorHappened() {
	
	Toast.makeText(getApplicationContext(), getResources().getString(
			R.string.qaforum_connection_error_happened), Toast.LENGTH_SHORT).show();
}

@Override
public void gotRequestReturn() {
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