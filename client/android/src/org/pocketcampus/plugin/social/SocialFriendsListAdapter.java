package org.pocketcampus.plugin.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.shared.plugin.social.User;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Adapter for the elements of the friends sublist, in the friend list activity.
 * 
 * @status ok
 * @author gldalmas@gmail.com
 */
public class SocialFriendsListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private LinkedList<User> friends_;
	private Activity socialFriendsListActivity_;
	private int checkCount_;
	private LinkedList<User> selectedFriends_;
	private ArrayList<ViewHolder> holders_;
	private SocialFriendsListAdapter this_;
	
	public SocialFriendsListAdapter(Context context, LinkedList<User> friends, Activity socialFriendsListActivity) {
		this.mInflater_ = LayoutInflater.from(context);
		this.friends_ = friends;
		this.socialFriendsListActivity_ = socialFriendsListActivity;
		this.checkCount_ = 0;
		this.selectedFriends_ = new LinkedList<User>();
		this.holders_ = new ArrayList<ViewHolder>();
		this.this_ = this;
	}
	
	
	@Override
	public Filter getFilter() {
		return null;
	}

	@Override
	public int getCount() {
		return friends_.size();
	}

	@Override
	public Object getItem(int position) {
		return friends_.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*
		 * A ViewHolder keeps references to children views to avoid unnecessary
		 * calls to findViewById() on each row.
		 */
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater_.inflate(R.layout.social_friends_list_item, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.username = (TextView) convertView.findViewById(R.id.social_friend);
			holder.selected = (CheckBox) convertView.findViewById(R.id.check);
			holders_.add(holder);
			
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}
		
		//Sets the name
		holder.username.setText(friends_.get(position).toString());
		
		
		final CheckBox _selected = holder.selected;
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				_selected.toggle();
//			}
//		});

		final View _convertView = convertView;
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_selected.toggle();
			}
		});
		
		//Open permission panel for a single friend if long click
		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				LinkedList<User> clickedFriend = new LinkedList<User>();
				clickedFriend.add(friends_.get(position));
				SocialPlugin.getSocialRequestHandler().execute(new PermissionRequest(this_, clickedFriend), "permissions", new RequestParameters());
				return false;
			}
		});

		//Updates button state and selected friends list on every status change
		holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			Button buttonPermission = (Button) socialFriendsListActivity_.findViewById(R.id.friendsListButtonPerm);
			Button buttonDelete = (Button) socialFriendsListActivity_.findViewById(R.id.friendsListButtonDel);
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					_convertView.setPressed(true);
					if(checkCount_ == 0) {
						//buttons are enabled if some friends are selected
						if(socialFriendsListActivity_ instanceof SocialFriendsList) {
							if(!buttonPermission.isEnabled())
								buttonPermission.setEnabled(true);
							if(!buttonDelete.isEnabled())
								buttonDelete.setEnabled(true);
						}
					}
					
					selectedFriends_.add(friends_.get(position));
					++checkCount_;
				} else {
					_convertView.setPressed(false);
					if(checkCount_ == 0) { //hum...
					} else {
						if(checkCount_ == 1) {
							//buttons are disabled if no friend is selected
							if(socialFriendsListActivity_ instanceof SocialFriendsList) {
								if(buttonPermission.isEnabled())
									buttonPermission.setEnabled(false);
								if(buttonDelete.isEnabled())
									buttonDelete.setEnabled(false);
							}
						}
						
						selectedFriends_.remove(friends_.get(position));
						--checkCount_;
					}
				}
			}
		});

		return convertView;
	}
	
	public void toggleAll() {
		boolean checkAll = true;
		int i = 0;
		//We uncheck every checkbox...
		for(ViewHolder holder : holders_) {
			if(holder.selected.isChecked()) {
				holder.selected.setChecked(false);
				checkAll = false;
				i++;
			}
		}
		//...except if every cb is already unchecked, in that case we check all.
		if(checkAll) {
			for(ViewHolder holder : holders_) {
				holder.selected.setChecked(true);
			}
		}
	}
	
	//Displays permission panel
	public void showDialog(Collection<User> users, Collection<Permission> permissions){
		SocialPermissionDialog r = new SocialPermissionDialog(socialFriendsListActivity_, new ArrayList<User>(users), new ArrayList<Permission>(permissions), socialFriendsListActivity_);
		r.show();
	}
	
	private static class ViewHolder {
		TextView username;
		CheckBox selected;
	}
	
	public LinkedList<User> getSelectedFriends() {
		return selectedFriends_;
	}
}

//Retrieve the list of permission ids available in the system
class PermissionRequest extends DataRequest {
	private final SocialFriendsListAdapter adapter_;
	private final Collection<User> users_;
	
	//Constructor using selected friends list of the adapter (usual)
	public PermissionRequest(SocialFriendsListAdapter adapter) {
		adapter_ = adapter;
		users_ = adapter.getSelectedFriends();
	}
	
	//Constructor using particular selected friends list (for the long click listener)
	public PermissionRequest(SocialFriendsListAdapter adapter, Collection<User> users) {
		adapter_ = adapter;
		users_ = users;
	}
	
	@Override
	protected void doInUiThread(String result) {
		Collection<Permission> permissions;
		
		if(result != null) {
			Gson gson = new Gson();
			try{
				permissions = gson.fromJson(result, new TypeToken<Collection<Permission>>(){}.getType());
			} catch (JsonSyntaxException e) {
				permissions = null;
				e.printStackTrace();
			}
		} else {
			permissions = null;
		}
		
		if(permissions != null) adapter_.showDialog(users_, permissions);
	}
}
