namespace java org.pocketcampus.plugin.camipro.shared

include "../include/common.thrift"

struct EbankingBean {
	1: required string paidNameTo;
	2: required string accountNr;
	3: required string BvrReference;
	4: required string BvrReferenceReadable;
	5: required double total1M;
	6: required double total3M;
	7: required double average3M;
}

struct Transaction {
	1: required string type;
	2: required string description;
	3: required string date;
	4: required double amount;
}

service FoodService {
	double getBalance();
	list<Transaction> getTransactions();
	EbankingBean getEbankingBean();
}
