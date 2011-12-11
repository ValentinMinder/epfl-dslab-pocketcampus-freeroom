package org.pocketcampus.plugin.isacademia.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.Course;
import org.pocketcampus.plugin.isacademia.shared.Exam;
import org.pocketcampus.plugin.isacademia.shared.Seance;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IsacademiaMainView extends PluginView implements IIsacademiaView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return IsacademiaController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		Log.v("DEBUG", "IsacademiaMainView::onDisplay");
		// Get and cast the controller and model
		mController = (IsacademiaController) controller;
		mModel = (IsacademiaModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		//mLayout = new StandardLayout(this);

		// The ActionBar is added automatically when you call setContentView
		//setContentView(mLayout);
		setContentView(R.layout.isacademia_main);

		//mLayout.setText("Loading");
		//refreshAll();
	}
	
	@Override
	protected void handleIntent(Intent aIntent) {
		Log.v("DEBUG", "IsacademiaMainView::handleIntent");
		// If we were pinged by auth plugin, then we must read the sessId
		if(aIntent != null && Intent.ACTION_VIEW.equals(aIntent.getAction())) {
			Uri aData = aIntent.getData();
			if(aData != null && "pocketcampus-authenticate".equals(aData.getScheme())) {
				String sessId = aData.getQueryParameter("sessid");
				mController.setIsacademiaCookie(sessId);
			}
		}
		
		// Normal start-up
		if(mModel.getIsacademiaCookie() == null) { // if we don't have cookie
			// get cookie (ping auth plugin)
			//Intent authIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=camipro"));
			//startActivity(authIntent);
			Intent authIntent = new Intent("org.pocketcampus.plugin.authentication.ACTION_AUTHENTICATE",
					Uri.parse("pocketcampus-authenticate://authentication.plugin.pocketcampus.org/do_auth?service=isacademia"));
			startService(authIntent);
		}
		if(mModel.getCourses() == null) { // if we don't have some data
			// fetch them
			refreshAll();
		}
		// update display
		updateDisplay();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.v("DEBUG", "IsacademiaMainView::onResume");
		if(mController != null && mController.getIsacademiaCookie() == null) {
			// Resumed and lot logged in? go back
			finish();
		}
	}

	private void refreshAll() {
		mController.refreshCourses();
		mController.refreshExams();
		mController.refreshSchedule();
	}


	@Override
	public void coursesUpdated() {
		List<Course> lc = mModel.getCourses();
		if(lc == null)
			return;
		for(Course e : lc) {
			Log.v("DEBUG", e.toString());
		}
		
		ListView lv = (ListView) findViewById(R.id.isacademia_courses_list);

		// Create an adapter for the data
		lv.setAdapter(new CourseAdapter(getApplicationContext(), R.layout.isacademia_course, lc));
	}
	
	@Override
	public void examsUpdated() {
		List<Exam> le = mModel.getExams();
		if(le == null)
			return;
		for(Exam e : le) {
			Log.v("DEBUG", e.toString());
		}
	}

	@Override
	public void scheduleUpdated() {
		List<Seance> ls = mModel.getSchedule();
		if(ls == null)
			return;
		for(Seance e : ls) {
			Log.v("DEBUG", e.toString());
		}
	}
	
	
	private void updateDisplay() {
		coursesUpdated();
		examsUpdated();
		scheduleUpdated();
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.isacademia_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		if(item.getItemId() == R.id.isacademia_refresh) {
			refreshAll();;
		}
		

		return true;
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT).show();
	}

	private IsacademiaController mController;
	private IsacademiaModel mModel;

	
	
	
	
	// TODO remove this class from here

	public class CourseAdapter extends ArrayAdapter<Course> {
		private LayoutInflater li_;

		public CourseAdapter(Context context, int textViewResourceId, List<Course> courses) {
			super(context, textViewResourceId, courses);
			li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				v = li_.inflate(R.layout.isacademia_course, null);
			}
			Course t = getItem(position);
			TextView tv;

			tv = (TextView) v.findViewById(R.id.isacademia_course_name);
			tv.setText(t.getName());
			
			return v;
		}
	}


















	
}
