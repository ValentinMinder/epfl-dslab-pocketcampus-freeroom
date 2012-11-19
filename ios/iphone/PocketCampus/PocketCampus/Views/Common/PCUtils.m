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

+ (BOOL)isIdiomPad {
    BOOL pad = NO;
#ifdef UI_USER_INTERFACE_IDIOM
    pad = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
#endif
    return pad;
}

+ (BOOL)isOSVersionSmallerThan:(float)version {
    return [[UIDevice currentDevice].systemVersion floatValue] < version;
}

+ (NSString*)userLanguageCode {
    return [[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode];
}

+ (NSString*)lastUpdateNowString {
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    [dateFormatter setLocale:[NSLocale systemLocale]];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    [dateFormatter setDateStyle:NSDateFormatterShortStyle];
    return [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"LastUpdate", @"PocketCampus", nil),[dateFormatter stringFromDate:[NSDate date]]];
}

+ (void)reloadTableView:(UITableView*)tableView withFadingDuration:(NSTimeInterval)duration {
    tableView.alpha = 0.0;
    [tableView reloadData];
    tableView.hidden = NO;
    [UIView transitionWithView:tableView duration:duration options:UIViewAnimationCurveEaseIn animations:^{
        tableView.alpha = 1.0;
    } completion:NULL];
}

+ (void)printFrame:(CGRect)frame {
    NSLog(@"Frame : %lf, %lf, %lf, %lf", frame.origin.x, frame.origin.y, frame.size.width, frame.size.height);
}

+ (NSString*)stringFromFileSize:(unsigned long long)size {
    float floatSize = size;
    if (size<1023)
        return([NSString stringWithFormat:@"%llu bytes",size]);
    floatSize = floatSize / 1024;
    if (floatSize<1023)
        return([NSString stringWithFormat:@"%1.1f KB",floatSize]);
    floatSize = floatSize / 1024;
    if (floatSize<1023)
        return([NSString stringWithFormat:@"%1.1f MB",floatSize]);
    floatSize = floatSize / 1024;
    
    return [NSString stringWithFormat:@"%1.1f GB",floatSize];
}

@end
