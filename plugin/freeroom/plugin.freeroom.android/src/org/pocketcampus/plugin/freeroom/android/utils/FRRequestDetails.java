package org.pocketcampus.plugin.freeroom.android.utils;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.pocketcampus.plugin.freeroom.shared.FROccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;

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
public class FRRequestDetails extends FROccupancyRequest {
	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = 1097781363203978779L;
	/**
	 * Time of generation of the object, to check the validity.
	 */
	private final long time = System.currentTimeMillis();

	private boolean any = false;
	private boolean fav = true;
	private boolean user = false;
	private Set<FRRoom> uidNonFav = null;
	private String userLanguage = "default";

	public FRRequestDetails(FRPeriod period, boolean onlyFreeRooms,
			List<String> uidList, boolean any, boolean fav, boolean user,
			Set<FRRoom> uidNonFav, int userGroup) {
		super(period, onlyFreeRooms, uidList, userGroup);
		this.any = any;
		this.fav = fav;
		this.user = user;
		this.uidNonFav = uidNonFav;
	}

	public void setULanguage(String language) {
		if (language != null) {
			this.userLanguage = language;
		}
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

	public Set<FRRoom> getUidNonFav() {
		return uidNonFav;
	}

	public void setUidNonFav(Set<FRRoom> uidNonFav) {
		this.uidNonFav = uidNonFav;
	}

	// TODO: bad constructors, see if there are really needed ??
	// public FRRequestDetails(FRPeriod period, boolean onlyFreeRooms,
	// List<String> uidList, int userGroup) {
	// super(period, onlyFreeRooms, uidList, userGroup);
	// // TODO Auto-generated constructor stub
	// }

	// public FRRequestDetails(FRRequest other) {
	// super(other);
	// // TODO Auto-generated constructor stub
	// }

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		// we dont check WHOLE super-type, only interested things!
		// int result = super.hashCode();
		int result = 1;
		// from super-type: we check only hours and is only free rooms.
		result = prime * result + (super.isOnlyFreeRooms() ? 1231 : 1237);
		FRPeriod period = this.getPeriod();

		Calendar calendar_s = Calendar.getInstance();
		Calendar calendar_e = Calendar.getInstance();

		calendar_s.setTimeInMillis(period.getTimeStampStart());
		calendar_e.setTimeInMillis(period.getTimeStampEnd());
		result = prime * result + calendar_s.get(Calendar.HOUR_OF_DAY);
		result = prime * result + calendar_e.get(Calendar.HOUR_OF_DAY);

		// from legacy generated hashcode
		result = prime * result + (any ? 1231 : 1237);
		result = prime * result + (fav ? 1231 : 1237);
		result = prime * result
				+ ((uidNonFav == null) ? 0 : uidNonFav.hashCode());
		result = prime * result + (user ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		// removed from generated equals: we dont check at WHOLE super-type
		// if (!super.equals(obj)) {
		// return false;
		// }
		// generated equals
		if (getClass() != obj.getClass()) {
			return false;
		}
		FRRequestDetails other = (FRRequestDetails) obj;
		// added to check super-type, only particular info
		// added to generated equals
		if (other.isOnlyFreeRooms() != this.isOnlyFreeRooms()) {
			return false;
		}
		FRPeriod period_1 = other.getPeriod();
		FRPeriod period_2 = this.getPeriod();
		Calendar calendar_1_s = Calendar.getInstance();
		Calendar calendar_1_e = Calendar.getInstance();
		Calendar calendar_2_s = Calendar.getInstance();
		Calendar calendar_2_e = Calendar.getInstance();

		calendar_1_s.setTimeInMillis(period_1.getTimeStampStart());
		calendar_1_e.setTimeInMillis(period_1.getTimeStampEnd());
		calendar_2_s.setTimeInMillis(period_2.getTimeStampStart());
		calendar_2_e.setTimeInMillis(period_2.getTimeStampEnd());

		if (calendar_1_s.get(Calendar.HOUR_OF_DAY) != calendar_2_s
				.get(Calendar.HOUR_OF_DAY)) {
			return false;
		}
		if (calendar_1_e.get(Calendar.HOUR_OF_DAY) != calendar_2_e
				.get(Calendar.HOUR_OF_DAY)) {
			return false;
		}

		// generated equals
		if (any != other.any) {
			return false;
		}
		if (fav != other.fav) {
			return false;
		}
		if (uidNonFav == null) {
			if (other.uidNonFav != null) {
				return false;
			}
		} else if (!uidNonFav.equals(other.uidNonFav)) {
			return false;
		}
		if (user != other.user) {
			return false;
		}
		return true;
	}
}
