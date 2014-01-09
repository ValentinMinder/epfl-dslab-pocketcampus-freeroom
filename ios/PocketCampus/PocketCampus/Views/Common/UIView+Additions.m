//
//  UIView+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "UIView+Additions.h"

@implementation UIView (Additions)

#pragma mark - Public

- (NSArray*)allSubviews {
    NSMutableArray* allSubviews = [NSMutableArray array];
    [self subviewsWithBuffer:allSubviews];
    return allSubviews;
}

#pragma mark - Private

- (void)subviewsWithBuffer:(NSMutableArray*)result {
    [result addObjectsFromArray:self.subviews];
    for (UIView* view in self.subviews) {
        [view subviewsWithBuffer:result];
    }
}

@end
