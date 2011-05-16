package org.pocketcampus.plugin.camipro;

import java.util.List;

/**
 * Class used to deserialize data from the camipro server
 * @author Jonas
 *
 */
public class TransactionServerList {
	private List<TransactionServer> LastTransactions;

	public List<TransactionServer> getLastTransactions() {
		return LastTransactions;
	}
	
}
