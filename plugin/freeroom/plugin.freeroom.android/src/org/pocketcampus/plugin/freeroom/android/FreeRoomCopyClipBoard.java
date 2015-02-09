package org.pocketcampus.plugin.freeroom.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * <code>FreeRoomCopyClipBoard</code> is a <code>View</code> that display
 * NOTHING and only handles an <code>Intent</code> in order to display a
 * "Copy to clipboard" button in share menus in Android. As it suggests, it
 * actually copies the text given in the Intent into the clipboard, and confirm
 * it by a Toast.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FreeRoomCopyClipBoard extends FreeRoomAbstractView implements
		IFreeRoomView {

	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FreeRoomController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/*".equals(type)) {
				handleSendText(intent); // Handle text being sent
			} else {
				// handling other types being sent
				// (THESE ARE NOT ALLOWED BY MANIFEST, WE DONT CARE)
			}
		} else {
			// Handle other intents, such as being started from the home screen
			// (THESE ARE NOT ALLOWED BY MANIFEST, WE DONT CARE)
		}
		this.finish(); // KILL THE VIEW
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void initializeView() {
		// WE DO NOTHING!
	}

	/**
	 * Actually copies the given text into the clipboard.
	 * 
	 * @param intent
	 *            the Intent coming form the share action.
	 */
	private void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		if (sharedText != null) {
			// this method may be depracated since Android level 11, but still
			// usable.
			// if it becomes unavailable in future release, and still want to
			// support before 11 version, please use the code under.
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(sharedText);

			// int sdk = android.os.Build.VERSION.SDK_INT;
			// if (sdk > android.os.Build.VERSION_CODES.FROYO) {
			// android.content.ClipboardManager clipboard =
			// (android.content.ClipboardManager)
			// getSystemService(Context.CLIPBOARD_SERVICE);
			// android.content.ClipData clip = android.content.ClipData
			// .newPlainText("text label", "text to clip");
			// clipboard.setPrimaryClip(clip);
			// } else {
			// android.text.ClipboardManager clipboard =
			// (android.text.ClipboardManager)
			// getSystemService(Context.CLIPBOARD_SERVICE);
			// clipboard.setText(sharedText);
			// }

			printAtTheEnd(true, sharedText, null);
		}
	}

	private void printAtTheEnd(boolean success, String text, String message) {
		String userMsg = "";
		if (success) {
			userMsg = getString(R.string.freeroom_share_copied_ClipBoard);
			Log.v(this.getClass().getName(), "copied to clipboard : " + text);
		} else {
			userMsg = getString(R.string.freeroom_share_copied_ClipBoard_error);
			Log.e(this.getClass().getName(), "No copied to clipboard : " + text
					+ ". Log message:" + message);
		}

		Toast.makeText(this, userMsg + ": " + text, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void anyError() {
		// WE DO NOTHING!
	}
	
	@Override
	public void autoCompleteLaunch() {
		// WE DO NOTHING!
	}

	@Override
	public void autoCompleteUpdated() {
		// WE DO NOTHING!
	}

	@Override
	public void occupancyResultsUpdated() {
		// WE DO NOTHING!
	}

	@Override
	public void refreshOccupancies() {
		// WE DO NOTHING!
	}

	@Override
	public void workingMessageUpdated() {
		// WE DO NOTHING!
	}

	@Override
	protected String screenName() {
		return "freeroom/clipboard";
	}
}