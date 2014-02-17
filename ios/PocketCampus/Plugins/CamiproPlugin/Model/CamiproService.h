/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */




//  Created by Loïc Gardiol on 17.05.12.


#import "Service.h"

#import "camipro.h"

@interface CamiproService : Service<ServiceProtocol>

/* Thrift client methods
 
 - (BalanceAndTransactions *) getBalanceAndTransactions: (CamiproRequest *) iRequest;  // throws TException
 - (StatsAndLoadingInfo *) getStatsAndLoadingInfo: (CamiproRequest *) iRequest;  // throws TException
 - (SendMailResult *) sendLoadingInfoByEmail: (CamiproRequest *) iRequest;  // throws TException
 
*/

/*
 * Use this property to store the camipro session
 * This property is persisted
 */
@property (nonatomic,readwrite, strong) CamiproSession* camiproSession;

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
