package org.pocketcampus.plugin.camipro;

/**
 * Class used to deserialize data from the camipro server
 * @author Jonas
 *
 */
public class TransactionsServer {
	private TransactionServerList LastTransactionsList;
	private String Status;
	
	public TransactionServerList getLastTransactionsList() {
		return LastTransactionsList;
	}
	public String getStatus() {
		return Status;
	}
}
