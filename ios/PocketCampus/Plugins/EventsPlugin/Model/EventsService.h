//
//  EventsService.h
//  PocketCampus
//
//  Created by Loïc Gardiol (loic.gardiol@gmail.com)
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "events.h"

extern NSString* const kEventsFavoritesEventItemsUpdatedNotification;

@interface EventsService : Service <ServiceProtocol>

/* Thrift methods
 
 - (EventItemReply *) getEventItem: (EventItemRequest *) iRequest;  // throws TException
 - (EventPoolReply *) getEventPool: (EventPoolRequest *) iRequest;  // throws TException
 - (ExchangeReply *) exchangeContacts: (ExchangeRequest *) iRequest;  // throws TException
 - (SendEmailReply *) sendStarredItemsByEmail: (SendEmailRequest *) iRequest;  // throws TException
 
 */


- (void)addUserTicket:(NSString*)ticket;
- (void)removeUserTicket:(NSString*)ticket;
- (NSArray*)allUserTickets;

- (void)addFavoriteEventItemId:(int64_t)itemId;
- (void)removeFavoriteEventItemId:(int64_t)itemId;
- (NSArray*)allFavoriteEventItemIds; //array of NSNumber of int64_t (unspecifed order)
- (BOOL)isEventItemIdFavorite:(int64_t)itemId;

/*- (NSData*)pictureLocalURLForEventItem:(EventItem*)eventItem;
- (BOOL)savePictureData:(NSData*)imageData forEventItem:(EventItem*)eventItem;*/

- (int32_t)lastSelectedPoolPeriod;
- (BOOL)saveSelectedPoolPeriod:(int32_t)period;


- (void)getEventItemForRequest:(EventItemRequest*)request delegate:(id)delegate;
- (void)getEventPoolForRequest:(EventPoolRequest*)request delegate:(id)delegate;
- (void)exchangeContactsForRequest:(ExchangeRequest*)request delegate:(id)delegate;
- (void)sendStarredItemsByEmail:(SendEmailRequest *)request delegate:(id)delegate;

- (EventPoolReply*)getFromCacheEventPoolForRequest:(EventPoolRequest*)request;

@end

@protocol EventsServiceDelegate <ServiceDelegate>

@optional


- (void)getEventItemForRequest:(EventItemRequest*)request didReturn:(EventItemReply*)reply;
- (void)getEventItemFailedForRequest:(EventItemRequest*)request;
- (void)getEventPoolForRequest:(EventPoolRequest*)request didReturn:(EventPoolReply*)reply;
- (void)getEventPoolFailedForRequest:(EventPoolRequest*)request;
- (void)exchangeContactsForRequest:(ExchangeRequest*)request didReturn:(ExchangeReply*)reply;
- (void)exchangeContactsFailedForRequest:(ExchangeRequest*)request;
- (void)sendStarredItemsByEmailForRequest:(SendEmailRequest*)request didReturn:(SendEmailReply*)reply;
- (void)sendStarredItemsByEmailFailedForRequest:(SendEmailRequest*)request;


@end
