package org.pocketcampus.plugin.isacademia.server;

/**
 * Utilities for room handling.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class RoomUtil {
	private static final char SPACE = ' ';
	private static final char MULTIPLE_ROOMS_SEPARATOR = '-';

	/**
	 * Parses room names, removing superfluous spacing and handling multiple rooms (e.g. "BC 07-08").
	 */
	public static String[] parseRoomNames(String roomNames) {
		StringBuilder nameBuilder = new StringBuilder();
		StringBuilder firstNumberBuilder = new StringBuilder();
		StringBuilder secondNumberBuilder = null;

		StringBuilder currentBuilder = nameBuilder;

		boolean wasSpace = true;
		for (char c : roomNames.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (!wasSpace) {
					currentBuilder.append(SPACE);
				}
				wasSpace = true;
				continue;
			}
			if (c == MULTIPLE_ROOMS_SEPARATOR) {
				if (currentBuilder != firstNumberBuilder) {
					// Not supposed to happen. Unknown format.
					return new String[] { roomNames };
				}

				secondNumberBuilder = new StringBuilder();
				currentBuilder = secondNumberBuilder;
				continue;
			}
			if (currentBuilder == nameBuilder && Character.isDigit(c) && wasSpace) {
				// the wasSpace check is necessary to not split stuff like "A3" in "MA A3"
				currentBuilder = firstNumberBuilder;
			}
			wasSpace = false;
			currentBuilder.append(c);
		}

		// need to trim since a space can appear at the end
		String name = nameBuilder.toString().trim();
		String firstNumber = firstNumberBuilder.toString().trim();

		if (secondNumberBuilder == null) {
			return new String[] { name + SPACE + firstNumber };
		}

		String secondNumber = secondNumberBuilder.toString().trim();
		return new String[] { name + SPACE + firstNumber, name + SPACE + secondNumber };
	}
}