//
//  EventsService.h
//  PocketCampus
//
//  Created by Loïc Gardiol (loic.gardiol@gmail.com)
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "events.h"

@interface EventsService : Service <ServiceProtocol>

/* Thrift methods
 
 - (EventItemReply *) getEventItem: (EventItemRequest *) iRequest;  // throws TException
 - (EventPoolReply *) getEventPool: (EventPoolRequest *) iRequest;  // throws TException
 - (ExchangeReply *) exchangeContacts: (ExchangeRequest *) iRequest;  // throws TException
 
 */


- (NSString*)lastUserToken;
- (BOOL)saveUserToken:(NSString*)token;
- (BOOL)deleteUserToken;

- (void)addFavoriteEventItemId:(int64_t)itemId;
- (void)removeFavoriteEventItemId:(int64_t)itemId;
- (BOOL)isEventItemIdFavorite:(int64_t)itemId;

- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate;
- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate;
- (void)exchangeContactsForRequest:(ExchangeRequest*)request delegate:(id)delegate;


@end

@protocol EventsServiceDelegate <ServiceDelegate>

@optional

// ----------------------------------------  TODO ----------------------------------------  //
// Prototype 2 delegate methods (*didReturn* and *Failed*) for each service method defined above.
// 
// IMPORTANT: 
// * The *didReturn* method MUST contain ALL arguments of service method (minus delegate) plus the didReturn:(type)result at the end.
// * The *Failed* method MUST contain ALL arguments of service method (minus delegate).
// 
// (WHY? So that the delegate can differentiate between 2 results when it has called the same service method
// twice with different arguments.)
//
// Examples (see above for corresponding service methods):
//
// 1) Method with 0 argument:
// Your *didReturn* definition must be: - (void)getMealsDidReturn:(NSArray*)meals;
// Your *Failed* definition must be: 		- (void)getMealsFailed;
// 
// 2) Method with arguments:
// Your *didReturn* definition must be: - (void)setRatingForMeal:(Id)mealId rating:(double)rating didReturn:(int)status;
// Your *Failed* definition must be: 		- (void)setRatingFailedForMeal:(Id)mealId rating:(double)rating;
//
// HANDLE CONNECTION TIMEOUT:
// *Failed* methods will be called on delegate if *the server WAS reachable but returned an error*
// On the other hand, if the server was NOT reachable (timeout), a generic method will be called on delegate:
//
// - (void)serviceConnectionToServerTimedOut;
//
// This method obviously does not differentiate between requests. This is because it assumes that 
// if the server was not reachable, no request could have succeeded.
// --------------------------------------------------------------------------------------  //

- (void)getEventItemForRequest:(EventItemRequest*)request didReturn:(EventItemReply*)reply;
- (void)getEventItemFailedForRequest:(EventItemRequest*)request;
- (void)getEventPoolForRequest:(EventPoolRequest*)request didReturn:(EventPoolReply*)reply;
- (void)getEventPoolFailedForRequest:(EventPoolRequest*)request;
- (void)exchangeContactsForRequest:(ExchangeRequest*)request didReturn:(ExchangeReply*)reply;
- (void)exchangeContactsFailedForRequest:(ExchangeRequest*)request;

@end
