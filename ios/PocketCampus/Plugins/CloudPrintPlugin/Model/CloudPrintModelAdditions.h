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

//  Created by Loïc Gardiol on 04.09.14.

#import "cloudprint.h"

@interface CloudPrintModelAdditions : NSObject

/**
 * @return a localized title for an enum value of CloudPrintMultiPageLayout,
 * nil if multiPageLayout does not exist.
 */
+ (NSString*)localizedTitleForMultiPageLayout:(NSInteger)multiPageLayout;

/**
 * @return a localized title for an enum value of CloudPrintDoubleSidedConfig,
 * nil if doubleSidedConfig does not exist.
 */
+ (NSString*)localizedTitleForDoubleSidedConfig:(NSInteger)doubleSidedConfig;

/**
 * @return a localized title for an enum value of CloudPrintNbPagesPerSheet,
 * "1" if nbPagesPerSheet is 1, nil for any other value.
 */
+ (NSString*)localizedTitleForNbPagesPerSheet:(NSInteger)nbPagesPerSheet;

/**
 * @return a localized title for an enum value of CloudPrintOrientation,
 * nil if orientation does not exist.
 */
+ (NSString*)localizedTitleForOrientation:(NSInteger)orientation;

@end

@interface PrintDocumentRequest (Additions)

+ (PrintDocumentRequest*)createDefaultRequest;

/**
 * @return a string ID that is unique for that print document request.
 * @discussion the id is randomly generated the first time the property is accessed and does not rely on any attribute value.
 */
@property (nonatomic, readonly) NSString* jobUniqueId;

/**
 * @return YES if documentId is the default
 * value, thus meaning that it was not set by the server.
 * Document typically needs to be uploaded if so.
 */
@property (nonatomic, readonly) BOOL documentIdIsDefault;

@end
