//
//  PCUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 04.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "PCUtils.h"

#import "Reachability.h"

#import <CoreLocation/CoreLocation.h>

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

+ (float)OSVersion {
   return [[UIDevice currentDevice].systemVersion floatValue];
}

+ (NSString*)uniqueDeviceIdentifier {
#ifdef DEBUG
    return @"TEST_IOS";
#else
    return [[[UIDevice currentDevice] identifierForVendor] UUIDString];
#endif
}

+ (NSString*)appVersion {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
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

+ (UIEdgeInsets)edgeInsetsForViewController:(UIViewController*)viewController {
    CGFloat topBar = [viewController prefersStatusBarHidden] ? 0.0 : 20.0;
    CGFloat top = viewController.navigationController ? topBar + viewController.navigationController.navigationBar.frame.size.height : topBar;
    CGFloat bottom = viewController.tabBarController ? viewController.tabBarController.tabBarController.tabBar.frame.size.height : 0.0;
    return UIEdgeInsetsMake(top, 0, bottom, 0);
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


+ (UILabel*)addCenteredLabelInView:(UIView*)view withMessage:(NSString*)message {
    [self removeCenteredLabelInView:view];
    UILabel* label = [[UILabel alloc] initWithFrame:view.frame];
    label.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    label.text = message;
    label.tag = 20;
    label.textAlignment = NSTextAlignmentCenter;
    label.numberOfLines = 0;
    label.textColor = [UIColor colorWithWhite:0.33 alpha:1.0];
    [view addSubview:label];
    return label;
}


+ (void)removeCenteredLabelInView:(UIView*)view {
    [[view viewWithTag:20] removeFromSuperview];
}


+ (void)showServerErrorAlert {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

+ (void)showConnectionToServerTimedOutAlert {
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
}

+ (NSDictionary*)urlStringParameters:(NSString*)urlString {
    
    [self throwExceptionIfObject:urlString notKindOfClass:[NSString class]];
    
    NSMutableDictionary* queryStringDictionary = [[NSMutableDictionary alloc] init];
    @try {
        
        NSArray* urlComponents = [urlString componentsSeparatedByString:@"?"];
        
        NSArray* paramsComponents = [urlComponents[1] componentsSeparatedByString:@"&"];
        
        for (NSString* keyValuePair in paramsComponents) {
            NSArray* pairComponents = [keyValuePair componentsSeparatedByString:@"="];
            NSString* key = [pairComponents objectAtIndex:0];
            NSString* value = [pairComponents objectAtIndex:1];
            [queryStringDictionary setObject:value forKey:key];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"!! ERROR: wrong URL format");
    }
    return  [queryStringDictionary copy]; //non-mutable copy
}

+ (BOOL)hasDeviceInternetConnection {
    return [[Reachability reachabilityForInternetConnection] isReachable];
}

+ (BOOL)hasAppAccessToLocation {
    return ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorized);
}

+ (void)throwExceptionIfObject:(id)object notKindOfClass:(Class)class; {
    if (![object isKindOfClass:class]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:[NSString stringWithFormat:@"object '%@' must be kind of class %@", object, NSStringFromClass(class)] userInfo:nil];
    }
}

@end
