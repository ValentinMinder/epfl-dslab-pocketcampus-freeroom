package org.pocketcampus.plugin.freeroom.data;

import java.util.Calendar;

/**
 * Provides method to be called anytime, the purpose of this class is tell
 * whether an update is necessary or not.
 * 
 * @author Team Freeroom
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class AutoUpdate {
	private int dayOfMonth;

	public AutoUpdate() {
		Calendar mCalendar = Calendar.getInstance();
		dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Check if an update is required
	 * 
	 * @return true if an update is necessary, false otherwise
	 */
	
	public boolean checkUpdate() {
		Calendar mCalendar = Calendar.getInstance();
		int today = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		if (today != dayOfMonth) {
			// launch update
			this.dayOfMonth = today;
			return true;
		}

		return false;
	}

}
