package org.pocketcampus.plugin.isacademia.server.tests;

import org.junit.Test;
import org.pocketcampus.plugin.isacademia.server.RoomUtil;

import static org.junit.Assert.assertArrayEquals;

public class RoomUtilTests {
	@Test
	public final void simpleRoom() {
		assertArrayEquals("Single room names without extra spaces should be left untouched.",
				new String[] { "CO 1" }, RoomUtil.parseRoomNames("CO 1"));
	}

	@Test
	public final void spacesBeforeAreRemoved() {
		assertArrayEquals("Spaces before room names should be removed.",
				new String[] { "CO 1" }, RoomUtil.parseRoomNames("  CO 1"));
	}

	@Test
	public final void extraSpacesInsideAreRemoved() {
		assertArrayEquals("Extra spaces inside room names should be removed.",
				new String[] { "CO 1" }, RoomUtil.parseRoomNames("CO  1"));
	}

	@Test
	public final void spacesAfterAreRemoved() {
		assertArrayEquals("Spaces after room names should be removed.",
				new String[] { "CO 1" }, RoomUtil.parseRoomNames("CO 1 "));
	}

	@Test
	public final void allExtraSpacesAreRemoved() {
		assertArrayEquals("All extra spaces should be removed.",
				new String[] { "MA A3 345" }, RoomUtil.parseRoomNames(" MA  A3 345   "));
	}

	@Test
	public final void multipleRoomsAreSplit() {
		assertArrayEquals("Multiple rooms should be split in two.",
				new String[] { "BC 07", "BC 08" }, RoomUtil.parseRoomNames("BC 07-08"));
	}

	@Test
	public final void spacesAreRemovedInMultipleRooms() {
		assertArrayEquals("Extra spaces should also be removed when splitting multiple rooms.",
				new String[] { "BC 07", "BC 08" }, RoomUtil.parseRoomNames(" BC  07-08 "));
	}
}