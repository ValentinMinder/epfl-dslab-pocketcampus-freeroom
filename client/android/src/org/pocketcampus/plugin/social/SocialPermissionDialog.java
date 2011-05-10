package org.pocketcampus.plugin.social;
//package org.pocketcampus.plugin.social;
//
//import org.pocketcampus.R;
//import org.pocketcampus.shared.plugin.authentication.Username;
//import org.pocketcampus.shared.plugin.social.PermissionScanner;
//import org.pocketcampus.shared.plugin.social.permissions.SocialPermission;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager.LayoutParams;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//public class PermissionDialog extends Dialog {
//	private final Username username_;
//	private final Activity menusActivity;
//	private RatingBar rateIt;
//	private TextView numbVotes;
//	// private final ConnexionHandler ch;
//	private ProgressDialog progressDialog_;
//	private Context context;
//	private boolean photoButtonsEnabled;
//	private final PermissionDialog thisDialog_ = this;
//
//	public PermissionDialog(final Context context, final Username username, final Activity menus, boolean photoButtonsEnabled) {
//		super(context);
//		this.username_ = username;
//		this.context = context;
//		menusActivity = menus;
//
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		this.photoButtonsEnabled = photoButtonsEnabled;
//
//		setContentView(R.layout.social_permission_dialog);
//
//		// Make the dialog box fit the width of the phone.
//		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//
//		// Dialog box is closed when we touch outside.
//		setCanceledOnTouchOutside(true);
//		setDialogContent();
//	}
//
//	private void setDialogContent() {
//		progressDialog_ = ProgressDialog.show(context, "Please Wait",
//				"Loading friends...", true, false);
//
//		// Set the title, description, rating and number of votes.
//		TextView title = (TextView) findViewById(R.id.social_friends_title);
//		
//		//		TextView rateItYourself = (TextView) findViewById(R.id.food_menudialog_rateityourself);
//		title.setText(username_.toString());
//		
//		//Add a checkbox for every permission
//		LinearLayout layout = (LinearLayout) findViewById(R.id.social_friends_permissions_holder);
//		for(SocialPermission permission : PermissionScanner.scanPermissions()) {
//			
//			
//			final CheckBox cb = new CheckBox(context);
//			cb.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
////					cb.toggle();
//				}
//			});
//			
//			TextView tv = new TextView(context);
//			tv.setText(permission.getName());
//			
//			LinearLayout newPermission = new LinearLayout(context);
//			newPermission.setOrientation(LinearLayout.VERTICAL);
//			newPermission.addView(cb);
//			newPermission.addView(tv);
//			layout.addView(newPermission);
//		}
//		
//		
//		
//		// BOUTON chat
//		Button chatButton = (Button) findViewById(R.id.social_friends_chat_button);
//		chatButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				
//			}
//		});
//		
//		//BOUTON positionning
//		
//		
//		Button okButton = (Button) findViewById(R.id.social_friends_ok);
//		okButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				thisDialog_.dismiss();
//			}
//		});
//		
////		//BOUTON
////		ImageButton takePic = (ImageButton) findViewById(R.id.food_menudialog_Pictures);
////		takePic.setOnClickListener(new View.OnClickListener() {
////			public void onClick(View v) {
////			}
////		});
//
//		progressDialog_.dismiss();
//	}
//}
