//
//  CamiproService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "camipro.h"

@interface CamiproService : Service<ServiceProtocol>

/* Thrift client methods
 
 - (BalanceAndTransactions *) getBalanceAndTransactions: (CamiproRequest *) iRequest;  // throws TException
 - (StatsAndLoadingInfo *) getStatsAndLoadingInfo: (CamiproRequest *) iRequest;  // throws TException
 - (SendMailResult *) sendLoadingInfoByEmail: (CamiproRequest *) iRequest;  // throws TException
 
*/

+ (CamiproSession*)lastSessionId;
+ (BOOL)saveSessionId:(CamiproSession*)sessionId;

- (void)getTequilaTokenForCamiproDelegate:(id)delegate;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)tequilaKey delegate:(id)delegate;

- (void)getBalanceAndTransactions:(CamiproRequest*)camiproRequest delegate:(id)delegate;
- (void)getStatsAndLoadingInfo:(CamiproRequest*)camiproRequest delegate:(id)delegate;
- (void)sendLoadingInfoByEmail:(CamiproRequest*)camiproRequest delegate:(id)delegate;

@end

@protocol CamiproServiceDelegate <ServiceDelegate>

@optional
- (void)getTequilaTokenForCamiproDidReturn:(TequilaToken*)tequilaKey;
- (void)getTequilaTokenForCamiproFailed;
- (void)getSessionIdForServiceWithTequilaKey:(TequilaToken*)aTequilaKey didReturn:(CamiproSession*)aSessionId;
- (void)getSessionIdForServiceFailedForTequilaKey:(TequilaToken*)aTequilaKey;

- (void)getBalanceAndTransactionsForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(BalanceAndTransactions*)balanceAndTransactions;
- (void)getBalanceAndTransactionsFailedForCamiproRequest:(CamiproRequest*)camiproRequest;
- (void)getStatsAndLoadingInfoForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(StatsAndLoadingInfo*)statsAndLoadingInfo;
- (void)getStatsAndLoadingInfoFailedForCamiproRequest:(CamiproRequest*)camiproRequest;
- (void)sendLoadingInfoByEmailForCamiproRequest:(CamiproRequest*)camiproRequest didReturn:(SendMailResult*)sendMailResult;
- (void)sendLoadingInfoByEmailFailedForCamiproRequest:(CamiproRequest*)camiproRequest;

@end
