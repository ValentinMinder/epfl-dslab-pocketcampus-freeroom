package org.pocketcampus.plugin.freeroom.android.utils;

/**
 * This class is intended for useful transformation that may be used by both
 * server and client.
 *
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */

public class FRStruct {
    /**
     * Removes safely the first char of a String.
     * <p/>
     * Used to extract the "/" in path of URI usage.
     *
     * @param intentUriData the original string
     * @return the same string without the first char, an empty string if the
     * original was 1 or 0 chars long.
     */
    public static String removeFirstCharSafely(String intentUriData) {
        int length = intentUriData.length();
        return intentUriData.substring(Math.min(length, 1), length);
    }
}
