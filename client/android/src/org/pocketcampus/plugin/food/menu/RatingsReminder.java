//package org.pocketcampus.plugin.food.menu;
//
//import java.util.Calendar;
//import java.util.Date;
//
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.preference.PreferenceManager;
//
//public class RatingsReminder {
//	private Context context;
//	public RatingsReminder(Context context){
//		this.context = context;
//	}
//	
//	public boolean hasAlreadyVotedToday(){
//		Date now = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(now);
//		SharedPreferences _sharedPreferences = PreferenceManager
//		.getDefaultSharedPreferences(context);
//		
//		//Get the current date (by year, month, week and day)
//		int currentYear = cal.get(Calendar.YEAR);
//		int currentMonth = cal.get(Calendar.MONTH);
//		int currentWeek = cal.get(Calendar.WEEK_OF_MONTH);
//		int currentDay = cal.get(Calendar.DAY_OF_WEEK);
//		
//		//Get the latest rated date (year, month, week and day)
//		int year = _sharedPreferences.getInt("year", -1);
//		int month = _sharedPreferences.getInt("month", -1);
//		int week =_sharedPreferences.getInt("week", -1);
//		int day =_sharedPreferences.getInt("day", -1);
//		
//		//Check if it is the same day or not
//		if((year == currentYear) && (month == currentMonth) &&
//				(week == currentWeek) && (day == currentDay)){
//			//means that we are the same day
//			if(_sharedPreferences.getBoolean("voted", false)){
//				//means that we already vote today
//				return true;
//			}
//		}
//		//Means that we still didn't vote today
//		return false;
//	}
//	
//	public void addVotedPreferences(){
//		Date now = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(now);
//		
//		SharedPreferences _sharedPreferences = PreferenceManager
//		.getDefaultSharedPreferences(context);
//		Editor edit = _sharedPreferences.edit();
//		edit.putBoolean("voted", true);
//		edit.putInt("year", cal.get(Calendar.YEAR));
//		edit.putInt("month", cal.get(Calendar.MONTH));
//		edit.putInt("week", cal.get(Calendar.WEEK_OF_MONTH));
//		edit.putInt("day", cal.get(Calendar.DAY_OF_WEEK));
//		edit.commit();
//	}
//	
//	public void printAlreadyVotedMessage() {
//		Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("Yo");
////		R.string.resto_rating_AlreadyVoted_BOX_title
//		builder.setMessage("Yeah");
//		builder.setPositiveButton("OK", null);
//		builder.show();
//	}
//	
//	
//}
