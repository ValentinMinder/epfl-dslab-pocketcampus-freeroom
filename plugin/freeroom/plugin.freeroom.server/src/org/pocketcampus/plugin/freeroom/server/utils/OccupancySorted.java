package org.pocketcampus.plugin.freeroom.server.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pocketcampus.plugin.freeroom.server.FreeRoomServiceImpl.OCCUPANCY_TYPE;
import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

/**
 * This class is used to sort, fill and then create an occupancy based on
 * ActualOccupation for a specific room.
 **/

public class OccupancySorted {

	private final long MARGIN_FOR_ERROR = 60 * 15 * 1000;

	private ArrayList<ActualOccupation> mActualOccupations;
	private FRRoom room;
	private long timestampStart;
	private long timestampEnd;
	private boolean isAtLeastFreeOnce;
	private boolean isAtLeastOccupiedOnce;
	private double worstRatio;

	public OccupancySorted(FRRoom room) {
		this.mActualOccupations = new ArrayList<ActualOccupation>();
		this.room = room;
		isAtLeastFreeOnce = false;
		isAtLeastOccupiedOnce = true;
		worstRatio = 0.0;
		timestampStart = 0;
		timestampEnd = 0;
	}

	public void addActualOccupation(ActualOccupation occ) {
		mActualOccupations.add(occ);
		long start = occ.getPeriod().getTimeStampStart();
		long end = occ.getPeriod().getTimeStampEnd();

		if (timestampStart == 0) {
			timestampStart = start;
			timestampEnd = end;
		} else {
			if (start < timestampStart) {
				timestampStart = start;
			}

			if (end > timestampEnd) {
				timestampEnd = end;
			}
		}

	}

	/**
	 * This method's job is to sort the data in the mActualOccupations ArrayList
	 * by timestamp start and type of the occupation. See doc for
	 * equalTimestamp() in the comparator below.
	 */
	private void sortByTimestampStart() {
		Collections.sort(mActualOccupations,
				new Comparator<ActualOccupation>() {

					@Override
					public int compare(ActualOccupation o1, ActualOccupation o2) {
						long thisStart = o1.getPeriod().getTimeStampStart();
						long toCompareStart = o2.getPeriod()
								.getTimeStampStart();
						boolean thisUser = o1.isAvailable();
						boolean toCompareUser = o2.isAvailable();

						return compareTimestamp(thisStart, toCompareStart,
								equalTimestamp(thisUser, toCompareUser));

					}

					/**
					 * This method is used in case of equalities. If both
					 * timestamp are equal, we want to put the occupancy for the
					 * room before the user. (For practical reasons in the
					 * fillGaps())
					 * 
					 * @param thisUser
					 *            true if user occupancy
					 * @param toCompareUser
					 *            true if user occupancy
					 * @return 0 if both have same type, 1 if the first is a
					 *         user occupancy and not the second, -1 in the
					 *         opposite case.
					 */
					private int equalTimestamp(boolean thisUser,
							boolean toCompareUser) {
						if (thisUser && toCompareUser) {
							return 0;
						} else if (thisUser && !toCompareUser) {
							return 1;
						} else if (!thisUser && toCompareUser) {
							return -1;
						} else {
							return 0;
						}
					}

					private int compareTimestamp(long t1, long t2, int equal) {
						if (t1 < t2) {
							return -1;
						} else if (t1 == t2) {
							return equal;
						}
						return 1;
					}
				});
	}

	/**
	 * This method fills the gaps between ActualOccupation in the list of this
	 * object, it reduces the period of an user occupancy that overlap some room
	 * occupancy. It also fill the blank time with ActualOccupation in order to
	 * have a contiguous list of period (with an error of MARGIN_FOR_ERROR)
	 */
	private void fillGaps() {
		long tsPerRoom = timestampStart;
		int index = 0;
		boolean previousIsRoom = false;
		long lastEnd = 0;
		for (ActualOccupation actual : mActualOccupations) {
			long tsStart = Math.max(tsPerRoom, actual.getPeriod()
					.getTimeStampStart());
			long tsEnd = Math.min(timestampEnd, actual.getPeriod()
					.getTimeStampEnd());
			
			if (previousIsRoom && tsStart < lastEnd) {
				//resize the period of this user occupancy
				tsStart = lastEnd;
				FRPeriod newPeriod = new FRPeriod(tsStart, tsEnd, false);
				actual.setPeriod(newPeriod);
			}
			
			if (tsStart - tsPerRoom > MARGIN_FOR_ERROR) {
				// We got a free period of time !
				ArrayList<ActualOccupation> subDivised = cutInStepsPeriod(
						tsPerRoom, tsStart);
				mActualOccupations.addAll(index + 1, subDivised);
				index += subDivised.size();
				isAtLeastFreeOnce = true;
				previousIsRoom = false;
			}
			
			tsPerRoom = tsEnd;
			index++;
			
			previousIsRoom = !actual.isAvailable();
			lastEnd = actual.getPeriod().getTimeStampEnd();
			
			double ratio = actual.getRatioOccupation();
			
			if (ratio > worstRatio) {
				worstRatio = ratio;
			}
		}
	}

	private ArrayList<ActualOccupation> cutInStepsPeriod(long start, long end) {
		ArrayList<ActualOccupation> result = new ArrayList<ActualOccupation>();
		long hourSharpBefore = Utils.roundHourBefore(start);
		long numberHours = Utils.determineNumberHour(start, end);

		for (int i = 0; i < numberHours; ++i) {
			FRPeriod period = new FRPeriod(hourSharpBefore + i
					* Utils.ONE_HOUR_MS, hourSharpBefore + (i + 1)
					* Utils.ONE_HOUR_MS, false);
			ActualOccupation mAccOcc = new ActualOccupation(period, true);
			mAccOcc.setProbableOccupation(0);
			mAccOcc.setRatioOccupation(0.0);
			result.add(mAccOcc);
		}

		return result;
	}

	/**
	 * Create an Occupancy object, set its properties. The resulting object is
	 * suitable for a reply to the client.
	 * 
	 * @return The occupancy filled and adapted.
	 */
	public Occupancy getOccupancy() {
		sortByTimestampStart();
		fillGaps();
		Occupancy mOccupancy = new Occupancy(room, mActualOccupations,
				isAtLeastOccupiedOnce, isAtLeastFreeOnce);
		mOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);
		return mOccupancy;
	}

}
