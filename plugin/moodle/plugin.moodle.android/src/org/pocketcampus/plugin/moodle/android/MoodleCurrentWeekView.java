package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleResource;
import org.pocketcampus.plugin.moodle.shared.MoodleSection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.AdapterView.OnItemLongClickListener;

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
public class MoodleCurrentWeekView extends PluginView implements IMoodleView {

	private MoodleController mController;
	private MoodleModel mModel;
	
	private StandardTitledLayout mLayout;
	private ListView fillerView;
	
	private Integer courseId;
	private String courseTitle;

	private int current;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MoodleController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("moodle/course");
		
		// Get and cast the controller and model
		mController = (MoodleController) controller;
		mModel = (MoodleModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();

		current = -1;
		
		ActionBar a = getActionBar();
		if (a != null) {
			RefreshAction refresh = new RefreshAction();
			ToggleShowAllAction toggle = new ToggleShowAllAction();
			a.addAction(refresh, 0);
			a.addAction(toggle, 0);
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

	/**
	 * This is called when the Activity is resumed.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(fillerView != null) {
        	int j = fillerView.getHeaderViewsCount();
			for(int i = fillerView.getFirstVisiblePosition(); i <= fillerView.getLastVisiblePosition(); i++) {
				String file = ((ResourceInfo) fillerView.getItemAtPosition(i)).value;
		        if(file != null && new File(MoodleController.getLocalPath(file)).exists())
		        	((TextView) fillerView.getChildAt(j).findViewById(R.id.moodle_course_resource_state)).setText("Saved");
		        j++;
			}
		}
	}

	@Override
	public void coursesListUpdated() {
	}

	@Override
	public void eventsListUpdated() {
	}

	@Override
	public void sectionsListUpdated() {
		List<MoodleSection> lms = mModel.getSections();
		if(lms == null)
			return;
		
		if(current == -1) {
			current = 0;
			for(int i = 1; i < lms.size(); i++) {
				List<MoodleResource> lmr = lms.get(i).getIResources();
				if(lmr.size() != 0 && lms.get(i).iCurrent) {
					current = i;
					break;
				}
			}
		}
		
		ArrayList<ResourceInfo> einfos = new ArrayList<ResourceInfo>();
		for(int i = 1; i < lms.size(); i++) {
			List<MoodleResource> lmr = lms.get(i).getIResources();
			if(lmr == null)
				continue;
			if(current != 0 && current != i)
				continue;
			if(current == 0 && lmr.size() == 0)
				continue;
			// add section title
			einfos.add(new ResourceInfo((current == 0 ? "Week " + i + " - " + courseTitle : "Current Week - " + courseTitle), null, true));
			// add section contents
			boolean empty = true;
			for(MoodleResource r : lmr) {
				empty = false;
				String basename = r.getIUrl();
				//basename = basename.substring(basename.lastIndexOf("/") + 1);
				einfos.add(new ResourceInfo(r.getIName(), basename, false));
			}
			if(empty)
				einfos.add(new ResourceInfo("Empty", null, false));
		}
		fillerView = new ListView(this);
		fillerView.setAdapter(new ResourcesListAdapter(this, R.layout.moodle_course_resource_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		fillerView.setLayoutParams(p);
		
		fillerView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ResourceInfo resourceInfo = ((ResourceInfo) arg0.getItemAtPosition(arg2));
				if(resourceInfo.value == null)
					return;
				File resourceFile = new File(MoodleController.getLocalPath(resourceInfo.value));
				if(resourceFile.exists()) {
					openFile(MoodleCurrentWeekView.this, resourceFile);
				} else {
					/*Toast.makeText(getApplicationContext(), getResources().getString(
							R.string.moodle_file_downloading), Toast.LENGTH_SHORT).show();*/
					mController.fetchFileResource(resourceInfo.value);
				}
			}
		});
		fillerView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent i = new Intent(MoodleCurrentWeekView.this, MoodleCourseSectionsView.class);
				i.putExtra("courseId", courseId);
				i.putExtra("courseTitle", courseTitle);
				MoodleCurrentWeekView.this.startActivity(i);
				return true;
			}
		});
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(fillerView);
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
		openFile(this, localFile);
		/*Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.moodle_file_downloaded), Toast.LENGTH_SHORT).show();*/
	}

	public static void openFile(Context c, File file) {
		Uri uri = Uri.fromFile(file);
		Intent viewFileIntent = new Intent(Intent.ACTION_VIEW);
		String guessedContentType = URLConnection.guessContentTypeFromName(file.getName());
		if(guessedContentType == null) {
			Toast.makeText(c.getApplicationContext(), c.getResources().getString(
					R.string.moodle_no_app_to_handle_filetype), Toast.LENGTH_SHORT).show();
			return;
		}
		viewFileIntent.setDataAndType(uri, guessedContentType);
		viewFileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(viewFileIntent);
	}
	

	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public class ResourceInfo {
		ResourceInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class ResourcesListAdapter extends ArrayAdapter<ResourceInfo> {

		private LayoutInflater li;
		private int rid;
		
		public ResourcesListAdapter(Context context, int textViewResourceId, List<ResourceInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO
			// this is still not SUPER efficient
			// the efficient way to do it is here
			// http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
	        ResourceInfo t = getItem(position);
	        TextView tv;
	        if(t.isSeparator) {
	        	if(v == null || v.findViewById(R.id.PCSectioned_list_item_section_text) == null)
	        		v = li.inflate(R.layout.sdk_sectioned_list_item_section, null);
	        	
	        	tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_text);
	        	tv.setText(t.title == null ? "" : t.title);
	        	tv.setVisibility(t.title == null ? View.GONE : View.VISIBLE);
	        	
		        tv = (TextView)v.findViewById(R.id.PCSectioned_list_item_section_description);
	        	tv.setText(t.value == null ? "" : t.value);
	        	tv.setVisibility(t.value == null ? View.GONE : View.VISIBLE);
	        } else {
	        	if(v == null || v.findViewById(R.id.moodle_course_resource_title) == null)
	        		v = li.inflate(rid, null);
	        	
		        tv = (TextView)v.findViewById(R.id.moodle_course_resource_title);
	        	tv.setText(t.title == null ? "" : t.title);
	        	tv.setVisibility(t.title == null ? View.GONE : View.VISIBLE);
	        	
		        tv = (TextView)v.findViewById(R.id.moodle_course_resource_body);
	        	tv.setText(t.value == null ? "" : basename(t.value));
	        	tv.setVisibility(t.value == null ? View.GONE : View.VISIBLE);
	        	
		        tv = (TextView)v.findViewById(R.id.moodle_course_resource_state);
	        	tv.setText(t.value != null && new File(MoodleController.getLocalPath(t.value)).exists() ? "Saved" : "");
	        	tv.setVisibility(t.value == null ? View.GONE : View.VISIBLE);
	        }
	        return v;
		}
		
		private String basename(String s) {
			if(s.length() < 1)
				return s;
			return s.substring(s.lastIndexOf("/") + 1);
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
			mController.refreshSectionsList(true, courseId);
		}
	}

	/**
	 * ToggleShowAllAction
	 * 
	 * @author Amer <amer.chamseddine@epfl.ch>
	 * 
	 */
	private class ToggleShowAllAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		ToggleShowAllAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar
		 */
		@Override
		public int getDrawable() {
			return R.drawable.moodle_sections_showall;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
			if(current != 0) {
				current = 0;
				sectionsListUpdated();
				return;
			}
			List<MoodleSection> lms = mModel.getSections();
			if(lms == null)
				return;
			for(int i = 1; i < lms.size(); i++) {
				List<MoodleResource> lmr = lms.get(i).getIResources();
				if(lmr != null && lms.get(i).iCurrent) {
					current = i;
					break;
				}
			}
			sectionsListUpdated();
		}
	}

}
