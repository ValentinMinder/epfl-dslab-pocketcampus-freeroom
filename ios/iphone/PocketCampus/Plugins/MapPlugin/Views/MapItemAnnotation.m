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
        title = [[NSString stringWithFormat:@"%@", mapItem.title] retain];
        if (item.description != nil) {
            subtitle = [[NSString stringWithFormat:@"%@", mapItem.description] retain];
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
    [title release];
    [subtitle release];
    [mapItem release];
    [super dealloc];
}

@end
