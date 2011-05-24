package org.pocketcampus.plugin.directory;

import org.pocketcampus.R;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.social.SocialPlugin;
import org.pocketcampus.shared.plugin.directory.Person;
import org.pocketcampus.shared.plugin.social.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonDetailsDialog extends Dialog implements OnClickListener {

	Context ctx_;
	
	Person displayedPerson_;
	
	TextView fname_;
	TextView lname_;
	TextView mail_;
	TextView office_;
	TextView phone_;
	TextView web_;
	
	public PersonDetailsDialog(Context context, Person person) {
		super(context);
		ctx_ = context;
		displayedPerson_ = person;
		build();
		setContent(person);
		setClickListener();
	}
	
	private void build(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("Details");
		setContentView(R.layout.directory_person_details_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}
	
	private void setContent(Person p){
		lname_ = (TextView) findViewById(R.id.directory_person_details_dialog_lname);
		lname_.setText(p.last_name);
		
		fname_ = (TextView) findViewById(R.id.directory_person_details_dialog_fname);
		fname_.setText(p.first_name + " ");
		
		mail_ = (TextView) findViewById(R.id.directory_person_details_dialog_mail);
		//mail_.setVisibility(visibility(p.hasMail()));
		mail_.setText(p.mail);
		
		office_ = (TextView) findViewById(R.id.directory_person_details_dialog_office);
		//office_.setVisibility(visibility(p.hasOffice()));
		office_.setText(p.room);
		
		phone_ = (TextView) findViewById(R.id.directory_person_details_dialog_phone_number);
		//phone_.setVisibility(visibility(p.hasPhone()));
		phone_.setText(p.phone_number);
		
		web_ = (TextView) findViewById(R.id.directory_person_details_dialog_web);
		//web_.setVisibility(visibility(p.hasWeb()));
		web_.setText(p.web);
	}
	
	private int visibility(boolean hasMail) {
		return hasMail?View.VISIBLE:View.GONE;
	}

	private void setClickListener(){
		ImageView mailButton = (ImageView) findViewById(R.id.directory_imageButton_mail);
		ImageView phoneButton = (ImageView) findViewById(R.id.directory_imageButton_phone);
		ImageView mapButton = (ImageView) findViewById(R.id.directory_imageButton_room);
		ImageView webButton = (ImageView) findViewById(R.id.directory_imageButton_web);
		ImageView addFriendButton = (ImageView) findViewById(R.id.directory_imageButton_save);
		
//		a.setEnabled(displayedPerson_.hasMail());
//		b.setEnabled(displayedPerson_.hasPhone());
//		c.setEnabled(displayedPerson_.hasOffice());
//		d.setEnabled(displayedPerson_.hasWeb());
//		e.setEnabled(true);
		
		mailButton.setVisibility(visibility(displayedPerson_.hasMail()));
		phoneButton.setVisibility(visibility(displayedPerson_.hasPhone()));
		//mapButton.setVisibility(visibility(displayedPerson_.hasOffice()));
		mapButton.setVisibility(visibility(false)); // TODO call map Intent when ready
		webButton.setVisibility(visibility(displayedPerson_.hasWeb()));
		addFriendButton.setVisibility(visibility(AuthenticationPlugin.getUser(ctx_)!=null));
		
		mailButton.setOnClickListener(this);
		phoneButton.setOnClickListener(this);
		mapButton.setOnClickListener(this);
		webButton.setOnClickListener(this);
		addFriendButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.directory_imageButton_mail:
				performMail();
				break;
			case R.id.directory_imageButton_phone:
				performDial();
				break;
			
			case R.id.directory_imageButton_room:
				performPath();
				break;
		
			case R.id.directory_imageButton_web:
				performWeb();
				break;
			case R.id.directory_imageButton_save:
				addToSocialAsFriend();
				break;			
		}
		//Toast.makeText(ctx, "image click", Toast.LENGTH_SHORT).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			performDial();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private void performDial() {
		AlertDialog dialog = new AlertDialog.Builder(ctx_)
		.setTitle("Call "+displayedPerson_.first_name+" "+displayedPerson_.last_name+"?")
		
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + displayedPerson_.phone_number));
				try{
					ctx_.startActivity(dialIntent);
				}catch (Exception e){
					Toast.makeText(ctx_, "Couldn't make the call.", Toast.LENGTH_SHORT).show();
				}
			}
		})
		
		.setNegativeButton(R.string.no, null)
		.show();
		
		dialog.setCanceledOnTouchOutside(true);
	}
	
	private void performMail(){
		Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
				+ displayedPerson_.mail));
		try {
		    ctx_.startActivity(Intent.createChooser(emailIntent, "Send email..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(ctx_, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void performPath(){
		//TODO call another plugin
	}
	
	private void performWeb(){
		Intent WebIntent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(displayedPerson_.web)); 
		ctx_.startActivity(WebIntent);
	}
	
	private void addToSocialAsFriend(){
		AlertDialog dialog = new AlertDialog.Builder(ctx_)
		.setTitle("Add "+displayedPerson_.first_name+" as a friend?")
		
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				User u = new User(displayedPerson_.first_name, displayedPerson_.last_name, displayedPerson_.uid);
				SocialPlugin.sendRequest(ctx_, null, u);
			}
		})
		
		.setNegativeButton(R.string.no, null)
		.show();
		
		dialog.setCanceledOnTouchOutside(true);
	}


}
