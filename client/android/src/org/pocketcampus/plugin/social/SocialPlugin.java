package org.pocketcampus.plugin.social;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.social.FriendsLists;
import org.pocketcampus.shared.plugin.social.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SocialPlugin extends PluginBase {

	SocialPreference preferences_;
	boolean logged_ = true;
	SocialPlugin thisActivity_ = this;
	Tracker tracker_;
	
	private static RequestHandler socialRequestHandler_ = null;
	
	public SocialPlugin() {
		socialRequestHandler_ = this.getRequestHandler();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tracker_ = Tracker.getInstance();
		
		AuthenticationPlugin.authenticate(this, SocialFriendsList.class);
		this.finish();
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		return new SocialInfo();
	}
	
	@Override
	public PluginPreference getPluginPreference() {
		return preferences_;
	}
	
	public static RequestHandler getSocialRequestHandler() {
		return socialRequestHandler_;
	}
	
	public static void sendRequest(Context context, Class<?> toStartNext, User target) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "send", null);
	}
	
	public static void sendRequest(Context context, Class<?> toStartNext, User target, SocialFriendsList listActivity) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "send", listActivity);
	}
	
	public static void deleteRequest(Context context, Class<?> toStartNext, User target) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "delete", null);
	}
	
	public static void deleteRequest(Context context, Class<?> toStartNext, User target, SocialFriendsList listActivity) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "delete", listActivity);
	}
	
	public static void acceptRequest(Context context, Class<?> toStartNext, User target) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "accept", null);
	}
	
	public static void acceptRequest(Context context, Class<?> toStartNext, User target, SocialFriendsList listActivity) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "accept", listActivity);
	}
	
	public static void ignoreRequest(Context context, Class<?> toStartNext, User target) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "ignore", null);
	}
	
	public static void ignoreRequest(Context context, Class<?> toStartNext, User target, SocialFriendsList listActivity) {
		if(context == null || target == null)
			throw new IllegalArgumentException();
		
		request(context, toStartNext, target, "ignore", listActivity);
	}
	
	private static void request(final Context context, final Class<?> toStartNext, User target, String type, final SocialFriendsList listActivity) {
		class SocialRequest extends DataRequest {
			@Override
			protected void doInUiThread(String result) {
				FriendsLists lists = null;
				if(result != null) {
					Gson gson = new Gson();
					try{
						lists = gson.fromJson(result, new TypeToken<FriendsLists>(){}.getType());
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					}
				}
				
				if(lists != null) {
					Toast.makeText(context, context.getResources().getString(R.string.social_operation_successful), Toast.LENGTH_LONG);
					
					if(listActivity != null) {
						listActivity.updateFriendsLists(lists);
					}
					
				} else {
					Toast.makeText(context, context.getResources().getString(R.string.social_operation_failure), Toast.LENGTH_LONG);
				}
				if(toStartNext != null) context.startActivity(new Intent(context, toStartNext));
			}
		}
		
		RequestParameters rp = new RequestParameters();
		rp.addParameter("username", PreferenceManager.getDefaultSharedPreferences(context).getString("username", null));
		rp.addParameter("sessionId", PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId", null));
		rp.addParameter("target", target.getSciper());
		
		SocialPlugin.getSocialRequestHandler().execute(new SocialRequest(), type, rp);
	}
}