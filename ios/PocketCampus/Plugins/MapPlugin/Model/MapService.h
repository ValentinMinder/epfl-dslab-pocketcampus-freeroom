//
//  MapService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "map.h"

@interface MapService : Service<ServiceProtocol>


/*
 
 THRIFT SERVICE
 
 - (NSArray *) getLayerList;  // throws TException
 - (NSArray *) getLayerItems: (Id) layerId;  // throws TException
 - (NSArray *) search: (NSString *) query;  // throws TException
 
*/

- (void)getLayerListWithDelegate:(id)delegate;
- (void)getLayerItemsForLayerId:(Id)layerId delegate:(id)delegate;
- (void)searchFor:(NSString*)query delegate:(id)delegate;

@end

@protocol MapServiceDelegate <ServiceDelegate>

@optional
- (void)getLayerListDidReturn:(NSArray*)layerList; //array of MapLayer
- (void)getLayerListFailed;
- (void)getLayerItemsForLayerId:(Id)layerID didReturn:(NSArray*)layerItems; //array of MapItem
- (void)getLayerItemsFailedForLayerId:(Id)layerID;
- (void)searchMapFor:(NSString*)query didReturn:(NSArray*)results; //array of MapItem
- (void)searchMapFailedFor:(NSString*)query;

@end