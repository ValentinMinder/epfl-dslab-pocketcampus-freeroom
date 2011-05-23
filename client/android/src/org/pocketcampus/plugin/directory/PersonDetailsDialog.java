package org.pocketcampus.plugin.directory;

import org.pocketcampus.R;
import org.pocketcampus.plugin.social.SocialPlugin;
import org.pocketcampus.shared.plugin.directory.Person;
import org.pocketcampus.shared.plugin.social.User;

import android.app.Dialog;
import android.content.Context;
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

	Context ctx;
	
	Person displayedPerson;
	
	TextView fname;
	TextView lname;
	TextView mail;
	TextView office;
	TextView phone;
	TextView web;
	//...
	
//	public PersonDetailsDialog(Context context, User user) {
//		super(context);
//		ctx = context;
//		build();
//	}
	
	public PersonDetailsDialog(Context context, Person person) {
		super(context);
		ctx = context;
		displayedPerson = person;
		build();
		setContent(person);
		setClickListener();
	}
	
	private void build(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("Details");
		setContentView(R.layout.directory_person_details_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		
		
	}
	
	private void setContent(Person p){
		lname = (TextView) findViewById(R.id.directory_person_details_dialog_lname);
		lname.setText(p.last_name);
		
		fname = (TextView) findViewById(R.id.directory_person_details_dialog_fname);
		fname.setText(p.first_name + " ");
		
		mail = (TextView) findViewById(R.id.directory_person_details_dialog_mail);
		mail.setText(p.mail);
		
		office = (TextView) findViewById(R.id.directory_person_details_dialog_office);
		office.setText(p.room);
		
		phone = (TextView) findViewById(R.id.directory_person_details_dialog_phone_number);
		phone.setText(p.phone_number);
		
		web = (TextView) findViewById(R.id.directory_person_details_dialog_web);
		web.setText(p.web);
	}
	
	private void setClickListener(){
		ImageView a = (ImageView) findViewById(R.id.directory_imageButton_mail);
		ImageView b = (ImageView) findViewById(R.id.directory_imageButton_phone);
		ImageView c = (ImageView) findViewById(R.id.directory_imageButton_room);
		ImageView d = (ImageView) findViewById(R.id.directory_imageButton_web);
		ImageView e = (ImageView) findViewById(R.id.directory_imageButton_save);
		
		a.setEnabled(displayedPerson.hasMail());
		b.setEnabled(displayedPerson.hasPhone());
		c.setEnabled(displayedPerson.hasOffice());
		d.setEnabled(displayedPerson.hasWeb());
		e.setEnabled(true);
		
		a.setOnClickListener(this);
		b.setOnClickListener(this);
		c.setOnClickListener(this);
		d.setOnClickListener(this);
		e.setOnClickListener(this);
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
		Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
											+ displayedPerson.phone_number));
		try{
			ctx.startActivity(dialIntent);
		}catch (Exception e){
			Toast.makeText(ctx, "Something went terribly wrong somewhere", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void performMail(){
		Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
				+ displayedPerson.mail));
		try {
		    ctx.startActivity(Intent.createChooser(emailIntent, "Send email..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(ctx, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void performPath(){
		//TODO call another plugin
	}
	
	private void performWeb(){
		Intent WebIntent = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(displayedPerson.web)); 
		ctx.startActivity(WebIntent);
	}
	
	private void addToSocialAsFriend(){
		User u = new User(displayedPerson.first_name, displayedPerson.last_name, displayedPerson.uid);
		SocialPlugin.sendRequest(ctx, null, u);
		//TODO trouver un toStartNext mieux que null
		
	}


}
