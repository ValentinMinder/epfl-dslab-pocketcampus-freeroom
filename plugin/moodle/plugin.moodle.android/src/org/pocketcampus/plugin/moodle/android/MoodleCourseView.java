package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.moodle.R;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSection2;
import org.pocketcampus.plugin.moodle.shared.MoodleResource2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * MoodleCourseView - Shows a particular course's contents.
 * 
 * TODO: For icons, we might want to do:
 * - get mime type from file extension http://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android
 * - get icon from default app that opens file http://stackoverflow.com/questions/8248466/get-icon-of-the-default-application-that-opens-a-file
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleCourseView extends PluginView implements IMoodleView {

	private MoodleController mController;
	private MoodleModel mModel;
	
	public static final String EXTRAS_KEY_MOODLECOURSEID = "courseId";
	public static final String EXTRAS_KEY_MOODLECOURSETITLE = "courseTitle";

	public static final String MAP_KEY_MOODLERESOURCE = "MOODLE_RESOURCE";
	
	private boolean displayingList;

	private int courseId;
	private String courseTitle;
	
	List<MoodleCourseSection2> sections = null;
	
	ListView mList;
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


		displayingList = false;
		setContentView(new StandardLayout(this));

		setActionBarTitle(getString(R.string.moodle_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras to know what is the courseId
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		Bundle aExtras = aIntent.getExtras();
		if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_MOODLECOURSEID))
			courseId = aExtras.getInt(EXTRAS_KEY_MOODLECOURSEID);
		if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_MOODLECOURSETITLE))
			courseTitle = aExtras.getString(EXTRAS_KEY_MOODLECOURSETITLE);
		
		if(MoodleController.sessionExists(this))
			mController.refreshCourseSections(this, courseId, false);
		else
			MoodleController.pingAuthPlugin(this);

	}


	@Override
	protected void onResume() {
		super.onResume();
		if(displayingList && scrollState != null)
			scrollState.restore(mList);
		
		updateDisplay();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}
	
	@Override
	protected String screenName() {
		return "/moodle/course";
	}
	
	@Override
	public void coursesListUpdated() {
	}
	
	@Override
	public void sectionsListUpdated() {
		sections = mModel.getSections();
		
		updateDisplay();
		

	}

	public void updateDisplay() {

		if(sections == null)
			return;

		if(displayingList)
			scrollState = new ScrollStateSaver(mList);
		

		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.sdk_separated_list_header2);
		
		boolean atLeastOneSection = false;

		for(MoodleCourseSection2 section : sections) {
			Preparated<MoodleResource2> p = new Preparated<MoodleResource2>(section.getResources(), new Preparator<MoodleResource2>() {
				public int[] resources() {
					return new int[] { R.id.moodle_resource_icon, R.id.moodle_resource_title, R.id.moodle_resource_second_line, R.id.moodle_resource_status, R.id.moodle_resource_arrow };
				}
				public Object content(int res, final MoodleResource2 e) {
					switch (res) {
					case R.id.moodle_resource_icon:
						return resourceGetIcon(MoodleCourseView.this, e);
					case R.id.moodle_resource_title:
						return resourceGetTitle(MoodleCourseView.this, e);
					case R.id.moodle_resource_second_line:
						return resourceGetSecondLine(MoodleCourseView.this, e);
					case R.id.moodle_resource_status:
						return resourceGetStatus(MoodleCourseView.this, e);
					case R.id.moodle_resource_arrow:
						return (e.getFolder() != null ? R.drawable.pocketcampus_list_arrow : -1);
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, MoodleResource2 item) {
					map.put(MAP_KEY_MOODLERESOURCE, item);
				}
			});
			if(section.getResourcesSize() != 0) {
				atLeastOneSection = true;
				adapter.addSection(MoodleCourseView.getSectionTitle(MoodleCourseView.this, section), new LazyAdapter(this, p.getMap(), 
						R.layout.moodle_course_resource_entry, p.getKeys(), p.getResources()));
			}
		}
		
		
		if(!atLeastOneSection) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText(getString(R.string.moodle_string_nothingtodisplay));
			setContentView(sl);
		} else {
			
			
			if(!displayingList) {
				setContentView(R.layout.moodle_course_container);
				mList = (ListView) findViewById(R.id.moodle_course_list);
				displayingList = true;
			}

			setActionBarTitle(courseTitle);

			mList.setAdapter(adapter);
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
						MoodleResource2 mr = (MoodleResource2) ((Map<?, ?>) o).get(MAP_KEY_MOODLERESOURCE);
						resourceOnClick(MoodleCourseView.this, mController, mr);
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			mList.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
						MoodleResource2 mr = (MoodleResource2) ((Map<?, ?>) o).get(MAP_KEY_MOODLERESOURCE);
						return resourceOnLongPress(MoodleCourseView.this, mController, mr);
					}
					return false;
				}
			});
			
			if(scrollState != null)
				scrollState.restore(mList);
			
		}
		
				
		
		
		
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
		MoodleController.openFile(this, localFile);
		/*Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.moodle_file_downloaded), Toast.LENGTH_SHORT).show();*/
	}
	

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.refreshCourseSections(this, courseId, true);
		
	}

	@Override
	public void notLoggedIn() {
		MoodleController.pingAuthPlugin(this);
		
	}

	@Override
	public void authenticationFinished() {
		mController.refreshCourseSections(this, courseId, false);
		
	}

	
	/****
	 * HELPER FUNCS
	 * 
	 */


	private static void resourceOnClick(MoodleCourseView context, MoodleController controller, MoodleResource2 item) {
		if(item.getFile() != null) {
			MoodleFolderView.fileOnClick(context, controller, item.getFile());
		} else if(item.getFolder() != null) {
			Intent i = new Intent(context, MoodleFolderView.class);
			i.putExtra(MoodleFolderView.EXTRAS_KEY_FOLDEROBJECT, item.getFolder());
			i.putExtra(MoodleFolderView.EXTRAS_KEY_MOODLECOURSETITLE, context.courseTitle);
			context.startActivity(i);
			context.trackEvent("OpenFolder", item.getFolder().getName());
			
		} else if(item.getUrl() != null) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl().getUrl()));
			context.startActivity(browserIntent);
			context.trackEvent("OpenLink", item.getUrl().getName());

			
		}
		
	}
	
	private static boolean resourceOnLongPress(MoodleCourseView context, MoodleController controller, MoodleResource2 item) {
		if(item.getFile() != null) {
			return MoodleFolderView.fileOnLongPress(context, controller, item.getFile());
		}
		return false;
	}
	
	
	private static String getSectionTitle(Context c, MoodleCourseSection2 section) {
		if(section.getTitle() != null)
			return section.getTitle();
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM", c.getResources().getConfiguration().locale);
		String startDate = dateFormat.format(new Date(section.getStartDate()));
		String endDate = dateFormat.format(new Date(section.getEndDate()));
		return startDate + " - " + endDate;
	}
	

	/***
	 * 
	 */
	
	private static Object resourceGetIcon(Context c, MoodleResource2 item) {
		if(item.getFile() != null) {
			return MoodleFolderView.fileGetIcon(c, item.getFile());
		} else if(item.getFolder() != null) {
			//return "http://moodle.epfl.ch/theme/image.php/epfl/core/1407927792/f/folder-128";
			return R.drawable.moodle_folder;
		} else if(item.getUrl() != null) {
			return R.drawable.moodle_webpage;
		}
		return null;
	}
	
	private static Object resourceGetTitle(Context c, MoodleResource2 item) {
		if(item.getFile() != null) {
			return MoodleFolderView.fileGetTitle(c, item.getFile());
		} else if(item.getFolder() != null) {
			return item.getFolder().getName();
		} else if(item.getUrl() != null) {
			return item.getUrl().getName();
		}
		return null;
	}
	
	private static Object resourceGetSecondLine(Context c, MoodleResource2 item) {
		if(item.getFile() != null) {
			return MoodleFolderView.fileGetSecondLine(c, item.getFile());
		} else if(item.getFolder() != null) {
			return String.format(c.getString(R.string.moodle_string_xfiles), item.getFolder().getFilesSize());
		} else if(item.getUrl() != null) {
			return item.getUrl().getUrl();
		}
		return null;
	}
	
	private static Object resourceGetStatus(Context c, MoodleResource2 item) {
		if(item.getFile() != null) {
			return MoodleFolderView.fileGetStatus(c, item.getFile());
		}
		return null;
	}
	
	
}
