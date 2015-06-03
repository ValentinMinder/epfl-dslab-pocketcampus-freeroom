package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter2;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.moodle.R;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse2;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * MoodleMainView - Main view that shows Moodle courses.
 * 
 * This is the main view in the Moodle Plugin. It checks if the user is logged
 * in, if not it pings the Authentication Plugin. When it gets back a valid
 * SessionId it fetches the user's Moodle data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleMainView extends PluginView implements IMoodleView {

	private MoodleController mController;
	private MoodleModel mModel;

	public static final String MAP_KEY_MOODLECOURSEID = "MOODLE_COURSE_ID";
	public static final String MAP_KEY_MOODLECOURSETITLE = "MOODLE_COURSE_TITLE";

	private boolean displayingList;

	// private Map<String, String> feedsInRS = new HashMap<String, String>();
	// private Set<String> filteredFeeds = new HashSet<String>();

	StickyListHeadersListView mList;
	ScrollStateSaver scrollState;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MoodleController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (MoodleController) controller;
		mModel = (MoodleModel) controller.getModel();
		// setActionBarTitle(getString(R.string.moodle_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * If we were pinged by auth plugin, then we must read the sessId. Otherwise
	 * we do a normal startup, and if we do not have the moodleCookie we ping
	 * the Authentication Plugin.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {

		if (MoodleController.sessionExists(this)) // I think this is no longer
													// necessary, since the auth
													// plugin doesnt blindly
													// redo auth (well, this
													// saves the one call that
													// the auth plugin does to
													// check if the session is
													// valid)
			mController.refreshCourseList(this, false);
		else
			MoodleController.pingAuthPlugin(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null) {
			scrollState.restore(mList);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}

	@Override
	protected String screenName() {
		return "/moodle";
	}

	@Override
	public void coursesListUpdated() {
		setContentView(R.layout.moodle_main_container);
		mList = (StickyListHeadersListView) findViewById(R.id.moodle_main_list);
		displayingList = true;
		updateDisplay();
	}

	public void updateDisplay() {

		List<MoodleCourse2> courses = mModel.getCourses();

		if (displayingList) {
			if (scrollState == null) {
				scrollState = new ScrollStateSaver(mList);
			}
		}
		SeparatedListAdapter2 adapter = new SeparatedListAdapter2(this, R.layout.sdk_separated_list_header2);

		Collections.sort(courses, MoodleController.getMoodleCourseItemComp4sort());

		Preparated<MoodleCourse2> p = new Preparated<MoodleCourse2>(courses, new Preparator<MoodleCourse2>() {
			public int[] resources() {
				return new int[] { R.id.moodle_courselist_coursetitle };
			}

			public Object content(int res, final MoodleCourse2 e) {
				switch (res) {
				case R.id.moodle_courselist_coursetitle:
					return e.getName();
				default:
					return null;
				}
			}

			public void finalize(Map<String, Object> map, MoodleCourse2 item) {
				map.put(MAP_KEY_MOODLECOURSEID, item.getCourseId());
				map.put(MAP_KEY_MOODLECOURSETITLE, item.getName());
			}
		});
		adapter.addSection(getString(R.string.moodle_string_courses), new LazyAdapter(this, p.getMap(),
				R.layout.moodle_main_course_entry, p.getKeys(), p.getResources()));

		if (courses.size() == 0) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText(getString(R.string.moodle_string_no_courses));
			setContentView(sl);
		} else {

			if (!displayingList) {
				setContentView(R.layout.moodle_main_container);
				mList = (StickyListHeadersListView) findViewById(R.id.moodle_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);

			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if (o instanceof Map<?, ?>) {
						Integer eId = (Integer) ((Map<?, ?>) o).get(MAP_KEY_MOODLECOURSEID);
						String eTitle = (String) ((Map<?, ?>) o).get(MAP_KEY_MOODLECOURSETITLE);
						Intent i = new Intent(MoodleMainView.this, MoodleCourseView.class);
						i.putExtra(MoodleCourseView.EXTRAS_KEY_MOODLECOURSEID, (int) eId);
						i.putExtra(MoodleCourseView.EXTRAS_KEY_MOODLECOURSETITLE, eTitle);
						MoodleMainView.this.startActivity(i);
						trackEvent("ViewCourse", eId + "-" + eTitle);
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});

			if (scrollState != null)
				scrollState.restore(mList);

		}

	}

	@Override
	public void sectionsListUpdated() {
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.moodle_connection_error_happened),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_authentication_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}

	@Override
	public void moodleServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.moodle_error_moodle_down),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void downloadComplete(File localFile) {
		/*
		 * Toast.makeText(getApplicationContext(), getResources().getString(
		 * R.string.moodle_file_downloaded), Toast.LENGTH_SHORT).show();
		 */
	}
	
	@Override
	public synchronized void showLoading() {
	}

	@Override
	public synchronized void hideLoading() {
	}	

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_connection_no_cache_yes),
				Toast.LENGTH_SHORT).show();
		mController.refreshCourseList(this, true);

	}

	@Override
	public void notLoggedIn() {
		MoodleController.pingAuthPlugin(this);

	}

	@Override
	public void authenticationFinished() {
		mController.refreshCourseList(this, false);

	}

}
