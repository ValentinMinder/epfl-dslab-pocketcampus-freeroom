//
//  MyEduModule+Comparison.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 14.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "myedu.h"

@interface MyEduModule (Comparison) <NSCopying>

- (BOOL)isEqual:(id)object;
- (NSUInteger)hash;

@end
