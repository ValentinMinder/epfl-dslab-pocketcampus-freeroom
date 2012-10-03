package org.pocketcampus.plugin.isacademia.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.IsaSeance;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * IsacademiaScheduleView - View that shows ISA schedule.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsacademiaScheduleView extends PluginView implements IIsacademiaView {

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

	@Override
	protected void handleIntent(Intent aIntent) {
		mController.refreshSchedule();
		updateDisplay();
	}
	@Override
	public void coursesUpdated() {
	}
	
	@Override
	public void examsUpdated() {
	}

	@Override
	public void scheduleUpdated() {
		List<IsaSeance> ls = mModel.getSchedule();
		if(ls == null)
			return;
		
		ArrayList<SeanceInfo> einfos = new ArrayList<SeanceInfo>();
		// add title
		einfos.add(new SeanceInfo(getResources().getString(R.string.isacademia_schedule_view_title), null, true));
		// add courses
		Log.v("DEBUG", "=========== SCHEDULE ===========");
		for(IsaSeance i : ls) {
			Log.v("DEBUG", i.toString());
			String details = i.getSeanceDate() + " - " + i.getSeanceRoom() + " - " + i.getStartTime() + " - " + i.getEndTime();
			einfos.add(new SeanceInfo(i.getCourseName(), details, false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new SeancesListAdapter(this, R.layout.isa_seance_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}
	
	@Override
	public void gotIsaCookie() {
		// TODO check if activity is visible
		mController.refreshSchedule();
	}
	
	private void updateDisplay() {
		scheduleUpdated();
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.isacademia_connection_error_happened), Toast.LENGTH_SHORT).show();
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
	public void isaServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.isacademia_error_isa_down), Toast.LENGTH_SHORT).show();
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public class SeanceInfo {
		SeanceInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class SeancesListAdapter extends ArrayAdapter<SeanceInfo> {

		private LayoutInflater li;
		private int rid;
		
		public SeancesListAdapter(Context context, int textViewResourceId, List<SeanceInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        SeanceInfo t = getItem(position);
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
		        tv = (TextView)v.findViewById(R.id.isa_seance_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.isa_seance_details);
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
			Tracker.getInstance().trackPageView("isa/schedule/refresh");
			mController.refreshSchedule();
		}
	}

}
