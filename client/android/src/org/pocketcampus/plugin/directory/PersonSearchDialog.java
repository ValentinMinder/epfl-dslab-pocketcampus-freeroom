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

	Button search_button;
	EditText first_name;
	EditText last_name;
	EditText sciper;
	
	public PersonSearchDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.directory_search);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		search_button = (Button)findViewById(R.id.directory_search_button);
		first_name = (EditText)findViewById(R.id.directory_first_name_input);
		last_name = (EditText)findViewById(R.id.directory_last_name_input);
		sciper = (EditText)findViewById(R.id.directory_sciper_input);
	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, Menu.NONE, "Search via sciper").setIcon(R.drawable.directory_search);
		menu.setGroupEnabled(0, true);
		menu.add(1, 2, Menu.NONE, "approximative search");
		menu.setGroupEnabled(1, false);

		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case 1: if(first_name.isEnabled()) {
						((LinearLayout) findViewById(R.id.directory_ll3)).setVisibility(View.VISIBLE);
						first_name.setEnabled(false);
						last_name.setEnabled(false);
						item.setTitle("Search via name");
					}else{
						((LinearLayout) findViewById(R.id.directory_ll3)).setVisibility(View.INVISIBLE);
						first_name.setEnabled(true);
						last_name.setEnabled(true);
						item.setTitle("Search via sciper");
					}
					
					break;
				
			case 2:
					break;
		}
		return true;
	}

}
