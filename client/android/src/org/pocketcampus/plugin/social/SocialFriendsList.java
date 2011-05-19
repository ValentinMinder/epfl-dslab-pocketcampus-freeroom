package org.pocketcampus.plugin.social;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.FriendsLists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Friends list
 * @status ok
 * @author gldalmas@gmail.com
 */
public class SocialFriendsList extends ListActivity { 
	private static Activity this_;
	private static SocialListSeparator listSeparator_;
	private SocialFriendsListAdapter friendsListAdapter_ = null;
	private SocialRequestingFriendsListAdapter requestingFriendsListAdapter_ = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_friends_list);
		
		this_ = this;
		
		setupActionBar((ActionBar) findViewById(R.id.actionbar));
		
		//Session info
		AuthToken token = AuthenticationPlugin.getAuthToken(this_);
		RequestParameters rp = new RequestParameters();
		rp.addParameter("username", token.getUsername());
		rp.addParameter("sessionId", token.getSessionId());
		
		//Retrieve friends list
		SocialPlugin.getSocialRequestHandler().execute(new FriendsListsRequest(), "friends", rp);
		
		//Button for toggling all check boxes at once
		Button buttonSelect = (Button) findViewById(R.id.friendsListButtonToggle);
		buttonSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggle();
			}
		});

		//Open permission panel for selected friends
		Button buttonPermission = (Button) findViewById(R.id.friendsListButtonPerm);
		buttonPermission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				permission();
			}
		});

		//Remove friends from contact list
		Button buttonDelete = (Button) findViewById(R.id.friendsListButtonDel);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();
			}
		});
	}
	
	private void toggle() {
		friendsListAdapter_.toggleAll();
	}
	
	private void permission() {
		SocialPlugin.getSocialRequestHandler().execute(new PermissionRequest(friendsListAdapter_), "permissions", new RequestParameters());
	}
	
	private void delete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this_);
		builder.setMessage(this_.getString(R.string.social_delete_confirm))
		.setCancelable(false)
		.setPositiveButton(this_.getString(R.string.social_delete_confirm_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				for(int i = 0; i < friendsListAdapter_.getSelectedFriends().size()-1; i++) {
					SocialPlugin.deleteRequest(this_, null, friendsListAdapter_.getSelectedFriends().get(i));
				}
				
				//last one reloads the page
				SocialPlugin.deleteRequest(this_, SocialFriendsList.class, friendsListAdapter_.getSelectedFriends().get(friendsListAdapter_.getSelectedFriends().size()-1));
			}
		})
		.setNegativeButton(this_.getString(R.string.social_delete_confirm_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	private class FriendsListsRequest extends DataRequest {
		@Override
		protected void doInUiThread(String result) {
			FriendsLists friendsLists = null;
			if(result != null) {
				Gson gson = new Gson();
				try{
					friendsLists = gson.fromJson(result, new TypeToken<FriendsLists>(){}.getType());
				} catch (JsonSyntaxException e) {
					friendsLists = null;
					e.printStackTrace();
				}
			}
			
			if(friendsLists != null) {
				listSeparator_ = new SocialListSeparator(this_);
				
				if(!friendsLists.getRequesting().isEmpty()) {
					//Starting adapter for requesting friends list
					requestingFriendsListAdapter_ = new SocialRequestingFriendsListAdapter(this_, friendsLists.getRequesting(), this_);
					listSeparator_.addSection(this_.getString(R.string.social_requesting_friends_list_separator), requestingFriendsListAdapter_);
				}
				if(!friendsLists.getFriends().isEmpty()) {
					//Starting adapter for friends list
					friendsListAdapter_ = new SocialFriendsListAdapter(this_, friendsLists.getFriends(), this_);
					listSeparator_.addSection(this_.getString(R.string.social_friends_list_separator), friendsListAdapter_);
				}
				setListAdapter(listSeparator_);
				getListView().setTextFilterEnabled(true);
			} else {
				//If request fails, we close connection.
				AuthenticationPlugin.logout(this_);
				this_.finish();
			}
		}
	}
	
	private static void setupActionBar(ActionBar actionBar) {
		actionBar.setTitle(R.string.app_name);
		actionBar.addAction(new Action() {
			@Override
			public void performAction(View view) {
				AuthenticationPlugin.logout(this_);
				this_.finish();
			}

			@Override
			public int getDrawable() {
				//TODO: logout icon
				return R.drawable.refresh;
			}
		});
		actionBar.addAction(new ActionBar.IntentAction(this_, MainscreenPlugin.createIntent(this_), R.drawable.mini_home));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.social, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.social_friendslist_optionmenu_delete:
			delete();
			return true;

		case R.id.social_friendslist_optionmenu_permission:
			permission();
			return true;
			
		case R.id.social_friendslist_optionmenu_toggle:
			toggle();
			return true;

		case R.id.social_friendslist_optionmenu_preferences:
			Intent intent = new Intent(this, SocialPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
