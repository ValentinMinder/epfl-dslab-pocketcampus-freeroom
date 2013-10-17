//
//  MapService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "map.h"

//actually used in MapViewController, Xcode simply does not see it
static NSString* kPersonsMapItemCategoryName __unused = @"persons";
static NSString* kMapRecentSearchesModifiedNotificationName __unused = @"MapRecentSearchesModified"; //posted by self on default notification center

@interface MapService : Service<ServiceProtocol>

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

@protocol MapServiceDelegate <ServiceDelegate>

@optional
- (void)getLayerListDidReturn:(NSArray*)layerList; //array of MapLayer
- (void)getLayerListFailed;
- (void)getLayerItemsForLayerId:(int64_t)layerID didReturn:(NSArray*)layerItems; //array of MapItem
- (void)getLayerItemsFailedForLayerId:(int64_t)layerID;
- (void)searchMapFor:(NSString*)query didReturn:(NSArray*)results; //array of MapItem
- (void)searchMapFailedFor:(NSString*)query;

@end