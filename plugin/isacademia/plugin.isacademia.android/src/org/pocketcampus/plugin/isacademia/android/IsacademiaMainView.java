package org.pocketcampus.plugin.isacademia.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.IsaCourse;
import org.pocketcampus.plugin.isacademia.shared.IsaExam;
import org.pocketcampus.plugin.isacademia.shared.IsaSeance;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * IsacademiaMainView - Main view that shows ISA courses.
 * 
 * This is the main view in the ISA Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's ISA data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsacademiaMainView extends PluginView implements IIsacademiaView {

	private IsacademiaController mController;
	private IsacademiaModel mModel;

	private StandardTitledLayout mLayout;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return IsacademiaController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("isacademia");
		
		// Get and cast the controller and model
		mController = (IsacademiaController) controller;
		mModel = (IsacademiaModel) controller.getModel();

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
	 * isacademiaCookie we ping the Authentication Plugin.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		// If we were pinged by auth plugin, then we must read the sessId
		if(aIntent != null && Intent.ACTION_VIEW.equals(aIntent.getAction())) {
			Uri aData = aIntent.getData();
			if(aData != null && "pocketcampus-authenticate".equals(aData.getScheme())) {
				String sessId = aData.getQueryParameter("sessid");
				mModel.setIsacademiaCookie(sessId);
			}
		}
		
		// Normal start-up
		if(mModel.getIsacademiaCookie() == null) { // if we don't have cookie
			// get cookie (ping auth plugin)
			pingAuthPlugin(this);
		}
		
		mController.refreshCourses();
		mController.refreshExams();
		mController.refreshSchedule();
		updateDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mModel != null && mModel.getIsacademiaCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}
	}

	@Override
	public void coursesUpdated() {
		List<IsaCourse> lc = mModel.getCourses();
		if(lc == null)
			return;
		
		ArrayList<CourseInfo> einfos = new ArrayList<CourseInfo>();
		// add title
		einfos.add(new CourseInfo(getResources().getString(R.string.isacademia_courses_section_title), null, true));
		// add courses
		Log.v("DEBUG", "=========== COURSES ===========");
		for(IsaCourse i : lc) {
			Log.v("DEBUG", i.toString());
			einfos.add(new CourseInfo(i.getName(), i.getInstructor(), false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new CoursesListAdapter(this, R.layout.isa_course_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}
	
	@Override
	public void examsUpdated() {
		List<IsaExam> le = mModel.getExams();
		if(le == null)
			return;
		Log.v("DEBUG", "=========== EXAMS ===========");
		for(IsaExam e : le) {
			Log.v("DEBUG", e.toString());
		}
	}

	@Override
	public void scheduleUpdated() {
		List<IsaSeance> ls = mModel.getSchedule();
		if(ls == null)
			return;
		Log.v("DEBUG", "=========== SCHEDULE ===========");
		for(IsaSeance e : ls) {
			Log.v("DEBUG", e.toString());
		}
	}
	
	private void updateDisplay() {
		coursesUpdated();
	}
	
	public static void pingAuthPlugin(Context context) {
		Intent authIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=isacademia"));
		context.startActivity(authIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.isacademia_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if(item.getItemId() == R.id.isacademia_logout) {
			//Tracker
			Tracker.getInstance().trackPageView("isa/menu/logout");
			mModel.setIsacademiaCookie(null);
			// Should not logout from Tequila because ISA is not Tequila based anyway
			/*Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
					Uri.parse("pocketcampus-logout://authentication.plugin.pocketcampus.org/tequila_logout"));
			startService(authIntent);*/
			finish();
		}
		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.isacademia_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void isaServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.isacademia_error_isa_down), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void notLoggedIn() {
		mModel.setIsacademiaCookie(null);
		pingAuthPlugin(this);
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
		        tv = (TextView)v.findViewById(R.id.isa_course_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.isa_course_instructor);
		        if(t.value != null)
		        	tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        }
	        return v;
		}
		
	}

	/**
	 * Refreshes ISA
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
			Tracker.getInstance().trackPageView("isa/refresh");
			mController.refreshCourses();
		}
	}

}
