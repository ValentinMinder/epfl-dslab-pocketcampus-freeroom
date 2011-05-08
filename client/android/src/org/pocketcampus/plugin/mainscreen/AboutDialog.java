package org.pocketcampus.plugin.mainscreen;

import org.pocketcampus.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class AboutDialog extends Dialog {

	public AboutDialog(Context context) {
		super(context);
		
		// Setups dialog.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainscreen_about_dialog);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}
}
