//
//  NSCache+LGAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "NSCache+LGAdditions.h"

@implementation NSCache (LGAdditions)

- (id)objectForKeyedSubscript:(id<NSCopying>)key {
    return [self objectForKey:key];
}

- (void)setObject:(id)obj forKeyedSubscript:(id<NSCopying>)key {
    [self setObject:obj forKey:key];
}

@end
