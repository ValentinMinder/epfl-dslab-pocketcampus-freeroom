/**
 * Sandwich CheckBox Dialog
 * 
 * @author Oriane
 * 
 */

package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.Sandwich;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;

public class SandwichCheckBoxDialog {

	private Activity activity_;
	private Sandwich currentSandwich_;
	private CheckBox box_;

	/** Constructor with two arguments (SandwichStore and Context) */
	public SandwichCheckBoxDialog(Activity activity, Context context, Sandwich sandwich, CheckBox box) {
		this.activity_ = activity;
		this.box_ = box;
		this.currentSandwich_ = sandwich;
		valid();
	}

	private void valid() {
		if (activity_ == null) {
			throw new IllegalArgumentException("Activity cannot be null ");
		}
		if(box_ == null){
			throw new IllegalArgumentException("CheckBox cannot be null ");
		}
		if(currentSandwich_ == null){
			throw new IllegalArgumentException("Sandwich cannot be null ");
		}
	}

	/** show this dialog (checkbox's list) */
	public void show() {
		confirmationModifyAvailability();
	}

	/** confirmation of activation to modify the check box */
	private void confirmationModifyAvailability() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				activity_);
		builder.setMessage(R.string.food_sandwich_question_modify);
		builder.setCancelable(false);
		builder.setTitle(R.string.food_sandwich_confirmation);
		
		builder.setPositiveButton(R.string.food_sandwich_button_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						/*Here we'll send the info to the sever*/
						box_.setChecked(false);
					}
				});

		builder.setNegativeButton(R.string.food_sandwich_button_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						box_.setChecked(true);
						dialog.dismiss();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}
}
