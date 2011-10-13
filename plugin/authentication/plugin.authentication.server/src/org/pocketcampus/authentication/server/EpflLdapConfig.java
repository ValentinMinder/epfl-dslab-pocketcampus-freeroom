package org.pocketcampus.authentication.server;

public class EpflLdapConfig implements LdapConfig {

	@Override
	public String getBaseDn() {
		return "o=epfl,c=ch";
	}

	@Override
	public int getPort() {
		return 636;
	}

}
