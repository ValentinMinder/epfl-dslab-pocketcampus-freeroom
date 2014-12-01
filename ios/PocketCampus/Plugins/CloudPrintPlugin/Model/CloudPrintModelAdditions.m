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

#import "CloudPrintModelAdditions.h"

#import <objc/runtime.h>

@implementation CloudPrintModelAdditions

+ (NSString*)localizedTitleForMultiPageLayout:(NSInteger)multiPageLayout {
    switch (multiPageLayout) {
        case CloudPrintMultiPageLayout_LEFT_TO_RIGHT_TOP_TO_BOTTOM:
            return NSLocalizedStringFromTable(@"LEFT_TO_RIGHT_TOP_TO_BOTTOM", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_LEFT_TO_RIGHT:
            return NSLocalizedStringFromTable(@"TOP_TO_BOTTOM_LEFT_TO_RIGHT", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_BOTTOM_TO_TOP_LEFT_TO_RIGHT:
            return NSLocalizedStringFromTable(@"BOTTOM_TO_TOP_LEFT_TO_RIGHT", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_BOTTOM_TO_TOP_RIGHT_TO_LEFT:
            return NSLocalizedStringFromTable(@"BOTTOM_TO_TOP_RIGHT_TO_LEFT", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_LEFT_TO_RIGHT_BOTTOM_TO_TOP:
            return NSLocalizedStringFromTable(@"LEFT_TO_RIGHT_BOTTOM_TO_TOP", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_RIGHT_TO_LEFT_BOTTOM_TO_TOP:
            return NSLocalizedStringFromTable(@"RIGHT_TO_LEFT_BOTTOM_TO_TOP", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_RIGHT_TO_LEFT_TOP_TO_BOTTOM:
            return NSLocalizedStringFromTable(@"RIGHT_TO_LEFT_TOP_TO_BOTTOM", @"CloudPrintPlugin", nil);
        case CloudPrintMultiPageLayout_TOP_TO_BOTTOM_RIGHT_TO_LEFT:
            return NSLocalizedStringFromTable(@"TOP_TO_BOTTOM_RIGHT_TO_LEFT", @"CloudPrintPlugin", nil);
    }
    return nil;
}

+ (NSString*)localizedTitleForDoubleSidedConfig:(NSInteger)doubleSidedConfig {
    switch (doubleSidedConfig) {
        case CloudPrintDoubleSidedConfig_SHORT_EDGE:
            return NSLocalizedStringFromTable(@"ShortEdge", @"CloudPrintPlugin", nil);
        case CloudPrintDoubleSidedConfig_LONG_EDGE:
            return NSLocalizedStringFromTable(@"LongEdge", @"CloudPrintPlugin", nil);
    }
    return nil;
}

+ (NSString*)localizedTitleForNbPagesPerSheet:(NSInteger)nbPagesPerSheet {
    if (nbPagesPerSheet == 1) {
        return [NSString stringWithFormat:@"%d", 1];
    }
    switch (nbPagesPerSheet) {
        case CloudPrintNbPagesPerSheet_TWO:
            return [NSString stringWithFormat:@"%d", 2];
        case CloudPrintNbPagesPerSheet_FOUR:
            return [NSString stringWithFormat:@"%d", 4];
        case CloudPrintNbPagesPerSheet_SIX:
            return [NSString stringWithFormat:@"%d", 6];
        case CloudPrintNbPagesPerSheet_NINE:
            return [NSString stringWithFormat:@"%d", 9];
        case CloudPrintNbPagesPerSheet_SIXTEEN:
            return [NSString stringWithFormat:@"%d", 16];
    }
    return nil;
}

+ (NSString*)localizedTitleForOrientation:(NSInteger)orientation {
    switch (orientation) {
        case CloudPrintOrientation_PORTRAIT:
            return NSLocalizedStringFromTable(@"Portrait", @"CloudPrintPlugin", nil);
        case CloudPrintOrientation_LANDSCAPE:
            return NSLocalizedStringFromTable(@"Landscape", @"CloudPrintPlugin", nil);
        case CloudPrintOrientation_REVERSE_PORTRAIT:
            return NSLocalizedStringFromTable(@"ReversePortrait", @"CloudPrintPlugin", nil);
        case CloudPrintOrientation_REVERSE_LANDSCAPE:
            return NSLocalizedStringFromTable(@"ReverseLandscape", @"CloudPrintPlugin", nil);
    }
    return nil;
}

@end

@implementation PrintDocumentRequest (Additions)

+ (PrintDocumentRequest*)createDefaultRequest {
    PrintDocumentRequest* request = [PrintDocumentRequest new];
    request.documentId = 0;
    request.orientation = CloudPrintOrientation_PORTRAIT;
    request.colorConfig = CloudPrintColorConfig_BLACK_WHITE;
    request.doubleSided = CloudPrintDoubleSidedConfig_LONG_EDGE;
    return request;
}

- (NSString*)jobUniqueId {
    static NSString* key;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        key = NSStringFromSelector(_cmd);
    });
    id value = objc_getAssociatedObject(self, (__bridge const void *)(key));
    if (!value) {
        unsigned int r = arc4random();
        value = [NSString stringWithFormat:@"%u", r];
        objc_setAssociatedObject(self, (__bridge const void *)(key), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return value;
}

- (BOOL)documentIdIsDefault {
    return self.documentId == 0;
}

@end