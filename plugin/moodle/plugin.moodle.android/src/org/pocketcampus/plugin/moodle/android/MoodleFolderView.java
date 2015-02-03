package org.pocketcampus.plugin.moodle.android;

import java.io.File;
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
import org.pocketcampus.plugin.moodle.shared.MoodleFile2;
import org.pocketcampus.plugin.moodle.shared.MoodleFolder2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * MoodleFolderView - Shows a particular folder's contents.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleFolderView extends PluginView implements IMoodleView {

	private MoodleController mController;
	// private MoodleModel mModel;

	public static final String EXTRAS_KEY_FOLDEROBJECT = "folderObject";
	public static final String EXTRAS_KEY_MOODLECOURSETITLE = "courseTitle";

	public static final String MAP_KEY_MOODLEFILE = "MOODLE_FILE";

	private boolean displayingList;

	private MoodleFolder2 folderObj;
	private String courseTitle;

	ListView mList;
	ScrollStateSaver scrollState;

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return MoodleController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		// Get and cast the controller and model
		mController = (MoodleController) controller;
		// mModel = (MoodleModel) controller.getModel();

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
		if (aExtras != null && aExtras.containsKey(EXTRAS_KEY_FOLDEROBJECT))
			folderObj = (MoodleFolder2) aExtras
					.getSerializable(EXTRAS_KEY_FOLDEROBJECT);
		if (aExtras != null
				&& aExtras.containsKey(EXTRAS_KEY_MOODLECOURSETITLE))
			courseTitle = aExtras.getString(EXTRAS_KEY_MOODLECOURSETITLE);

		updateDisplay();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null)
			scrollState.restore(mList);

		updateDisplay();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}

	@Override
	protected String screenName() {
		return "/moodle/course/folder";
	}

	@Override
	public void coursesListUpdated() {
	}

	@Override
	public void sectionsListUpdated() {
	}

	public void updateDisplay() {

		if (folderObj == null)
			return;

		if (displayingList)
			scrollState = new ScrollStateSaver(mList);

		SeparatedListAdapter adapter = new SeparatedListAdapter(this,
				R.layout.sdk_separated_list_header2);

		Preparated<MoodleFile2> p = new Preparated<MoodleFile2>(
				folderObj.getFiles(), new Preparator<MoodleFile2>() {
					public int[] resources() {
						return new int[] { R.id.moodle_resource_icon,
								R.id.moodle_resource_title,
								R.id.moodle_resource_second_line,
								R.id.moodle_resource_status,
								R.id.moodle_resource_arrow };
					}

					public Object content(int res, final MoodleFile2 e) {
						switch (res) {
						case R.id.moodle_resource_icon:
							return fileGetIcon(MoodleFolderView.this, e);
						case R.id.moodle_resource_title:
							return fileGetTitle(MoodleFolderView.this, e);
						case R.id.moodle_resource_second_line:
							return fileGetSecondLine(MoodleFolderView.this, e);
						case R.id.moodle_resource_status:
							return fileGetStatus(MoodleFolderView.this, e);
						case R.id.moodle_resource_arrow:
							return -1;
						default:
							return null;
						}
					}

					public void finalize(Map<String, Object> map,
							MoodleFile2 item) {
						map.put(MAP_KEY_MOODLEFILE, item);
					}
				});
		adapter.addSection(
				folderObj.getName(),
				new LazyAdapter(this, p.getMap(),
						R.layout.moodle_course_resource_entry, p.getKeys(), p
								.getResources()));

		if (folderObj.getFilesSize() == 0) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText(getString(R.string.moodle_string_nothingtodisplay));
			setContentView(sl);
		} else {

			if (!displayingList) {
				setContentView(R.layout.moodle_course_container);
				mList = (ListView) findViewById(R.id.moodle_course_list);
				displayingList = true;
			}

			setActionBarTitle(courseTitle);

			mList.setAdapter(adapter);
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader
					.getInstance(), true, true));
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if (o instanceof Map<?, ?>) {
						MoodleFile2 mf = (MoodleFile2) ((Map<?, ?>) o)
								.get(MAP_KEY_MOODLEFILE);
						fileOnClick(MoodleFolderView.this, mController, mf);
					} else {
						Toast.makeText(getApplicationContext(), o.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			mList.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if (o instanceof Map<?, ?>) {
						MoodleFile2 mf = (MoodleFile2) ((Map<?, ?>) o)
								.get(MAP_KEY_MOODLEFILE);
						return fileOnLongPress(MoodleFolderView.this,
								mController, mf);
					}
					return false;
				}
			});

			if (scrollState != null)
				scrollState.restore(mList);

		}

	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(
				getApplicationContext(),
				getResources().getString(
						R.string.moodle_connection_error_happened),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void moodleServersDown() {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.moodle_error_moodle_down),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void downloadComplete(File localFile) {
		MoodleController.openFile(this, localFile);
	}

	@Override
	public void networkErrorCacheExists() {
	}

	@Override
	public void notLoggedIn() {
		MoodleController.pingAuthPlugin(this);
		finish();
	}

	@Override
	public void authenticationFailed() {
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}

	@Override
	public void authenticationFinished() {
	}

	/****
	 * HELPER FUNCS
	 * 
	 */

	private static <T extends PluginView & IMoodleView> void localFileMenu(
			final T context, final MoodleController controller,
			final MoodleFile2 item, final File file) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, new String[] {
						context.getString(R.string.moodle_filemenu_open),
						context.getString(R.string.moodle_filemenu_share),
						context.getString(R.string.moodle_filemenu_redownload),
						context.getString(R.string.moodle_filemenu_print),
						context.getString(R.string.moodle_filemenu_delete) });
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
				case 0:
					context.trackEvent("Open", item.getName());
					MoodleController.openFile(context, file);
					break;
				case 1:
					context.trackEvent("Share", item.getName());
					MoodleController.shareFile(context, file);
					break;
				case 2:
					context.trackEvent("ReDownload", item.getName());
					controller.fetchFileResource(context, item.getUrl());
					break;
				case 3:
					context.trackEvent("Print", item.getName());
					controller.printFileResource(context, item.getUrl());
					break;
				case 4:
					context.trackEvent("Delete", item.getName());
					file.delete();
					context.updateDisplay();
					break;
				default:
					Toast.makeText(context.getApplicationContext(), arg1,
							Toast.LENGTH_SHORT).show();
					break;
				}

			}

		};
		fileMenu(context, controller, item, file, adapter, listener);
	}

	private static <T extends PluginView & IMoodleView> void remoteFileMenu(
			final T context, final MoodleController controller,
			final MoodleFile2 item, final File file) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, new String[] {
						context.getString(R.string.moodle_filemenu_download),
						context.getString(R.string.moodle_filemenu_print) });
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
				case 0:
					context.trackEvent("Download", item.getName());
					controller.fetchFileResource(context, item.getUrl());
					break;
				case 1:
					context.trackEvent("Print", item.getName());
					controller.printFileResource(context, item.getUrl());
					break;
				default:
					Toast.makeText(context.getApplicationContext(), arg1,
							Toast.LENGTH_SHORT).show();
					break;
				}

			}

		};
		fileMenu(context, controller, item, file, adapter, listener);

	}

	private static <T extends PluginView & IMoodleView> void fileMenu(
			final T context, final MoodleController controller,
			final MoodleFile2 item, final File file, final ListAdapter adapter,
			final DialogInterface.OnClickListener listener) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(item.getName()).setAdapter(adapter, listener)
				.setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

	}

	public static <T extends PluginView & IMoodleView> void fileOnClick(
			T context, MoodleController controller, MoodleFile2 item) {
		context.trackEvent("DownloadAndOpenFile", item.getName());
		File resourceFile = new File(MoodleController.getLocalPath(
				item.getUrl(), false));
		if (resourceFile.exists()) {
			MoodleController.openFile(context, resourceFile);
		} else {
			controller.fetchFileResource(context, item.getUrl());
		}
	}

	public static <T extends PluginView & IMoodleView> boolean fileOnLongPress(
			T context, MoodleController controller, MoodleFile2 item) {
		context.trackEvent("ContextMenu", item.getName());
		File resourceFile = new File(MoodleController.getLocalPath(
				item.getUrl(), false));
		if (resourceFile.exists()) {
			localFileMenu(context, controller, item, resourceFile);
			return true;
		} else {
			remoteFileMenu(context, controller, item, resourceFile);
			return true;
		}
	}

	/**
	 * 
	 */

	public static Object fileGetIcon(Context c, MoodleFile2 item) {
		if (item.getIcon() == null)
			return null;
		return item.getIcon().replace("{size}", "128");
	}

	public static Object fileGetTitle(Context c, MoodleFile2 item) {
		return item.getName();
	}

	public static Object fileGetSecondLine(Context c, MoodleFile2 item) {
		return MoodleController.getPrettyName(item.getUrl());
	}

	public static Object fileGetStatus(Context c, MoodleFile2 item) {
		File mf = new File(MoodleController.getLocalPath(item.getUrl(), false));
		return (mf.exists() ? c.getString(R.string.moodle_string_downloaded)
				: "");
	}

}
