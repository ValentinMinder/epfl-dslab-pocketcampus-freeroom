package org.pocketcampus.plugin.directory.android.ui;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.utils.LoaderImageView;
import org.pocketcampus.plugin.directory.android.DirectoryResultListView;
import org.pocketcampus.plugin.directory.shared.Person;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Customized dialog to show <code>Person</code>'s information
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class PersonDetailsDialog extends Dialog implements OnClickListener {

	Context ctx_;

	Person displayedPerson_;

	TextView title_;
	TextView fname_;
	TextView lname_;
	TextView mail_;
	TextView office_;
	TextView officePhone_;
	TextView phone_;
	TextView web_;
	TextView ou_;
	
	static int clickCount = 0;

	/**
	 * Constructor with basic need
	 * @param context Application context to use for this dialog
	 * @param person Person to display in this dialog
	 */
	public PersonDetailsDialog(Context context, Person person) {
		super(context);

		ctx_ = context;
		displayedPerson_ = person;
		

		build();
		setContent(person);
		setClickListener();

	}

	/**
	 * Called when the url of the picture is updated when the request is done.
	 * Loads the picture from the url in the dialog
	 */
	public void loadPicture() {
		LoaderImageView liv = (LoaderImageView) findViewById(R.id.directory_person_details_dialog_photo);
		if (liv == null) {
			Log.d("Directory", "loaderImageView in the layout is null");
			return;
		}

		if (displayedPerson_.pictureUrl != null) {
			liv.setImageDrawable(displayedPerson_.pictureUrl);

			liv.setVisibility(View.VISIBLE);
		} else {
			liv.setVisibility(View.GONE);
		}

	}

	/**
	 * Sets up the basic UI material of this dialog 
	 */
	private void build() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setTitle(ctx_.getResources().getString(R.string.directory_person_details_dialog_tile));
		setContentView(R.layout.directory_details_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}

	/**
	 * Sets the UI material relative to displayed person
	 * @param p The <code>Person</code> to display
	 */
	private void setContent(Person p) {
		title_ = (TextView) findViewById(R.id.directory_person_details_title_dialog);
		title_.setText(p.getFirstName() + " " + p.getLastName());

		// lname_ = (TextView)
		// findViewById(R.id.directory_person_details_dialog_lname);
		// lname_.setText(p.getLastName());
		//
		// fname_ = (TextView)
		// findViewById(R.id.directory_person_details_dialog_fname);
		// fname_.setText(p.getFirstName() + " ");

		mail_ = (TextView) findViewById(R.id.directory_person_details_dialog_mail);
		mail_.setVisibility(visibility(p.isSetEmail()));
		mail_.setText(p.email);

		office_ = (TextView) findViewById(R.id.directory_person_details_dialog_office);
		office_.setVisibility(visibility(p.isSetOffice()));
		office_.setText(p.office);

		phone_ = (TextView) findViewById(R.id.directory_person_details_dialog_phone_number);
		phone_.setVisibility(visibility(p.isSetPrivatePhoneNumber()));
		phone_.setText(p.privatePhoneNumber);

		officePhone_ = (TextView) findViewById(R.id.directory_person_details_dialog_office_phone_number);
		officePhone_.setVisibility(visibility(p.isSetOfficePhoneNumber()));
		officePhone_.setText(p.officePhoneNumber);

		// web_ = (TextView)
		// findViewById(R.id.directory_person_details_dialog_web);
		// web_.setVisibility(visibility(p.isSetWeb()));
		// web_.setText(p.web);

		ou_ = (TextView) findViewById(R.id.directory_person_details_dialog_ou);
		ou_.setVisibility(visibility(p.isSetOrganisationalUnit()));
		String multipleLinesOU = "";
		for (String s : p.OrganisationalUnit) {
			multipleLinesOU += (s + "\n");
			System.out.println(s);
		}
		multipleLinesOU.substring(0, multipleLinesOU.length() - 1);
		ou_.setText(multipleLinesOU);
	}

	/**
	 * Get the visibility constant from a boolean
	 * @param bool True for VISIBLE and False for GONE
	 * @return
	 */
	private int visibility(boolean bool) {
		return bool ? View.VISIBLE : View.GONE;
	}

	/**
	 * Set all the clicklistener for all the button
	 */
	private void setClickListener() {
		Button mailButton = (Button) findViewById(R.id.directory_imageButton_mail);
		Button phoneButton = (Button) findViewById(R.id.directory_imageButton_call);
//		Button mapButton = (Button)findViewById(R.id.directory_imageButton_map);
		Button webButton = (Button) findViewById(R.id.directory_imageButton_web);

		mailButton.setVisibility(visibility(displayedPerson_.isSetEmail()));
		phoneButton.setVisibility(visibility(displayedPerson_.isSetOfficePhoneNumber()));
//		System.out.println(mapButton);
//		mapButton.setVisibility(visibility(displayedPerson_.isSetOffice()));
		webButton.setVisibility(visibility(displayedPerson_.isSetWeb()));

		mailButton.setOnClickListener(this);
		phoneButton.setOnClickListener(this);
//		mapButton.setOnClickListener(this);
		webButton.setOnClickListener(this);
	}


	/**
	 * Inherited from the <code>OnClickListener</code> interface
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.directory_imageButton_mail) {
			performMail();
		} else if (v.getId() == R.id.directory_imageButton_call) {
			performDial();
		} else if (v.getId() == R.id.directory_imageButton_web) {
			performWeb();
		}
//		else if (v.getId() == R.id.directory_imageButton_map){
//			performMap();
//		}
	}

	/**
	 * Overriding the onKeyDown method of the super class to define what the call button does. (It dials the number of the person)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if(clickCount < 3){
				clickCount++;
				return false;
			}else{
				((DirectoryResultListView) ctx_).showPreviousPerson();
				return true;
			}

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if(clickCount < 3){
				clickCount++;
				return false;
			}else{
				((DirectoryResultListView) ctx_).showNextPerson();
				return true;
			}

		case KeyEvent.KEYCODE_CALL:
			if(displayedPerson_.isSetOfficePhoneNumber()){
			performDial();
			return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}

		default:
			return super.onKeyDown(keyCode, event);
		}

	}

	/**
	 * Dials the number of the person
	 */
	private void performDial() {
//		AlertDialog dialog = new AlertDialog.Builder(ctx_)
//				.setTitle(
//						getString(R.string.directory_call)
//								+ " " + displayedPerson_.getFirstName() + " "
//								+ displayedPerson_.getLastName() + "?")
//
//				.setPositiveButton(getString(R.string.directory_yes),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
								// Tracker
								Tracker.getInstance().trackPageView(
										"directory/call/"
												+ displayedPerson_.sciper);

								Intent dialIntent = new Intent(
										Intent.ACTION_DIAL,
										Uri.parse("tel:"
												+ displayedPerson_.officePhoneNumber));

								try {
									ctx_.startActivity(dialIntent);
								} catch (Exception e) {
									Toast.makeText(
											ctx_,
											getString(R.string.directory_couldnt_call),
											Toast.LENGTH_SHORT).show();
								}
//							}
//						})
//
//				.setNegativeButton(getString(R.string.directory_no), null)
//				.show();
//
//		dialog.setCanceledOnTouchOutside(true);
	}

	private void performMail() {
		Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"
				+ displayedPerson_.email));

		try {
			ctx_.startActivity(Intent.createChooser(emailIntent,
					"Send email..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(ctx_, getString(R.string.directory_couldnt_email),
					Toast.LENGTH_SHORT).show();
		}

	}
	/**
	 * Shows the office of the person on the map
	 */
//	private void performMap() {
//		// call another plugin
//	}

	/**
	 * Opens the web browser of the phone and shows the webpage of the person
	 */
	private void performWeb() {
		Intent WebIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(displayedPerson_.web));
		ctx_.startActivity(WebIntent);
	}

	/**
	 * Get a displayed text via his ressource id
	 * @param resId The id of the wanted ressource.
	 * @return A <code>CharSequence</code> of the specified ressource
	 */
	private CharSequence getString(int resId) {
		return ctx_.getString(resId);
	}
}
