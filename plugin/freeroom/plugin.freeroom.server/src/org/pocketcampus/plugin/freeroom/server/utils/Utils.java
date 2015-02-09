package org.pocketcampus.plugin.freeroom.server.utils;

import org.pocketcampus.plugin.freeroom.shared.FRMessageFrequency;
import org.pocketcampus.plugin.freeroom.shared.FRPeriodOccupation;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FRRoomOccupancy;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an utility class doing useful conversions, and defining a few
 * constants.
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */
public class Utils {
    public static final int GROUP_STUDENT = 1;
    public static final int GROUP_STAFF = 20;
    public static String[] supportedLanguages = {"en", "fr"};
    public static String defaultLanguage = "en";

    public static final List<String> mediacomList = Arrays.asList("875", "876",
            "9001", "877", "878", "880", "1884", "1886", "1887", "1888",
            "1895", "1835", "1898", "1837", "1891", "1896", "2043", "2044",
            "2045", "2046", "2047", "2124", "2125", "2126", "2127", "12205",
            "12206", "12207", "12208", "9208", "9209", "9210", "9275", "9276",
            "9277", "9278", "9281", "9313", "9054", "9055", "4911", "4913",
            "4914", "4915", "3014", "3137", "3208", "3623", "3624", "3625",
            "3702", "3738");

    /**
     * Extract the building from the doorCode
     *
     * @param doorCode The doorCode from which we extract the building, it is assumed
     *                 to be well formatted (with space separating the building from
     *                 the zone and number e.g BC 01) otherwise it takes the first
     *                 characters of the string until it hits a number.
     * @return The building of the given door code if the door code is correct
     * as defined above or the door code itself if no matches has been
     * found.
     */
    public static String extractBuilding(String doorCode) {
        String mDoorCode = doorCode.trim();
        int firstSpace = mDoorCode.indexOf(" ");
        if (firstSpace > 0) {
            mDoorCode = mDoorCode.substring(0, firstSpace);
        } else {
            Pattern mBuildingPattern = Pattern
                    .compile("^([A-Za-z]+)[^A-Za-z]$");
            Matcher mMatcher = mBuildingPattern.matcher(doorCode);

            if (mMatcher.matches()) {
                return mMatcher.group(0);
            } else {
                return mDoorCode;
            }
        }
        return mDoorCode;
    }

    /**
     * From a list of rooms it creates a HashMap that maps a building to a list
     * of rooms (contained in this building).
     *
     * @param rooms The rooms to sort
     * @return The HashMap as defined above, an empty HashMap is rooms is null
     * or is empty
     */
    public static Map<String, List<FRRoom>> sortRoomsByBuilding(
            List<FRRoom> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return new HashMap<String, List<FRRoom>>();
        }

        Iterator<FRRoom> iter = rooms.iterator();
        HashMap<String, List<FRRoom>> sortedResult = new HashMap<String, List<FRRoom>>();

        while (iter.hasNext()) {
            FRRoom frRoom = iter.next();

            String building = extractBuilding(frRoom.getDoorCode());

            List<FRRoom> roomsNumbers = sortedResult.get(building);
            if (roomsNumbers == null) {
                roomsNumbers = new ArrayList<FRRoom>();
                sortedResult.put(building, roomsNumbers);
            }
            roomsNumbers.add(frRoom);
        }

        return sortedResult;
    }

    /**
     * Remove duplicates in a list of rooms
     *
     * @param uidList The list to check
     * @return The list with unique ids without duplicates
     */
    public static List<String> removeDuplicate(List<String> uidList) {
        HashSet<String> uidSet = new HashSet<String>();
        uidSet.addAll(uidList);
        return new ArrayList<String>(uidSet);
    }

    public static int determineGroupAccessRoom(String uid) {
        return mediacomList.contains(uid) ? GROUP_STUDENT : GROUP_STAFF;
    }

    /**
     * The HashMap is organized by the following relation(building -> list of
     * rooms) and each list of rooms is sorted independently. Sort the rooms
     * according to some criteria. See the comparator roomsFreeComparator.
     *
     * @param occ The HashMap to be sorted
     * @return The HashMap sorted
     */
    public static HashMap<String, List<FRRoomOccupancy>> sortRooms(
            HashMap<String, List<FRRoomOccupancy>> occ) {
        if (occ == null) {
            return null;
        }

        for (String key : occ.keySet()) {
            List<FRRoomOccupancy> value = occ.get(key);
            Collections.sort(value, roomsFreeComparator);
        }

        return occ;
    }

    /**
     * Comparator used to sort rooms according to some criteria. First put the
     * rooms entirely free , then the partially occupied and then the rooms
     * unavailable. Entirely free rooms are sorted by probable occupancy
     * (users), partially occupied are sorted first by percentage of room
     * occupation (i.e how many hours compared to the total period the room is
     * occupied) then by probable occupancy (users). Totally occupied rooms
     * are not sorted.
     */
    private static Comparator<FRRoomOccupancy> roomsFreeComparator = new Comparator<FRRoomOccupancy>() {

        @Override
        public int compare(FRRoomOccupancy o0, FRRoomOccupancy o1) {

            boolean onlyFree1 = !o0.isIsOccupiedAtLeastOnce();
            boolean onlyFree2 = !o1.isIsOccupiedAtLeastOnce();

            if (onlyFree1 && onlyFree2) {
                return compareOnlyFree(o0.getRatioWorstCaseProbableOccupancy(),
                        o1.getRatioWorstCaseProbableOccupancy());
            } else if (onlyFree1) {
                return -1;
            } else if (onlyFree2) {
                return 1;
            } else {
                double rate1 = rateOccupied(o0.getOccupancy());
                double rate2 = rateOccupied(o1.getOccupancy());
                return comparePartiallyOccupied(rate1, rate2,
                        o0.getRatioWorstCaseProbableOccupancy(),
                        o1.getRatioWorstCaseProbableOccupancy());
            }
        }

        private int comparePartiallyOccupied(double rate1, double rate2,
                                             double prob1, double prob2) {
            if (rate1 == rate2) {
                return equalPartiallyOccupied(prob1, prob2);
            } else if (rate1 < rate2) {
                return -1;
            } else {
                return 1;
            }
        }

        private int equalPartiallyOccupied(double prob1, double prob2) {
            if (prob1 < prob2) {
                return -1;
            } else if (prob1 > prob2) {
                return 1;
            }
            return 0;
        }

        /**
         * Count the number of hours in the ActualOccupation given
         *
         * @param acc
         *            The ActualOccupation to be counted.
         * @return The number of hours in the ActualOccupation
         */
        private int countNumberHour(FRPeriodOccupation acc) {
            long tsStart = acc.getPeriod().getTimeStampStart();
            long tsEnd = acc.getPeriod().getTimeStampEnd();
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(tsStart);
            int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            mCalendar.setTimeInMillis(tsEnd);
            int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            return Math.abs(endHour - startHour);
        }

        /**
         * Rate occupied is the ratio between occupied hours to total hours in the period.
         */
        private double rateOccupied(List<FRPeriodOccupation> occupations) {
            int count = 0;
            int total = 0;
            for (FRPeriodOccupation acc : occupations) {
                int nbHours = countNumberHour(acc);
                if (!acc.isAvailable()) {
                    count += nbHours;
                }
                total += nbHours;

            }
            return total > 0 ? (double) count / total : 0.0;
        }

        private int compareOnlyFree(double prob1, double prob2) {
            if (prob1 < prob2) {
                return -1;
            } else if (prob1 > prob2) {
                return +1;
            }
            return 0;
        }
    };

    public static ArrayList<FRMessageFrequency> removeGroupMessages(
            List<String> listMessages) {
        if (listMessages == null) {
            return null;
        }

        HashMap<String, Integer> answer = new HashMap<String, Integer>();
        for (String message : listMessages) {
            if (message != null) {
                String lowerCaseMessage = message.toLowerCase();
                Integer count = answer.get(lowerCaseMessage);
                if (count == null) {
                    answer.put(lowerCaseMessage, 1);
                } else {
                    answer.put(lowerCaseMessage, count + 1);
                }
            }
        }
        return convertMapToListMessageFrequency(answer);
    }

    public static ArrayList<FRMessageFrequency> convertMapToListMessageFrequency(
            HashMap<String, Integer> map) {
        ArrayList<FRMessageFrequency> answer = new ArrayList<FRMessageFrequency>();

        for (Entry<String, Integer> e : map.entrySet()) {
            answer.add(new FRMessageFrequency(e.getKey(), e.getValue()));
        }

        Collections.sort(answer);
        return answer;
    }

    /**
     * Add an alias to the room only if it's not null, and not the same as the
     * existing door code.
     *
     * @param room  the room
     * @param alias the alias
     */
    public static void addAliasIfNeeded(FRRoom room, String alias) {
        if (alias != null) {
            if (!room.getDoorCode().trim().replaceAll("\\s+", "")
                    .equalsIgnoreCase(alias.trim().replaceAll("\\s+", ""))) {
                room.setDoorCodeAlias(alias);
            }
        }
    }

    public static String getSupportedLanguage(String lang) {
        if (lang == null) {
            return defaultLanguage;
        }

        for (String sl : supportedLanguages) {
            if (lang.toLowerCase().equals(sl)) {
                return lang.toLowerCase();
            }
        }
        return defaultLanguage;
    }
}
