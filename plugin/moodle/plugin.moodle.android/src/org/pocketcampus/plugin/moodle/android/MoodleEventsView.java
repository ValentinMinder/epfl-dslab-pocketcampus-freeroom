package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleAssignment;
import org.pocketcampus.plugin.moodle.shared.MoodleEvent;
import org.pocketcampus.plugin.moodle.shared.MoodleUserEvent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * MoodleEventsView
 * 
 * This is the events view in the Moodle Plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleEventsView extends PluginView implements IMoodleView {

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
		Tracker.getInstance().trackPageView("moodle/events");
		
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
		
		mController.refreshEventsList(false);
		updateDisplay();
	}

	@Override
	public void coursesListUpdated() {
	}

	@Override
	public void eventsListUpdated() {
		List<MoodleEvent> ltb = mModel.getEvents();
		if(ltb == null)
			return;
		
		ArrayList<EventInfo> einfos = new ArrayList<EventInfo>();
		// add title
		einfos.add(new EventInfo(getResources().getString(R.string.moodle_events_view_title), null, true));
		// add courses
		for(MoodleEvent i : ltb) {
			String details = "";
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH'h'mm");
			switch (i.getIType()) {
			case MOODLE_EVENT_ASSIGNMENT:
				MoodleAssignment ass = i.getIAssignment();
				details += ass.getICourse().getITitle() + "\n";
				details += sdf.format(new Date(ass.getIDueDate())) + "\n";
				break;
			case MOODLE_EVENT_USEREVENT:
				MoodleUserEvent ue = i.getIUserEvent();
				details += ue.getIDesc() + "\n";
				details += sdf.format(new Date(ue.getIStartDate()));
				if(ue.isSetIEndDate())
					details += " - " + sdf.format(new Date(ue.getIEndDate()));
				break;
			default:
				break;
			}
			einfos.add(new EventInfo(i.getITitle(), details, false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new EventsListAdapter(this, R.layout.moodle_event_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	@Override
	public void sectionsListUpdated() {
	}

	@Override
	public void gotMoodleCookie() {
		mController.refreshCoursesList(true);
	}
	
	private void updateDisplay() {
		eventsListUpdated();
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
	
	public class EventInfo {
		EventInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class EventsListAdapter extends ArrayAdapter<EventInfo> {

		private LayoutInflater li;
		private int rid;
		
		public EventsListAdapter(Context context, int textViewResourceId, List<EventInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        EventInfo t = getItem(position);
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
		        tv = (TextView)v.findViewById(R.id.moodle_event_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.moodle_event_details);
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
			Tracker.getInstance().trackPageView("moodle/events/refresh");
			mController.refreshEventsList(true);
		}
	}

}
