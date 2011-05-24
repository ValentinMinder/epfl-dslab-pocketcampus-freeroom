package org.pocketcampus.plugin.social;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.directory.DirectoryInfo;
import org.pocketcampus.plugin.directory.DirectoryPlugin;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.FriendsLists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	
	private ActionBar actionBar_;
	private Button buttonSelect_;
	private Button buttonPermission_;
	private Button buttonDelete_;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_friends_list);
		
		this_ = this;
		
		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		setupActionBar(actionBar_);
		
		//Session info
		AuthToken token = AuthenticationPlugin.getAuthToken(this_);
		RequestParameters rp = new RequestParameters();
		rp.addParameter("username", token.getUsername());
		rp.addParameter("sessionId", token.getSessionId());
		
		//Retrieve friends list
		SocialPlugin.getSocialRequestHandler().execute(new FriendsListsRequest(), "friends", rp);
		
		//Button for toggling all check boxes at once
		buttonSelect_ = (Button) findViewById(R.id.friendsListButtonToggle);
		buttonSelect_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggle();
			}
		});

		//Open permission panel for selected friends
		buttonPermission_ = (Button) findViewById(R.id.friendsListButtonPerm);
		buttonPermission_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				permission();
			}
		});

		//Remove friends from contact list
		buttonDelete_ = (Button) findViewById(R.id.friendsListButtonDel);
		buttonDelete_.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();
			}
		});
	}
	
	private void toggle() {
		if(friendsListAdapter_ != null) friendsListAdapter_.toggleAll();
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
				
				//close previous
				this_.finish();
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
				boolean allEmpty = true;
				boolean friendsEmpty = true;
				
				if(!friendsLists.getRequesting().isEmpty()) {
					//Starting adapter for requesting friends list
					requestingFriendsListAdapter_ = new SocialRequestingFriendsListAdapter(this_, friendsLists.getRequesting(), this_);
					listSeparator_.addSection(this_.getString(R.string.social_requesting_friends_list_separator), requestingFriendsListAdapter_);
					allEmpty = false;
				}
				if(!friendsLists.getFriends().isEmpty()) {
					//Starting adapter for friends list
					friendsListAdapter_ = new SocialFriendsListAdapter(this_, friendsLists.getFriends(), this_);
					listSeparator_.addSection(this_.getString(R.string.social_friends_list_separator), friendsListAdapter_);
					allEmpty = false;
					friendsEmpty = false;
				}
				
				if(!friendsEmpty) {
					buttonSelect_.setEnabled(true);
				}
				
				if(allEmpty) {
					displayMessage();
				}
				
				actionBar_.setProgressBarVisibility(View.GONE);
				
				setListAdapter(listSeparator_);
				
				getListView().setTextFilterEnabled(true);
			} else {
				//If request fails, we close connection.
				AuthenticationPlugin.logout(this_);
				this_.finish();
			}
		}
	}
	
	private void displayMessage() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.SocialFriendsListBodyHolder);
		
		LinearLayout msgHolder = new LinearLayout(this);
//		msgHolder.setGravity(Gravity.CENTER);
		msgHolder.setOrientation(LinearLayout.VERTICAL);
		
		TextView tv = new TextView(this);
		tv.setText(this.getResources().getString(R.string.social_friendlist_empty_message));
//		tv.setGravity(Gravity.CENTER);
		
		LinearLayout buttonHolder = new LinearLayout(this);
		buttonHolder.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		buttonHolder.setPadding(0, 10, 0, 0);
//		buttonHolder.setGravity(Gravity.CENTER);
		
		ImageButton ib = new ImageButton(this);
		ib.setImageDrawable(new DirectoryInfo().getIcon().getDrawable(this));
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				this_.startActivity(new Intent(this_, DirectoryPlugin.class));
				this_.finish();
			}
		});
//		ib.setAdjustViewBounds(true);
//		ib.setMaxWidth(new DirectoryInfo().getIcon().getDrawable(this).getIntrinsicWidth());
//		ib.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		buttonHolder.addView(ib);
		
		msgHolder.addView(tv);
		msgHolder.addView(buttonHolder);
		
		LinearLayout newMsg = new LinearLayout(this);
//		newMsg.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		newMsg.setGravity(Gravity.CENTER);
		newMsg.addView(msgHolder);
		
		ll.removeAllViews();
		ll.addView(newMsg);
	}
	
	private static void setupActionBar(ActionBar actionBar) {
		actionBar.setTitle(R.string.app_name);
		actionBar.setProgressBarVisibility(View.VISIBLE);
		
		actionBar.addAction(new Action() {
			@Override
			public void performAction(View view) {
				AuthenticationPlugin.logout(this_);
				this_.finish();
			}

			@Override
			public int getDrawable() {
				return R.drawable.logout;
			}
		});
		actionBar.addAction(new ActionBar.IntentAction(this_, MainscreenPlugin.createIntent(this_), R.drawable.mini_home));
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.social, menu);
//		
//		return true;
//	}
//	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		menu.getItem(R.id.social_friendslist_optionmenu_delete).setEnabled(buttonDelete_.isEnabled());
//		menu.getItem(R.id.social_friendslist_optionmenu_permission).setEnabled(buttonPermission_.isEnabled());
//		menu.getItem(R.id.social_friendslist_optionmenu_toggle).setEnabled(buttonSelect_.isEnabled());
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.social_friendslist_optionmenu_delete:
//			delete();
//			return true;
//
//		case R.id.social_friendslist_optionmenu_permission:
//			permission();
//			return true;
//			
//		case R.id.social_friendslist_optionmenu_toggle:
//			toggle();
//			return true;
//
//		case R.id.social_friendslist_optionmenu_preferences:
//			Intent intent = new Intent(this, SocialPreference.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			this.startActivity(intent);
//			this.finish();
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
}
