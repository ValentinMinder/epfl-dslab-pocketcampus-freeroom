package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * MoodleMainView - Main view that shows Moodle courses.
 * 
 * This is the main view in the Moodle Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's Moodle data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleMainView extends PluginView implements IMoodleView {

	private MoodleController mController;
	private MoodleModel mModel;
	
	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MoodleController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("moodle");
		
		// Get and cast the controller and model
		mController = (MoodleController) controller;
		mModel = (MoodleModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();

		ActionBar a = getActionBar();
		if (a != null) {
			RefreshAction refresh = new RefreshAction();
			a.addAction(refresh, 0);
		}
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * If we were pinged by auth plugin, then we must read the sessId.
	 * Otherwise we do a normal startup, and if we do not have the
	 * moodleCookie we ping the Authentication Plugin.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		
		
		mController.refreshCoursesList(false);
		updateDisplay();
		
		
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window,
	 * This Activity is resumed but we do not have the
	 * moodleCookie. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		/*if(mModel != null && mModel.getMoodleCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}*/
	}

	@Override
	public void coursesListUpdated() {
		List<MoodleCourse> ltb = mModel.getCourses();
		if(ltb == null)
			return;
		
		ArrayList<CourseInfo> einfos = new ArrayList<CourseInfo>();
		// add title
		einfos.add(new CourseInfo(getResources().getString(R.string.moodle_courses_view_title), null, true));
		// add courses
		for(MoodleCourse i : ltb) {
			einfos.add(new CourseInfo(i.getITitle(), i.getIId() + "", false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new CoursesListAdapter(this, R.layout.moodle_course_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CourseInfo courseInfo = ((CourseInfo) arg0.getItemAtPosition(arg2));
				Intent i = new Intent(MoodleMainView.this, MoodleCurrentWeekView.class);
				i.putExtra("courseId", Integer.parseInt(courseInfo.value));
				i.putExtra("courseTitle", courseInfo.title);
				MoodleMainView.this.startActivity(i);
			}
		});
		//lv.setItemsCanFocus(true);
		//lv.setClickable(true);
		//lv.setFocusableInTouchMode(true);
		//lv.setDrawSelectorOnTop(true);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	@Override
	public void eventsListUpdated() {
	}

	@Override
	public void sectionsListUpdated() {
	}

	@Override
	public void gotMoodleCookie() {
		// TODO check if activity is visible
		mController.refreshCoursesList(true);
	}
	
	private void updateDisplay() {
		coursesListUpdated();
	}
	
	/*public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=moodle"));
		context.startActivity(authIntent);
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.moodle_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		/*if(item.getItemId() == R.id.moodle_menu_events) {
			Intent i = new Intent(this, MoodleEventsView.class);
			startActivity(i);
		}*/
		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.moodle_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_authentication_failed), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void userCancelledAuthentication() {
		finish();
	}
	
	@Override
	public void moodleServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.moodle_error_moodle_down), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void downloadComplete(File localFile) {
		/*Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.moodle_file_downloaded), Toast.LENGTH_SHORT).show();*/
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public class CourseInfo {
		CourseInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class CoursesListAdapter extends ArrayAdapter<CourseInfo> {

		private LayoutInflater li;
		private int rid;
		
		public CoursesListAdapter(Context context, int textViewResourceId, List<CourseInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        CourseInfo t = getItem(position);
	        if(t.isSeparator) {
				v = li.inflate(R.layout.sdk_sectioned_list_item_section, null);
		        TextView tv;
		        tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_text);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_description);
		        if(t.value != null)
		        	tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        } else {
	            v = li.inflate(rid, null);
		        TextView tv;
		        tv = (TextView)v.findViewById(R.id.moodle_course_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.moodle_course_instructor);
		        if(t.value != null)
		        	tv.setVisibility(View.GONE);//tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        }
	        return v;
		}
		
	}

	/**
	 * Refreshes moodle
	 * 
	 * @author Amer <amer.chamseddine@epfl.ch>
	 * 
	 */
	private class RefreshAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		RefreshAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar
		 */
		@Override
		public int getDrawable() {
			return R.drawable.sdk_action_bar_refresh;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
			//Tracker
			Tracker.getInstance().trackPageView("moodle/refresh");
			mController.refreshCoursesList(true);
		}
	}

}
