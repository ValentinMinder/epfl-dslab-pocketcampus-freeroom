package org.pocketcampus.plugin.isacademia.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import com.markupartist.android.widget.Action;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuated;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter2;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.isacademia.R;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaView;
import org.pocketcampus.plugin.isacademia.shared.SemesterGrades;
import org.pocketcampus.plugin.isacademia.shared.StudyPeriod;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * IsAcademiaGradesView - Show IsAcademia student grades.
 *
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsAcademiaGradesView extends PluginView implements IIsAcademiaView {

	private IsAcademiaController mController;
	private IsAcademiaModel mModel;

	// public static final String MAP_KEY_ISACADEMIASTUDYPERIOD =
	// "MAP_KEY_ISACADEMIASTUDYPERIOD";

	private boolean displayingList;

	StickyListHeadersListView mList;
	ScrollStateSaver scrollState;



	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return IsAcademiaController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Get and cast the controller and model
		mController = (IsAcademiaController) controller;
		mModel = (IsAcademiaModel) controller.getModel();

		setActionBarTitle(getString(R.string.isacademia_plugin_title));

		// transform currentTime to dayKey in device's timezone
		// try {
		// currentTime = keyFormatter.parse("20111012").getTime();
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * If we were pinged by auth plugin, then we must read the sessId. Otherwise
	 * we do a normal startup, and if we do not have the isacademiaCookie we
	 * ping the Authentication Plugin.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		setLoadingContentScreen();
		triggerRefresh(false);
	}

	private void triggerRefresh(boolean useCache) {
		mController.refreshGrades(this, useCache);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null)
			scrollState.restore(mList);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}

	@Override
	protected String screenName() {
		return "/isacademia/grades";
	}

	@Override
	public void scheduleUpdated() {
	}

	@Override
	public void gradesUpdated() {
		updateDisplay();
	}


	public void updateDisplay() {
		setContentView(R.layout.isacademia_main_container);
		mList = (StickyListHeadersListView) findViewById(R.id.isacademia_main_list);
		displayingList = true;


		List<SemesterGrades> grades = mModel.getGrades();

		if (grades == null) {
			setLoadingContentScreen();
			triggerRefresh(false);
			return;
		}

		if (displayingList)
			scrollState = new ScrollStateSaver(mList);

		SeparatedListAdapter2 adapter = new SeparatedListAdapter2(this,
				R.layout.sdk_separated_list_header2);

		boolean atLeastOne = false;
		for(SemesterGrades g : grades) {


			List<Map.Entry<String, String>> l = new LinkedList<Map.Entry<String, String>>(g.getGrades().entrySet());
			atLeastOne = (l.size() > 0);
			Preparated<Map.Entry<String, String>> p = new Preparated<Map.Entry<String, String>>(l,
					new Preparator<Map.Entry<String, String>>() {

						public int[] resources() {
							return new int[] { R.id.isacademia_period_title,
									R.id.isacademia_period_type,
									R.id.isacademia_period_room,
									R.id.isacademia_period_time };
						}

						public Object content(int res, final Map.Entry<String, String> e) {
							switch (res) {
								case R.id.isacademia_period_title:
									return e.getKey();
								case R.id.isacademia_period_type:
									return null;
								case R.id.isacademia_period_room:
									return e.getValue();
								case R.id.isacademia_period_time:
									return " ";
								default:
									return null;
							}
						}

						public void finalize(Map<String, Object> map,
											 Map.Entry<String, String> item) {
							map.put(LazyAdapter.NOT_SELECTABLE, "1");
						}
					});
			adapter.addSection(g.getSemesterName(),
					new LazyAdapter(this, p.getMap(),
							R.layout.isacademia_main_period_entry, p.getKeys(), p
							.getResources()));

		}

		if (!atLeastOne) {
			showOnlySingleMessage(getString(R.string.isacademia_no_grades));
		} else {
			if (!displayingList) {
				setContentView(R.layout.isacademia_main_container);
				mList = (StickyListHeadersListView) findViewById(R.id.isacademia_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);


			if (scrollState != null)
				scrollState.restore(mList);
		}

	}


	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	private void showOnlySingleMessage(String message) {
		displayingList = false;
		StandardLayout sl = new StandardLayout(this);
		sl.setText(message);
		setContentView(sl);
	}

	@Override
	public void authenticationFailed() {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.sdk_authentication_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void userCancelledAuthentication() {
		finish();
	}

	@Override
	public void isacademiaServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.isacademia_error_isacademia_down));
	}

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.sdk_connection_no_cache_yes),
				Toast.LENGTH_SHORT).show();
		triggerRefresh(true);

	}

	@Override
	public void notLoggedIn() {
	}

	@Override
	public void authenticationFinished() {
		triggerRefresh(false);
	}

}
