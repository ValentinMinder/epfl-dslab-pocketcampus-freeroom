package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourRoom;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel.HomeBehaviourTime;
import org.pocketcampus.plugin.freeroom.android.iface.IAbstractFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRRequestDetails;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.android.utils.OrderMapListFew;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * FreeRoomAbstractView - An abstract view that handles shared methods among all
 * FreeRoom views.
 * <p>
 * This view is NOT intended to be instanciated or even used. It's only there to
 * group together all commons method among all the views, like network error
 * message.
 * <p>
 * Be careful to declare there ONLY methods that the views don't need, otherwise
 * no one will warn you if your methods in the view are not declared.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public abstract class FreeRoomAbstractView extends PluginView implements IAbstractFreeRoomView {

	/**
	 * COMMON: Reference to other utility method for client-side.
	 */
	protected FRUtilsClient u;

	/* MVC STRUCTURE */
	/**
	 * COMMON: {@link FreeRoomController} is the controller in MVC scheme.
	 */
	protected FreeRoomController mController;
	/**
	 * COMMON: {@link FreeRoomModel} is the model in MVC scheme.
	 */
	protected FreeRoomModel mModel;

	// ERROR

	/**
	 * {@link #error}: Dialog that holds the {@link #error} (with one button:
	 * dismiss)
	 */
	private AlertDialog error;

	/**
	 * {@link #error}: Inits the {@link #error} to show a single error message
	 * with only a close button.
	 */
	private void initErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_error_title));
		builder.setIcon(R.drawable.sdk_error);
		builder.setNeutralButton(R.string.freeroom_dialog_error_dismiss, null);

		// Get the AlertDialog from create()
		error = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = error.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		error.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		error.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		error.getWindow().setAttributes(lp);

		// reset the message when dismiss
		// (to avoid showing with previous message!)
		error.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				error.setMessage("");
			}
		});

		error.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("Error", null);
			}
		});
	}

	protected void onDisplay(android.os.Bundle savedInstanceState, PluginController controller) {
		initErrorDialog();
		mController = (FreeRoomController) controller;
		mModel = (FreeRoomModel) mController.getModel();
		u = new FRUtilsClient(this);
	}

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	public void networkErrorHappened() {
		anyError();
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.freeroom_connection_error_happened),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	private boolean debug = false;

	@Override
	public void freeRoomServerBadRequest() {
		if (debug) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.freeroom_error_bad_request),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	@Override
	public void freeRoomServersInternalError() {
		if (debug) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.freeroom_error_internal_error),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}

	/**
	 * TODO: may not appear in final version ! >> basic error.
	 */
	@Override
	public void freeRoomServersUnknownError() {
		if (debug) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.freeroom_error_unknown_error),
					Toast.LENGTH_SHORT).show();
		} else {
			anyError();
		}
	}

	/**
	 * {@link #error}: Show the {@link #error} with the given message.
	 * 
	 * @param text
	 *            error message to display
	 */
	protected void errorDialogShowMessage(String text) {
		// error dialog may be null at init time!
		if (error != null) {
			error.setMessage(text);
			error.show();
		}
	}

	/**
	 * COMMON: Dismiss the keyboard associated with the view.
	 * 
	 * @param v
	 *            the view to which the keyboard is attached to, as specified by
	 *            {@link InputMethodManager#hideSoftInputFromWindow(android.os.IBinder, int)}
	 */
	protected void commonDismissSoftKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * HOME: Construct a valid and default request. If useFavorites is true, it
	 * will check all the favorites for the next valid period, otherwise or if
	 * there are not.
	 * 
	 * @param forceUseFavorites
	 *            if it should consider the favorites or not
	 * @return a valid and default request, based or nor on the favorites.
	 */
	protected FRRequestDetails homeValidRequest(boolean forceUseFavorites) {
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel.getFavorites();

		// we choose the period according to settings in model.
		FRPeriod period = null;
		HomeBehaviourTime time = mModel.getHomeBehaviourTime();
		if (time.equals(HomeBehaviourTime.CURRENT_TIME)) {
			period = FRTimes.getNextValidPeriod();
		} else if (time.equals(HomeBehaviourTime.UP_TO_END_OF_DAY)) {
			period = FRTimes.getNextValidPeriodTillEndOfDay();
		} else if (time.equals(HomeBehaviourTime.WHOLE_DAY)) {
			period = FRTimes.getNextValidPeriodWholeDay();
		} else {
			u.logE("unknown time behavior: ");
			u.logE(time.name());
			u.logE("going for default value");
			period = FRTimes.getNextValidPeriod();
		}

		// we choose the request according to the model settings
		HomeBehaviourRoom room = mModel.getHomeBehaviourRoom();
		// if there are favorites and we want: to force their usage, despite of
		// model settings, or the model ask for favorites.
		if (forceUseFavorites || room.equals(HomeBehaviourRoom.FAVORITES)
				|| room.equals(HomeBehaviourRoom.FAVORITES_ONLY_FREE)) {
			if (!set.isEmpty()) {

				// FAV: check occupancy of ALL favs
				ArrayList<String> array = new ArrayList<String>(set.size());

				addAllFavoriteToCollection(array, true);

				// if we want only free favorites.
				boolean onlyFree = room.equals(HomeBehaviourRoom.FAVORITES_ONLY_FREE);
				return new FRRequestDetails(period, onlyFree, array, false, true, false, null, mModel.getGroupAccess());
			} else {
				u.logV("no favorites in model: going for any free room");
				room = HomeBehaviourRoom.ANYFREEROOM;
			}
		}

		if (room.equals(HomeBehaviourRoom.LASTREQUEST)) {
			// this feature has been disabled => going from default
			u.logD("last request is not operational now");
			room = HomeBehaviourRoom.ANYFREEROOM;
		}

		if (!room.equals(HomeBehaviourRoom.ANYFREEROOM)) {
			u.logE("unknown room behavior: ");
			u.logE(room.name());
			u.logE("going for any free room");
		}
		// any free room behavior
		return new FRRequestDetails(period, true, new ArrayList<String>(1), true, false, false, null,
				mModel.getGroupAccess());
	}

	/**
	 * Add all the favorites FRRoom to the collection. The collection will be
	 * cleared prior to any adding.
	 * 
	 * @param collection
	 *            collection in which you want the favorites to be added.
	 * 
	 * @param addOnlyUID
	 *            true to add UID, false to add fully FRRoom object.
	 */
	protected void addAllFavoriteToCollection(Collection collection, boolean addOnlyUID) {
		collection.clear();
		OrderMapListFew<String, List<FRRoom>, FRRoom> set = mModel.getFavorites();
		Iterator<String> iter = set.keySetOrdered().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Iterator<FRRoom> iter2 = set.get(key).iterator();
			while (iter2.hasNext()) {
				FRRoom mRoom = iter2.next();

				if (addOnlyUID) {
					collection.add(mRoom.getUid());
				} else {
					collection.add(mRoom);
				}
			}
		}
	}
}
