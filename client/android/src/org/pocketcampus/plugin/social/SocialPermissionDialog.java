package org.pocketcampus.plugin.social;

import java.util.ArrayList;
import java.util.Collection;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.FriendsLists;
import org.pocketcampus.shared.plugin.social.User;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Permission panel
 * @status ugly, but fine
 * @author gldalmas@gmail.com
 */
public class SocialPermissionDialog extends Dialog {
	private Context context_;
	private final SocialPermissionDialog this_;
	private final CheckBox[] permissionBoxes_;
	private final ArrayList<Permission> permissions_;
	private final ArrayList<User> selectedUsers_;
	private final SocialFriendsList parentActivity_;
	private boolean updated_;
	private final LinearLayout permissionHolder_;
	private final LinearLayout permissionCheckboxesLayout_;
	private final LinearLayout progressBarLayout_;
	private final boolean connectionStatus_;
	
	private final Button requestPositionButton_;
	private final Button okButton_;

	public SocialPermissionDialog(final Context context, ArrayList<User> selectedUsers, ArrayList<Permission> permissions, SocialFriendsList parentActivity, boolean connectionStatus) {
		super(context);
		this.context_ = context;
		this.selectedUsers_ = selectedUsers;
		this.permissionBoxes_ = new CheckBox[permissions.size()];
		this.permissions_ = permissions;
		this.parentActivity_ = parentActivity;
		this.this_ = this;
		this.updated_ = false;
		this.permissionCheckboxesLayout_ = new LinearLayout(context_);
		this.progressBarLayout_ = new LinearLayout(context_);
		this.connectionStatus_ = connectionStatus;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.social_permission_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		
		this.requestPositionButton_ = (Button) findViewById(R.id.social_friends_request_position_button);
		this.okButton_ = (Button) findViewById(R.id.social_friends_ok);
		this.permissionHolder_ = (LinearLayout) findViewById(R.id.social_friends_permissions_holder);
		
		setDialogContent();
	}

	private void setDialogContent() {
		//Sets title accordingly
		((TextView) findViewById(R.id.social_friends_title)).setText(getTitle());
		
		if(permissions_ != null) {
			
			permissionCheckboxesLayout_.setOrientation(LinearLayout.VERTICAL);
			permissionCheckboxesLayout_.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			permissionCheckboxesLayout_.setVisibility(View.GONE);
			
			int i = 0;
			for(Permission permission : permissions_) {
				permissionBoxes_[i] = new CheckBox(context_);
				permissionBoxes_[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//						if(!updated_) {
//							updated_ = true;
//							okButton_.setEnabled(true);
//						}
					}
				});
				permissionBoxes_[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!updated_) {
							updated_ = true;
							okButton_.setEnabled(true);
						}	
					}
				});
				permissionBoxes_[i].setGravity(Gravity.LEFT);
				
				TextView tv = new TextView(context_);
				tv.setText(permission.getName());
				tv.setGravity(Gravity.RIGHT);

				LinearLayout ll = new LinearLayout(context_);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.addView(permissionBoxes_[i]);
				ll.addView(tv);
				
				permissionCheckboxesLayout_.addView(ll);
//				permissionCheckboxesLayout_.addView(tv);
				
				++i;
			}
			
			permissionHolder_.addView(permissionCheckboxesLayout_);
		}
		
		requestPositionButton_.setEnabled(connectionStatus_);// if friend offline, button disabled
		requestPositionButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AuthToken token = AuthenticationPlugin.getAuthToken(context_);
				
				RequestParameters rp = new RequestParameters();
				rp.addParameter("sciper", token.getSciper());
				rp.addParameter("sessionId", token.getSessionId());
				
				int n = selectedUsers_.size();
				rp.addParameter("n", n+"");
				
				for(int i = 0; i < n; i++) {
					rp.addParameter("target__"+i, selectedUsers_.get(i).getSciper());
				}
				
				parentActivity_.setProgressBarVisible();
				SocialPlugin.getSocialRequestHandler().execute(new RequestPositionRequest(), "requestPositions", rp);
			}
			
			class RequestPositionRequest extends DataRequest {
				@Override
				protected void doInUiThread(String result) {
					parentActivity_.setProgressBarGone();
					Toast toast = Toast.makeText(parentActivity_, parentActivity_.getResources().getString(R.string.social_permission_dialog_request_sent), Toast.LENGTH_LONG);
					toast.show();
					this_.dismiss();
				}
			}
		});
		
		okButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AuthToken token = AuthenticationPlugin.getAuthToken(context_);
				
				int nbU = selectedUsers_.size();
				int nbP = permissions_.size();
				int n = nbU * nbP;
				
				//allows to send all the data in a single request
				RequestParameters rp = new RequestParameters();
				rp.addParameter("sciper", token.getSciper());
				rp.addParameter("sessionId", token.getSessionId());
				rp.addParameter("n", ""+n);
				for(int i = 0; i < n; i++) {
					rp.addParameter("user__"+i, selectedUsers_.get(i % nbU).getSciper());
					rp.addParameter("permission__"+i, permissions_.get(i % nbP).getName());
					rp.addParameter("granted__"+i, permissionBoxes_[i % nbP].isChecked() ? "yes" : "no");
				}
				
				parentActivity_.setProgressBarVisible();
				SocialPlugin.getSocialRequestHandler().execute(new UpdatePermissionsRequest(), "updatePermissions", rp);
			}
			class UpdatePermissionsRequest extends DataRequest {
				@Override
				protected void doInUiThread(String result) {
					FriendsLists lists = null;
					if(result != null) {
						Gson gson = new Gson();
						try{
							lists = gson.fromJson(result, new TypeToken<FriendsLists>(){}.getType());
						} catch (JsonSyntaxException e) {
							e.printStackTrace();
						}
					}
					
					if(lists != null) {
						parentActivity_.updateFriendsLists(lists);
						parentActivity_.setProgressBarGone();
						this_.dismiss();
					}
				}
			}
		});
		
		//If only one user is selected, we try to get permissions that are already assigned to him
		if(selectedUsers_.size() == 1) {
			progressBarLayout_.setOrientation(LinearLayout.HORIZONTAL);
			
			ProgressBar progressBar = new ProgressBar(context_);
			
			TextView tv = new TextView(context_);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setText(context_.getResources().getString(R.string.social_loading));
			
			progressBarLayout_.addView(progressBar);
			progressBarLayout_.addView(tv);
			
			permissionHolder_.addView(progressBarLayout_);
			
			AuthToken token = AuthenticationPlugin.getAuthToken(context_);
			RequestParameters rp = new RequestParameters();
			rp.addParameter("sciper", token.getSciper());
			rp.addParameter("sessionId", token.getSessionId());
			rp.addParameter("granted_to", selectedUsers_.get(0).getSciper());
			
			SocialPlugin.getSocialRequestHandler().execute(new GetPermissionsRequest(), "getPermissions", rp);	
		} else {
			permissionCheckboxesLayout_.setVisibility(View.VISIBLE);
		}
	}
	
	class GetPermissionsRequest extends DataRequest {
		@Override
		protected void doInUiThread(String result) {
			ArrayList<Permission> grantedPermissions = null;
			if(result != null) {
				Gson gson = new Gson();
				try{
					Collection<Permission> collection = gson.fromJson(result, new TypeToken<Collection<Permission>>(){}.getType());
					grantedPermissions = (collection != null) ? new ArrayList<Permission>(collection) : null;
				} catch (JsonSyntaxException e) {
					grantedPermissions = null;
					e.printStackTrace();
				}
			}
			
			if(grantedPermissions != null) {
				for(int i = 0; i < permissions_.size(); i++) {
					permissionBoxes_[i].setChecked(grantedPermissions.contains(permissions_.get(i)));
				}
			}
			
			progressBarLayout_.setVisibility(View.GONE);
			permissionCheckboxesLayout_.setVisibility(View.VISIBLE);
		}
	}
	
	private String getTitle() {
		return context_.getString(R.string.social_permissions_title_a) + ((selectedUsers_.size() == 1) ? 
			selectedUsers_.get(0).getFirstName() + context_.getString(R.string.social_permissions_title_b) : 
			context_.getString(R.string.social_permissions_title_c));
	}
}
