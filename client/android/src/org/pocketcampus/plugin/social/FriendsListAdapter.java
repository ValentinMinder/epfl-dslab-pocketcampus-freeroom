package org.pocketcampus.plugin.social;

import java.util.Vector;

import org.pocketcampus.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FriendsListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Context context_;
	private Vector<Username> friends_;
	private Activity friendsListActivity_;
	private int checkCount_;
	private Vector<Username> selectedFriends;
	
	public FriendsListAdapter(Context context, Vector<Username> friends, Activity friendsListActivity) {
		this.mInflater_ = LayoutInflater.from(context);
		this.context_ = context;
		this.friends_ = friends;
		this.friendsListActivity_ = friendsListActivity;
		this.checkCount_ = 0;
		this.selectedFriends = new Vector<Username>();
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
		// When you click on a menu description you'll get more info
		holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			Button buttonPermission = (Button) friendsListActivity_.findViewById(R.id.friendsListButtonPerm);
			Button buttonDelete = (Button) friendsListActivity_.findViewById(R.id.friendsListButtonDel);
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
//					_convertView.setSelected(true);
					_convertView.setPressed(true);
					if(checkCount_ == 0) {
						//buttons are disabled enabled if some friends are selected
						if(friendsListActivity_ instanceof FriendsList) {
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
							if(friendsListActivity_ instanceof FriendsList) {
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
		Username currentUsername = friends_.get(position);

		holder.username.setText(currentUsername.toString());

		return convertView;
	}
	
	private static class ViewHolder {
		TextView username;
		CheckBox selected;
	}
	
	public Vector<Username> getSelectedFriends() {
		return selectedFriends;
	}
}
