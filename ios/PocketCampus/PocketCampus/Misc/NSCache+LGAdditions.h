//
//  NSCache+LGAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 03.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSCache (LGAdditions)

- (id)objectForKeyedSubscript:(id<NSCopying>)key;
- (void)setObject:(id)obj forKeyedSubscript:(id<NSCopying>)key;

@end
