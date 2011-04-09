package org.pocketcampus.plugin.transport;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DayPickerButton extends Button implements OnClickListener {
	private static final int DAY_PICKER = 0;
	private Calendar date_;
	private Context ctx_;
	private Activity parentActivity_;
	
	public DayPickerButton(Activity activity, Context context) {
		super(context);
		parentActivity_ = activity;
		date_ = Calendar.getInstance();
	}

	@Override
	public void onClick(View v) {
		parentActivity_.showDialog(DAY_PICKER);
	}
	
	/*public DayPickerButton(Button button) {
		button_ = button;
		date_ = Calendar.getInstance();
		
		setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}*/
	
}
