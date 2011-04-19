package org.pocketcampus.plugin.social;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.shared.plugin.authentication.Username;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FriendsList extends ListActivity {
	private FriendsListAdapter friendsListAdapter_;
	private final FriendsList thisActivity_ = this;
	private Vector<Username> friendsCollection;
	
	private final String username_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username", null);
	private final String sessionId_ = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("sessionId", null);

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
		//			actionBar.addAction(new ActionBar.IntentClosingAction(this, new Intent(this, SocialLogout.class), android.R.drawable.presence_offline, this));

		friendsCollection = retrieveFriends(username_, sessionId_);
		
		ListSeparator listSeparator = new ListSeparator(this);
		friendsListAdapter_ = new FriendsListAdapter(this, friendsCollection, this);
		listSeparator.addSection(thisActivity_.getString(R.string.social_friends_list_separator), friendsListAdapter_);
		setListAdapter(listSeparator);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		

		Button buttonSearch = (Button) findViewById(R.id.friendsListButtonAdd);
		buttonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(thisActivity_, FriendsSearch.class);
				thisActivity_.startActivity(intent);
				thisActivity_.finish();
			}
		});

		Button buttonPermission = (Button) findViewById(R.id.friendsListButtonPerm);
		buttonPermission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent friendsPremissionsIntent = new Intent(thisActivity_, FriendsPermissions.class);
				friendsPremissionsIntent.putExtra("usernames", friendsListAdapter_.getSelectedFriends());
				startActivity(friendsPremissionsIntent);
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
						Vector<Username> selectedFriends = friendsListAdapter_.getSelectedFriends();
						deleteFriendship(username_, sessionId_, selectedFriends);
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

	private Vector<Username> retrieveFriends(String username, String sessionId) {

		return null;
	}
	
	private void deleteFriendship(String username, String sessionId, Vector<Username> badFriends) {
		
	}
}
