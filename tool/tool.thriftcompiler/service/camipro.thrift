namespace java org.pocketcampus.plugin.camipro.shared

include "authentication.thrift"


struct Transaction {
	1: required string iDate;
	2: required string iOperation;
	3: required string iPlace;
	4: required double iAmount;
}

struct BalanceAndTransactions {
	1: required double iBalance; 
	2: required list<Transaction> iTransactions;
}


struct CardStatistics {
	1: required double iTotalPaymentsLastMonth;
	2: required double iTotalPaymentsLastThreeMonths;
}

struct CardLoadingWithEbankingInfo {
	1: required string iPaidTo;
	2: required string iAccountNumber;
	3: required string iReferenceNumber;
}

struct StatsAndLoadingInfo {
	1: required CardStatistics iCardStatistics;
	2: required CardLoadingWithEbankingInfo iCardLoadingWithEbankingInfo;
}

service CamiproService {
	BalanceAndTransactions getBalanceAndTransactions(1: authentication.SessionId aSessionId);
	StatsAndLoadingInfo getStatsAndLoadingInfo(1: authentication.SessionId aSessionId);
}
