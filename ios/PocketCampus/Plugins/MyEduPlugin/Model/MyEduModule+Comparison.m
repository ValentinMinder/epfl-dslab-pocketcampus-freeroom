//
//  MyEduModule+Comparison.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 14.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MyEduModule+Comparison.h"

@implementation MyEduModule (Comparison)

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToMyEduModule:object];
}

- (BOOL)isEqualToMyEduModule:(MyEduModule*)module {
    return self.iId == module.iId;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.iId;
    return hash;
}

- (id)copyWithZone:(NSZone *)zone {
    MyEduModule* newInstance = [[[self class] allocWithZone:zone] init];
    newInstance.iId = self.iId;
    newInstance.iSectionId = self.iSectionId;
    newInstance.iTitle = [self.iTitle copy];
    newInstance.iVisible = self.iVisible;
    newInstance.iTextContent = [self.iTextContent copy];
    newInstance.iVideoSourceProvider = [self.iVideoSourceProvider copy];
    newInstance.iVideoID = [self.iVideoID copy];
    newInstance.iVideoDownloadURL = [self.iVideoDownloadURL copy];
    newInstance.iCreationTimestamp = self.iCreationTimestamp;
    newInstance.iLastUpdateTimestamp = self.iLastUpdateTimestamp;
    return newInstance;
}


@end
