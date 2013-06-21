package org.pocketcampus.plugin.qaforum.android;


import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.qaforum.shared.QATequilaToken;
import org.pocketcampus.plugin.qaforum.shared.s_session;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumModel;
import org.pocketcampus.plugin.qaforum.android.iface.IQAforumView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * QAforumModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the QAforum plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.qaforumCookie.
 * Other data are temporary.
 * 
 * @author Susheng <susheng.shi@epfl.ch>
 *
 */
public class QAforumModel extends PluginModel implements IQAforumModel {
	
	/**
	 * Some constants.
	 */
	private static final String QAFORUM_STORAGE_NAME = "QAFORUM_STORAGE_NAME";
	private static final String QAFORUM_COOKIE_KEY_SESSION = "QAFORUM_COOKIE_KEY_SESSION";
	private static final String QAFORUM_COOKIE_KEY_ACCEPT = "QAFORUM_COOKIE_KEY_ACCEPT";
	private static final String QAFORUM_COOKIE_KEY_LANG = "QAFORUM_COOKIE_KEY_LANG";
	private static final String QAFORUM_COOKIE_KEY_REST = "QAFORUM_COOKIE_KEY_REST";
	private static final String QAFORUM_COOKIE_KEY_TOPIC = "QAFORUM_COOKIE_KEY_TOPIC";
	private static final String QAFORUM_COOKIE_KEY_ASKT = "QAFORUM_COOKIE_KEY_ASKT";
	private static final String QAFORUM_COOKIE_KEY_ASKE = "QAFORUM_COOKIE_KEY_ASKE";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IQAforumView mListeners = (IQAforumView) getListeners();
	private QATequilaToken tequilaToken;

	public PluginView currentActivity;
	private boolean forceReauth;
	private String mMyQuestionString;
	/**
	 * Data that need to be persistent. And could be changed by user when using.
	 */
	private int mAccept;
	private String mLanguage;
	private int mResttime;
	private String mTopic;
	
	/**
	 * Data that need to be persistent. And could not be changed by user.
	 */
	private String sessionId;
	private int mAskexpirytime;
	private int mAsktopic;
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public QAforumModel(Context context) {
		iStorage = context.getSharedPreferences(QAFORUM_STORAGE_NAME, 0);
		sessionId = iStorage.getString(QAFORUM_COOKIE_KEY_SESSION, null);
		mAsktopic = iStorage.getInt(QAFORUM_COOKIE_KEY_ASKT, 0);
		mAskexpirytime = iStorage.getInt(QAFORUM_COOKIE_KEY_ASKE, 0);
		
		mAccept = iStorage.getInt(QAFORUM_COOKIE_KEY_ACCEPT, 0);
		mLanguage = iStorage.getString(QAFORUM_COOKIE_KEY_LANG, null);
		mResttime = iStorage.getInt(QAFORUM_COOKIE_KEY_REST, 0);
		mTopic = iStorage.getString(QAFORUM_COOKIE_KEY_TOPIC, null);
	}
	
	public QATequilaToken getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(QATequilaToken arg) {
		tequilaToken = arg;
	}
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IQAforumView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IQAforumView getListenersToNotify() {
		return mListeners;
	}
	
	
	//getter and setter
	public boolean getForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean val) {
		forceReauth = val;
	}
	
	/**
	 * Setter and getter for camiproCookie
	 */
	public String getSessionid() {
		return sessionId;
	}
	public void clearQAforumCookie() {
		sessionId = null;
		mAsktopic = 0;
		mAskexpirytime =0;
		mAccept =0;
		mLanguage = null;
		mResttime =0;
		mTopic =null;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(QAFORUM_COOKIE_KEY_SESSION, null);
			editor.putInt(QAFORUM_COOKIE_KEY_ACCEPT, 0);
			editor.putString(QAFORUM_COOKIE_KEY_LANG, null);
			editor.putInt(QAFORUM_COOKIE_KEY_REST, 0);
			editor.putString(QAFORUM_COOKIE_KEY_TOPIC, null);
			editor.putInt(QAFORUM_COOKIE_KEY_ASKT, 0);
			editor.putInt(QAFORUM_COOKIE_KEY_ASKE, 0);
			editor.commit();
		}
	}
	
	public void setQAforumCookie(s_session result) {
		sessionId=result.sessionid;
		mAccept=result.accept;
		mLanguage=result.language;
		mResttime=result.resttime;
		mTopic=result.topic;
		mAsktopic=result.asktopic;
		mAskexpirytime=result.askexpiry;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(QAFORUM_COOKIE_KEY_SESSION, sessionId);
			editor.putInt(QAFORUM_COOKIE_KEY_ACCEPT, mAccept);
			editor.putString(QAFORUM_COOKIE_KEY_LANG, mLanguage);
			editor.putInt(QAFORUM_COOKIE_KEY_REST, mResttime);
			editor.putString(QAFORUM_COOKIE_KEY_TOPIC, mTopic);
			editor.putInt(QAFORUM_COOKIE_KEY_ASKT, mAsktopic);
			editor.putInt(QAFORUM_COOKIE_KEY_ASKE, mAskexpirytime);
			editor.commit();
		}
	}
	
	public void updateSettingCookie(s_session result) {
		mAccept=result.accept;
		mLanguage=result.language;
		mResttime=result.resttime;
		mTopic=result.topic;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putInt(QAFORUM_COOKIE_KEY_ACCEPT, mAccept);
			editor.putString(QAFORUM_COOKIE_KEY_LANG, mLanguage);
			editor.putInt(QAFORUM_COOKIE_KEY_REST, mResttime);
			editor.putString(QAFORUM_COOKIE_KEY_TOPIC, mTopic);
			editor.commit();
		}
	}
	
	public int getAccept() {
		return mAccept;
	}
	
	public String getLanguage() {
		return mLanguage;
	}
	
	public int getResttime() {
		return mResttime;
	}
	
	public String getTopic() {
		return mTopic;
	}
	public void setMyQuestions(String myquestionString) {
		mMyQuestionString = myquestionString;
	}
	public String getMyQuestions() {
		return mMyQuestionString;
	}
	public int getAskTopic() {
		return mAsktopic;
	}
	public int getAskExpiryTime() {
		return mAskexpirytime;
	}
}
