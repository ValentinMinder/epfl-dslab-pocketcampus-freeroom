package org.pocketcampus.plugin.qaforum.android;

import org.pocketcampus.plugin.qaforum.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.qaforum.android.req.GetTequilaTokenRequest;
import org.pocketcampus.plugin.qaforum.android.activity.AnswerActivity;
import org.pocketcampus.plugin.qaforum.android.activity.AnswerListActivity;
import org.pocketcampus.plugin.qaforum.android.activity.AskActivity;
import org.pocketcampus.plugin.qaforum.android.activity.FeedbackActivity;
import org.pocketcampus.plugin.qaforum.android.activity.FeedbackListActivity;
import org.pocketcampus.plugin.qaforum.android.activity.GuideActivity;
import org.pocketcampus.plugin.qaforum.android.activity.LatestQuestionListActivity;
import org.pocketcampus.plugin.qaforum.android.activity.MyAnswerActivity;
import org.pocketcampus.plugin.qaforum.android.activity.MyQuestionActivity;
import org.pocketcampus.plugin.qaforum.android.activity.MyQuestionListActivity;
import org.pocketcampus.plugin.qaforum.android.activity.PendingNotificationActivity;
import org.pocketcampus.plugin.qaforum.android.activity.QuestionActivity;
import org.pocketcampus.plugin.qaforum.android.activity.QuestionListActivity;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumController;
import org.pocketcampus.plugin.qaforum.android.req.AcceptRequest;
import org.pocketcampus.plugin.qaforum.android.req.AskRequest;
import org.pocketcampus.plugin.qaforum.android.req.DeleteNotificationRequest;
import org.pocketcampus.plugin.qaforum.android.req.GetSessionIdRequest;
import org.pocketcampus.plugin.qaforum.android.req.LatestQuestionsRequest;
import org.pocketcampus.plugin.qaforum.android.req.MatchingQuestionRequest;
import org.pocketcampus.plugin.qaforum.android.req.MyAnswersRequest;
import org.pocketcampus.plugin.qaforum.android.req.MyQuestionsRequest;
import org.pocketcampus.plugin.qaforum.android.req.OneAnswerRequest;
import org.pocketcampus.plugin.qaforum.android.req.OneLatestQuestionsRequest;
import org.pocketcampus.plugin.qaforum.android.req.OneQuestionRequest;
import org.pocketcampus.plugin.qaforum.android.req.PendingNotificationRequest;
import org.pocketcampus.plugin.qaforum.android.req.RelationRequest;
import org.pocketcampus.plugin.qaforum.android.req.ReportRequest;
import org.pocketcampus.plugin.qaforum.android.req.SettingRequest;
import org.pocketcampus.plugin.qaforum.android.req.AnswerRequest;
import org.pocketcampus.plugin.qaforum.android.req.FeedbackRequest;
import org.pocketcampus.plugin.qaforum.android.QAforumModel;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Client;
import org.pocketcampus.plugin.qaforum.shared.QAforumService.Iface;
import org.pocketcampus.plugin.qaforum.shared.s_accept;
import org.pocketcampus.plugin.qaforum.shared.s_answer;
import org.pocketcampus.plugin.qaforum.shared.s_ask;
import org.pocketcampus.plugin.qaforum.shared.s_delete;
import org.pocketcampus.plugin.qaforum.shared.s_feedback;
import org.pocketcampus.plugin.qaforum.shared.s_latest;
import org.pocketcampus.plugin.qaforum.shared.s_relation;
import org.pocketcampus.plugin.qaforum.shared.s_report;
import org.pocketcampus.plugin.qaforum.shared.s_session;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


/**
 * QAforumController - Main logic for the QAforum Plugin.
 * 
 * This class issues requests to the QAforum PocketCampus
 * server to get the QAforum data of the logged in user.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 * 
 */


public class QAforumController extends PluginController implements IQAforumController{

	public static class Logouter extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.LOGOUT",
					Uri.parse("pocketcampus://qaforum.plugin.pocketcampus.org/logout"));
			context.startService(authIntent);
		}
	};

	/**
	 *  This name must match given in the Server.java file in plugin.launcher.server.
	 *  It's used to route the request to the right server implementation.
	 */
	private String mPluginName = "qaforum";
	

	/**
	 * Stores reference to the Model associated with this plugin.
	 */
	private QAforumModel mModel;
	
	/**
	 * HTTP Clients used to communicate with the PocketCampus server.
	 * Use thrift to transport the data.
	 */
	private Iface mClient;



	@Override
	public void onCreate() {
		mModel = new QAforumModel(getApplicationContext());
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	
	
	@Override
	public int onStartCommand(Intent aIntent, int flags, int startId) {
		if("org.pocketcampus.plugin.authentication.AUTHENTICATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("usercancelled") != 0) {
				Log.v("DEBUG", "QAforumController::onStartCommand user cancelled");
				mModel.getListenersToNotify().userCancelledAuthentication();
			} else if(extras != null && extras.getString("tequilatoken") != null) {
				Log.v("DEBUG", "QAforumController::onStartCommand auth succ");
				if(extras.getInt("forcereauth") != 0)
					mModel.setForceReauth(true);
				tokenAuthenticationFinished();
			} else {
				Log.v("DEBUG", "QAForumController::onStartCommand auth failed");
				mModel.getListenersToNotify().authenticationFailed();
			}
		}
		
		if("org.pocketcampus.plugin.authentication.LOGOUT".equals(aIntent.getAction())) {
			Log.v("DEBUG", "QAforumController::onStartCommand logout");
			//request to delete the sessionid in the server.
			s_delete deleteInfo = new s_delete(mModel.getSessionid(), 0, -1);
        	deleteNotification(deleteInfo);
			//mModel.clearQAforumCookie();
		}
		
		if("org.pocketcampus.plugin.pushnotif.REGISTRATION_FINISHED".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			if(extras != null && extras.getInt("succeeded") != 0) {
				System.out.println("push registration succeed.");
			} else if(extras != null && extras.getInt("failed") != 0) {
				System.out.println("push registration failed.");
			} else if(extras != null && extras.getInt("networkerror") != 0) {
				System.out.println("push registration networkerror.");
			} else {
				System.out.println("push registration unkown situation.");
			}
		}
		
		if("org.pocketcampus.plugin.pushnotif.PUSHNOTIF_MESSAGE".equals(aIntent.getAction())) {
			Bundle extras = aIntent.getExtras();
			NotificationManager manager = (NotificationManager) getApplicationContext()
				        .getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = prepareNotification(getApplicationContext(),  extras.getString("alert"), extras.getString("notificationid"));
			manager.notify(R.id.qaforum_notification_id, notification);
		}
		stopSelf();
		return START_NOT_STICKY;
	}
	
	
	  private Notification prepareNotification(Context context, String msg, String notificationid) {
		    long when = System.currentTimeMillis();
		    
		    Notification notification = new Notification(R.drawable.qaforum_notification, msg, when);
		    notification.flags |= Notification.FLAG_AUTO_CANCEL;

		    Intent intent = new Intent(context, MessageActivity.class);
		    intent.setData(Uri.parse(msg));
		    intent.putExtra("message", msg);
		    intent.putExtra("notificationid", notificationid);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
		        0);
		    String title = context.getString(R.string.app_name);
		    notification.setLatestEventInfo(context, title, msg, pendingIntent);
		    return notification;
		  }
	
	private void tokenAuthenticationFinished() {
		new GetSessionIdRequest().start(this, mClient, mModel.getTequilaToken());
	}
	
	public void gotTequilaToken() {
		pingAuthPlugin(getApplicationContext(), mModel.getTequilaToken().getITequilaKey());
	}
	
	public void pushnotification() {
		Intent authIntent = new Intent("org.pocketcampus.plugin.pushnotif.REGISTER_FOR_PUSH",
				Uri.parse("pocketcampus://pushnotif.plugin.pocketcampus.org/reg_for_push"));
		authIntent.putExtra("callbackurl", "pocketcampus://qaforum.plugin.pocketcampus.org/reg_finished");
		startService(authIntent);
	}
	
	public void getTequilaToken() {
		new GetTequilaTokenRequest().start(this, mClient, null);
	}
	
	public void acceptNotif(int notificationid, int accept) {
		s_accept temp=new s_accept(notificationid, accept);
		new AcceptRequest().start(this, mClient, temp);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}
	
	public void notLoggedIn() {
		mModel.clearQAforumCookie();
		getTequilaToken();
	}
	
	public static void pingAuthPlugin(Context context, String tequilaToken) {
		Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
				Uri.parse("pocketcampus://authentication.plugin.pocketcampus.org/authenticatetoken"));
		authIntent.putExtra("tequilatoken", tequilaToken);
		authIntent.putExtra("callbackurl", "pocketcampus://qaforum.plugin.pocketcampus.org/tokenauthenticated");
		authIntent.putExtra("shortname", "qaforum");
		authIntent.putExtra("longname", "QAforum");
		context.startService(authIntent);
	}
	
	public void ask(s_ask data) {
		new AskRequest().start(this, mClient, data);
	}
	public void answer(s_answer data) {
		new AnswerRequest().start(this, mClient, data);
	}
	public void feedback(s_feedback data) {
		new FeedbackRequest().start(this, mClient, data);
	}
	public void report(s_report data) {
		new ReportRequest().start(this, mClient, data);
	}
	public void setting(s_session data) {
		new SettingRequest().start(this, mClient, data);
	}
	public void myquestion(String data) {
		new MyQuestionsRequest().start(this, mClient, data);
	}
	public void onequestion(int data) {
		new OneQuestionRequest().start(this, mClient, data);
	}
	public void myanswer(String data) {
		new MyAnswersRequest().start(this, mClient, data);
	}
	public void oneanswer(int data) {
		new OneAnswerRequest().start(this, mClient, data);
	}
	public void latestquestions(String data) {
		new LatestQuestionsRequest().start(this, mClient, data);
	}
	public void onelatest(s_latest data) {
		new OneLatestQuestionsRequest().start(this, mClient, data);
	}
	public void pendingNotification(String data) {
		new PendingNotificationRequest().start(this, mClient, data);
	}
	public void deleteNotification(s_delete data) {
		new DeleteNotificationRequest().start(this, mClient, data);
	}
	public void matchingQuestion(String data) {
		new MatchingQuestionRequest().start(this, mClient, data);
	}
	public void relationship(s_relation data) {
		new RelationRequest().start(this, mClient, data);
	}
	
	public void callactivityOnelatest(String data) {
		Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
		intent.putExtra("data", data);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void callactivityQuestion(JSONObject data) throws JSONException {
		if(data.getString("type").equals("alert")){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_no_questions), Toast.LENGTH_SHORT).show();
			return;
		}
		if(data.getInt("number")==1){
			Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
			intent.putExtra("data", data.getJSONArray("questionlist").getJSONObject(0).toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
		else {
			Intent intent = new Intent(getApplicationContext(), QuestionListActivity.class);
			intent.putExtra("data", data.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}
	
	public void callactivityLatestQuestion(JSONObject data) throws JSONException {
		//recieve questions, asking for answers
		if(data.getString("type").equals("alert")){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_no_questions), Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(getApplicationContext(), LatestQuestionListActivity.class);
		intent.putExtra("data", data.toString());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	
	public void callactivityAnswer(JSONObject data) throws JSONException {
		//receive answers, asking feedbacks
		if(data.getInt("number")==1){
			Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
			intent.putExtra("data", data.getJSONArray("answerlist").getJSONObject(0).toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
		else {
			Intent intent = new Intent(getApplicationContext(), AnswerListActivity.class);
			intent.putExtra("data", data.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}
	
	public void callactivityFeedback(JSONObject data) throws JSONException {
		if(data.getInt("number")==1){
			Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
			intent.putExtra("data", data.getJSONArray("feedbacklist").getJSONObject(0).toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
		else {
			Intent intent = new Intent(getApplicationContext(), FeedbackListActivity.class);
			intent.putExtra("data", data.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}


	
	public void callactivityMyquestions(String result) throws JSONException {
		mModel.setMyQuestions(result);
		myanswer(mModel.getSessionid());
	}
	
	public void callactivityMyanswers(String result) throws JSONException {
		JSONObject dataJsonObject=new JSONObject(result);
		JSONObject myquestionJsonObject=new JSONObject(mModel.getMyQuestions());
		if (dataJsonObject.getJSONArray("myanswerlist").length()==0&&myquestionJsonObject.getJSONArray("myquestionlist").length()==0) {
			Toast.makeText(getApplicationContext(), "No history.", Toast.LENGTH_SHORT).show();
			mModel.getListenersToNotify().gotRequestReturn();
			return;
		}
		else {
			Intent intent=new Intent(getApplicationContext(), MyQuestionListActivity.class);
			intent.putExtra("questions", mModel.getMyQuestions());
			intent.putExtra("answers", result);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}
	
	public void callactivityOneanswer(String result) {
		Intent intent=new Intent(getApplicationContext(), MyAnswerActivity.class);
		intent.putExtra("data", result);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	public void callactivityOnequestion(String result) {
		Intent intent=new Intent(getApplicationContext(), MyQuestionActivity.class);
		intent.putExtra("data", result);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	public void callMainView() {
		Intent intent=new Intent(getApplicationContext(), QAforumMainView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	public void callactivityPending(JSONObject data) throws JSONException {
		if(data.getInt("qnum")==0&&data.getInt("anum")==0&&data.getInt("fnum")==0){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.qaforum_no_pending), Toast.LENGTH_SHORT).show();
			mModel.getListenersToNotify().gotRequestReturn();
			return;
		}	else {
			Intent intent = new Intent(getApplicationContext(), PendingNotificationActivity.class);
			intent.putExtra("data", data.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
	}
	
	public void callactivityRelation(String data) throws JSONException {

        if(mModel.currentActivity.getClass() == MyQuestionActivity.class)
		    ((MyQuestionActivity) mModel.currentActivity).showRelation(data);
        else
		    ((QuestionActivity) mModel.currentActivity).showRelation(data);
	}
	
	public void showMatching(String tempString) {
		((AskActivity) mModel.currentActivity).ShowMatchingDialog(tempString);
	}
	
	public void callactivityHelp() {
		/*
		Intent intent=new Intent(getApplicationContext(), HelpActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    */
		Intent intent=new Intent(getApplicationContext(), GuideActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
}


