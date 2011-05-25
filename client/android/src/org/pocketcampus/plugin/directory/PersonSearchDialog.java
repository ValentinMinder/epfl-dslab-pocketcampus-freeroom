package org.pocketcampus.plugin.directory;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PersonSearchDialog extends Dialog {
	private boolean accurateSearch_ = true;
	private Button searchButton_;
	private EditText firstNameEditText_;
	private EditText lastNameEditText_;
	private EditText sciperEditText_;
	private LinearLayout firstNameLayout_;
	private LinearLayout lastNameLayout_;
	private LinearLayout sciperNameLayout_;
	
	public PersonSearchDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.directory_search);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		searchButton_ = (Button)findViewById(R.id.directory_search_button);
		firstNameEditText_ = (EditText)findViewById(R.id.directory_first_name_input);
		lastNameEditText_ = (EditText)findViewById(R.id.directory_last_name_input);
		sciperEditText_ = (EditText)findViewById(R.id.directory_sciper_input);
		firstNameLayout_ = (LinearLayout) findViewById(R.id.directory_ll1);
		lastNameLayout_ = (LinearLayout) findViewById(R.id.directory_ll2);
		sciperNameLayout_ = (LinearLayout) findViewById(R.id.directory_ll3);
		
		setCanceledOnTouchOutside(true);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, Menu.NONE, "Search by sciper").setIcon(android.R.drawable.ic_menu_search);
		menu.setGroupEnabled(0, true);
		
		menu.add(1, 2, Menu.NONE, "Approximative search").setIcon(android.R.drawable.ic_menu_zoom);
		menu.setGroupEnabled(1, true);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case 1: if(firstNameLayout_.getVisibility() == View.VISIBLE) {
				firstNameLayout_.setVisibility(View.GONE);
				lastNameLayout_.setVisibility(View.GONE);
				sciperNameLayout_.setVisibility(View.VISIBLE);
				item.setTitle("Search by name");
				clearEditTexts();
	
			} else {
				firstNameLayout_.setVisibility(View.VISIBLE);
				lastNameLayout_.setVisibility(View.VISIBLE);
				sciperNameLayout_.setVisibility(View.GONE);
				item.setTitle("Search by Sciper");
				clearEditTexts();
			}
			break;
	
			case 2: if(accurateSearch_){
				searchButton_.setText("~ Search");
			}else{
				searchButton_.setText("Search");
			}
			accurateSearch_ = !accurateSearch_;
			break;
		}
		return true;
	}
	
	private void clearEditTexts() {
		firstNameEditText_.setText("");
		lastNameEditText_.setText("");
		sciperEditText_.setText("");
	}

	public String getFirstName() {
		return firstNameEditText_.getText().toString();
	}
	
	public String getLastName() {
		return lastNameEditText_.getText().toString();
	}
	
	public String getSciper() {
		return sciperEditText_.getText().toString();
	}


	public boolean isSearchAccurate() {
		return accurateSearch_;
	}


	public void setOnClickListener(android.view.View.OnClickListener onClickListener) {
		searchButton_.setOnClickListener((android.view.View.OnClickListener) onClickListener);
	}
}



















