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
import org.pocketcampus.plugin.moodle.shared.MoodleSection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
public class MoodleCourseSectionsView extends PluginView implements IMoodleView {

	private MoodleController mController;
	private MoodleModel mModel;
	
	private StandardTitledLayout mLayout;
	
	private Integer courseId;
	private String courseTitle;
	
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
	 * We need to read the Extras to know what is the courseId
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			if(aExtras != null && aExtras.containsKey("courseId")) {
				courseId = aExtras.getInt("courseId");
				courseTitle = aExtras.getString("courseTitle");
			}
		}
		
		mController.refreshSectionsList(false, courseId);
		//updateDisplay(); // might contain data for a different course
	}

	@Override
	public void coursesListUpdated() {
	}

	@Override
	public void eventsListUpdated() {
	}

	@Override
	public void sectionsListUpdated() {
		List<MoodleSection> ltb = mModel.getSections();
		if(ltb == null)
			return;
		
		ArrayList<SectionInfo> einfos = new ArrayList<SectionInfo>();
		// add title
		einfos.add(new SectionInfo(courseTitle, null, true));
		// add courses
		int c = 0;
		for(MoodleSection i : ltb) {
			if(c == 0)
				einfos.add(new SectionInfo(null, i.getIText(), false));
			else
				einfos.add(new SectionInfo(c + "", i.getIText(), false));
			c++;
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new SectionsListAdapter(this, R.layout.moodle_course_section_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//SectionInfo sectionInfo = ((SectionInfo) arg0.getItemAtPosition(arg2));
				Intent i = new Intent(MoodleCourseSectionsView.this, MoodleCourseSectionResourcesView.class);
				i.putExtra("sectionNbr", arg2 - 1);
				i.putExtra("courseTitle", courseTitle);
				MoodleCourseSectionsView.this.startActivity(i);
				//MoodleCourseSectionResourcesDialog dialog = new MoodleCourseSectionResourcesDialog(MoodleCourseSectionsView.this, mModel.getSections(), arg2 - 1);
				//dialog.show();
			}
		});
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	@Override
	public void gotMoodleCookie() {
		mController.refreshCoursesList(true);
	}
	
	/*private void updateDisplay() {
		sectionsListUpdated();
	}*/
	
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
	
	public class SectionInfo {
		SectionInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class SectionsListAdapter extends ArrayAdapter<SectionInfo> {

		private LayoutInflater li;
		private int rid;
		
		public SectionsListAdapter(Context context, int textViewResourceId, List<SectionInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        SectionInfo t = getItem(position);
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
		        tv = (TextView)v.findViewById(R.id.moodle_course_section_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.moodle_course_section_body);
		        if(t.value != null)
		        	tv.setText(t.value);
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
			Tracker.getInstance().trackPageView("moodle/sections/refresh");
			mController.refreshSectionsList(true, courseId);
		}
	}

}
