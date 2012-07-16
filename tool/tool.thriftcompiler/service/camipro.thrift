namespace java org.pocketcampus.plugin.camipro.shared

include "authentication.thrift"


struct CamiproRequest {
	1: required authentication.SessionId iSessionId;
	2: required string iLanguage;
}


struct CamiproSession {
	1: required string camiproCookie;
}


struct Transaction {
	1: required string iDate;
	2: required string iOperation;
	3: required string iPlace;
	4: required double iAmount;
}

struct BalanceAndTransactions {
	1: optional double iBalance; 
	2: optional list<Transaction> iTransactions;
	3: optional string iDate;
	4: required i32 iStatus;
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
	1: optional CardStatistics iCardStatistics;
	2: optional CardLoadingWithEbankingInfo iCardLoadingWithEbankingInfo;
	3: required i32 iStatus;
}


struct SendMailResult {
	1: optional string iResultText;
	2: required i32 iStatus;
}


service CamiproService {
	authentication.TequilaToken getTequilaTokenForCamipro();
	CamiproSession getCamiproSession(1: authentication.TequilaToken iTequilaToken);
	BalanceAndTransactions getBalanceAndTransactions(1: CamiproRequest iRequest);
	StatsAndLoadingInfo getStatsAndLoadingInfo(1: CamiproRequest iRequest);
	SendMailResult sendLoadingInfoByEmail(1: CamiproRequest iRequest);
}
