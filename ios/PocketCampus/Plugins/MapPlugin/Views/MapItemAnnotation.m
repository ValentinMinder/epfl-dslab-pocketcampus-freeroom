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
        if (![item isKindOfClass:[MapItem class]]) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"item must be kind of class MapItem" userInfo:nil];
        }
        _mapItem = item;
        if (item.title.length != 0) {
            _title = [self.mapItem.title copy];
        }
        if (item.description.length != 0) {
            _subtitle = [self.mapItem.description copy];
        }
    }
    return self;
}

- (CLLocationCoordinate2D)coordinate {
    return CLLocationCoordinate2DMake(self.mapItem.latitude, self.mapItem.longitude);
}

@end
