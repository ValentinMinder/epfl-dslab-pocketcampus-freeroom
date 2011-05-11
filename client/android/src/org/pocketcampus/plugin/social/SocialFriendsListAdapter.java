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
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SocialFriendsListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private LinkedList<User> friends_;
	private Activity socialFriendsListActivity_;
	private int checkCount_;
	private LinkedList<User> selectedFriends;
	private ArrayList<ViewHolder> holders_;
	
	public SocialFriendsListAdapter(Context context, LinkedList<User> friends, Activity socialFriendsListActivity) {
		this.mInflater_ = LayoutInflater.from(context);
		this.friends_ = friends;
		this.socialFriendsListActivity_ = socialFriendsListActivity;
		this.checkCount_ = 0;
		this.selectedFriends = new LinkedList<User>();
		this.holders_ = new ArrayList<ViewHolder>();
	}
	
	
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		
		final CheckBox _selected = holder.selected;
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_selected.toggle();
			}
		});

		
		final View _convertView = convertView;
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				_selected.toggle();
			}
		});
		
		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				SocialPlugin.getSocialRequestHandler().execute(new PermissionRequest(), "permissions", new RequestParameters());
				return false;
			}
			
			class PermissionRequest extends DataRequest {
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
					Toast.makeText(socialFriendsListActivity_, ""+permissions.size(), Toast.LENGTH_LONG).show();
					if(permissions != null) showDialog(position, permissions);
				}
			}
		});
		
		// When you click on a menu description you'll get more info
		holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			Button buttonPermission = (Button) socialFriendsListActivity_.findViewById(R.id.friendsListButtonPerm);
			Button buttonDelete = (Button) socialFriendsListActivity_.findViewById(R.id.friendsListButtonDel);
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
//					_convertView.setSelected(true);
					_convertView.setPressed(true);
					if(checkCount_ == 0) {
						//buttons are disabled enabled if some friends are selected
						if(socialFriendsListActivity_ instanceof SocialFriendsList) {
							if(!buttonPermission.isEnabled())
								buttonPermission.setEnabled(true);
							if(!buttonDelete.isEnabled())
								buttonDelete.setEnabled(true);
						}
					}
					
					selectedFriends.add(friends_.get(position));
					++checkCount_;
				} else {
//					_convertView.setSelected(false);
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
						
						selectedFriends.remove(friends_.get(position));
						--checkCount_;
					}
				}
			}
		});

		// Bind the data efficiently with the holder.
		User currentUser = friends_.get(position);

		holder.username.setText(currentUser.toString());

		return convertView;
	}
	
	public void toggleAll() {
		boolean uncheckAll = true;
		for(ViewHolder holder : holders_) {
			if(!holder.selected.isChecked()) {
				holder.selected.toggle();
				uncheckAll = false;
			}
		}
		if(uncheckAll) {
			for(ViewHolder holder : holders_) {
				holder.selected.toggle();
			}
		}
	}
	
	private void showDialog(int pos, Collection<Permission> permissions){
		ArrayList<User> list = new ArrayList<User>();
		list.add(friends_.get(pos));
		SocialPermissionDialog r = new SocialPermissionDialog(socialFriendsListActivity_, list, new ArrayList<Permission>(permissions), socialFriendsListActivity_);
		r.show();
	}
	//(final Context context, Collection<User> selectedUsers, Collection<Permission> permissions)
	private static class ViewHolder {
		TextView username;
		CheckBox selected;
	}
	
	public LinkedList<User> getSelectedFriends() {
		return selectedFriends;
	}
}
