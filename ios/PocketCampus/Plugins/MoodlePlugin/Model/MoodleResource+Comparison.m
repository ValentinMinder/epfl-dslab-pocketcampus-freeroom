//
//  MoodleResource+Comparison.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MoodleResource+Comparison.h"

@implementation MoodleResource (Comparison)


- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMoodleResource:object];
}

- (BOOL)isEqualToMoodleResource:(MoodleResource*)moodleResource {
    return [self.iUrl isEqualToString:moodleResource.iUrl];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.iUrl hash];
    return hash;
}

- (id)copyWithZone:(NSZone *)zone {
    MoodleResource* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.iName = self.iName;
    newInstance.iUrl = self.iUrl;
    return newInstance;
}


@end
