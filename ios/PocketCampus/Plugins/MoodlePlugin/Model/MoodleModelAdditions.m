//
//  MoodleResource+Comparison.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MoodleModelAdditions.h"

#import <objc/runtime.h>

@implementation MoodleResource (Comparison)

- (NSString*)filename {
    static NSString* const kFilenameKey = @"filename";
    NSString* filename = objc_getAssociatedObject(self, (__bridge const void *)(kFilenameKey));
    if (!filename) {
        NSString* filename = [[self.iUrl pathComponents] lastObject];
        objc_setAssociatedObject(self, (__bridge const void *)(kFilenameKey), filename, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return filename;
}

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

@implementation MoodleSection (Additions)

- (id)copyWithZone:(NSZone *)zone {
    MoodleSection* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.iResources = [self.iResources copy];
    newInstance.iText = [self.iText copy];
    newInstance.iStartDate = self.iStartDate;
    newInstance.iEndDate = self.iEndDate;
    newInstance.iCurrent = self.iCurrent;
    return newInstance;
}

@end