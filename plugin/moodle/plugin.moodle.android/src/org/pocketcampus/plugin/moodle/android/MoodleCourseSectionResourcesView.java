package org.pocketcampus.plugin.moodle.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.moodle.android.MoodleMainView.CourseInfo;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;
import org.pocketcampus.plugin.moodle.shared.MoodleResource;
import org.pocketcampus.plugin.moodle.shared.MoodleSection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
 * MoodleCourseSectionResourcesView
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleCourseSectionResourcesView extends PluginView implements IMoodleView {

	private MoodleModel mModel;
	
	private StandardTitledLayout mLayout;
	
	private Integer sectionNbr;
	private String viewTitle;
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MoodleController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		//Tracker
		Tracker.getInstance().trackPageView("moodle");
		
		// Get and cast the model
		mModel = (MoodleModel) controller.getModel();

		// Setup the layout
		mLayout = new StandardTitledLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
		mLayout.hideTitle();
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
			if(aExtras != null && aExtras.containsKey("sectionNbr")) {
				sectionNbr = aExtras.getInt("sectionNbr");
				viewTitle = aExtras.getString("courseTitle");
				if(sectionNbr > 0)
					viewTitle += " - " + sectionNbr;
			}
		}
		updateDisplay();
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
		List<MoodleResource> lmr = lms.get(sectionNbr).getIResources();
		if(lmr == null)
			return;
		
		ArrayList<ResourceInfo> einfos = new ArrayList<ResourceInfo>();
		// add title
		einfos.add(new ResourceInfo(viewTitle, null, true));
		// add courses
		for(MoodleResource i : lmr) {
			String basename = i.getIUrl();
			basename = basename.substring(basename.lastIndexOf("/") + 1);
			einfos.add(new ResourceInfo(i.getIName(), basename, false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new ResourcesListAdapter(this, R.layout.moodle_course_resource_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	private void updateDisplay() {
		sectionsListUpdated();
	}
	
	@Override
	public void networkErrorHappened() {
	}
	
	@Override
	public void moodleServersDown() {
	}

	@Override
	public void notLoggedIn() {
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
	        ResourceInfo t = getItem(position);
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
		        tv = (TextView)v.findViewById(R.id.moodle_course_resource_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.moodle_course_resource_body);
		        if(t.value != null)
		        	tv.setText(t.value);
		        else
		        	tv.setVisibility(View.GONE);
	        }
	        return v;
		}
		
	}

}
