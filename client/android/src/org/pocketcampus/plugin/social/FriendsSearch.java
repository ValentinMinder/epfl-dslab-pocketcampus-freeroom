package org.pocketcampus.plugin.social;

import java.util.ArrayList;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FriendsSearch extends ListActivity {
//	private ServerAPI serverAPI_;
	private SharedPreferences sharedPreferences_;
	private AuthToken authToken_;
	private FriendsListAdapter requestingFriendsListAdapter_;
	private FriendsSearch thisActivity_;
	private Vector<Username> requestingFriendsCollection_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_friends_requests_list);
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
		
//		serverAPI_ = new ServerAPI();
		sharedPreferences_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		thisActivity_ = this;
		
		String username = sharedPreferences_.getString("preferences_keyUsername", "N/a");
		String sessionId = sharedPreferences_.getString("preferences_keySessionId", "N/a");
		
		authToken_ = new AuthToken(new Username(username), new SessionId(sessionId));
		
		requestingFriendsCollection_ = new Vector<Username>();
		
		try {
//			Collection<INotification> notifications = serverAPI_.getNotifications(authToken_);
			ArrayList<ContactNotification> notifications = new ArrayList<ContactNotification>();
			notifications.add(new ContactNotification(new Username("pas pote 1"), null));
			notifications.add(new ContactNotification(new Username("pas pote 2"), null));
			notifications.add(new ContactNotification(new Username("pas pote 3"), null));
			notifications.add(new ContactNotification(new Username("pas pote 4"), null));
			notifications.add(new ContactNotification(new Username("pas pote 5"), null));
			notifications.add(new ContactNotification(new Username("pas pote 6"), null));
			notifications.add(new ContactNotification(new Username("pas pote 7"), null));
			notifications.add(new ContactNotification(new Username("pas pote 8"), null));
			notifications.add(new ContactNotification(new Username("pas pote 9"), null));
			notifications.add(new ContactNotification(new Username("pas pote 10"), null));
			
			if(notifications != null) {
				for(ContactNotification n : notifications) {
//					if(n instanceof ContactNotification) {
						ContactNotification nn = (ContactNotification) n;
						requestingFriendsCollection_.add(nn.getFrom());
//					}
				}

				ListSeparator listSeparator = new ListSeparator(this);
				requestingFriendsListAdapter_ = new FriendsListAdapter(this, requestingFriendsCollection_, this);
				listSeparator.addSection(thisActivity_.getString(R.string.social_friends_search_requesting_friends_separator), requestingFriendsListAdapter_);
				setListAdapter(listSeparator);

				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
			} else {
				new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_login));
			}
		} catch(Exception e) {
			e.printStackTrace();
			new NotConnectedAlert(thisActivity_, thisActivity_.getString(R.string.social_login));
		}
		
		Button buttonValid = (Button) findViewById(R.id.friendsRequestsListButtonValid);
		buttonValid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				EditText friendSearch = (EditText) findViewById(R.id.friendsRequestsListField);
//				String message = "";
//				
//				try {
//					String typed = friendSearch.getText().toString();
//					String username = sharedPreferences_.getString("preferences_keyUsername", "N/a");
//					
//					if(!typed.equals("") && !typed.equals(username)) {
//						boolean status = serverAPI_.requestContact(new Username(typed), authToken_);
//						
//						if(status) 
//							message = thisActivity_.getString(R.string.social_friends_search_request_sent)+" "+typed;
//						else
//							message = thisActivity_.getString(R.string.social_friends_search_request_not_sent)+" "+typed;
//						
//						
//					}
//					
//					//refreshing page
//					Intent refresh = getIntent();
//					thisActivity_.finish();
//					thisActivity_.startActivity(refresh);
//					
//					
//				} catch(ServerException e) {
//					e.printStackTrace();
//				}
//				
//				try {
//					Collection<Username> selectedFriends = requestingFriendsListAdapter_.getSelectedFriends();
//					int size = 0;
//					if(selectedFriends.size() > 0) {
//						for(Username username : selectedFriends) {
//							if(serverAPI_.acceptContact(username, authToken_))
//								++size;
//						}
//					}
//					
//					if(size > 0) {
//						if(!message.equals("")) 
//							message+="\n";
//						
//						message += size+" "+thisActivity_.getString(R.string.social_friends_search_request_accepted);
//					}
//				} catch(ServerException e) {
//					e.printStackTrace();
//				}
//				
//				if(!message.equals(""))
//					Toast.makeText(thisActivity_, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void onBackPressed() {
		Intent friendsListIntent = new Intent(FriendsSearch.this,
				FriendsList.class);
		startActivity(friendsListIntent);
		this.finish();
	}
}
