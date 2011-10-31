package org.pocketcampus.plugin.camipro.server.gsonobjects;

/**
 * Class used to deserialize data from the camipro server
 * @author Jonas
 *
 */
public class BalanceObj {
	private float PersonalAccountBalance;
	private String Status;
	
	public float getPersonalAccountBalance() {
		return PersonalAccountBalance;
	}
	public void setPersonalAccountBalance(float personalAccountBalance) {
		PersonalAccountBalance = personalAccountBalance;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
}
