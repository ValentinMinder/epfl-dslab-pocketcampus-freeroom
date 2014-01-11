/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */




//  Created by Loïc Gardiol on 04.07.12.


#import "PCUtils.h"

#import "AFNetworking.h"

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
    return [[NSBundle mainBundle] infoDictionary][@"CFBundleShortVersionString"];
}

+ (NSString*)userLanguageCode {
    return [NSLocale preferredLanguages][0];
}

+ (NSString*)lastUpdateNowString {
    static NSDateFormatter* dateFormatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        dateFormatter = [NSDateFormatter new];
        dateFormatter.timeZone = [NSTimeZone systemTimeZone];
        dateFormatter.locale = [NSLocale systemLocale];
        dateFormatter.timeStyle = NSDateFormatterShortStyle;
        dateFormatter.dateStyle = NSDateFormatterShortStyle;
    });
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
            NSString* key = pairComponents[0];
            NSString* value = pairComponents[1];
            [queryStringDictionary setObject:value forKey:key];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"!! ERROR: wrong URL format");
    }
    return  [queryStringDictionary copy]; //non-mutable copy
}

+ (BOOL)hasDeviceInternetConnection {
    return [[AFNetworkReachabilityManager sharedManager] isReachable];
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
