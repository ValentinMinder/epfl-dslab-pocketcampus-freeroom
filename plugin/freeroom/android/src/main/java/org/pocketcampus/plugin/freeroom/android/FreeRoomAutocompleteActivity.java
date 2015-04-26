package org.pocketcampus.plugin.freeroom.android;

import java.util.Iterator;
import java.util.List;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.android.utils.FRUtilsClient;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.os.Bundle;
import android.view.View;

public abstract class FreeRoomAutocompleteActivity extends FreeRoomAbstractView implements IFreeRoomView {

	protected abstract void autoCompleteUpdatedClear();

	//
	/**
	 * AUTOCOMPLETE: Update the text message in autocomplete status text view
	 * (updating/up-to-date/error/...)
	 * 
	 * @param text
	 *            the new message to display.
	 */
	protected abstract void autoCompleteUpdateMessage(CharSequence text);

	protected abstract void addAutocompletedRoom(FRRoom room);

	protected abstract void autocompleteCheckEmptyResult();

	protected abstract void autocompleteFinished();

	public abstract void autoCompleteCancel();

	// AUTOCOMPLETE - shared for add room and add favorites

	/**
	 * MVC METHOD/AUTOCOMPLETE: Override
	 * {@link IFreeRoomView#autoCompleteUpdated()} and notifies the autocomplete
	 * have been updated. Results in {@link #addFavorites} AND
	 * {@link #addSearchRoom} are updated, as they share teh SAME autocomplete
	 * (they cannot be displayed at the same time).
	 */
	@Override
	public void autoCompleteUpdated() {
		autoCompleteUpdatedClear();

		boolean emptyResult = (mModel.getAutoComplete().values().size() == 0);
		if (emptyResult) {
			autoCompleteUpdateMessage(getString(R.string.freeroom_dialog_add_autocomplete_noresult));
		} else {
			autoCompleteUpdateMessage(getString(R.string.freeroom_dialog_add_autocomplete_uptodate));
		}

		// FIXME: adapt to use the new version of autocomplete mapped by
		// building
		Iterator<List<FRRoom>> iter = mModel.getAutoComplete().values().iterator();
		while (iter.hasNext()) {
			List<FRRoom> list = iter.next();
			Iterator<FRRoom> iterroom = list.iterator();
			while (iterroom.hasNext()) {
				FRRoom room = iterroom.next();
				// rooms that are already selected are not displayed...
				addAutocompletedRoom(room);
			}
		}

		/*
		 * If there was a non-empty result but all rooms got rejected, we
		 * display "no more" instead of "up-to-date". Not useful for favorites
		 * as no room is rejected.
		 */
		if (!emptyResult) {
			autocompleteCheckEmptyResult();
		}
		// FIXME
		// if (searchByIntentUriTriggered) {
		// searchByIntentUriMakeRequest(addSearchRoomAutoCompleteArrayListFRRoom);
		// }
		autocompleteFinished();
	}

	/**
	 * AUTOCOMPLETE: checks if a query is valid, and if it is, it will try
	 * remove the soft keyboard.
	 * 
	 * @param query
	 *            the query to check
	 * @param view
	 *            the view from which the soft keyboard should be hidden.
	 */
	protected void autoCompleteValidateQuery(String query, View view) {
		if (u.validQuery(query)) {
			commonDismissSoftKeyBoard(view);
			FRAutoCompleteRequest request = new FRAutoCompleteRequest(query, mModel.getGroupAccess());
			mController.autoCompleteBuilding(this, request);
		} else {
			autoCompleteCancel();
		}
		// activation of hidden settings (not wanted visible in production, but
		// kept for compatibility reason. DO NOT DELETE)
		if (query.matches("[Dd][Aa][Yy]")) {
			mModel.setAdvancedTime(!mModel.getAdvancedTime());
			errorDialogShowMessage("Advanced time activated:" + mModel.getAdvancedTime());
		}
		// devTestActivateDebug(query);
	}

	@Override
	public void anyError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void networkErrorHappened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void occupancyResultsUpdated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshOccupancies() {
		// TODO Auto-generated method stub

	}

	@Override
	public void workingMessageUpdated() {
		// TODO Auto-generated method stub

	}
}
