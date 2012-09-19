//
//  PCUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCUtils.h"

@implementation PCUtils

+ (BOOL)isRetinaDevice{
    return ([[UIScreen mainScreen] respondsToSelector:@selector(displayLinkWithTarget:selector:)] && ([UIScreen mainScreen].scale == 2.0))?1:0;
}

+ (BOOL)is4inchDevice {
    if ([UIScreen mainScreen].bounds.size.height == 568) {
        return YES;
    }
    return NO;
}

+ (BOOL)isOSVersionSmallerThan:(float)version {
    return [[UIDevice currentDevice].systemVersion floatValue] < version;
}

+ (void)reloadTableView:(UITableView*)tableView withFadingDuration:(NSTimeInterval)duration {
    tableView.alpha = 0.0;
    [tableView reloadData];
    tableView.hidden = NO;
    [UIView transitionWithView:tableView duration:duration options:UIViewAnimationCurveEaseIn animations:^{
        tableView.alpha = 1.0;
    } completion:NULL];
}

@end
