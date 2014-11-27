package org.pocketcampus.plugin.edx.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.pocketcampus.platform.sdk.shared.utils.Callback;
import org.pocketcampus.plugin.edx.R;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.edx.android.EdXModel.ActiveRoom;
import org.pocketcampus.plugin.edx.android.EdXModel.MyMenuItem;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * EdXActiveRoomsView - ActiveRooms view that shows EdX study rooms that are currently active
 * and joinable by users who want to co-watch a lecture.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EdXActiveRoomsView extends PluginView implements IEdXView {

	private EdXController mController;
	private EdXModel mModel;
	
	public static final String MAP_KEY_VIDID = "EDX__VIDID";
	public static final String MAP_KEY_ROOMNBR = "EDX__ROOMNBR";
	public static final String MAP_KEY_PROMPT = "EDX__PROMPT";
	public static final String EXTRAS_KEY_VIDEOID = "EDX_VIDEO_ID";
	
	private boolean displayingList;
	
	String videoId;
	
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
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_VIDEOID)) {
				videoId = aExtras.getString(EXTRAS_KEY_VIDEOID);
				
			}
		}
		
		
		if(videoId != null) {
			mController.refreshActiveRooms(this, videoId);
		} else {
			Log.e("EdXActiveRooms", "videiId is null");
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
		
		/*
		List<ActiveRoom> activeRooms = mModel.getActiveRooms();
		
		Preparated<ActiveRoom> p = new Preparated<ActiveRoom>(activeRooms, new Preparator<ActiveRoom>() {
			public int[] resources() {
				return new int[] { R.id.edx_title, R.id.edx_speaker, R.id.edx_thumbnail, R.id.edx_time, R.id.edx_fav_star };
			}
			public Object content(int res, final ActiveRoom e) {
				switch (res) {
				case R.id.edx_title:
					return "Room #" + e.name.split("[!]")[1];
				case R.id.edx_speaker:
					return "occupancy: " + e.occupancy;
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
			public void finalize(Map<String, Object> map, ActiveRoom item) {
				//map.put(MAP_KEY_ROOMNAME, item.name);
			}
		});
		if(activeRooms.size() > 0) { 
			adapter.addSection("Active public study rooms", new LazyAdapter(this, p.getMap(), 
					R.layout.edx_list_row, p.getKeys(), p.getResources()));
		}
		*/


		List<MyMenuItem> newRoom = new LinkedList<MyMenuItem>();
		Random rand = new Random();
		int roomNbr = 1000 + rand.nextInt(9000);
		//newRoom.add(new ActiveRoom(videoId + "!" + roomNbr, -1));
		newRoom.add(new MyMenuItem(-2, "Create a study room",  videoId, "" + roomNbr, false));
		newRoom.add(new MyMenuItem(-3, "Join a study room",  videoId, null, true));
		newRoom.add(new MyMenuItem(-4, "Watch individually", videoId, null, false));
		
		Preparated<MyMenuItem> p2 = new Preparated<MyMenuItem>(newRoom, new Preparator<MyMenuItem>() {
			public int[] resources() {
				return new int[] { R.id.edx_title, R.id.edx_speaker, R.id.edx_thumbnail, R.id.edx_time, R.id.edx_fav_star };
			}
			public Object content(int res, final MyMenuItem e) {
				switch (res) {
				case R.id.edx_title:
					/*if(e.occupancy == -1) return "Create a public study room";
					if(e.occupancy == -2) return "";
					if(e.occupancy == -3) return "";
					if(e.occupancy == -4) return "";*/
					return e.title;
				case R.id.edx_speaker:
					return null;
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
			public void finalize(Map<String, Object> map, MyMenuItem item) {
				if(item.vidID != null)
					map.put(MAP_KEY_VIDID, item.vidID);
				if(item.roomNbr != null)
					map.put(MAP_KEY_ROOMNBR, item.roomNbr);
				if(item.prompt)
					map.put(MAP_KEY_PROMPT, "1");
			}
		});
		adapter.addSection("Create or join study rooms", new LazyAdapter(this, p2.getMap(), 
				R.layout.edx_list_row, p2.getKeys(), p2.getResources()));
		
		
		
		
		if(true == false) { // always false
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
						final Object vidId1 = ((Map<?, ?>) o).get(MAP_KEY_VIDID);
						final Object roomNbr1 = ((Map<?, ?>) o).get(MAP_KEY_ROOMNBR);
						final Object prompt1 = ((Map<?, ?>) o).get(MAP_KEY_PROMPT);
						
						final String vidId = (vidId1 == null ? null : vidId1.toString());
						final String roomNbr = (roomNbr1 == null ? null : roomNbr1.toString());
						final String prompt = (prompt1 == null ? null : prompt1.toString());
						if(prompt != null) {
							EdXController.prompt(EdXActiveRoomsView.this, "Join private study room", "Room number", "", new Callback<String>() {
								public void callback(String s) {
									int number = Integer.parseInt(s);
									Intent i = new Intent(EdXActiveRoomsView.this, EdXStudyRoomView.class);
									i.putExtra(EdXStudyRoomView.EXTRAS_KEY_YOUTUBE_VIDID, vidId);
									i.putExtra(EdXStudyRoomView.EXTRAS_KEY_ROOM_NBR, "" + number);
									EdXActiveRoomsView.this.startActivity(i);
								}
							});
						} else {
							Intent i = new Intent(EdXActiveRoomsView.this, EdXStudyRoomView.class);
							i.putExtra(EdXStudyRoomView.EXTRAS_KEY_YOUTUBE_VIDID, vidId);
							if(roomNbr != null)
								i.putExtra(EdXStudyRoomView.EXTRAS_KEY_ROOM_NBR, roomNbr);
							EdXActiveRoomsView.this.startActivity(i);
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
	}
	@Override
	public void activeRoomsUpdated() {
		updateDisplay(true);
	}

	
	
	

	
	
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	@Override
	public void networkErrorCacheExists() {
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
		mController.refreshActiveRooms(this, videoId);
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
