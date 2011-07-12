package org.pocketcampus.plugin.social;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.directory.DirectoryInfo;
import org.pocketcampus.plugin.directory.DirectoryPlugin;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;
import org.pocketcampus.plugin.positioning.IUserLocationListener;
import org.pocketcampus.plugin.positioning.UserPosition;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.FriendsLists;
import org.pocketcampus.shared.plugin.social.User;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
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
	private static SocialFriendsList this_;
	private SocialFriendsListAdapter friendsListAdapter_ = null;
	private SocialRequestingFriendsListAdapter requestingFriendsListAdapter_ = null;
	private Collection<User> friendsList_ = null;
	private Collection<User> requestingList_ = null;

	private ActionBar actionBar_;
	
	
	private boolean selectButtonIsEnabled_ = false;
	private boolean permissionButtonIsEnabled_ = false;
	private boolean deleteButtonIsEnabled_ = false;

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
		rp.addParameter("sciper", token.getSciper());
		rp.addParameter("sessionId", token.getSessionId());

		//Retrieve friends list
		SocialPlugin.getSocialRequestHandler().execute(new FriendsListsRequest(), "friends", rp);
	}
	
	public void enablePermissionButton() {
		if(!permissionButtonIsEnabled_)
			permissionButtonIsEnabled_ = true;
	}
	
	public void disablePermissionButton() {
		if(permissionButtonIsEnabled_)
			permissionButtonIsEnabled_ = false;
	}
	
	public void enableDeleteButton() {
		if(!deleteButtonIsEnabled_)
			deleteButtonIsEnabled_ = true;
	}
	
	public void disableDeleteButton() {
		if(deleteButtonIsEnabled_) 
			deleteButtonIsEnabled_ = false;
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
		.setPositiveButton(this_.getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				for(int i = 0; i < friendsListAdapter_.getSelectedFriends().size(); i++) {
					SocialPlugin.deleteRequest(this_, null, friendsListAdapter_.getSelectedFriends().get(i), this_);
				}
			}
		})
		.setNegativeButton(this_.getString(R.string.no), new DialogInterface.OnClickListener() {
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
				//Refresh content
				updateFriendsLists(friendsLists);

				actionBar_.setProgressBarVisibility(View.GONE);
				getListView().setTextFilterEnabled(true);
			} else {
				//If request fails, we close connection.
				AuthenticationPlugin.logout(this_);
				this_.finish();
			}
		}
	}

	private void updatePosition() {
		new UserPosition(this_, new IUserLocationListener() {
			@Override
			public void userLocationReceived(Location location) {
				if(location != null) {
					AuthToken token = AuthenticationPlugin.getAuthToken(this_);
					if(token != null) {
						RequestParameters rp = new RequestParameters();
						rp.addParameter("sciper", token.getSciper());
						rp.addParameter("sessionId", token.getSessionId());
						rp.addParameter("longitude", location.getLongitude()+"");
						rp.addParameter("latitude", location.getLatitude()+"");
						rp.addParameter("altitude", location.getAltitude()+"");

						RequestHandler handler = SocialPlugin.getSocialRequestHandler();
						if(handler != null) {
							handler.execute(new UpdatePositionRequest(), "updatePosition", rp);
						}
					}
				}
			}
		}, 7000, 20); //7000 = la limite max en millisecondes pour avoir la position, 20 = la précision souhaitée


		NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(R.string.app_name);

		int icon = R.drawable.app_icon;

		long when = System.currentTimeMillis();

		CharSequence contentTitle = this.getResources().getString(R.string.app_name);

		CharSequence contentText = this.getResources().getString(R.string.social_positioning_notification_content);

		Notification notificationActivity = new Notification(icon, contentTitle, when);

		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
		notificationActivity.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		manager.notify(R.string.app_name, notificationActivity);
	}

	class UpdatePositionRequest extends DataRequest {
		@Override
		protected void doInUiThread(String result) {
			boolean status = false;
			if(result != null) {
				Gson gson = new Gson();
				try{
					status = gson.fromJson(result, new TypeToken<Boolean>(){}.getType());
				} catch (JsonSyntaxException e) {
					status = false;
					e.printStackTrace();
				}
			}

			if(!status) {
				//				If request fails, we close connection.
				AuthenticationPlugin.logout(this_);
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

	/**
	 * Refresh the content of the adapters
	 * @param newFriends
	 */
	public void updateFriendsLists(FriendsLists newFriends) {
		boolean allEmpty = true;
		boolean friendsEmpty = true;
		int nbOnline = 0;

		SocialListSeparator listSeparator = new SocialListSeparator(this);

		//get updated requesting friends list
		requestingList_ = newFriends.getRequesting();
		requestingFriendsListAdapter_ = new SocialRequestingFriendsListAdapter(this_, requestingList_, this_);

		//if not empty, update the View
		if(!requestingList_.isEmpty()) {
			listSeparator.addSection(this_.getString(R.string.social_friendlist_section_requests), requestingFriendsListAdapter_);
			allEmpty = false;
		}

		LinkedList<User> onlineFriends = new LinkedList<User>(newFriends.getOnlineFriends());
		Collections.sort(onlineFriends);
		LinkedList<User> offlineFriends = new LinkedList<User>(newFriends.getOfflineFriends());
		Collections.sort(offlineFriends);

		nbOnline = onlineFriends.size();

		//get updated friends list
		friendsList_ = onlineFriends;
		for(User u : offlineFriends) {
			friendsList_.add(u);
		}
		friendsListAdapter_ = new SocialFriendsListAdapter(this_, friendsList_, nbOnline, this_);

		//if not empty, update the View
		if(!friendsList_.isEmpty()) {
			listSeparator.addSection(this_.getString(R.string.social_friendlist_section_friends), friendsListAdapter_);
			allEmpty = false;
			friendsEmpty = false;
		}

		//apply changes
		this.setListAdapter(listSeparator);

		//toggle button only if friends not empty
		selectButtonIsEnabled_ = !friendsEmpty;

		Collection<User> requestingPositionFriends = newFriends.getRequestingPositionFriends();
		if(!requestingPositionFriends.isEmpty()) showPositioningRequestAlert(requestingPositionFriends);

		//if nothing in both lists, display directory invitation
		if(allEmpty) displayMessage();
	}

	private void showPositioningRequestAlert(Collection<User> requesting) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this_);

		//Compose title
		CharSequence title = (requesting.size() == 1) ? 
				this.getString(R.string.social_positionalert_title_before)+requesting.iterator().next().getFirstName()+this.getString(R.string.social_positionalert_title_after) :
					this.getString(R.string.social_positionalert_title_before_plural)+requesting.size()+this.getString(R.string.social_positionalert_title_after_plural);

				builder.setMessage(title)
				.setCancelable(false)
				.setPositiveButton(this.getString(R.string.social_positionalert_button_update), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						updatePosition();
					}
				})
				.setNegativeButton(this.getString(R.string.social_positionalert_button_ignore), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				AlertDialog alert = builder.create();
				alert.setCanceledOnTouchOutside(true);
				alert.show();
	}

	public void setProgressBarVisible() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	public void setProgressBarGone() {
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.social, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		for(int i = 0; i < menu.size(); ++i) {
			MenuItem mi =  menu.getItem(i);
			boolean enabled = false;

			switch (menu.getItem(i).getItemId()) {
			case R.id.social_friendslist_optionmenu_delete:
				enabled = deleteButtonIsEnabled_;
				break;
			case R.id.social_friendslist_optionmenu_permission:
				enabled = permissionButtonIsEnabled_;
				break;
			case R.id.social_friendslist_optionmenu_toggle:
				enabled = selectButtonIsEnabled_;
				break;
			case R.id.social_friendslist_optionmenu_tracking:
				enabled = true;
				break;
			}

			mi.setEnabled(enabled);
		}

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

		case R.id.social_friendslist_optionmenu_tracking:
			updatePosition();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
