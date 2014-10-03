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




//  Created by Loïc Gardiol on 12.04.12.


#import "PCService.h"

#import "map.h"

#import "MapModelAdditions.h"

extern NSString* const kMapRecentSearchesModifiedNotification; //posted by self on default notification center

@interface MapService : PCService<PCServiceProtocol>

/*
 
 THRIFT SERVICE
 
 - (NSArray *) getLayerList;  // throws TException
 - (NSArray *) getLayerItems: (int64_t) layerId;  // throws TException
 - (NSArray *) search: (NSString *) query;  // throws TException
 
*/

- (void)getLayerListWithDelegate:(id)delegate;
- (void)getLayerItemsForLayerId:(int64_t)layerId delegate:(id)delegate;
- (void)searchFor:(NSString*)query delegate:(id)delegate;


#pragma mark - Recent searches

/*
 * Returns ordered set of string of recent search pattern, with most recent at beginning.
 * recentSearches are persisted.
 * Returns empty array if not recent search.
 */
- (NSOrderedSet*)recentSearches;

/*
 * Adds pattern to top of recent searches. If pattern
 * already exists in recentSearches, it is put at beginnig of ordered set.
 */
- (void)addOrPromoteRecentSearch:(NSString*)pattern;

/*
 * Clears in memory and persisted recent searches.
 */
- (void)clearRecentSearches;

@end

@protocol MapServiceDelegate <PCServiceDelegate>

@optional
- (void)getLayerListDidReturn:(NSArray*)layerList; //array of MapLayer
- (void)getLayerListFailed;
- (void)getLayerItemsForLayerId:(int64_t)layerID didReturn:(NSArray*)layerItems; //array of MapItem
- (void)getLayerItemsFailedForLayerId:(int64_t)layerID;
- (void)searchMapFor:(NSString*)query didReturn:(NSArray*)results; //array of MapItem
- (void)searchMapFailedFor:(NSString*)query;

@end