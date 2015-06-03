package org.pocketcampus.plugin.freeroom.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.ui.element.InputBarElement;
import org.pocketcampus.platform.android.ui.element.OnKeyPressedListener;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.adapter.ExpandableListViewFavoriteAdapter;
import org.pocketcampus.plugin.freeroom.android.adapter.FRRoomSuggestionArrayAdapter;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class FreeRoomFavoritesActivity extends FreeRoomAutocompleteActivity {

	/* ADD FAVORITES */

	/**
	 * {@link #addFavorites}: AlertDialog that holds the {@link #addFavorites}
	 * dialog.
	 */
	private AlertDialog addFavorites;
	/**
	 * {@link #addFavorites}: View that holds the {@link #addFavorites} dialog
	 * content, defined in xml in layout folder.
	 */
	private View addFavoritesView;
	/**
	 * {@link #addFavorites}: TextView for autocomplete status for adding
	 * favorites.
	 */
	private TextView addFavoritesAutoCompleteStatus;

	/**
	 * {@link #addFavorites}: listview to display autocomplete results.
	 */
	private ListView addFavoritesAutoCompleteListView;
	/**
	 * {@link #addFavorites}: list of suggestion room by autocomplete.
	 */
	private List<FRRoom> addFavoritesAutoCompleteArrayListFRRoom;

	/**
	 * {@link #addFavorites}:The input bar to make the search
	 */
	private InputBarElement addFavoritesAutoCompleteInputBarElement;
	/**
	 * {@link #addFavorites}:Adapter for the <code>mListView</code>
	 */
	private FRRoomSuggestionArrayAdapter<FRRoom> mAddFavoritesAdapter;

	// FAVORITES
	/**
	 * {@link #favorites}: adapter for the favorites ListView.
	 */
	private ExpandableListViewFavoriteAdapter<FRRoom> favoritesListAdapter;

	/**
	 * {@link #favorites}: Updates the favorites summary after something has
	 * changed.
	 * <p>
	 * Display the number of favorites, or a small message if no favorites.
	 */
	public void favoritesUpdateSummary() {
		int count = favoritesListAdapter.getGroupCount();
		String text = "";
		if (count == 0) {
			text = getString(R.string.freeroom_dialog_fav_status_no);
		} else {
			text = getString(R.string.freeroom_dialog_fav_status_fav);
			int total = 0;
			for (int i = 0; i < count; i++) {
				total += favoritesListAdapter.getChildrenCount(i);
			}
			text += getResources().getQuantityString(R.plurals.freeroom_results_room_header, total, total);
		}
		setActionBarTitle(text);
	}

	/**
	 * {@link #addFavorites}: inits the {@link #addFavorites} to add new
	 * favorites (and alos remove them!)
	 */
	private void initAddFavoritesDialog() {
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_add_favorites_title));
		builder.setIcon(R.drawable.freeroom_ic_action_new_white);

		// Get the AlertDialog from create()
		addFavorites = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = addFavorites.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.FILL_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		addFavorites.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		addFavorites.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		addFavorites.getWindow().setAttributes(lp);

		addFavoritesView = getLayoutInflater().inflate(R.layout.freeroom_layout_dialog_add_favorites_room, null);

		addFavorites.setView(addFavoritesView);

		addFavoritesAutoCompleteStatus = (TextView) addFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_room_status);

		addFavorites.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				trackEvent("AddFavorite", null);
			}
		});

		addFavorites.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				favoritesListAdapter.notifyDataSetChanged();

				// WARNING: BUG FIX
				// when the favorites are modified (removed) and the parent
				// group is opened, this cause a NullPointerException on the
				// main favorites window.
				ExpandableListView lv = (ExpandableListView) findViewById(R.id.freeroom_layout_dialog_fav_list);
				for (int i = favoritesListAdapter.getGroupCount() - 1; i >= 0; i--) {
					lv.collapseGroup(i);
				}
				favoritesUpdateSummary();
				// autoCompleteCancel();
				// addSearchRoomAutoCompleteInputBarElement.setInputText("");
				addFavoritesAutoCompleteInputBarElement.setInputText("");
			}
		});

		addFavoritesAutoCompleteArrayListFRRoom = new ArrayList<FRRoom>(50);

		addFavoritesUIConstructInputBar();
		LinearLayout ll = (LinearLayout) addFavoritesView
				.findViewById(R.id.freeroom_layout_dialog_add_favorites_layout_main);
		ll.addView(addFavoritesAutoCompleteInputBarElement);
		addFavoritesUICreateSuggestionsList();
	}

	/**
	 * {@link #addFavorites}:
	 */
	private void addFavoritesUIConstructInputBar() {
		final IFreeRoomView view = this;

		addFavoritesAutoCompleteInputBarElement = new InputBarElement(this, null,
				getString(R.string.freeroom_check_occupancy_search_inputbarhint));
		addFavoritesAutoCompleteInputBarElement.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		// click on magnify glass on the keyboard
		addFavoritesAutoCompleteInputBarElement.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String query = addFavoritesAutoCompleteInputBarElement.getInputText();
					// autoCompleteValidateQuery(query, v);
				}

				return true;
			}
		});

		// click on BUTTON magnify glass on the inputbar
		addFavoritesAutoCompleteInputBarElement.setOnButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = addFavoritesAutoCompleteInputBarElement.getInputText();
				// autoCompleteValidateQuery(query, v);
			}
		});

		mAddFavoritesAdapter = new FRRoomSuggestionArrayAdapter<FRRoom>(getApplicationContext(),
				R.layout.freeroom_layout_list_room_add_fav, R.id.freeroom_layout_list_room_add_fav,
				addFavoritesAutoCompleteArrayListFRRoom, mModel, true);

		addFavoritesAutoCompleteInputBarElement.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				addFavoritesAutoCompleteListView.setAdapter(mAddFavoritesAdapter);

				if (!u.validQuery(text)) {
					addFavoritesAutoCompleteInputBarElement.setButtonText(null);
					// autoCompleteCancel();
				} else {
					addFavoritesAutoCompleteInputBarElement.setButtonText("");
					// remove this if you don't want
					// automatic autocomplete
					// without pressing the button
					FRAutoCompleteRequest request = new FRAutoCompleteRequest(text, mModel.getGroupAccess());
					request.setUserLanguage(mModel.getUserLanguage());
					mController.autoCompleteBuilding(view, request);
				}
			}
		});
	}

	/**
	 * {@link #addFavorites}: Initialize the autocomplete suggestion list
	 */
	private void addFavoritesUICreateSuggestionsList() {
		addFavoritesAutoCompleteListView = new ListView(this);
		addFavoritesAutoCompleteInputBarElement.addView(addFavoritesAutoCompleteListView);

		addFavoritesAutoCompleteListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				// when an item is clicked, the keyboard is dimissed
				commonDismissSoftKeyBoard(view);
				FRRoom room = addFavoritesAutoCompleteArrayListFRRoom.get(pos);
				if (mModel.isFavorite(room)) {
					mModel.removeFavorite(room);
				} else {
					mModel.addFavorite(room);
				}

				// WE DONT REMOVE the text in the input bar
				// INTENTIONNALLY: user may want to select multiple
				// rooms in the same building
				autoCompleteUpdated();
			}
		});
		addFavoritesAutoCompleteListView.setAdapter(mAddFavoritesAdapter);
	}

	@Override
	protected void autoCompleteUpdatedClear() {
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		addFavoritesAutoCompleteArrayListFRRoom.clear();
	}

	/**
	 * AUTOCOMPLETE: Update the text message in autocomplete status text view
	 * (updating/up-to-date/error/...)
	 * 
	 * @param text
	 *            the new message to display.
	 */
	protected void autoCompleteUpdateMessage(CharSequence text) {
		addFavoritesAutoCompleteStatus.setText(text);
	}

	@Override
	protected void addAutocompletedRoom(FRRoom room) {
		addFavoritesAutoCompleteArrayListFRRoom.add(room);
	}

	@Override
	protected void autocompleteCheckEmptyResult() {

		if (addFavoritesAutoCompleteArrayListFRRoom.isEmpty()) {
			addFavoritesAutoCompleteStatus.setText(getString(R.string.freeroom_dialog_add_autocomplete_nomore));
		}
	}

	@Override
	protected void autocompleteFinished() {
		mAddFavoritesAdapter.notifyDataSetChanged();
	}

	/**
	 * MVC METHOD/AUTOCOMPLETE: Override
	 * {@link IFreeRoomView#autoCompleteLaunch()} and notify an autocomplete
	 * request have been launched, and that the user should way until it's
	 * completed.
	 */
	@Override
	public void autoCompleteLaunch() {
		addFavoritesAutoCompleteStatus.setText(getString(R.string.freeroom_dialog_add_autocomplete_updating));
	}

	/**
	 * AUTOCOMPLETE: To be called when autocomplete is not lauchable and ask the
	 * user to type in.
	 */
	public void autoCompleteCancel() {
		addFavoritesAutoCompleteArrayListFRRoom.clear();
		mAddFavoritesAdapter.notifyDataSetInvalidated();
		addFavoritesAutoCompleteStatus.setText(getString(R.string.freeroom_dialog_add_autocomplete_typein));
	}

	@Override
	public void initializeView() {
		setContentView(R.layout.freeroom_layout_dialog_fav);

		ExpandableListView lv = (ExpandableListView) findViewById(R.id.freeroom_layout_dialog_fav_list);
		favoritesListAdapter = new ExpandableListViewFavoriteAdapter<FRRoom>(this, mModel.getFavorites(), mModel, this);
		lv.setAdapter(favoritesListAdapter);
		favoritesListAdapter.notifyDataSetChanged();
		favoritesUpdateSummary();
		trackEvent("Favorites", null);

		Button tv = (Button) findViewById(R.id.freeroom_layout_fav_add);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addFavorites.show();
			}
		});

		Button bt = (Button) findViewById(R.id.freeroom_layout_fav_user_reset);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				warning.show();
			}
		});

		initAddFavoritesDialog();
		initWarningDialog();
	}

	// WARNING

	/**
	 * {@link #warning}: Dialog that holds the {@link #warning} Dialog (with two
	 * button: confirm/cancel).
	 */
	private AlertDialog warning;

	/**
	 * Inits the {@link #warning}.
	 * <p>
	 * CAUTION: so far, it has a SINGLE function: erase favorites !!!
	 */
	private void initWarningDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.freeroom_dialog_warn_title));
		builder.setMessage(getString(R.string.freeroom_dialog_warn_delete_fav_text));
		builder.setIcon(R.drawable.freeroom_ic_action_warning);
		builder.setPositiveButton(getString(R.string.freeroom_dialog_warn_confirm), new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mModel.resetFavorites();
				favoritesListAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton(getString(R.string.freeroom_dialog_warn_cancel), null);

		// Get the AlertDialog from create()
		warning = builder.create();

		// redefine paramaters to dim screen when displayed
		WindowManager.LayoutParams lp = warning.getWindow().getAttributes();
		lp.dimAmount = 0.60f;
		// these doesn't work
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		warning.getWindow().addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		warning.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		warning.getWindow().setAttributes(lp);

		warning.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				favoritesUpdateSummary();
				favoritesListAdapter.notifyDataSetChanged();
				trackEvent("Warning", null);
			}
		});
	}

	@Override
	protected String screenName() {
		return "/freeroom/favorites";
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		super.onDisplay(savedInstanceState, controller);
		initializeView();
	}
}
