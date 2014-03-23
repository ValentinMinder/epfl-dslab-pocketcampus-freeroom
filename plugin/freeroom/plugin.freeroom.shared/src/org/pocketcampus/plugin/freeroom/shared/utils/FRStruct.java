package org.pocketcampus.plugin.freeroom.shared.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pocketcampus.plugin.freeroom.shared.FRRoom;

/**
 * This class is intended for useful transformation that may be used by both
 * server and client.
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
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
}
