package org.pocketcampus.authentication.server;

import ch.epfl.tequila.client.model.TequilaPrincipal;

public class TequilaSession {
	
	public void setTequilaPrincipal(TequilaPrincipal tequilaPrincipal) {
		iTequilaPrincipal = tequilaPrincipal;
	}
	
	public TequilaPrincipal getTequilaPrincipal() {
		return iTequilaPrincipal;
	}
	
	private TequilaPrincipal iTequilaPrincipal;

}
