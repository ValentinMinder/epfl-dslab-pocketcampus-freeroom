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
    return [[NSLocale preferredLanguages] objectAtIndex:0];
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
    [UIView transitionWithView:tableView duration:duration options:UIViewAnimationOptionCurveEaseInOut animations:^{
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

+ (BOOL)double:(double)d1 isEqualToDouble:(double)d2 epsilon:(double)epsilon {
    return (fabs(d1-d2) < epsilon);
}


+ (void)showServerErrorAlert {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

+ (void)showConnectionToServerTimedOutAlert {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

@end
