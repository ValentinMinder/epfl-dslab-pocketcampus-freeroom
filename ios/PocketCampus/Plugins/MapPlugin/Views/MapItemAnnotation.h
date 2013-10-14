//
//  MapItemAnnotation.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

#import "map.h"

@interface MapItemAnnotation : NSObject<MKAnnotation>

- (id)initWithMapItem:(MapItem*)item;

@property (nonatomic, readonly) MapItem* mapItem;
@property (nonatomic, readonly, copy) NSString* title;
@property (nonatomic, readonly, copy) NSString* subtitle;

@end
