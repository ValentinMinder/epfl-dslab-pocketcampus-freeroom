package org.pocketcampus.plugin.camipro.server.gsonobjects;

import java.util.List;

public class TransactionsObj {
	
	private static String[] stopWords_ = new String[] {"*", "-", "2"} ;
	
	private TransactionsList LastTransactionsList;
	private String Status;
	
	public TransactionsList getLastTransactionsList() {
		return LastTransactionsList;
	}
	public String getStatus() {
		return Status;
	}
	
	
	public class TransactionsList {
		private List<TransactionObj> LastTransactions;

		public List<TransactionObj> getLastTransactions() {
			return LastTransactions;
		}
		
		
		
		public class TransactionObj {
			
			
			private String TransactionType;
			private String ElementDescription;
			private String TransactionDate;
			private float TransactionAmount;
			
			public String getTransactionType() {
				return TransactionType;
			}
			public String getElementDescription() {
				return ElementDescription;
			}
			
			/**
			 * This method tries to remove content that should not appear 
			 * on the UI, such as stars, dashes, ...
			 * @return Cleaned description
			 */
			public String getElementPrettyDescription() {
				StringBuffer sb = new StringBuffer(ElementDescription);
				
				int position;
				for(String s : stopWords_) {
					position = sb.indexOf(s);
					if(position > 0) {
						sb.delete(position, 9999);
					}
				}
				
				return sb.toString();
			}
			
			public String getTransactionDate() {
				return TransactionDate;
			}
			public float getTransactionAmount() {
				return TransactionAmount;
			}
			
		}
		
	}
	
}
