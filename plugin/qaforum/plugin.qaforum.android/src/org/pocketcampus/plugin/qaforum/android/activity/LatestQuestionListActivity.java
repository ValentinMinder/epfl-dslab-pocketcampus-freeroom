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
import org.pocketcampus.plugin.qaforum.shared.s_latest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * LatestQuestionListActivity - show the lastest questions.
 * 
 * It is somewhere to show lastest questions of others.
 * Questions could be divided to tewo categories: Open and Closed.
 * For open questions, user could give answer.
 * For closed questions, user could check the detailed information of them.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */
public class LatestQuestionListActivity extends PluginView implements IQAforumView {
	private String msg;
	private QAforumController mController;
	private QAforumModel mModel;
	private JSONArray m_queslistArray;
  
  @Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return QAforumController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		mController = (QAforumController) controller;
		mModel = (QAforumModel) controller.getModel();

		if(getIntent().getExtras() !=null){
			msg=getIntent().getStringExtra("data");
			try {
				JSONObject dataJsonObject=new JSONObject(msg);	
				m_queslistArray=dataJsonObject.getJSONArray("questionlist");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		setContentView(R.layout.qaforum_list_main_options);
		LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		try {
		 for (int i = 0; i <m_queslistArray.length() ; i++) {
			 View customView;
			 final int typeofquestion =  m_queslistArray.getJSONObject(i).getInt("closed");
			 if(typeofquestion==1){
				 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null); 
			 } else {
				 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
			 }
		        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
		        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
		        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
		        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+"   by "+m_queslistArray.getJSONObject(i).getString("askername"));
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
				tv.setText((i+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
				tv.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	try {
		        		if (typeofquestion==1) {
							mController.onequestion(m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
						}
		        		else {
		        			s_latest tempLatest = new s_latest(mModel.getSessionid(),m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
		        			mController.onelatest(tempLatest);
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

  @Override
  public void onResume() {
    super.onResume();
  }

  public void topicFilter(View view) {
	final Dialog dialog = new Dialog(this);
	dialog.setContentView(R.layout.qaforum_topic_filter);
	dialog.setTitle(getResources().getString(R.string.qaforum_topic));
	Button dialogButton = (Button) dialog.findViewById(R.id.button2);
	// if button is clicked, close the custom dialog
	dialogButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			//filter according to topics
			String topicnameString = "";
			CheckBox checkBox1=(CheckBox)dialog.findViewById(R.id.checkBox1);
			CheckBox checkBox2=(CheckBox)dialog.findViewById(R.id.checkBox2);
			CheckBox checkBox3=(CheckBox)dialog.findViewById(R.id.checkBox3);
			CheckBox checkBox4=(CheckBox)dialog.findViewById(R.id.checkBox4);
			if(checkBox1.isChecked()) topicnameString+="Travel ";
			if(checkBox2.isChecked()) topicnameString+="Study ";
			if(checkBox3.isChecked()) topicnameString+="Living ";
			if(checkBox4.isChecked()) topicnameString+="Others";
			LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
			l.removeAllViews();
			LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int j = -1;
			try {
			 for (int i = 0; i <m_queslistArray.length() ; i++) {
				 String topicString = m_queslistArray.getJSONObject(i).getString("topic");
				 if (!topicnameString.contains(topicString)) {
					continue;
				}
				 j++;
				 View customView;
				 final int typeofquestion =  m_queslistArray.getJSONObject(i).getInt("closed");
				 if(typeofquestion==1){
					 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null); 
				 } else {
					 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
				 }
			        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
			        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
			        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
			        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+"   by "+m_queslistArray.getJSONObject(i).getString("askername"));
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
					tv.setText((j+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
			        tv.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	try {
			        		if (typeofquestion==1) {
								mController.onequestion(m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
							}
			        		else {
			        			s_latest tempLatest = new s_latest(mModel.getSessionid(),m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
			        			mController.onelatest(tempLatest);
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
  
  public void tagsFilter(View view) {
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.qaforum_tags_filter);
		dialog.setTitle(getResources().getString(R.string.qaforum_tags));
		EditText tagsEditText= (EditText)dialog.findViewById(R.id.editText1);
		tagsEditText.setHint(getResources().getString(R.string.qaforum_question_hint_tags));
		Button dialogButton = (Button) dialog.findViewById(R.id.button2);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//filter according to tags
				EditText tagsEditText= (EditText)dialog.findViewById(R.id.editText1);
				String tagsString = tagsEditText.getText().toString();
				
				LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
				l.removeAllViews();
				LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				int j = -1;
				try {
				 for (int i = 0; i <m_queslistArray.length() ; i++) {
					 String temptagsString= m_queslistArray.getJSONObject(i).getString("tags");
					 if (!temptagsString.contains(tagsString)) {
						continue;
					}
					 j++;
					 View customView;
					 final int typeofquestion =  m_queslistArray.getJSONObject(i).getInt("closed");
					 if(typeofquestion==1){
						 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null); 
					 } else {
						 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
					 }
				        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
				        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
				        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
				        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+"   by "+m_queslistArray.getJSONObject(i).getString("askername"));
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
						tv.setText((j+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
				        tv.setOnClickListener(new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				        	try {
				        		if (typeofquestion==1) {
									mController.onequestion(m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
								}
				        		else {
				        			s_latest tempLatest = new s_latest(mModel.getSessionid(),m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
				        			mController.onelatest(tempLatest);
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
  public void contentFilter(View view) {
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.qaforum_tags_filter);
			dialog.setTitle(getResources().getString(R.string.qaforum_content_search));
			Button dialogButton = (Button) dialog.findViewById(R.id.button2);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//filter according to content
					EditText tagsEditText= (EditText)dialog.findViewById(R.id.editText1);
					String tagsString = tagsEditText.getText().toString();
					LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
					l.removeAllViews();
					LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					int j = -1;
					try {
					 for (int i = 0; i <m_queslistArray.length() ; i++) {
						 String temptagsString= m_queslistArray.getJSONObject(i).getString("content");
						 if (!temptagsString.contains(tagsString)) {
							continue;
						}
						 j++;
						 View customView;
						 final int typeofquestion =  m_queslistArray.getJSONObject(i).getInt("closed");
						 if(typeofquestion==1){
							 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null); 
						 } else {
							 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
						 }
					        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
					        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
					        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
					        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+"   by "+m_queslistArray.getJSONObject(i).getString("askername"));
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
							tv.setText((j+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
					        tv.setOnClickListener(new View.OnClickListener() {
					        @Override
					        public void onClick(View v) {
					        	try {
					        		if (typeofquestion==1) {
										mController.onequestion(m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
									}
					        		else {
					        			s_latest tempLatest = new s_latest(mModel.getSessionid(),m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
					        			mController.onelatest(tempLatest);
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
  
  public void showall(View view) {
	  LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
	  l.removeAllViews();
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		try {
		 for (int i = 0; i <m_queslistArray.length() ; i++) {
			 View customView;
			 final int typeofquestion =  m_queslistArray.getJSONObject(i).getInt("closed");
			 if(typeofquestion==1){
				 customView = linflater.inflate(R.layout.qaforum_list_item_closed, null); 
			 } else {
				 customView = linflater.inflate(R.layout.qaforum_list_item_open, null);
			 }
		        TextView tv = (TextView) customView.findViewById(R.id.TextView01);
		        TextView topictext = (TextView) customView.findViewById(R.id.textView1);
		        TextView timetext = (TextView) customView.findViewById(R.id.textView2);
		        topictext.setText(m_queslistArray.getJSONObject(i).getString("topic")+"   by "+m_queslistArray.getJSONObject(i).getString("askername"));
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
				tv.setText((i+1)+": "+m_queslistArray.getJSONObject(i).getString("content"));
		        tv.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	try {
		        		if (typeofquestion==1) {
							mController.onequestion(m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
						}
		        		else {
		        			s_latest tempLatest = new s_latest(mModel.getSessionid(),m_queslistArray.getJSONObject(v.getId()).getInt("questionid"));
		        			mController.onelatest(tempLatest);
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
    if(hasFocus){
    	//notify to update mainview
    	mModel.getListenersToNotify().loadingFinished();
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