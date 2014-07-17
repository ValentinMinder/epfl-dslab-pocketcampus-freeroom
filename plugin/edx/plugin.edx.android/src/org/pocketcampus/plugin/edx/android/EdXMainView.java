package org.pocketcampus.plugin.edx.android;

import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.edx.R;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdxCourse;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * EdXMainView - Main view that shows EdX courses.
 * 
 * This is the main view in the EdX Plugin.
 * It checks if the user is logged in, if not it pings
 * the Authentication Plugin.
 * When it gets back a valid SessionId it fetches the
 * user's EdX data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXMainView extends PluginView implements IEdXView {

	private EdXController mController;
	private EdXModel mModel;
	
	public static final String MAP_KEY_COURSEID = "EDX_COURSE_ID";
	
	private boolean displayingList;
	
	List<EdxCourse> userCourses;
	
	ListView mList;
	ScrollStateSaver scrollState;
		
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return EdXController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (EdXController) controller;
		mModel = (EdXModel) controller.getModel();

		setContentView(R.layout.edx_main);
		mList = (ListView) findViewById(R.id.edx_main_list);
		displayingList = true;

	}


	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		

		if(mModel.getCredentials() == null) {
			mController.openLoginDialog();
		} else {
			mController.refreshUserCourses(this, false);
		}

		
		
		//Tracker
		//if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("edx");
		//else Tracker.getInstance().trackPageView("edx/" + eventPoolId + "/subevents");
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window,
	 * This Activity is resumed but we do not have the
	 * credentials. In this case we close the Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(mModel != null && mModel.getCredentials() == null) {
			// Resumed and lot logged in? go back
			finish();
		} else  if(displayingList && scrollState != null) {
			scrollState.restore(mList);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(displayingList)
			scrollState = new ScrollStateSaver(mList);
	}
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if(mModel.getCredentials() != null) {
			MenuItem pastMenu = menu.add("Log out");
			pastMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					mModel.setCredentials(null, null);
					mModel.setSession(null);
					finish();
					return true;
				}
			});
		}
		return true;
	}

	

	private void updateDisplay(boolean saveScroll) {

		if(saveScroll && displayingList)
			scrollState = new ScrollStateSaver(mList);
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.edx_list_header);
		
		Preparated<EdxCourse> p = new Preparated<EdxCourse>(userCourses, new Preparator<EdxCourse>() {
			public int[] resources() {
				return new int[] { R.id.edx_title, R.id.edx_speaker, R.id.edx_thumbnail, R.id.edx_time, R.id.edx_fav_star };
			}
			public Object content(int res, final EdxCourse e) {
				switch (res) {
				case R.id.edx_title:
					return e.getCourseTitle();
				case R.id.edx_speaker:
					return e.getCourseId();
				case R.id.edx_thumbnail:
					return null;
				case R.id.edx_time:
					return null;
				case R.id.edx_fav_star:
					return R.drawable.sdk_transparent;
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, EdxCourse item) {
				map.put(MAP_KEY_COURSEID, item.getCourseId());
			}
		});
		adapter.addSection("Courses", new LazyAdapter(this, p.getMap(), 
				R.layout.edx_list_row, p.getKeys(), p.getResources()));
		
		
		if(userCourses.size() == 0) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText("No courses");
			setContentView(sl);
		} else {
			if(!displayingList) {
				setContentView(R.layout.edx_main);
				mList = (ListView) findViewById(R.id.edx_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);
			//mList.setCacheColorHint(Color.TRANSPARENT);
			//mList.setFastScrollEnabled(true);
			//mList.setScrollingCacheEnabled(false);
			//mList.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
			//mList.setDivider(null);
			//mList.setDividerHeight(0);
			
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
						Intent i = new Intent(EdXMainView.this, EdXCourseView.class);
						i.putExtra(EdXCourseView.EXTRAS_KEY_COURSEID, ((Map<?, ?>) o).get(MAP_KEY_COURSEID).toString());
						EdXMainView.this.startActivity(i);
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			if(scrollState != null)
				scrollState.restore(mList);
			
		}
	}
	

	
	
	@Override
	public void userCoursesUpdated() {
		userCourses = mModel.getUserCourses();
		updateDisplay(true);
	}
	@Override
	public void courseSectionsUpdated() {
	}
	@Override
	public void moduleDetailsUpdated() {
	}
	@Override
	public void activeRoomsUpdated() {
	}

	
	
	

	
	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.refreshUserCourses(this, true);
	}
	@Override
	public void upstreamServerFailure() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
	}
	@Override
	public void serverFailure() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_server_failed), Toast.LENGTH_SHORT).show();
	}

	
	
	
	@Override
	public void userCredentialsUpdated() {
		mController.performLogin(this);
	}
	@Override
	public void loginSucceeded() {
		mController.refreshUserCourses(this, false);
	}
	@Override
	public void loginFailed() {
		mController.openLoginDialog();
	}
	@Override
	public void sessionTimedOut() {
		mController.performLogin(this);
	}

	@Override
	protected String screenName() {
		// TODO Auto-generated method stub
		return null;
	}

}
