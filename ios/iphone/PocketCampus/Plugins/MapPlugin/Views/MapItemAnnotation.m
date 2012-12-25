//
//  MapItemAnnotation.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapItemAnnotation.h"

@interface MapItemAnnotation ()

@property (nonatomic, readwrite, strong) MapItem* mapItem;
@property (nonatomic, readwrite, copy) NSString* title;
@property (nonatomic, readwrite, copy) NSString* subtitle;

@end

@implementation MapItemAnnotation

- (id)initWithMapItem:(MapItem*)item {
    self = [super init];
    if (self) {
        self.mapItem = item;
        self.title = [NSString stringWithFormat:@"%@", self.mapItem.title];
        if (item.description != nil) {
            self.subtitle = [NSString stringWithFormat:@"%@", self.mapItem.description];
        } else {
            self.subtitle = nil;
        }
    }
    return self;
}

- (CLLocationCoordinate2D)coordinate {
    return CLLocationCoordinate2DMake(self.mapItem.latitude, self.mapItem.longitude);
}

@end
