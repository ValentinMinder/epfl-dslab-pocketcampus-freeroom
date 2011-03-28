package org.pocketcampus.plugin.social;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FriendsList extends ListActivity {
//	private ServerAPI serverAPI_;
	private SharedPreferences sharedPreferences_;
	private AuthToken authToken_;
	private FriendsListAdapter friendsListAdapter_;
	private final FriendsList thisActivity_ = this;
	private Vector<Username> friendsCollection;
	private boolean hasNotifications;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_friends_list);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new Action() {
			@Override
			public void performAction(View view) {
				SocialLogin.logout(thisActivity_);
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));
//		actionBar.addAction(new ActionBar.IntentClosingAction(this, new Intent(this, SocialLogout.class), android.R.drawable.presence_offline, this));
		
		
		
//		serverAPI_ = new ServerAPI();
		sharedPreferences_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		hasNotifications = false;
		
		String username = sharedPreferences_.getString("preferences_keyUsername", "N/a");
		String sessionId = sharedPreferences_.getString("preferences_keySessionId", "N/a");
		
		try {
			if(!username.equals("N/a") && !sessionId.equals("N/a") || true) {
				authToken_ = new AuthToken(new Username(username), new SessionId(sessionId));
//				friendsCollection = new Vector<Username>(serverAPI_.getFriends(authToken_));
				friendsCollection = new Vector<Username>();
				friendsCollection.add(new Username("pote 1"));
				friendsCollection.add(new Username("pote 2"));
				friendsCollection.add(new Username("pote 3"));
				friendsCollection.add(new Username("pote 4"));
				friendsCollection.add(new Username("pote 5"));
				friendsCollection.add(new Username("pote 6"));
				friendsCollection.add(new Username("pote 7"));
				friendsCollection.add(new Username("pote 8"));
				friendsCollection.add(new Username("pote 9"));
				friendsCollection.add(new Username("pote 10"));
				friendsCollection.add(new Username("pote 11"));
				friendsCollection.add(new Username("pote 12"));
				friendsCollection.add(new Username("pote 13"));
				friendsCollection.add(new Username("pote 14"));
				friendsCollection.add(new Username("pote 15"));
				
//				if(serverAPI_.getNotifications(authToken_).size() > 0)
//					hasNotifications = true;
				
				ListSeparator listSeparator = new ListSeparator(this);
				friendsListAdapter_ = new FriendsListAdapter(this, friendsCollection, this);
				listSeparator.addSection(thisActivity_.getString(R.string.social_friends_list_separator), friendsListAdapter_);
				setListAdapter(listSeparator);

				ListView lv = getListView();
				lv.setTextFilterEnabled(true);

			}
		} catch(Exception e) {
			e.printStackTrace();
			//TODO: something I guess
		}
		
		Button buttonSearch = (Button) findViewById(R.id.friendsListButtonAdd);
		buttonSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent friendsSearchIntent = new Intent(thisActivity_, FriendsSearch.class);
				thisActivity_.finish();
				thisActivity_.startActivity(friendsSearchIntent);
			}
		});
		
//		if(hasNotifications) {
//			buttonSearch.setBackgroundResource(R.drawable.social_notifications_button);
//		}
				
		Button buttonPermission = (Button) findViewById(R.id.friendsListButtonPerm);
		buttonPermission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent friendsPremissionsIntent = new Intent(thisActivity_, FriendsPermissions.class);
				friendsPremissionsIntent.putExtra("usernames", friendsListAdapter_.getSelectedFriends());
	        	startActivity(friendsPremissionsIntent);
	        	
//	        	thisActivity_.finish();
			}
		});
		
		Button buttonDelete = (Button) findViewById(R.id.friendsListButtonDel);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity_);
				builder.setMessage(thisActivity_.getString(R.string.social_delete_confirm))
				       .setCancelable(false)
				       .setPositiveButton(thisActivity_.getString(R.string.social_delete_confirm_yes), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
//				        	   
//				        	   try {
//					        	   Vector<Username> selectedFriends = friendsListAdapter_.getSelectedFriends();
//					        	   for(Username u : selectedFriends) 
//					        		   serverAPI_.removeContact(u, authToken_);
//					        	   
//					        	   Intent refresh = getIntent();
//					        	   thisActivity_.finish();
//					        	   thisActivity_.startActivity(refresh);
//
//				        	   } catch(ServerException e) {
//				        		   e.printStackTrace();
//				        		   new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_login));
//				        	   }
				           }
				       })
				       .setNegativeButton(thisActivity_.getString(R.string.social_delete_confirm_no), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent backToLogin = new Intent(thisActivity_, SocialLogin.class);
		this.finish();
		startActivity(backToLogin);
	}
}
