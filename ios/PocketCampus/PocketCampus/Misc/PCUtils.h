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

/**
 * You can use these constants for +iconForFileExtension
 */
extern NSString* const kPCUtilsExtensionLink;
extern NSString* const kPCUtilsExtensionFolder;

@interface PCUtils : NSObject

+ (BOOL)isRetinaDevice;
+ (BOOL)is3_5inchDevice;
+ (BOOL)is4inchDevice;
+ (BOOL)is4_7inchDevice;
+ (BOOL)is5_5inchDevice;
+ (BOOL)isIdiomPad;
+ (BOOL)isOSVersionSmallerThan:(float)version;
+ (BOOL)isOSVersionGreaterThanOrEqualTo:(float)version;
+ (float)OSVersion;
+ (NSString*)uniqueDeviceIdentifier;
+ (NSString*)appVersion;
+ (NSString*)userLanguageCode;
+ (BOOL)userLocaleIs24Hour;
+ (BOOL)systemIsOutsideEPFLTimeZone;
+ (NSString*)lastUpdateNowString;
+ (UIEdgeInsets)edgeInsetsForViewController:(UIViewController*)viewController;
+ (void)reloadTableView:(UITableView*)tableView withFadingDuration:(NSTimeInterval)duration;
+ (void)printFrame:(CGRect)frame;
+ (BOOL)double:(double)d1 isEqualToDouble:(double)d2 epsilon:(double)epsilon;

+ (UILabel*)addCenteredLabelInView:(UIView*)view withMessage:(NSString*)message;
+ (void)removeCenteredLabelInView:(UIView*)view;

+ (void)showUnknownErrorAlertTryRefresh:(BOOL)tryRefresh;
+ (void)showServerErrorAlert;
+ (void)showConnectionToServerTimedOutAlert;

+ (NSDictionary*)urlStringParameters:(NSString*)urlString;

/*
 * Computes in background total size of file or folder.
 * totalNbBytes is 0 if either path does not exist or size is actually 0
 * error is YES if there was an error while reading the file
 */
+ (void)fileOrFolderSizeWithPath:(NSString*)path completion:(void (^)(unsigned long long totalNbBytes, BOOL error))completion;

/**
 * @return an system icon for the extension, or a generic file icon if extension is nil or a specific one could not be found.
 * @param normal file extension, or constants defined above (not copies, it must be the same address)
 * @discussion uses UIDocumentInteractionController for that. Images are cached for each extension.
 */
+ (UIImage*)iconForFileExtension:(NSString*)extension;

/*
 * Returns [[Reachability reachabilityForInternetConnection] isReachable], i.e. whether internet is reachable
 */
+ (BOOL)hasDeviceInternetConnection;
+ (BOOL)hasAppAccessToLocation;

+ (void)throwExceptionIfObject:(id)object notKindOfClass:(Class)class;

/**
 * Applies roundf() to x, y, width, height
 */
void PCRoundCGRect(CGRect rect);


@end
