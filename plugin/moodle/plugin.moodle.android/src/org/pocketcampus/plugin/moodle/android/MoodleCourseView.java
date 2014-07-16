package org.pocketcampus.plugin.moodle.android;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.utils.Preparated;
import org.pocketcampus.android.platform.sdk.utils.Preparator;
import org.pocketcampus.android.platform.sdk.utils.ScrollStateSaver;
import org.pocketcampus.plugin.moodle.R;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSection2;
import org.pocketcampus.plugin.moodle.shared.MoodleFile2;
import org.pocketcampus.plugin.moodle.shared.MoodleFolder2;
import org.pocketcampus.plugin.moodle.shared.MoodleResource2;
import org.pocketcampus.plugin.moodle.shared.MoodleUrl2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * MoodleCourseView - Shows a particular course's contents.
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

	private void updateDisplay() {

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
						return (e.getFile() != null ? e.getFile().getIcon() : null);
					case R.id.moodle_resource_title:
						return resourceGetTitle(e);
					case R.id.moodle_resource_second_line:
						return resourceGetSecondLine(e);
					case R.id.moodle_resource_status:
						return resourceGetStatus(e);
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
				adapter.addSection(getSectionTitle(section), new LazyAdapter(this, p.getMap(), 
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
			
			TextView header = (TextView) findViewById(R.id.moodle_course_header_title);
			header.setText(courseTitle);
			
			mList.setAdapter(adapter);
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if(o instanceof Map<?, ?>) {
						MoodleResource2 mr = (MoodleResource2) ((Map<?, ?>) o).get(MAP_KEY_MOODLERESOURCE);
						resourceOnClick(mr);
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

	

	private Object resourceGetTitle(MoodleResource2 item) {
		if(item.getFile() != null) {
			MoodleFile2 mf = item.getFile();
			return mf.getName();
		} else if(item.getFolder() != null) {
			MoodleFolder2 mf = item.getFolder();
			return mf.getName();
		} else if(item.getUrl() != null) {
			MoodleUrl2 mu = item.getUrl();
			return mu.getName();
		}
		return null;
	}
	
	private Object resourceGetSecondLine(MoodleResource2 item) {
		if(item.getFile() != null) {
			MoodleFile2 mf = item.getFile();
			return MoodleController.getPrettyName(mf.getUrl());
		} else if(item.getFolder() != null) {
			MoodleFolder2 mf = item.getFolder();
			return String.format(getString(R.string.moodle_string_xfiles), mf.getFilesSize());
		} else if(item.getUrl() != null) {
			MoodleUrl2 mu = item.getUrl();
			return mu.getUrl();
		}
		return null;
	}
	
	private Object resourceGetStatus(MoodleResource2 item) {
		if(item.getFile() != null) {
			MoodleFile2 mf = item.getFile();
			return (new File(MoodleController.getLocalPath(mf.getUrl(), false)).exists() ? getString(R.string.moodle_string_downloaded) : "");
		} else if(item.getFolder() != null) {
			return "";
			
		} else if(item.getUrl() != null) {
			return "";
			
		}
		return null;
	}
	
	private void resourceOnClick(MoodleResource2 item) {
		if(item.getFile() != null) {
			MoodleFile2 mf = item.getFile();
			trackEvent("DownloadAndOpenFile", mf.getName());
			File resourceFile = new File(MoodleController.getLocalPath(mf.getUrl(), false));
			if(resourceFile.exists()) {
				MoodleController.openFile(MoodleCourseView.this, resourceFile);
			} else {
				mController.fetchFileResource(MoodleCourseView.this, mf.getUrl());
			}
		} else if(item.getFolder() != null) {
			// TODO
			Toast.makeText(getApplicationContext(), "Folders not yet implemented", Toast.LENGTH_SHORT).show();
			
		} else if(item.getUrl() != null) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl().getUrl()));
			startActivity(browserIntent);

			
		}
		
	}
	
	private String getSectionTitle(MoodleCourseSection2 section) {
		if(section.getTitle() != null)
			return section.getTitle();
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM", getResources().getConfiguration().locale);
		String startDate = dateFormat.format(new Date(section.getStartDate()));
		String endDate = dateFormat.format(new Date(section.getEndDate()));
		return startDate + " - " + endDate;
	}
	

}
