//
//  MapItemAnnotation.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapItemAnnotation.h"

@implementation MapItemAnnotation

@synthesize mapItem, title, subtitle;

- (id)initWithMapItem:(MapItem*)item {
    self = [super init];
    if (self) {
        mapItem = [item retain];
        title = [item.title copy];
        if (item.description != nil) {
            subtitle = [item.description copy];
        } else {
            subtitle = nil;
        }
    }
    return self;
}

- (CLLocationCoordinate2D)coordinate {
    return CLLocationCoordinate2DMake(mapItem.latitude, mapItem.longitude);
}

- (void)dealloc
{
    [mapItem release];
    [title release];
    [subtitle release];
    [super dealloc];
}

@end
