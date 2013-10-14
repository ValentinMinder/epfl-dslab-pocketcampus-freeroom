//
//  PocketCampusLogicTests.m
//  PocketCampusLogicTests
//
//  Created by Loïc Gardiol on 24.05.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "PocketCampusLogicTests.h"

@implementation PocketCampusLogicTests

- (id)init
{
    self = [super init];
    if (self) {
        directoryServiceTests = [[DirectoryServiceTests alloc] init];
    }
    return self;
}

- (void)testAll {
    [directoryServiceTests test];
}


@end
