package org.pocketcampus.plugin.freeroom.server.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.pocketcampus.plugin.freeroom.shared.ActualOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.Occupancy;

/**
 * This class is used to sort, fill and then create an occupancy based on
 * ActualOccupation for a specific room. Note that the timestamps given in each
 * ActualOccupation will have the seconds and milliseconds set to 0 for
 * practical purpose in the sort and its use (fillGaps()).
 **/

public class OccupancySorted {

	//TODO logger
	// TODO put all constants in Utils
	private final long MARGIN_FOR_ERROR = 60 * 15 * 1000;
	private final long MIN_PERIOD = 15 * 60 * 1000;

	private boolean onlyFreeRooms;
	private ArrayList<ActualOccupation> mActualOccupations;
	private FRRoom room;
	private long timestampStart;
	private long timestampEnd;
	private boolean isAtLeastFreeOnce;
	private boolean isAtLeastOccupiedOnce;
	private double worstRatio;

	public OccupancySorted(FRRoom room, long tsStart, long tsEnd,
			boolean onlyFree) {
		this.mActualOccupations = new ArrayList<ActualOccupation>();
		this.room = room;
		isAtLeastFreeOnce = false;
		isAtLeastOccupiedOnce = false;
		worstRatio = 0.0;
		timestampStart = tsStart;
		timestampEnd = tsEnd;
		onlyFreeRooms = onlyFree;
	}

	public void addActualOccupation(ActualOccupation occ) {
		FRPeriod period = occ.getPeriod();
		long start = Utils.roundSAndMSToZero(period.getTimeStampStart());
		long end = Utils.roundSAndMSToZero(period.getTimeStampEnd());

		if (start < timestampStart) {
			start = timestampStart;
		}

		if (end > timestampEnd) {
			end = timestampEnd;
		}

		if (occ.isAvailable() && end - start > Utils.ONE_HOUR_MS) {
			mActualOccupations.addAll(cutInStepsPeriod(start, end));
		} else {
			mActualOccupations.add(occ
					.setPeriod(new FRPeriod(start, end, false)));
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
					 * timestamps are equal, we want to put the occupancy for
					 * the room before the user. (For practical reasons in the
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
	// TODO if user from 10 to 11 and course from 10 30 to 11 !
	// TODO maybe not necessary to distinguish case in case of eq timestamps
	private void fillGaps() {
		ArrayList<ActualOccupation> resultList = new ArrayList<ActualOccupation>();
		long tsPerRoom = timestampStart;
		boolean previousIsRoom = false;
		long lastEnd = tsPerRoom;
		int countFree = 0;
		int countOccupied = 0;
		
		// TODO lastEnd, tsPerRoom same thing ?
		for (ActualOccupation actual : mActualOccupations) {
			long tsStart = Math.max(timestampStart, actual.getPeriod()
					.getTimeStampStart());

			// we want to add a room and the previous added occupation is a user
			// occupancy and this one end after the room occupancy starts ! it
			// has to be resized
			if (!actual.isAvailable() && !previousIsRoom && tsPerRoom > tsStart) {
				ActualOccupation lastOccupation = resultList.remove(resultList
						.size() - 1);
				countFree = Math.min(0, countFree - 1);
				FRPeriod previousPeriod = lastOccupation.getPeriod();
				if (tsStart - previousPeriod.getTimeStampStart() > MIN_PERIOD) {
					FRPeriod newPeriod = new FRPeriod(
							previousPeriod.getTimeStampStart(), tsStart, false);
					lastOccupation.setPeriod(newPeriod);
					resultList.add(lastOccupation);
					countFree++;
				} 				
			}

			long tsEnd = Math.min(timestampEnd, actual.getPeriod()
					.getTimeStampEnd());

			// the previous occupation is a room thus it has priority over user
			// : we need to resize.
			if (previousIsRoom && tsStart < lastEnd) {
				tsStart = lastEnd;
				FRPeriod newPeriod = new FRPeriod(tsStart, tsEnd, false);
				actual.setPeriod(newPeriod);
			}

			if (tsStart - tsPerRoom > MIN_PERIOD) {
				// We got a free period of time !
				ArrayList<ActualOccupation> subDivised = cutInStepsPeriod(
						tsPerRoom, tsStart);
				resultList.addAll(subDivised);
				countFree += subDivised.size();
			}

			long actualStart = actual.getPeriod().getTimeStampStart();
			long actualEnd = actual.getPeriod().getTimeStampEnd();

			// if the period is big enough (it might not be as we resize without
			// checking when there are a room-user conflict, see above)
			if (actualEnd - actualStart > MIN_PERIOD) {
				resultList.add(actual);
				previousIsRoom = !actual.isAvailable();
				double ratio = actual.getRatioOccupation();

				if (ratio > worstRatio) {
					worstRatio = ratio;
				}

				if (!actual.isAvailable()) {
					countOccupied++;
				}

				if (actual.isAvailable()) {
					isAtLeastFreeOnce = true;
					countFree++;
				}
				
				tsPerRoom = tsEnd;
				lastEnd = actual.getPeriod().getTimeStampEnd();
			}

		}

		if (timestampEnd - lastEnd > MARGIN_FOR_ERROR) {
			ArrayList<ActualOccupation> subDivised = cutInStepsPeriod(lastEnd,
					timestampEnd);
			resultList.addAll(subDivised);
			countFree++;
		}
		isAtLeastFreeOnce = countFree > 0 ? true : false;
		isAtLeastOccupiedOnce = countOccupied > 0 ? true : false;
		mActualOccupations = resultList;
	}

	/**
	 * This method's job is to cut in steps of fixed length a given period. The
	 * start will the rounded hour of the start's timestamp (e.g if given 10h13
	 * -> start is 10h00) up to the end hour rounded (e.g 10h43 -> 11h). We
	 * round the hour due to constraint on the database we decided. See
	 * create-tables.sql for more information
	 * 
	 * @param start
	 *            The start of the period
	 * @param end
	 *            The end of the period
	 * @return A List of actualoccupations, each of them has a period of at most
	 *         ONE_HOUR_MS
	 */
	private ArrayList<ActualOccupation> cutInStepsPeriod(long start, long end) {
		ArrayList<ActualOccupation> result = new ArrayList<ActualOccupation>();
		long hourSharpBefore = Utils.roundHourBefore(start);
		long numberHours = Utils.determineNumberHour(start, end);

		for (int i = 0; i < numberHours; ++i) {
			long minStart = Math.max(hourSharpBefore, hourSharpBefore + i
					* Utils.ONE_HOUR_MS);
			long maxEnd = Math.min(hourSharpBefore + (i + 1)
					* Utils.ONE_HOUR_MS, end);
			FRPeriod period = new FRPeriod(minStart, maxEnd, false);
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
	 * @return If boolean onlyFreeRooms (passed to the constructor) is true it
	 *         returns the occupancy only if there is no occupied period in the
	 *         list of ActualOccupation, otherwise it returns null. If boolean
	 *         onlyFreeRooms is false, it returns the occupancy adapted and
	 *         filled during the period given. //TODO maybe to this things in
	 *         the server (can check the isAtleastoccupied once.)
	 */
	public Occupancy getOccupancy() {
		sortByTimestampStart();
		fillGaps();
		if (isAtLeastOccupiedOnce && onlyFreeRooms) {
			return null;
		} else {
			Occupancy mOccupancy = new Occupancy(room, mActualOccupations,
					isAtLeastOccupiedOnce, isAtLeastFreeOnce);
			mOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);

			return mOccupancy;
		}
	}

	public int size() {
		return mActualOccupations.size();
	}

}
