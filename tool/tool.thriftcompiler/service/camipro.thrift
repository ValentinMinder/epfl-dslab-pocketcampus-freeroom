namespace java org.pocketcampus.plugin.camipro.shared

include "../include/common.thrift"

// refrain from using the following names in your Thrift files: int, id,  description, value, or any C/C++ keyword.

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
	1: required string xType;
	2: required string xDescription;
	3: required string xDate;
	4: required double xAmount;
}

service CamiproService {
	double getBalance();
	list<Transaction> getTransactions();
	EbankingBean getEbankingBean();
}
