//
//  EpflRestaurant+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 19.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EpflRestaurant+Additions.h"

@implementation EpflRestaurant (Additions)

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToEpflRestaurant:object];
}

- (BOOL)isEqualToEpflRestaurant:(EpflRestaurant*)otherRestaurant {
    [PCUtils throwExceptionIfObject:otherRestaurant notKindOfClass:[EpflRestaurant class]];
    return self.rId == otherRestaurant.rId;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.rId;
    return hash;
}

@end
