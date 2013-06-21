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
import org.pocketcampus.plugin.qaforum.shared.s_delete;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PendingNotificationActivity extends PluginView implements IQAforumView {

	private String msg;
	private QAforumController mController;
	private QAforumModel mModel;
	private int qnum;
	private int anum;
	private int fnum;
	private JSONArray m_questionlistArray;
	private JSONArray m_answerlistArray;
	private JSONArray m_feedbacklistArray;
	private Context context = this;
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
				qnum=dataJsonObject.getInt("qnum");
				anum=dataJsonObject.getInt("anum");
				fnum=dataJsonObject.getInt("fnum");
				m_questionlistArray=dataJsonObject.getJSONArray("questionlist");
				m_answerlistArray=dataJsonObject.getJSONArray("answerlist");
				m_feedbacklistArray=dataJsonObject.getJSONArray("feedbacklist");
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
		
		setContentView(R.layout.qaforum_list_main);
		
		final LinearLayout l = (LinearLayout) findViewById(R.id.mylayout1);
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		try {
			if (qnum!=0) {
				final View customViewtitle = linflater.inflate(R.layout.qaforum_pending_title, null);
		        TextView tv = (TextView) customViewtitle.findViewById(R.id.textView1);
		        tv.setText(getResources().getString(R.string.qaforum_question_question));
		        l.addView(customViewtitle);
				for (int i = 0; i <qnum; i++) {
			        final View customView = linflater.inflate(R.layout.qaforum_list_item_pending, null);
			        tv = (TextView) customView.findViewById(R.id.TextView01);
			        TextView timeTextView = (TextView) customView.findViewById(R.id.textView1);
			        timeTextView.setText(m_questionlistArray.getJSONObject(i).getString("time"));
			        tv.setId(i);
					tv.setText("Q: "+m_questionlistArray.getJSONObject(i).getString("content"));
					tv.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	try {
							String dataString=m_questionlistArray.getJSONObject(v.getId()).toString();
							Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
							intent.putExtra("data", dataString);
							startActivity(intent);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
			        }
			        });
			       Button deleteButton = (Button)customView.findViewById(R.id.button1);
			       final int forwardid=m_questionlistArray.getJSONObject(i).getInt("forwardid");
			       deleteButton.setOnClickListener(new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				    			// set title
				    			alertDialogBuilder.setTitle(getResources().getString(R.string.qaforum_confirmation));
				    			// set dialog message
				    			alertDialogBuilder
				    				.setMessage(getResources().getString(R.string.qaforum_confirm_delete))
				    				.setCancelable(false)
				    				.setPositiveButton(getResources().getString(R.string.qaforum_yes),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, close
				    						// current activity
				    						dialog.cancel();
				    						s_delete deleteInfo = new s_delete(mModel.getSessionid(), forwardid, 0);
								        	mController.deleteNotification(deleteInfo);
								        	l.removeView(customView);
								        	qnum--;
								        	if (qnum==0) {
								        		l.removeView(customViewtitle);
											}
				    					}
				    				  })
				    				.setNegativeButton(getResources().getString(R.string.qaforum_no),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, just close
				    						// the dialog box and do nothing
				    						dialog.cancel();
				    					}
				    				});
				    				// create alert dialog
				    				AlertDialog alertDialog = alertDialogBuilder.create();
				    				// show it
				    				alertDialog.show();
				        	}
				        });
			        l.addView(customView);
			    }
			}
			
			if (anum!=0) {
				final View customViewTitle = linflater.inflate(R.layout.qaforum_pending_title, null);
		        TextView tv = (TextView) customViewTitle.findViewById(R.id.textView1);
		        tv.setText(getResources().getString(R.string.qaforum_record_a)+":");
		        l.addView(customViewTitle);
				for (int i = 0; i <anum; i++) {
			        final View customView = linflater.inflate(R.layout.qaforum_list_item_pending, null);
			        tv = (TextView) customView.findViewById(R.id.TextView01);
			        TextView timeTextView = (TextView) customView.findViewById(R.id.textView1);
			        timeTextView.setText(m_answerlistArray.getJSONObject(i).getString("time"));
			        tv.setId(i);
					tv.setText("A: "+m_answerlistArray.getJSONObject(i).getString("answer"));
			        tv.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            //Toast.makeText(getApplicationContext(), v.getId() + "", Toast.LENGTH_LONG).show();
			        	try {
							String dataString=m_answerlistArray.getJSONObject(v.getId()).toString();
							Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
							intent.putExtra("data", dataString);
							startActivity(intent);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
			        }
			        });
			        Button deleteButton = (Button)customView.findViewById(R.id.button1);
				       final int forwardid=m_answerlistArray.getJSONObject(i).getInt("forwardid");
				       deleteButton.setOnClickListener(new View.OnClickListener() {
					        @Override
					        public void onClick(View v) {
					        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				    			// set title
					        	alertDialogBuilder.setTitle(getResources().getString(R.string.qaforum_confirmation));
				    			// set dialog message
				    			alertDialogBuilder
				    				.setMessage(getResources().getString(R.string.qaforum_confirm_delete))
				    				.setCancelable(false)
				    				.setPositiveButton(getResources().getString(R.string.qaforum_yes),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, close
				    						// current activity
				    						dialog.cancel();
				    						s_delete deleteInfo = new s_delete(mModel.getSessionid(), forwardid, 1);
								        	mController.deleteNotification(deleteInfo);
								        	l.removeView(customView);
								        	anum--;
								        	if (anum==0) {
								        		l.removeView(customViewTitle);
											}
				    					}
				    				  })
				    				.setNegativeButton(getResources().getString(R.string.qaforum_no),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, just close
				    						// the dialog box and do nothing
				    						dialog.cancel();
				    					}
				    				});
				    				// create alert dialog
				    				AlertDialog alertDialog = alertDialogBuilder.create();
				    				// show it
				    				alertDialog.show();
					        }
					        });
			        l.addView(customView);
			    }
			}
			
			if (fnum!=0) {
				final View customViewTitle = linflater.inflate(R.layout.qaforum_pending_title, null);
		        TextView tv = (TextView) customViewTitle.findViewById(R.id.textView1);
		        tv.setText(getResources().getString(R.string.qaforum_feedback));
		        l.addView(customViewTitle);
				for (int i = 0; i <fnum; i++) {
			        final View customView = linflater.inflate(R.layout.qaforum_list_item_pending, null);
			        tv = (TextView) customView.findViewById(R.id.TextView01);
			        TextView timeTextView = (TextView) customView.findViewById(R.id.textView1);
			        timeTextView.setText(m_feedbacklistArray.getJSONObject(i).getString("feedbacktime"));
			        tv.setId(i);
					tv.setText("F: "+m_feedbacklistArray.getJSONObject(i).getString("feedback"));
			        tv.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            //Toast.makeText(getApplicationContext(), v.getId() + "", Toast.LENGTH_LONG).show();
			        	try {
							String dataString=m_feedbacklistArray.getJSONObject(v.getId()).toString();
							Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
							intent.putExtra("data", dataString);
							startActivity(intent);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
			        }
			        });
			        Button deleteButton = (Button)customView.findViewById(R.id.button1);
				       final int forwardid=m_feedbacklistArray.getJSONObject(i).getInt("forwardid");
				       deleteButton.setOnClickListener(new View.OnClickListener() {
					        @Override
					        public void onClick(View v) {
					        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				    			// set title
					        	alertDialogBuilder.setTitle(getResources().getString(R.string.qaforum_confirmation));
				    			// set dialog message
				    			alertDialogBuilder
				    				.setMessage(getResources().getString(R.string.qaforum_confirm_delete))
				    				.setCancelable(false)
				    				.setPositiveButton(getResources().getString(R.string.qaforum_yes),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, close
				    						// current activity
				    						dialog.cancel();
				    						s_delete deleteInfo = new s_delete(mModel.getSessionid(), forwardid, 2);
								        	mController.deleteNotification(deleteInfo);
								        	l.removeView(customView);
								        	anum--;
								        	if (anum==0) {
								        		l.removeView(customViewTitle);
											}
				    					}
				    				  })
				    				.setNegativeButton(getResources().getString(R.string.qaforum_no),new DialogInterface.OnClickListener() {
				    					public void onClick(DialogInterface dialog,int id) {
				    						// if this button is clicked, just close
				    						// the dialog box and do nothing
				    						dialog.cancel();
				    					}
				    				});
				    				// create alert dialog
				    				AlertDialog alertDialog = alertDialogBuilder.create();
				    				// show it
				    				alertDialog.show();
					        }
					        });
			        l.addView(customView);
			    }
			}
			
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
	}
		 

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	    if(hasFocus){
	    	mModel.getListenersToNotify().loadingFinished();
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
	public void messageDeleted() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.qaforum_message_delelted), Toast.LENGTH_SHORT).show();
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