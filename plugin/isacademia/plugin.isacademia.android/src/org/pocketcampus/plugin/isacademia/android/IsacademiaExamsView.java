package org.pocketcampus.plugin.isacademia.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.IsaExam;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.content.Context;
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
 * IsacademiaExamsView - View that shows ISA exams.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsacademiaExamsView extends PluginView implements IIsacademiaView {

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
		
		if(mModel.getIsacademiaCookie() == null) { // if we don't have cookie
			// get cookie (ping auth plugin)
			IsacademiaMainView.pingAuthPlugin(this);
		}
		
		mController.refreshExams();
		updateDisplay();
	}

	@Override
	public void coursesUpdated() {
	}
	
	@Override
	public void examsUpdated() {
		List<IsaExam> le = mModel.getExams();
		if(le == null)
			return;
		
		ArrayList<ExamInfo> einfos = new ArrayList<ExamInfo>();
		// add title
		einfos.add(new ExamInfo(getResources().getString(R.string.isacademia_exams_view_title), null, true));
		// add courses
		Log.v("DEBUG", "=========== EXAMS ===========");
		for(IsaExam i : le) {
			Log.v("DEBUG", i.toString());
			String details = i.getRooms() + " - " + i.getDateTime();
			einfos.add(new ExamInfo(i.getCourse(), details, false));
		}
		ListView lv = new ListView(this);
		lv.setAdapter(new ExamsListAdapter(this, R.layout.isa_exam_record, einfos));
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lv.setLayoutParams(p);
		
		mLayout.hideTitle();
		mLayout.removeFillerView();
		mLayout.addFillerView(lv);
	}

	@Override
	public void scheduleUpdated() {
	}
	
	private void updateDisplay() {
		examsUpdated();
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
		IsacademiaMainView.pingAuthPlugin(this);
	}
	

	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public class ExamInfo {
		ExamInfo(String t, String v, boolean s) {
			title = t;
			value = v;
			isSeparator = s;
		}
		public String title;
		public String value;
		public boolean isSeparator;
	}
	
	public class ExamsListAdapter extends ArrayAdapter<ExamInfo> {

		private LayoutInflater li;
		private int rid;
		
		public ExamsListAdapter(Context context, int textViewResourceId, List<ExamInfo> list) {
			super(context, textViewResourceId, list);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rid = textViewResourceId;
		}
	
		@Override
		public View getView(int position, View v, ViewGroup parent) {
	        ExamInfo t = getItem(position);
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
		        tv = (TextView)v.findViewById(R.id.isa_exam_title);
		        if(t.title != null)
		        	tv.setText(t.title);
		        else
		        	tv.setVisibility(View.GONE);
		        tv = (TextView)v.findViewById(R.id.isa_exam_details);
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
			Tracker.getInstance().trackPageView("isa/exams/refresh");
			mController.refreshExams();
		}
	}

}
