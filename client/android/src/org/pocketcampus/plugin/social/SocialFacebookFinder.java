package org.pocketcampus.plugin.social;

import java.util.Collection;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.social.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SocialFacebookFinder extends Activity { 
	/** Called when the activity is first created. */
    private static ProgressDialog progressDialog;
    private static Activity this_;
    private String fbUsername_;
    private String fbPassword_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.social_login);
		
		this_ = this;
		
		Button button = (Button)findViewById(R.id.socialLoginButton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fbUsername_ = ((EditText) findViewById(R.id.socialLoginUsernameField)).getText().toString();
				fbPassword_ = ((EditText) findViewById(R.id.socialLoginPasswordField)).getText().toString();
				
				send(fbUsername_, fbPassword_);
			}
		});
    }
    
    private void send(String fbUsername, String fbPassword) {
    	progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("[HC] Retrieving friends from Facebook. Please wait");
        progressDialog.show();
        
        AuthToken token = AuthenticationPlugin.getAuthToken(this_);
        if(token != null) {
        	RequestParameters rp = new RequestParameters();
        	rp.addParameter("username", token.getUsername());
        	rp.addParameter("sessionId", token.getSessionId());
        	rp.addParameter("fbUsername", fbUsername);
        	rp.addParameter("fbPassword", fbPassword);
        	SocialPlugin.getSocialRequestHandler().execute(new FacebookRequest(), "facebook", rp);
        }
    }
    
    private static class FacebookRequest extends DataRequest {
    	@Override
    	protected void doInUiThread(String result) {
    		Collection<User> users = null;
    		if(result != null) {
				Gson gson = new Gson();
				try{
					users = gson.fromJson(result, new TypeToken<Collection<User>>(){}.getType());
				} catch (JsonSyntaxException e) {
					users = null;
					e.printStackTrace();
				}
			}
    		
    		if(users != null) {
    			if(!users.isEmpty()) {
//    				SocialListSeparator listSeparator = new SocialListSeparator(this_);
//        			SocialImportListAdapter socialImportListAdapter_ = new SocialImportListAdapter(this_, friendsLists.getRequesting(), this_);
//    				listSeparator.addSection("Invite your facebook friends !"), socialImportListAdapter_);
    			} else {
    				//no matches
    			}
    		} else {
    			//logout
    		}
    		
    		progressDialog.dismiss();
    	}
    }
}
