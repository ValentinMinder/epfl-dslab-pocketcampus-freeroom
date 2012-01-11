package org.pocketcampus.authentication.server;

import ch.epfl.tequila.client.model.TequilaPrincipal;

/**
 * TequilaSession
 * 
 * Stores the information we want to keep in the Session
 * of a user authenticated to the service "PocketCampus" 
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class TequilaSession {
	
	public void setTequilaPrincipal(TequilaPrincipal tequilaPrincipal) {
		iTequilaPrincipal = tequilaPrincipal;
	}
	
	public TequilaPrincipal getTequilaPrincipal() {
		return iTequilaPrincipal;
	}
	
	private TequilaPrincipal iTequilaPrincipal;

}
