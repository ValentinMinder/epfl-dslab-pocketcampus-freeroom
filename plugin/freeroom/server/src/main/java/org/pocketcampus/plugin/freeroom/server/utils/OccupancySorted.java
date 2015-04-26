package org.pocketcampus.plugin.freeroom.server.utils;

import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class is used to sort, fill and then create an occupancy based on
 * ActualOccupation for a specific room. Note that the timestamps given in each
 * ActualOccupation will have the seconds and milliseconds set to 0 for
 * practical purpose in the sort and its use (fillGaps()).
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */

public class OccupancySorted {

    private boolean onlyFreeRooms;
    private ArrayList<FRPeriodOccupation> mActualOccupations;
    private FRRoom room;
    private long timestampStart;
    private long timestampEnd;
    private boolean isAtLeastFreeOnce;
    private boolean isAtLeastOccupiedOnce;
    private double worstRatio;

    public OccupancySorted(FRRoom room, long tsStart, long tsEnd,
                           boolean onlyFree) {
        this.mActualOccupations = new ArrayList<FRPeriodOccupation>();
        this.room = room;
        isAtLeastFreeOnce = false;
        isAtLeastOccupiedOnce = false;
        worstRatio = 0.0;
        timestampStart = tsStart;
        timestampEnd = tsEnd;
        onlyFreeRooms = onlyFree;
    }

    /**
     * Add an occupation to be ordered later.
     *
     * @param occ The occupation to add
     */
    public void addActualOccupation(FRPeriodOccupation occ) {
        FRPeriod period = occ.getPeriod();
        long start = FRTimes.roundSAndMSToZero(period.getTimeStampStart());
        long end = FRTimes.roundSAndMSToZero(period.getTimeStampEnd());

        if (start < timestampStart) {
            start = timestampStart;
        }

        if (end > timestampEnd) {
            end = timestampEnd;
        }

        if (occ.isAvailable() && end - start > FRTimes.ONE_HOUR_IN_MS) {
            mActualOccupations.addAll(cutInStepsPeriod(start, end));
        } else {
            mActualOccupations.add(occ.setPeriod(new FRPeriod(start, end)));
        }
    }

    /**
     * Create an Occupancy object, set its properties. The resulting object is
     * suitable for a reply to the client.
     *
     * @return If boolean onlyFreeRooms (passed to the constructor) is true it
     * returns the occupancy only if there is no occupied period in the
     * list of ActualOccupation, otherwise it returns null. If boolean
     * onlyFreeRooms is false, it returns the occupancy adapted and
     * filled during the given period.
     * *
     */
    public FRRoomOccupancy getOccupancy() {
        fillGaps();
        if (isAtLeastOccupiedOnce && onlyFreeRooms) {
            return null;
        } else {
            if (mActualOccupations.size() >= 1) {
                long start = mActualOccupations.get(0).getPeriod()
                        .getTimeStampStart();
                long end = mActualOccupations
                        .get(mActualOccupations.size() - 1).getPeriod()
                        .getTimeStampEnd();
                FRPeriod periodTreated = new FRPeriod(start, end);
                FRRoomOccupancy mOccupancy = new FRRoomOccupancy(room,
                        mActualOccupations, isAtLeastOccupiedOnce,
                        isAtLeastFreeOnce, periodTreated);
                mOccupancy.setRatioWorstCaseProbableOccupancy(worstRatio);

                return mOccupancy;
            } else {
                return null;
            }
        }
    }

    public int size() {
        return mActualOccupations.size();
    }

    public FRRoom getRoom() {
        return room;
    }

    /**
     * This method's job is to sort the data in the mActualOccupations ArrayList
     * by timestamp start and type of the occupation. See doc for
     * equalTimestamp() in the comparator below.
     */
    private void sortByTimestampStart() {
        Collections.sort(mActualOccupations,
                new Comparator<FRPeriodOccupation>() {

                    @Override
                    public int compare(FRPeriodOccupation o1,
                                       FRPeriodOccupation o2) {
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
                        } else if (thisUser) {
                            return 1;
                        } else if (toCompareUser) {
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
     * This method fills the gaps between occupations's entries in the list of
     * this object, it reduces the period of an user occupancy that overlap some
     * room occupancy. It also fill the blank with user occupations in order to
     * have a contiguous list of period (with a margin of FRTimes.MIN_PERIOD).
     * No particular order is needed.
     */
    private void fillGaps() {
        sortByTimestampStart();
        ArrayList<FRPeriodOccupation> resultList = new ArrayList<FRPeriodOccupation>();
        boolean previousIsRoom = false;
        long lastEnd = timestampStart;
        int countFree = 0;
        int countOccupied = 0;

        for (FRPeriodOccupation actual : mActualOccupations) {
            boolean added = false;
            long tsStart = Math.max(timestampStart, actual.getPeriod()
                    .getTimeStampStart());

            // we want to add a room and the previous occupation added is an
            // user
            // occupancy and this one ends after the room occupancy starts
            // (overlap) !
            // -> we resize the user occupancy.
            if (!actual.isAvailable() && !previousIsRoom && lastEnd > tsStart) {
                FRPeriodOccupation lastOccupation = resultList
                        .remove(resultList.size() - 1);
                countFree = Math.max(0, countFree - 1);
                FRPeriod previousPeriod = lastOccupation.getPeriod();
                if (tsStart - previousPeriod.getTimeStampStart() > FRTimes.MIN_PERIOD) {
                    FRPeriod newPeriod = new FRPeriod(
                            previousPeriod.getTimeStampStart(), tsStart);
                    lastOccupation.setPeriod(newPeriod);
                    resultList.add(lastOccupation);
                    countFree++;
                }
            }

            long tsEnd = Math.min(timestampEnd, actual.getPeriod()
                    .getTimeStampEnd());

            // the previous occupation is a room thus it has priority over user
            // -> we need to resize.
            if (previousIsRoom && tsStart < lastEnd && actual.isAvailable()) {
                tsStart = lastEnd;
                FRPeriod newPeriod = new FRPeriod(tsStart, tsEnd);
                actual.setPeriod(newPeriod);
            } else if (previousIsRoom && tsStart < lastEnd) {
                // special case, overlap of two rooms occupancies
                FRPeriodOccupation lastOccupation = resultList
                        .remove(resultList.size() - 1);
                FRPeriod previousPeriod = lastOccupation.getPeriod();
                previousPeriod.setTimeStampEnd(tsEnd);
                resultList.add(lastOccupation);
                added = true;
                actual = lastOccupation;
            }

            if (tsStart - lastEnd > FRTimes.MIN_PERIOD) {
                // We got a free period of time ! -> we fill the blank
                ArrayList<FRPeriodOccupation> subDivised = cutInStepsPeriod(
                        lastEnd, tsStart);
                resultList.addAll(subDivised);
                countFree += subDivised.size();
            }

            long actualStart = actual.getPeriod().getTimeStampStart();

            // if the period is big enough (it might not be as we resize without
            // checking when there are a room-user conflict, see above)
            if (added || tsEnd - actualStart > FRTimes.MIN_PERIOD) {
                if (!added) {
                    resultList.add(actual);
                }
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

                lastEnd = tsEnd;
            }

        }

        // if we have blank time between the last occupancy and the end of the
        // period we want to cover
        if (timestampEnd - lastEnd > FRTimes.MIN_PERIOD) {
            ArrayList<FRPeriodOccupation> subDivised = cutInStepsPeriod(
                    lastEnd, timestampEnd);
            resultList.addAll(subDivised);
            countFree++;
        }
        isAtLeastFreeOnce = countFree > 0;
        isAtLeastOccupiedOnce = countOccupied > 0;
        mActualOccupations = resultList;
    }

    /**
     * This method's job is to cut in steps of fixed length a given period. The
     * start will the rounded hour of the start's timestamp (e.g if given 10h13
     * -> start is 10h00) up to the end hour rounded (e.g 10h43 -> 11h). We
     * round the hour due to constraint on the database we decided. See
     * create-tables.sql for more information.
     *
     * @param start The start of the period
     * @param end   The end of the period
     * @return A List of actualoccupations, each of them has a period of at most
     * ONE_HOUR_MS
     */
    private ArrayList<FRPeriodOccupation> cutInStepsPeriod(long start, long end) {
        ArrayList<FRPeriodOccupation> result = new ArrayList<FRPeriodOccupation>();
        long hourSharpBefore = FRTimes.roundHourBefore(start);
        long numberHours = FRTimes.determineNumberHour(start, end);

        for (int i = 0; i < numberHours; ++i) {
            long minStart = Math.max(start, hourSharpBefore + i
                    * FRTimes.ONE_HOUR_IN_MS);
            long maxEnd = Math.min(hourSharpBefore + (i + 1)
                    * FRTimes.ONE_HOUR_IN_MS, end);
            if (maxEnd - minStart > FRTimes.MIN_PERIOD) {
                FRPeriod period = new FRPeriod(minStart, maxEnd);
                FRPeriodOccupation mAccOcc = new FRPeriodOccupation(period,
                        true);
                mAccOcc.setRatioOccupation(0.0);
                result.add(mAccOcc);
            }
        }
        return result;
    }

}
