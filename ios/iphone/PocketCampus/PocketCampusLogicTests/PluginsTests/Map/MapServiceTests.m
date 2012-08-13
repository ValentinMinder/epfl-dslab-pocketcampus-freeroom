//
//  DirectoryServiceTests.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapServiceTests.h"

@implementation MapServiceTests

- (id)init
{
    self = [super init];
    if (self) {
        mapService = [[MapService sharedInstanceToRetain] retain];
    }
    return self;
}

- (void)tempTest {
    [mapService getLayerListWithDelegate:self];
}

- (void)getLayerListDidReturn:(NSArray *)layerList {
    NSLog(@"%@", layerList);
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"timeout");
}

- (void)dealloc
{
    [mapService release];
    [super dealloc];
}


@end
