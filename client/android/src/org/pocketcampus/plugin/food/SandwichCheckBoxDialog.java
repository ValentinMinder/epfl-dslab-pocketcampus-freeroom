/**
 * Sandwich CheckBox Dialog
 * 
 * @author Oriane
 * 
 */ 

package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.Sandwich;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class SandwichCheckBoxDialog{

	private Vector<Sandwich> sandwichList_;		/* sandwich's list of the restaurant */
	private Context WeeklyMenusContext_;		/* context (where to display) */
	private int sandwichListSize_;				/* sandwich list size */
//	private ServerAPI sApi;						/* to use the method setSandwicheAvailability */
	private ProgressDialog progressDialog_;		/* the progressDialog */
	private String currentSandwich_;

	/**Constructor with two arguments (SandwichStore and Context) */
	public SandwichCheckBoxDialog(Vector<Sandwich> store, Context WeeklyMenusContext) {
		this.WeeklyMenusContext_ = WeeklyMenusContext;
		this.sandwichList_ = store;
		this.sandwichListSize_ = sandwichList_.size();
//		sApi = new ServerAPI();
		valid();
	}
	
	private void valid(){
		if(sandwichList_ == null) throw new IllegalArgumentException("SandwichList cannot be null ");
		if(WeeklyMenusContext_ == null) throw new IllegalArgumentException("WeeklyMenusContext cannot be null ");
//		if(sApi == null) throw new IllegalArgumentException("ServerAPI cannot be null ");
	}
	
	/** show this dialog (checkbox's list) */
	public void show(){
		createAlertDialog(false);
	}

	/** create the dialog checkbox's list */
	private void createAlertDialog(final boolean modify) {
		
		/* Initialization tab for my dialog (name and availability) */
		final CharSequence[] nameSandwichList = new CharSequence[sandwichListSize_];
		final boolean[] sandwichListavailable = new boolean[sandwichListSize_];
		for(int i=0; i<sandwichListSize_; i++){
			nameSandwichList[i] = (CharSequence)sandwichList_.get(i).getName();
			sandwichListavailable[i] = sandwichList_.get(i).isAvailable();
		}

		/* Create the appearance of my Dialog Box  */
		AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyMenusContext_);
		builder.setTitle(R.string.food_sandwich_list);
		builder.setMultiChoiceItems(nameSandwichList, sandwichListavailable, new DialogInterface.OnMultiChoiceClickListener() {
				
			public void onClick(DialogInterface dialog, int item, boolean isChecked) {
				if(!modify){dialog.cancel(); createAlertDialog(false);}
				else confirmationChangeCheckBox(nameSandwichList[item].toString(), item, isChecked, dialog);
				
//			else ((AlertDialog) dialog).getListView().setItemChecked(0, true);
		}});
		
		/* button modify of my dialog box */
		if(!modify /* dialog box no modify */){
			builder.setNeutralButton(R.string.food_sandwich_button_modify, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					confirmationActivationModify();
				}
			});
		}

		/* Build my Dialog box */
		AlertDialog alert = builder.create();
		/* Dialog box is closed when we touch outside */
		alert.setCanceledOnTouchOutside(true);
		/* Show my Dialog box */
		alert.show();
	}
	
	/** confirmation change checkbox's checked */
	private void confirmationChangeCheckBox(final String nameSandwich, final int item, final boolean isChecked, final DialogInterface dialog2){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyMenusContext_);
		builder.setMessage(R.string.food_sandwich_question_modify);
		builder.setCancelable(false);
		
		builder.setPositiveButton(R.string.food_sandwich_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				progressDialog_ = ProgressDialog.show(WeeklyMenusContext_, WeeklyMenusContext_.getString(R.string.food_sandwich_please_wait),
						WeeklyMenusContext_.getString(R.string.food_sandwich_checking_connection), true, false);
				
				currentSandwich_ = sandwichList_.get(item).getName();
				
				new Thread() {
					public void run() {
						/*
						try {
							if(sApi.ping()){
								//envoie les information au serveur. 
								sApi.setSandwichAvailability(sandwichList_.get(item), isChecked);
								handler.sendEmptyMessage(1);
							}
							else {
								handler.sendEmptyMessage(0);
							}
						} catch (ServerException e) {
							handler.sendEmptyMessage(-4); 
							e.printStackTrace();
							dialog2.dismiss();
						}*/
					
					}
				}.start();
				
		    }
		});
		
		builder.setNegativeButton(R.string.food_sandwich_button_no, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
		    	dialog.dismiss();
		    	dialog2.dismiss();
//		    	createAlertDialog(true);
		    }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/* will stop the progressBar and other thing (to implement like display sth) */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(progressDialog_.isShowing()) progressDialog_.dismiss();
			
			/* message from the method startActionHomeScreenSwitcher() */
			if(msg.what == 0) {
				Toast.makeText(WeeklyMenusContext_, WeeklyMenusContext_.getString(R.string.food_sandwich_noConnection) , Toast.LENGTH_SHORT).show();
			}
			if(msg.what == 1){
				Toast.makeText(WeeklyMenusContext_, currentSandwich_ + WeeklyMenusContext_.getString(R.string.food_sandwich_isModify) , Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	/** confirmation of activation to modify the checkbox's  */
	private void confirmationActivationModify(){
		AlertDialog.Builder builder = new AlertDialog.Builder(WeeklyMenusContext_);
		builder.setMessage(R.string.food_sandwich_question_actiavtion_modify);
		builder.setCancelable(false);
		
		builder.setPositiveButton(R.string.food_sandwich_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				createAlertDialog(true);
		    }
		});
		
		builder.setNegativeButton(R.string.food_sandwich_button_no, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
		    	dialog.cancel();
		    }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}

}
