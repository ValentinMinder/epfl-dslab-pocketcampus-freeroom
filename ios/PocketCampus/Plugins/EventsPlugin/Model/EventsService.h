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

//  Created by Loïc Gardiol (loic.gardiol@gmail.com)

@import Foundation;

#import "PCService.h"

#import "events.h"

/*
 * Possible action parameter for PocketCampus URL scheme for Events
 */
extern NSString* const kEventsURLActionShowEventPool;
extern NSString* const kEventsURLActionShowEventItem;

/*
 * Other parameters for PocketCampus URL scheme for Events
 */
extern NSString* const kEventsURLParameterEventPoolId;
extern NSString* const kEventsURLParameterEventItemId;
extern NSString* const kEventsURLParameterMarkFavoriteEventItemId;
extern NSString* const kEventsURLParameterUserTicket;
extern NSString* const kEventsURLParameterExchangeToken;

/*
 * Posted on default notification center when favorites are updated
 */
extern NSString* const kEventsFavoritesEventItemsUpdatedNotification;

@interface EventsService : PCService <PCServiceProtocol>

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

@protocol EventsServiceDelegate <PCServiceDelegate>

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
