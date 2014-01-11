

//  Created by Loïc Gardiol on 12.04.12.


#import "Service.h"

#import "map.h"

extern NSString* const kMapPersonsMapItemCategoryName;
extern NSString* const kMapRecentSearchesModifiedNotification; //posted by self on default notification center

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