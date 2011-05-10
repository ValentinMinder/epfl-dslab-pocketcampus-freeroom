package org.pocketcampus.plugin.social;

import java.util.LinkedList;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.social.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class SocialRequestingFriendsListAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private LinkedList<User> requestingUsers_;
	private Context context_;
	private Activity this_;
	
	public SocialRequestingFriendsListAdapter(Context context, LinkedList<User> requestingUsers, Activity activity) {
		this.mInflater_ = LayoutInflater.from(context);
		this.requestingUsers_ = requestingUsers;
		this.context_ = context;
		this.this_ = activity;
		
	}
	
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		return requestingUsers_.size();
	}

	@Override
	public Object getItem(int position) {
		return requestingUsers_.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater_.inflate(R.layout.social_requesting_friends_list_item, null);
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});

		
		final TextView _username = (TextView) convertView.findViewById(R.id.social_requesting_friend);
		_username.setText(requestingUsers_.get(position).toString());
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context_);
				builder.setMessage(_username.getText() + " " + context_.getString(R.string.social_requesting_friend_notice))
				.setCancelable(false)
				.setPositiveButton(context_.getString(R.string.social_requesting_friend_confirm), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SocialPlugin.acceptRequest(context_, SocialFriendsList.class, requestingUsers_.get(position));
						this_.finish();
					}
				})
				.setNegativeButton(context_.getString(R.string.social_requesting_friend_ignore), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SocialPlugin.ignoreRequest(context_, SocialFriendsList.class, requestingUsers_.get(position));
						this_.finish();
					}
				});
				AlertDialog alert = builder.create();
				alert.setCanceledOnTouchOutside(true);
				alert.show();
			}
		});
		
		return convertView;
	}
}
