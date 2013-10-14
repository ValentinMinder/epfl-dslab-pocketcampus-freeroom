//
//  MoodleResource+Comparison.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "moodle.h"

@interface MoodleResource (Comparison)<NSCopying>

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

@end
