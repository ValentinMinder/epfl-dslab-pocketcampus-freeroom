package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.List;

import org.pocketcampus.plugin.freeroom.android.views.FreeRoomHomeView;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

import android.content.Context;

/**
 * <code>FRRequestDetails</code> is an extension of the legacy shared
 * <code>FRRequest</code> that is able to store the state of the search UI, in
 * order to store them for history and reuse them in the future.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class FRRequestDetails extends FRRequest {
	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = 1097781363203978779L;
	/**
	 * Time of generation of the object, to check the validity.
	 */
	private final long time = System.currentTimeMillis();

	private FRRequest req = null;
	private boolean any = false;
	private boolean fav = true;
	private boolean user = false;
	private SetArrayList<FRRoom> uidNonFav = null;

	// TODO change group accordingly, set to 1 by default and for testing
	// purpose
	public FRRequestDetails(FRPeriod period, boolean onlyFreeRooms,
			List<String> uidList, boolean any, boolean fav, boolean user,
			SetArrayList<FRRoom> uidNonFav, int userGroup) {
		super(period, onlyFreeRooms, uidList, userGroup);
		this.any = any;
		this.fav = fav;
		this.user = user;
		this.uidNonFav = uidNonFav;
	}

	public boolean isAny() {
		return any;
	}

	public void setAny(boolean any) {
		this.any = any;
	}

	public boolean isFav() {
		return fav;
	}

	public void setFav(boolean fav) {
		this.fav = fav;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public SetArrayList<FRRoom> getUidNonFav() {
		return uidNonFav;
	}

	public void setUidNonFav(SetArrayList<FRRoom> uidNonFav) {
		this.uidNonFav = uidNonFav;
	}

	public FRRequestDetails(FRPeriod period, boolean onlyFreeRooms,
			List<String> uidList, int userGroup) {
		super(period, onlyFreeRooms, uidList, userGroup);
		// TODO Auto-generated constructor stub
	}

	public FRRequestDetails(FRRequest other) {
		super(other);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Checks if the request is outdated. It checks if it has been generated
	 * before the given timeout expires or not.
	 * 
	 * @param timeout
	 *            the wanted expiration timeout.
	 * @return true if outdated, false otherwise.
	 */
	public boolean isOutDated(long timeout) {
		return (time - System.currentTimeMillis()) > timeout;
	}

}
