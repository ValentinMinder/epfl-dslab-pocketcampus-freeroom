package org.pocketcampus.shared.plugin.camipro;

/**
 * Class used to transfer Balance data from the server to the client
 * @author Jonas
 */
public class BalanceBean {
	private float currentBalance;

	public BalanceBean(float currentBalance) {
		this.currentBalance = currentBalance;
	}

	public float getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(float currentBalance) {
		this.currentBalance = currentBalance;
	}
}
