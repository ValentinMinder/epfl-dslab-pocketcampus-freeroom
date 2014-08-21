package org.pocketcampus.plugin.authentication.server;

import java.util.List;

import ch.epfl.tequila.client.model.TequilaPrincipal;

public interface SessionManager {
	String insert(TequilaPrincipal principal, boolean rememberMe);
	List<String> getFields(String sessionId, List<String> fields);
	Integer destroySessions(String sciper);
}
