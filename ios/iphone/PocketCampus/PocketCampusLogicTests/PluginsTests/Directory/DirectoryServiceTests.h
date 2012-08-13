//
//  DirectoryServiceTests.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryService.h"

typedef enum {
    TestTypeStress,
    TestTypeRandom,
    TestTypeSearchPersons,
} TestType;

@interface DirectoryServiceTests : NSObject<ServiceDelegate> {
    DirectoryService* directoryService;
    int searchPersonsRet;
    int autocompletRet;
    int profilePicRet;
    TestType testType;
    
}
- (void)tempTest;
- (void)test;

@end
