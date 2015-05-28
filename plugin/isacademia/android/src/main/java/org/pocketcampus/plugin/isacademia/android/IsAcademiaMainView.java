package org.pocketcampus.plugin.isacademia.android;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PCAndroidConfig;
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
import org.pocketcampus.plugin.isacademia.shared.StudyPeriod;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
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

/**
 * IsAcademiaMainView - Main view that shows IsAcademia courses.
 * 
 * This is the main view in the IsAcademia Plugin. It checks if the user is
 * logged in, if not it pings the Authentication Plugin. When it gets back a
 * valid SessionId it fetches the user's IsAcademia data.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsAcademiaMainView extends PluginView implements IIsAcademiaView {

	private IsAcademiaController mController;
	private IsAcademiaModel mModel;

	// public static final String MAP_KEY_ISACADEMIASTUDYPERIOD =
	// "MAP_KEY_ISACADEMIASTUDYPERIOD";

	private boolean displayingList;

	StickyListHeadersListView mList;
	ScrollStateSaver scrollState;

	private long currentTime;
	private SimpleDateFormat keyFormatter;

	private GestureDetector gestureDetector;

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
		
		setActionBarTitle(getString(R.string.isacademia_timetables));

		// transform currentTime to dayKey in device's timezone
		keyFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
		currentTime = System.currentTimeMillis();
		// try {
		// currentTime = keyFormatter.parse("20111012").getTime();
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		gestureDetector = buildGestureDetector();
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
		if (IsAcademiaController.sessionExists(this)) // I think this is no
														// longer necessary,
														// since the auth plugin
														// doesnt blindly
			// redo auth (well, this saves the one call that the auth plugin
			// does to check if the session is valid)
			updateDisplay(); // triggerRefresh(false);
		else
			IsAcademiaController.pingAuthPlugin(this);
	}

	private void triggerRefresh(boolean useCache) {
		mController.refreshSchedule(this,
				keyFormatter.format(new Date(currentTime)), useCache);
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
		return "/isacademia";
	}

	@Override
	public void scheduleUpdated() {
		updateDisplay();
	}

	@Override
	public void gradesUpdated() {
	}

	private String formatStringWithDate(int res) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEE d MMM yyyy",
				Locale.getDefault());
		return String.format(getString(res), fmt.format(new Date(currentTime)));

	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		addActionToActionBar(new Action() {
			public void performAction(View view) {
				currentTime -= 24 * 3600 * 1000;
				updateDisplay();
				trackEvent("PreviousDay", null);
			}

			public int getDrawable() {
				return R.drawable.isacademia_left_arrow;
			}

			@Override
			public String getDescription() {
				return getString(R.string.isacademia_yesterday);
			}

		});
		addActionToActionBar(new Action() {
			public void performAction(View view) {
				currentTime += 24 * 3600 * 1000;
				updateDisplay();
				trackEvent("NextDay", null);
			}

			public int getDrawable() {
				return R.drawable.isacademia_right_arrow;
			}

			@Override
			public String getDescription() {
				return getString(R.string.isacademia_tomorrow);
			}
		});

		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				trackEvent("GoToDate", null);
				Calendar c = Calendar.getInstance();
				c.setTime(new Date(currentTime));
				DatePickerDialog dpd = new DatePickerDialog(
						IsAcademiaMainView.this, new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								if (!view.isShown())
									return; // bug in Jelly Bean:
											// http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
								currentTime = computeCurrentTime(year,
										monthOfYear + 1, dayOfMonth);
								trackEvent("GoToDateSelected",
										new SimpleDateFormat("yyyy-MM-dd",
												Locale.US).format(new Date(
												currentTime)));
								updateDisplay();
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH));
				dpd.show();
			}

			@Override
			public int getDrawable() {
				return R.drawable.sdk_pick_date;
			}

			@Override
			public String getDescription() {
				return getString(R.string.isacademia_go_to_date);
			}
		});

		if(PCAndroidConfig.PC_ANDR_CFG.getInteger("DISABLE_ISA_GRADES") == 0) {
			addActionToActionBar(new Action() {

				@Override
				public void performAction(View view) {
					trackEvent("ShowGrades", null);
					startActivity(new Intent(IsAcademiaMainView.this, IsAcademiaGradesView.class));
				}

				@Override
				public int getDrawable() {
					return R.drawable.isa_grades_icon;
				}

				@Override
				public String getDescription() {
					return getString(R.string.isacademia_show_grades);
				}
			});
		}

	}

	private static String getRoomsString(StudyPeriod p) {
		if (p.getRoomsSize() > 2) {
			return TextUtils.join(", ", p.getRooms().subList(0, 2))
					+ ", &hellip;";
		} else {
			return TextUtils.join(", ", p.getRooms());
		}
	}

	private static Uri buildLink(String room) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("pocketcampus").authority("map.plugin.pocketcampus.org")
				.appendPath("search").appendQueryParameter("q", room);
		return builder.build();
	}

	private String getLocalizedPeriodType(String aString) {
		String packageName = getPackageName();
		int resId = getResources().getIdentifier(
				"isacademia_period_type_" + aString, "string", packageName);
		if (resId == 0)
			return aString;
		return getString(resId);
	}

	public void updateDisplay() {
		setContentView(R.layout.isacademia_main_container);
		mList = (StickyListHeadersListView) findViewById(R.id.isacademia_main_list);
		displayingList = true;

		updateActionBar();

		List<StudyPeriod> courses = mModel.getDay(keyFormatter.format(new Date(
				currentTime)));

		if (courses == null) {
			setLoadingContentScreen();
			triggerRefresh(false);
			return;
		}

		if (displayingList)
			scrollState = new ScrollStateSaver(mList);

		SeparatedListAdapter2 adapter = new SeparatedListAdapter2(this,
				R.layout.sdk_separated_list_header2);

		Preparated<StudyPeriod> p = new Preparated<StudyPeriod>(courses,
				new Preparator<StudyPeriod>() {
					DateFormat fmt = android.text.format.DateFormat
							.getTimeFormat(IsAcademiaMainView.this);

					public int[] resources() {
						return new int[] { R.id.isacademia_period_title,
								R.id.isacademia_period_type,
								R.id.isacademia_period_room,
								R.id.isacademia_period_time };
					}

					public Object content(int res, final StudyPeriod e) {
						switch (res) {
						case R.id.isacademia_period_title:
							return e.getName();
						case R.id.isacademia_period_type:
							return getLocalizedPeriodType(e.getPeriodType()
									.name());
						case R.id.isacademia_period_room:
							return new Actuated(getRoomsString(e),
									new Actuator() {
										public void triggered() {
											if (e.getRoomsSize() > 1) {
												roomsMenu(e);
											} else {
												openMap(e.getRooms().get(0));
											}
										}
									});
						case R.id.isacademia_period_time:
							return fmt.format(new Date(e.getStartTime()))
									+ " - "
									+ fmt.format(new Date(e.getEndTime()));
						default:
							return null;
						}
					}

					public void finalize(Map<String, Object> map,
							StudyPeriod item) {
						map.put(LazyAdapter.NOT_SELECTABLE, "1");
					}
				});
		adapter.addSection(
				formatStringWithDate(R.string.isacademia_schedule_for),
				new LazyAdapter(this, p.getMap(),
						R.layout.isacademia_main_period_entry, p.getKeys(), p
								.getResources()));

		if (courses.size() == 0) {
			showOnlySingleMessage(formatStringWithDate(R.string.isacademia_no_classes_on));
		} else {
			if (!displayingList) {
				setContentView(R.layout.isacademia_main_container);
				mList = (StickyListHeadersListView) findViewById(R.id.isacademia_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);

			attachGestureDetector(mList);

			if (scrollState != null)
				scrollState.restore(mList);
		}

	}

	private Long computeCurrentTime(int year, int month, int day) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-M-d'T'HH:mm:ss'Z'",
					Locale.US);
			// df.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
			String d = year + "-" + month + "-" + day;
			return df.parse(d + "T12:00:00Z").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void openMap(String room) {
		try {
			trackEvent("ViewRoomOnMap", room);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					buildLink(room));
			startActivity(browserIntent);

		} catch (ActivityNotFoundException e) {
			// should never happen
			Toast.makeText(getApplicationContext(), "Map plugin not found",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void roomsMenu(final StudyPeriod m) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.isacademia_show_on_map)
				.setAdapter(
						new ArrayAdapter<String>(this,
								android.R.layout.select_dialog_item, m
										.getRooms().toArray(
												new String[m.getRoomsSize()])),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								openMap(m.getRooms().get(arg1));
							}

						}).setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(formatStringWithDate(R.string.isacademia_network_error));
	}

	private void showOnlySingleMessage(String message) {
		displayingList = false;
		StandardLayout sl = new StandardLayout(this);
		sl.setText(message);
		setContentView(sl);
		attachGestureDetector(sl);
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
		IsAcademiaController.pingAuthPlugin(this);
	}

	@Override
	public void authenticationFinished() {
		triggerRefresh(false);
	}

	private GestureDetector buildGestureDetector() {
		return new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {

					private static final int SWIPE_MIN_DISTANCE = 120;
					private static final int SWIPE_MAX_OFF_PATH = 250;
					private static final int SWIPE_THRESHOLD_VELOCITY = 200;

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						try {
							if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
								return false;
							if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								// Toast.makeText(IsAcademiaMainView.this,
								// "LeftSwipe", Toast.LENGTH_SHORT).show();
								currentTime += 24 * 3600 * 1000;
								updateDisplay();
								trackEvent("LeftSwipe", null);
							} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								// Toast.makeText(IsAcademiaMainView.this,
								// "RightSwipe", Toast.LENGTH_SHORT).show();
								currentTime -= 24 * 3600 * 1000;
								updateDisplay();
								trackEvent("RightSwipe", null);
							}
						} catch (Exception e) {
							// nothing
						}
						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}
				});
	}

	private void attachGestureDetector(View v) {
		v.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return gestureDetector.onTouchEvent(arg1);
			}
		});
	}
}
