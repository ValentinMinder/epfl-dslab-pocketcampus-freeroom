//
//  DirectoryServiceTests.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "DirectoryServiceTests.h"

static int NB_TESTS = 30;

@implementation DirectoryServiceTests

- (id)init
{
    self = [super init];
    if (self) {
        directoryService = [[DirectoryService sharedInstanceToRetain] retain];
        searchPersonsRet = 0;
        profilePicRet = 0;
        autocompletRet = 0;
    }
    return self;
}

- (void)tempTest {
    [directoryService searchPersons:@"loic" delegate:self];
}

- (void)test {
    [self testStress];
}

- (void)testStress {
    testType = TestTypeStress;
    for (int i = 0; i<NB_TESTS; i++) {
        [directoryService searchPersons:@"George Candea" delegate:self];
        //[directoryService autocomplete:@"ni" delegate:self];
        //[directoryService getProfilePicture:@"185853" delegate:self];
    }
}

- (void)testRandom {
    testType = TestTypeRandom;
    
    //int nbTimes = (rand() % 3)+1;
    int nbTimes = 1;
    
    for (int i = 0; i<=nbTimes; i++) {
        long method = rand() % 3;
        switch (method) {
            case 0:
                NSLog(@"starting searchPersons");
                [directoryService searchPersons:@"George Candea" delegate:self];
                break;
            case 1:
                NSLog(@"starting autocomplete");
                [directoryService autocomplete:@"ni" delegate:self];
                break;
            case 2:
                NSLog(@"starting getProfilePicture");
                [directoryService getProfilePicture:@"185853" delegate:self];
                break;
            default:
                break;
        }
    }
    
    double cancelTime = (double)(rand() % 10);
    [NSTimer scheduledTimerWithTimeInterval:cancelTime target:self selector:@selector(internalTestCancelAndRelease) userInfo:nil repeats:NO];
}

- (void)testSearchPersons {
    testType = TestTypeSearchPersons;
    [directoryService searchPersons:@"George Candea" delegate:self];
}

- (void)internalTestCancelAndRelease {
    //[directoryService cancelOperationsForDelegate:self];
    [directoryService release];
    directoryService = [[DirectoryService sharedInstanceToRetain] retain];
    [self testRandom];
}

/* delegation */

- (void)searchFor:(NSString*)searchPattern didReturn:(NSArray*)results; {
        NSLog(@"%@", results);
    switch (testType) {
        case TestTypeStress:
        {
            if (results != nil && results.count == 1) {
                searchPersonsRet++;
            }
            if (searchPersonsRet == NB_TESTS) {
                NSLog(@"search finished successfully");
                searchPersonsRet = 0;
                [self testStress];
            }
        }
            break;
        case TestTypeRandom:
        {
            NSLog(@"searchFor random returned");
            //double time = (double)(rand() % 5) / 4.0;
            [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(testRandom) userInfo:nil repeats:NO];
        }
            break;
        case TestTypeSearchPersons:
            [self testSearchPersons];
            break;
        default:
            break;
    }
    
}

- (void)searchFailedFor:(NSString*)searchPattern {
    NSLog(@"search failed");
    [directoryService cancelOperationsForDelegate:self];
}

- (void)profilePictureFor:(NSString*)sciper didReturn:(NSData*)data {
    switch (testType) {
        case TestTypeStress:
        {
            if (data != nil) {
                profilePicRet++;
            }
            if (profilePicRet == NB_TESTS) {
                NSLog(@"picture finished successfully");
            }
        }
            break;
        case TestTypeRandom:
        {
            NSLog(@"profilePictureFor random returned");
            //double time = (double)(rand() % 5) / 4.0;
            [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(testRandom) userInfo:nil repeats:NO];
        }
            break;
        default:
            break;
    }
}

- (void)profilePictureFailedFor:(NSString*)sciper {
    NSLog(@"profile picture failed");
    [directoryService cancelOperationsForDelegate:self];
}

- (void)autocompleteFor:(NSString *)constraint didReturn:(NSArray*)results {
    NSLog(@"%@", results);
    switch (testType) {
        case TestTypeStress:
        {
            if (results != nil) {
                autocompletRet++;
            }
            if (autocompletRet == NB_TESTS) {
                NSLog(@"autocomplete finished successfully");
            }
        }
            break;
        case TestTypeRandom:
        {
            NSLog(@"autocompleteFor random returned");
            //double time = (double)(rand() % 5) / 4.0;
            [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(testRandom) userInfo:nil repeats:NO];
        }
            break;
        default:
            break;
    }
}

- (void)autocompleteFailedFor:(NSString *)constraint {
    NSLog(@"autocomplete failed");
    [directoryService cancelOperationsForDelegate:self];
}

- (void)serviceConnectionToServerTimedOut {
    NSLog(@"TIMEOUT");
}

- (void)dealloc
{
    [directoryService release];
    [super dealloc];
}


@end
