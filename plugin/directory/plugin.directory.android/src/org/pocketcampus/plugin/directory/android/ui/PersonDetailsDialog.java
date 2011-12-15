package org.pocketcampus.plugin.directory.android.ui;


import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.utils.LoaderImageView;
import org.pocketcampus.plugin.directory.android.DirectoryResultListView;
import org.pocketcampus.plugin.directory.shared.Person;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
	TextView ou_;

	
	public PersonDetailsDialog(Context context, Person person) {
		super(context);
		
		ctx_ = context;
		displayedPerson_ = person;
		
		build();
		setContent(person);
		setClickListener();
		
	}
	
	public void loadPicture() {
		LoaderImageView liv = (LoaderImageView) findViewById(R.id.directory_person_details_dialog_photo);
		if(liv == null){
			Log.d("Directory", "loaderImageView in the layout is null");
			return;
		}
		
		if(displayedPerson_.pictureUrl != null){
			liv.setImageDrawable(displayedPerson_.pictureUrl);
		
			liv.setVisibility(View.VISIBLE);
		}else{
			liv.setVisibility(View.GONE);
		}

	}

	private void build(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(ctx_.getResources().getString(R.string.directory_person_details_dialog_tile));
		setContentView(R.layout.directory_person_details_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}
	
	private void setContent(Person p){
		lname_ = (TextView) findViewById(R.id.directory_person_details_dialog_lname);
		lname_.setText(p.getLastName());
		
		fname_ = (TextView) findViewById(R.id.directory_person_details_dialog_fname);
		fname_.setText(p.getFirstName() + " ");
		
		mail_ = (TextView) findViewById(R.id.directory_person_details_dialog_mail);
		mail_.setVisibility(visibility(p.isSetEmail()));
		mail_.setText(p.email);
		
		office_ = (TextView) findViewById(R.id.directory_person_details_dialog_office);
		office_.setVisibility(visibility(p.isSetOffice()));
		office_.setText(p.office);
		
		phone_ = (TextView) findViewById(R.id.directory_person_details_dialog_phone_number);
		phone_.setVisibility(visibility(p.isSetOfficePhoneNumber()));
		phone_.setText(p.officePhoneNumber);
		
//		web_ = (TextView) findViewById(R.id.directory_person_details_dialog_web);
//		web_.setVisibility(visibility(p.isSetWeb()));
//		web_.setText(p.web);
		
		ou_ = (TextView) findViewById(R.id.directory_person_details_dialog_ou);
		ou_.setVisibility(visibility(p.isSetOrganisationalUnit()));
		String multipleLinesOU = "";
		for(String s : p.OrganisationalUnit){
			multipleLinesOU += (s + "\n");
		}
		multipleLinesOU.substring(0, multipleLinesOU.length() -1);
		ou_.setText(multipleLinesOU);
	}
	
	private int visibility(boolean hasMail) {
		return hasMail?View.VISIBLE:View.GONE;
	}

	private void setClickListener(){
		ImageView mailButton = (ImageView) findViewById(R.id.directory_imageButton_mail);
		ImageView phoneButton = (ImageView) findViewById(R.id.directory_imageButton_phone);
		ImageView mapButton = (ImageView) findViewById(R.id.directory_imageButton_room);
		ImageView webButton = (ImageView) findViewById(R.id.directory_imageButton_web);
		
		mailButton.setVisibility(visibility(displayedPerson_.isSetEmail()));
		phoneButton.setVisibility(visibility(displayedPerson_.isSetOfficePhoneNumber()));
		//mapButton.setVisibility(visibility(displayedPerson_.hasOffice()));
		mapButton.setVisibility(visibility(false)); // TODO call map when ready
		webButton.setVisibility(visibility(displayedPerson_.isSetWeb()));
		
		mailButton.setOnClickListener(this);
		phoneButton.setOnClickListener(this);
		mapButton.setOnClickListener(this);
		webButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.directory_imageButton_mail) {
			performMail();
		} else if (v.getId() == R.id.directory_imageButton_phone) {
			performDial();
		} else if (v.getId() == R.id.directory_imageButton_room) {
			performPath();
		} else if (v.getId() == R.id.directory_imageButton_web) {
			performWeb();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_UP:
				((DirectoryResultListView)ctx_).showPreviousPerson();
				return true;
			
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				((DirectoryResultListView)ctx_).showNextPerson();
				return true;				
				
			case KeyEvent.KEYCODE_CALL:
				performDial();
				return true;
			
			default:
				return super.onKeyDown(keyCode, event);
		}
		
	}
	
	private void performDial() {
		AlertDialog dialog = new AlertDialog.Builder(ctx_)
		.setTitle(getString(R.string.directory_call) + displayedPerson_.getFirstName()+" "+displayedPerson_.getLastName()+"?")
		
		.setPositiveButton(getString(R.string.directory_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + displayedPerson_.officePhoneNumber));
				
				try{
					ctx_.startActivity(dialIntent);
				}catch (Exception e){
					Toast.makeText(ctx_,getString(R.string.directory_couldnt_call), Toast.LENGTH_SHORT).show();
				}
			}
		})
		
		.setNegativeButton(getString(R.string.directory_no), null)
		.show();
		
		dialog.setCanceledOnTouchOutside(true);
	}
	
	private void performMail(){
		Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + displayedPerson_.email));
		
		try {
		    ctx_.startActivity(Intent.createChooser(emailIntent, "Send email..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(ctx_, getString(R.string.directory_couldnt_email), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void performPath(){
		//TODO call another plugin
	}
	
	private void performWeb(){
		Intent WebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(displayedPerson_.web)); 
		ctx_.startActivity(WebIntent);
	}

	
	
	private CharSequence getString(int resId) {
		return ctx_.getString(resId);
	}
}
