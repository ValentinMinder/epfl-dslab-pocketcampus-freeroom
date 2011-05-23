package org.pocketcampus.plugin.social;

import java.util.ArrayList;
import java.util.Collection;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.User;
import org.pocketcampus.shared.plugin.social.permissions.Permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Permission panel
 * @status ugly, but fine
 * @author gldalmas@gmail.
 */
public class SocialPermissionDialog extends Dialog {
	private Context context_;
	private final SocialPermissionDialog this_;
	private final CheckBox[] permissionBoxes_;
	private final ArrayList<Permission> permissions_;
	private final ArrayList<User> selectedUsers_;
	private final Activity parentActivity_;
	private boolean updated_;
	
//	private final Button chatButton_;
	private final Button okButton_;

	public SocialPermissionDialog(final Context context, ArrayList<User> selectedUsers, ArrayList<Permission> permissions, Activity parentActivity) {
		super(context);
		this.context_ = context;
		this.selectedUsers_ = selectedUsers;
		this.permissionBoxes_ = new CheckBox[permissions.size()];
		this.permissions_ = permissions;
		this.parentActivity_ = parentActivity;
		this.this_ = this;
		this.updated_ = false;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.social_permission_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		
//		chatButton_ = (Button) findViewById(R.id.social_friends_chat_button);
		okButton_ = (Button) findViewById(R.id.social_friends_ok);
		
		setDialogContent();
	}

	private void setDialogContent() {
		//Sets title accordingly
		((TextView) findViewById(R.id.social_friends_title)).setText(getTitle());
		
		if(permissions_ != null) {
			LinearLayout permissionHolder = (LinearLayout) findViewById(R.id.social_friends_permissions_holder);
			int i = 0;
			for(Permission permission : permissions_) {
				permissionBoxes_[i] = new CheckBox(context_);
				permissionBoxes_[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
				
				LinearLayout newPermission = new LinearLayout(context_);
				newPermission.setOrientation(LinearLayout.HORIZONTAL);
				newPermission.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				newPermission.addView(permissionBoxes_[i]);
				newPermission.addView(tv);
				permissionHolder.addView(newPermission);
				
				++i;
			}
		}
		
//		// BOUTON chat
//		chatButton_.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				
//			}
//		});
		
		okButton_.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AuthToken token = AuthenticationPlugin.getAuthToken(context_);
				
				int nbU = selectedUsers_.size();
				int nbP = permissions_.size();
				int n = nbU * nbP;
				
				//allows to send all the data in a single request
				RequestParameters rp = new RequestParameters();
				rp.addParameter("username", token.getUsername());
				rp.addParameter("sessionId", token.getSessionId());
				rp.addParameter("n", ""+n);
				for(int i = 0; i < n; i++) {
					rp.addParameter("user__"+i, selectedUsers_.get(i % nbU).getIdFormat());
					rp.addParameter("permission__"+i, permissions_.get(i % nbP).getName());
					rp.addParameter("granted__"+i, permissionBoxes_[i % nbP].isChecked() ? "yes" : "no");
				}
				
				SocialPlugin.getSocialRequestHandler().execute(new UpdatePermissionsRequest(), "updatePermissions", rp);
				
				this_.dismiss();
			}
			
			class UpdatePermissionsRequest extends DataRequest {
				@Override
				protected void doInUiThread(String result) {
					this_.dismiss();
					parentActivity_.startActivity(new Intent(parentActivity_, SocialFriendsList.class));
					parentActivity_.finish();
				}
			}
		});
		
		
		//If only one user is selected, we try to get permissions that are already assigned to him
		if(selectedUsers_.size() == 1) {
			AuthToken token = AuthenticationPlugin.getAuthToken(context_);
			RequestParameters rp = new RequestParameters();
			rp.addParameter("username", token.getUsername());
			rp.addParameter("sessionId", token.getSessionId());
			rp.addParameter("granted_to", selectedUsers_.get(0).getIdFormat());
			
			SocialPlugin.getSocialRequestHandler().execute(new GetPermissionsRequest(), "getPermissions", rp);	
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
					if(grantedPermissions.contains(permissions_.get(i))) permissionBoxes_[i].setChecked(true);
					else permissionBoxes_[i].setChecked(false);
				} 
			}
		}
	}
	
	private String getTitle() {
		return context_.getString(R.string.social_permissions_title_a) + ((selectedUsers_.size() == 1) ? 
			selectedUsers_.get(0).getFirstName() + context_.getString(R.string.social_permissions_title_b) : 
			context_.getString(R.string.social_permissions_title_c));
	}
}
