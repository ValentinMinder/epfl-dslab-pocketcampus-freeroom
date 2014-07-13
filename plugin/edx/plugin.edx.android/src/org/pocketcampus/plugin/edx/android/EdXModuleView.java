package org.pocketcampus.plugin.edx.android;

import java.util.LinkedList;
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
import org.pocketcampus.plugin.edx.android.EdXController.EdxGenericItem;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdxItemType;
import org.pocketcampus.plugin.edx.shared.EdxSequence;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * EdXModuleView - Module view that shows EdX module.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXModuleView extends PluginView implements IEdXView {

	private EdXController mController;
	private EdXModel mModel;
	
	public static final String MAP_KEY_VIDEOID = "EDX_VIDEO_ID";
	public static final String EXTRAS_KEY_COURSEID = "EDX_COURSE_ID";
	public static final String EXTRAS_KEY_MODULEID = "EDX_MODULE_ID";
	
	private boolean displayingList;
	
	String courseId;
	String moduleId;
	List<EdxSequence> moduleDetails;
	
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
		
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			//Uri aData = aIntent.getData();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_COURSEID) && aExtras.containsKey(EXTRAS_KEY_MODULEID)) {
				courseId = aExtras.getString(EXTRAS_KEY_COURSEID);
				moduleId = aExtras.getString(EXTRAS_KEY_MODULEID);
				mController.refreshModuleDetails(this, courseId, moduleId, false);
			}
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
	
	
	

	

	
	private void updateDisplay(boolean saveScroll) {

		if(saveScroll && displayingList)
			scrollState = new ScrollStateSaver(mList);
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.edx_list_header);
		int tabsCount = 0;
		
		for(final EdxSequence seq : moduleDetails) {
			
			// TODO do this in thrift
			List<EdxGenericItem> items = new LinkedList<EdxGenericItem>();
			List<EdxItemType> returnedItems = seq.getItems();
			for(int i = 0; i < returnedItems.size(); i++) {
				items.add(new EdxGenericItem(returnedItems.get(i), i));
			}
			
			Preparated<EdxGenericItem> p = new Preparated<EdxGenericItem>(items, new Preparator<EdxGenericItem>() {
				public int[] resources() {
					return new int[] { R.id.edx_title, R.id.edx_speaker, R.id.edx_thumbnail, R.id.edx_time, R.id.edx_fav_star };
				}
				public Object content(int res, final EdxGenericItem e) {
					switch (res) {
					case R.id.edx_title:
						switch(e.type) {
						case VIDEO:
							return seq.getVideoItems().get(e.index).getTitle();
						default:
							return e.type.name();
						}
					case R.id.edx_speaker:
						switch(e.type) {
						case VIDEO:
							return "Youtube video: " + seq.getVideoItems().get(e.index).getYoutubeId();
						case HTML:
							return "Html content: " + seq.getHtmlItems().get(e.index).getHtmlContent();
						case PROBLEM:
							return "Problem at url: " + seq.getProblemItems().get(e.index).getItemId();
						default:
							return "Unknown type";
						}
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
				public void finalize(Map<String, Object> map, EdxGenericItem e) {
					switch(e.type) {
					case VIDEO:
						map.put(MAP_KEY_VIDEOID, seq.getVideoItems().get(e.index).getYoutubeId());
					case HTML:
						//map.put(MAP_KEY_MODULEID, item.getModuleUrl());
					case PROBLEM:
						//map.put(MAP_KEY_MODULEID, item.getModuleUrl());
					default:
						// nothing
					}
				}
			});
			adapter.addSection("Tab " + ++tabsCount/*seq.getVerticalId()*/, new LazyAdapter(this, p.getMap(), 
					R.layout.edx_list_row, p.getKeys(), p.getResources()));
			
		}
		
		
		
		if(moduleDetails.size() == 0) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText("Nothing to display");
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
						if(((Map<?, ?>) o).get(MAP_KEY_VIDEOID) != null) { // Video
							Intent i = new Intent(EdXModuleView.this, EdXActiveRoomsView.class);
							i.putExtra(EdXActiveRoomsView.EXTRAS_KEY_VIDEOID, ((Map<?, ?>) o).get(MAP_KEY_VIDEOID).toString());
							EdXModuleView.this.startActivity(i);
						} else {
							Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
						}
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
	}
	@Override
	public void courseSectionsUpdated() {
	}
	@Override
	public void moduleDetailsUpdated() {
		moduleDetails = mModel.getModuleDetails();
		updateDisplay(true);
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
		mController.refreshModuleDetails(this, courseId, moduleId, true);
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
		// this is called on all listeners, so no need to do anything
	}
	@Override
	public void loginSucceeded() {
		mController.refreshModuleDetails(this, courseId, moduleId, false);
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
