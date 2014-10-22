package org.pocketcampus.platform.server;

import java.io.IOException;

public interface StateChecker {
	int checkState() throws IOException;
}
