package org.pocketcampus.shared.plugin.camipro;

/**
 * Class used to transfer Balance data from the server to the client
 * @author Jonas
 *
 */
public class BalanceBean {
	private float currentBalance_;

	public BalanceBean(float currentBalance) {
		this.currentBalance_ = currentBalance;
	}

	public float getCurrentBalance() {
		return currentBalance_;
	}

	public void setCurrentBalance(float currentBalance) {
		this.currentBalance_ = currentBalance;
	}
}
