package org.pocketcampus.plugin.freeroom.shared.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pocketcampus.plugin.freeroom.shared.FRRoom;

/**
 * This class is intended for useful transformation that may be used by both
 * server and client.
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class FRStruct {

	public static List<String> getListUID(List<FRRoom> list) {
		Iterator<FRRoom> iter = list.iterator();
		List<String> listUID = new ArrayList<String>();
		while (iter.hasNext()) {
			FRRoom frRoom = iter.next();
			listUID.add(frRoom.getUid());
		}
		return listUID;
	}

	/**
	 * Removes safely the first char of a String.
	 * <p>
	 * Used to extract the "/" in path of URI usage.
	 * 
	 * @param intentUriData
	 *            the original string
	 * @return the same string without the first char, an empty string if the
	 *         original was 1 or 0 chars long.
	 */
	public static String removeFirstCharSafely(String intentUriData) {
		int length = intentUriData.length();
		String query = intentUriData.substring(Math.min(length, 1), length);
		return query;
	}
}
