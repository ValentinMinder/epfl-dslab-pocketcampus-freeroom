package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Collection;
import java.util.Iterator;

import org.pocketcampus.plugin.freeroom.R;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;
import android.util.Log;

/**
 * <code>FRUtilsClient</code> give some other utility method for the client,
 * that have nothing to do in UI and View classes, and that are not
 * time-related.
 * <p>
 * None of them are useful to the server, so that's why the are not shared.
 * <p>
 * Most of them are used at only one place and are very specific. Be careful
 * when reusing it if you're not sure what it does exactly.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class FRUtilsClient {
	/**
	 * Holder of context (useful for getString).
	 */
	private Context context;
	/**
	 * Class name for logs printing.
	 */
	private String className;
	/**
	 * True if you want debug logs to be printed.
	 */
	private boolean debug = true;
	/**
	 * True if you want verbose logs to be printed.
	 */
	private boolean verbose = true;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            context of the application.
	 */
	public FRUtilsClient(Context context) {
		this.context = context;
		className = this.context.getClass().getSimpleName();
	}

	/**
	 * Logs a given message using Log.V (VERBOSE MODE), if verbose boolean is
	 * true. Otherwise, does nothing.
	 * <p>
	 * Don't forget that String parameters are first constructed by a
	 * StringBuilder. To avoid unnecessary usage of memory, try to have only 1
	 * element/token in your message.
	 * 
	 * @param msg
	 *            message to log.
	 */
	public void logV(String msg) {
		if (verbose) {
			Log.v(className, msg);
		}
	}

	/**
	 * Logs a given message using Log.d (DEBUG MODE), if debug boolean is true.
	 * Otherwise, does nothing.
	 * <p>
	 * Don't forget that String parameters are first constructed by a
	 * StringBuilder. To avoid unnecessary usage of memory, try to have only 1
	 * element/token in your message.
	 * 
	 * @param msg
	 *            message to log.
	 */
	public void logD(String msg) {
		if (debug) {
			Log.d(className, msg);
		}
	}

	/**
	 * Logs a given message using Log.E (ERROR MODE)
	 * 
	 * @param msg
	 *            message to log.
	 */
	public void logE(String msg) {
		Log.e(className, msg);
	}

	/**
	 * Generates the share text summary displayed when wanting to share a
	 * location and time, with an personalized optional message.
	 * 
	 * @param mPeriod
	 *            period to share.
	 * @param mRoom
	 *            location to share.
	 * @param toShare
	 *            personalized text to share.
	 * @return a string summarizing these informations.
	 */
	public String wantToShare(FRPeriod mPeriod, FRRoom mRoom, String toShare) {
		// TODO: in case of "now" request (nextPeriodValid is now), just put
		// "i am, now, " instead of
		// time
		StringBuilder textBuilder = new StringBuilder(100);
		textBuilder.append(context.getString(R.string.freeroom_share_iwillbe));
		textBuilder.append(" ");
		textBuilder.append(context.getString(R.string.freeroom_share_in_room));
		textBuilder.append(" ");
		if (mRoom.isSetDoorCodeAlias()) {
			textBuilder.append(mRoom.getDoorCodeAlias() + " ("
					+ mRoom.getDoorCode() + ")");
		} else {
			textBuilder.append(mRoom.getDoorCode());
		}
		textBuilder.append(", ");

		// TODO: which period to use ? in case of specified in request, we
		// should use the personalized period
		textBuilder.append(FRTimesClient.getInstance()
				.formatFullDateFullTimePeriod(mPeriod));
		textBuilder.append(". ");
		textBuilder.append(context
				.getString(R.string.freeroom_share_please_come));
		if (toShare.length() != 0) {
			textBuilder.append(context
					.getString(R.string.freeroom_share_working_on));
			textBuilder.append(toShare);
		}
		return textBuilder.toString();
	}

	/**
	 * Converts a FRRoom to a String of only major properties, in order to
	 * display them. It includes name (with alias), type, capacity, surface and
	 * UID.
	 * <p>
	 * TODO: this method may be changed
	 * 
	 * @param mFrRoom
	 * @return
	 */
	public String getInfoFRRoom(FRRoom mFrRoom) {
		StringBuilder builder = new StringBuilder(50);
		if (mFrRoom.isSetDoorCode()) {
			if (mFrRoom.isSetDoorCodeAlias()) {
				builder.append(mFrRoom.getDoorCode() + " (alias: "
						+ mFrRoom.getDoorCodeAlias() + ")");
			} else {
				builder.append(mFrRoom.getDoorCode());
			}
		}
		if (mFrRoom.isSetTypeFR() || mFrRoom.isSetTypeEN()) {
			builder.append(" / "
					+ context.getString(R.string.freeroom_dialog_info_type)
					+ ": ");
			if (mFrRoom.isSetTypeFR()) {
				builder.append(mFrRoom.getTypeFR());
			}
			if (mFrRoom.isSetTypeFR() && mFrRoom.isSetTypeEN()) {
				builder.append(" / ");
			}
			if (mFrRoom.isSetTypeFR()) {
				builder.append(mFrRoom.getTypeEN());
			}
		}
		if (mFrRoom.isSetCapacity()) {
			builder.append(" / "
					+ context.getString(R.string.freeroom_dialog_info_capacity)
					+ ": " + mFrRoom.getCapacity() + " "
					+ context.getString(R.string.freeroom_dialog_info_places));
		}
		if (mFrRoom.isSetSurface()) {
			builder.append(" / "
					+ context.getString(R.string.freeroom_dialog_info_surface)
					+ ": " + mFrRoom.getSurface() + " "
					+ context.getString(R.string.freeroom_dialog_info_sqm));
		}
		// TODO: for production, remove UID (it's useful for debugging for the
		// moment)
		boolean printUID = false;
		if (mFrRoom.isSetUid() && printUID) {
			builder.append(" / "
					+ context.getString(R.string.freeroom_dialog_info_uniqID)
					+ ": " + getUIDString(mFrRoom));
		}
		return builder.toString();
	}

	/**
	 * Return the uniq UID as a String, formatted like be 1201XXUID, with XX
	 * filled with 0 such that it has 10 digit, he prefix "1201" indicates that
	 * it's a EPFL room (not a phone, a computer, ...)
	 * 
	 * @param mFrRoom
	 * @return
	 */
	public String getUIDString(FRRoom mFrRoom) {
		String communUID = "1201";
		String roomUID = mFrRoom.getUid();
		for (int i = roomUID.length() + 1; i <= 6; i++) {
			communUID += "0";
		}
		communUID += roomUID;
		return communUID;
	}

	/**
	 * Summarize as a String the content of a collection of rooms, with default
	 * prefix and limit.
	 * 
	 * @param collec
	 *            a collection of FRRoom.
	 * @return a string summary of the collection.
	 */
	public String getSummaryTextFromCollection(Collection<FRRoom> collec) {
		int limit = 50;
		return getSummaryTextFromCollection(
				collec,
				context.getString(R.string.freeroom_check_occupancy_search_text_selected_rooms),
				limit);
	}

	/**
	 * Summarize as a String the content of a collection of rooms, with default
	 * prefix and limit.
	 * 
	 * @param collec
	 *            a collection of FRRoom.
	 * @param prefix
	 *            the prefix to display before the collection (can be left empty
	 *            or null for no display)
	 * @param limit
	 *            maximal length to return (negative value: no limit).
	 * @return a string summary of the collection, starting by the prefix and
	 *         limited in size.
	 */
	public String getSummaryTextFromCollection(Collection<FRRoom> collec,
			String prefix, int limit) {
		if (limit < 0) {
			limit = Integer.MAX_VALUE;
		}
		if (prefix == null) {
			prefix = "";
		}
		Iterator<FRRoom> iter = collec.iterator();
		StringBuffer buffer = new StringBuffer(Math.min(prefix.length()
				+ collec.size() * 7, limit) + 10);
		FRRoom room = null;
		buffer.append(prefix);
		boolean empty = !iter.hasNext();
		while (iter.hasNext() && buffer.length() < limit) {
			room = iter.next();
			String name = room.getDoorCode();
			if (room.isSetDoorCodeAlias()) {
				name = room.getDoorCodeAlias();
			}
			buffer.append(name + ", ");
		}
		buffer.setLength(Math.max(0, buffer.length() - 2));
		if (iter.hasNext()) {
			buffer.append(", ...");
		}
		String result = "";
		if (empty) {
			result = context
					.getString(R.string.freeroom_check_occupancy_search_text_no_selected_rooms);
		} else {
			result = buffer.toString();
		}
		return result;
	}

	/**
	 * Checks if a email is an epfl well-formatted adress.
	 * <p>
	 * No spaces, only alphanum + "-" + "." for username, with end @epfl.ch
	 * 
	 * @param email
	 *            the email to check.
	 * @return if the check is succesful.
	 */
	public boolean validEmail(String email) {
		return email.matches("^[a-zA-Z0-9-]+[.][a-zA-Z0-9-]+@epfl[.]ch$");
	}

	public final int MIN_QUERY_LENGTH = 2;

	public boolean validQuery(String query) {
		return query.trim().length() >= MIN_QUERY_LENGTH;
	}

	public static String formatRoom(FRRoom room) {
		if (room.isSetDoorCodeAlias() && room.getDoorCodeAlias() != null) {
			return room.getDoorCodeAlias();
		}
		return room.getDoorCode();
	}

}
