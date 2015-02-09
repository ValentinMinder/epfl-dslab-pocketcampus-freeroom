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

NSString* const kPCUtilsExtensionLink = @"PCUtilsExtensionLink";
NSString* const kPCUtilsExtensionFolder = @"PCUtilsExtensionFolder";

@implementation PCUtils

+ (BOOL)isRetinaDevice{
    return [UIScreen mainScreen].scale >= 2.0;
}

+ (BOOL)is3_5inchDevice {
    return ([UIScreen mainScreen].bounds.size.height == 480);
}

+ (BOOL)is4inchDevice {
    return ([UIScreen mainScreen].bounds.size.height == 568);
}

+ (BOOL)is4_7inchDevice {
    return ([UIScreen mainScreen].bounds.size.height == 667);
}

+ (BOOL)is5_5inchDevice {
    return ([UIScreen mainScreen].bounds.size.height == 736);
}

+ (BOOL)isIdiomPad {
    BOOL pad = NO;
#ifdef UI_USER_INTERFACE_IDIOM
    pad = (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
#endif
    return pad;
}

+ (NSString*)pathForImageResource:(NSString*)resourceName {
    [PCUtils throwExceptionIfObject:resourceName notKindOfClass:[NSString class]];
    return [[NSBundle mainBundle] pathForResource:[NSString stringWithFormat:@"%@%@", resourceName, [PCUtils postfixForResources]] ofType:@"png"];
}

+ (NSString*)postfixForResources {
    CGFloat scale = [UIScreen mainScreen].scale;
    if (scale == 2.0) {
        return @"@2x";
    }
    if (scale == 3.0) {
        return @"@3x";
    }
    return @"";
}

+ (BOOL)isOSVersionSmallerThan:(float)version {
    return [[UIDevice currentDevice].systemVersion floatValue] < version;
}

+ (BOOL)isOSVersionGreaterThanOrEqualTo:(float)version {
    return [[UIDevice currentDevice].systemVersion floatValue] >= version;
}

+ (float)OSVersion {
   return [[UIDevice currentDevice].systemVersion floatValue];
}

+ (NSString*)uniqueDeviceIdentifier {
    return [[[UIDevice currentDevice] identifierForVendor] UUIDString];
}

+ (NSString*)appVersion {
    return [[NSBundle mainBundle] infoDictionary][@"CFBundleShortVersionString"];
}

+ (NSString*)userLanguageCode {
    return [NSLocale preferredLanguages][0];
}

+ (BOOL)userLocaleIs24Hour {
    NSString* formatStringForHours = [NSDateFormatter dateFormatFromTemplate:@"j" options:0 locale:[NSLocale currentLocale]];
    NSRange containsA = [formatStringForHours rangeOfString:@"a"];
    BOOL hasAMPM = containsA.location != NSNotFound;
    return !hasAMPM;
}

+ (BOOL)systemIsOutsideEPFLTimeZone {
    NSTimeZone* epflTimeZone = [NSTimeZone timeZoneWithName:@"Europe/Zurich"];
    NSTimeZone* systemTimeZone = [NSTimeZone systemTimeZone];
    return (epflTimeZone.secondsFromGMT != systemTimeZone.secondsFromGMT);
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
    bottom += viewController.navigationController.toolbarHidden ? 0.0 : viewController.navigationController.toolbar.frame.size.height;
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

+ (void)showUnknownErrorAlertTryRefresh:(BOOL)tryRefresh {
#ifndef TARGET_IS_EXTENSION
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:tryRefresh ? NSLocalizedStringFromTable(@"UnknownErrorTryRefresh", @"PocketCampus", nil) : NSLocalizedStringFromTable(@"UnknownError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
#endif
}

+ (void)showServerErrorAlert {
#ifndef TARGET_IS_EXTENSION
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
#endif
}

+ (void)showConnectionToServerTimedOutAlert {
#ifndef TARGET_IS_EXTENSION
    [[[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] show];
#endif
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
            value = [value stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding]; // convert HTML entities
            value = [value stringByReplacingOccurrencesOfString:@"+" withString:@" "]; //sometimes + are used for spaces in URLs
            queryStringDictionary[key] = value;
        }
    }
    @catch (NSException *exception) {
        return nil;
    }
    return  [queryStringDictionary copy]; //non-mutable copy
}

+ (void)fileOrFolderSizeWithPath:(NSString*)path completion:(void (^)(unsigned long long totalNbBytes, BOOL error))completion {
    [PCUtils throwExceptionIfObject:path notKindOfClass:[NSString class]];
    if (!completion) {
        return;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSFileManager* fileManager = [NSFileManager new];
        BOOL isDirectory;
        BOOL fileExists = [fileManager fileExistsAtPath:path isDirectory:&isDirectory];
        if (!fileExists) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(0, NO);
            });
            return;
        }
        unsigned long long totalSize = 0;
        NSDirectoryEnumerator* dirEnum = [fileManager enumeratorAtPath:path];
        NSString* file;
        while ((file = [dirEnum nextObject])) {
            NSDictionary* attributes = [dirEnum fileAttributes];
            if (!attributes) {
                completion(0, YES);
                return;
            }
            if ([attributes[NSFileType] isEqualToString:NSFileTypeDirectory]) {
                continue;
            }
            totalSize += [attributes fileSize];
        }
        dispatch_async(dispatch_get_main_queue(), ^(void) {
            completion(totalSize, NO);
        });
    });
}

+ (UIImage*)iconForFileExtension:(NSString*)extension {
    if (extension == kPCUtilsExtensionFolder) {
        return [UIImage imageNamed:@"FolderIcon"];
    }
    if (extension == kPCUtilsExtensionLink) {
        return [UIImage imageNamed:@"LinkIcon"];
    }
    static NSCache* cachedIconForExtension;
    static NSString* const kExtensionForGenericFile = @"qwertzuiop"; //this exentsion does not exist, thus a generic file icon will be generated
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        cachedIconForExtension = [NSCache new];
    });
    if (!extension) {
        extension = kExtensionForGenericFile;
    }
    UIImage* cachedIcon = cachedIconForExtension[extension];
    if (cachedIcon) {
        return cachedIcon;
    }
    
    // This is a trick, UIDocumentInteractionController does not actually need
    // to have the file downloaded to have the icon, it just looks at the extension.
    NSString* fakePath = [NSString stringWithFormat:@"sample.%@", extension];
    UIDocumentInteractionController* controller = [UIDocumentInteractionController interactionControllerWithURL:[NSURL fileURLWithPath:fakePath]];
    UIImage* systemImage = [controller.icons lastObject]; //take biggest as source, see doc.
    if (!systemImage) {
        // should not happen, doc says controller.icons ALWAYS contain an image
        return nil;
    }
    
    CGFloat newWidth = ceilf(systemImage.size.width * 0.89);
    CGFloat newHeight = ceilf(systemImage.size.height * 0.89);
    UIImage* smallerSystemImage = [systemImage imageScaledToSize:CGSizeMake(newWidth, newHeight) applyDeviceScreenMultiplyingFactor:NO];
    
    UIGraphicsBeginImageContextWithOptions(systemImage.size, NO, systemImage.scale);
    CGFloat x = (systemImage.size.width - newWidth) / 2.0;
    CGFloat y = (systemImage.size.height - newHeight) / 2.0;
    [smallerSystemImage drawAtPoint:CGPointMake(x, y)];
    UIImage* finalImage = UIGraphicsGetImageFromCurrentImageContext();
    
    cachedIconForExtension[extension] = finalImage ?: systemImage;
    return finalImage ?: systemImage;
}

+ (BOOL)hasDeviceInternetConnection {
    AFNetworkReachabilityManager* manager = [AFNetworkReachabilityManager sharedManager];
    if (manager.networkReachabilityStatus == AFNetworkReachabilityStatusUnknown) {
        return YES;
    }
    return [manager isReachable];
}

+ (BOOL)hasAppAccessToLocation {
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    if ([PCUtils isOSVersionSmallerThan:8.0]) {
#ifndef TARGET_IS_EXTENSION
        return (status == kCLAuthorizationStatusAuthorized || status == kCLAuthorizationStatusNotDetermined);
#else
        return NO;
#endif
    } else {
        return (status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusNotDetermined);
    }
}

+ (void)throwExceptionIfObject:(id)object notKindOfClass:(Class)class; {
    if (![object isKindOfClass:class]) {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:[NSString stringWithFormat:@"object '%@' must be kind of class %@", object, NSStringFromClass(class)] userInfo:nil];
    }
}

void PCRoundCGRect(CGRect rect) {
    rect.origin.x = roundf(rect.origin.x);
    rect.origin.y = roundf(rect.origin.y);
    rect.size.width = roundf(rect.size.width);
    rect.size.height = roundf(rect.size.height);
}

@end
