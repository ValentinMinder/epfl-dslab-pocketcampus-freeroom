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

//  Created by Loïc Gardiol on 24.04.14.


#import "NewsModelAdditions.h"

NSString* const kNewsFeedItemImageUrlParameterX = @"{x}";
NSString* const kNewsFeedItemImageUrlParameterY = @"{y}";

@implementation NewsFeedItem (Additions)

- (NSString*)imageUrlStringForSize:(CGSize)size applyDeviceScreenMultiplyingFactor:(BOOL)applyFactor {
    return [NewsFeedItem imageUrlStringForParameterizedImageUrl:self.imageUrl size:size applyDeviceScreenMultiplyingFactor:applyFactor];
}

- (BOOL)isEqualToNewsFeedItem:(NewsFeedItem*)otherItem {
    return [self.title isEqualToString:otherItem.title];
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToNewsFeedItem:object];
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += [self.title hash];
    return hash;
}

- (NSComparisonResult)compareDateToNewsFeedItem:(NewsFeedItem*)otherItem {
    if (self.date < otherItem.date) {
        return NSOrderedDescending;
    } else if (self.date > otherItem.date) {
        return NSOrderedAscending;
    } else {
        //order same => sort alphabetically
        return [self.title compare:otherItem.title];
    }
}

#pragma mark Private

+ (NSString*)imageUrlStringForParameterizedImageUrl:(NSString*)parameterizedImageUrl size:(CGSize)size applyDeviceScreenMultiplyingFactor:(BOOL)applyFactor {
    if (applyFactor) {
        CGFloat screenScale = [UIScreen mainScreen].scale;
        size.width *= screenScale;
        size.height *= screenScale;
    }
    NSString* imageUrl = [parameterizedImageUrl stringByReplacingOccurrencesOfString:kNewsFeedItemImageUrlParameterX withString:[NSString stringWithFormat:@"%d", (int)size.width]];
    imageUrl = [imageUrl stringByReplacingOccurrencesOfString:kNewsFeedItemImageUrlParameterY withString:[NSString stringWithFormat:@"%d", (int)size.height]];
    return imageUrl;
}

@end

@implementation NewsFeedItemContent (Additions)

- (NSString*)imageUrlStringForSize:(CGSize)size applyDeviceScreenMultiplyingFactor:(BOOL)applyFactor {
    return [NewsFeedItem imageUrlStringForParameterizedImageUrl:self.imageUrl size:size applyDeviceScreenMultiplyingFactor:applyFactor];
}

- (NSString*)contentWithoutMainImage {
    if (!self.imageUrl) {
        return self.content;
    }
    NSString* imageUrlWithoutFilename = [[[NSURL URLWithString:[self imageUrlStringForSize:CGSizeZero applyDeviceScreenMultiplyingFactor:NO]] URLByDeletingLastPathComponent] absoluteString];
    
    NSError* error = nil;
    NSString* pattern = [NSString stringWithFormat:@"(<img.*%@.*>)", imageUrlWithoutFilename];
    NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:&error];
    NSTextCheckingResult* result = [regex firstMatchInString:self.content options:0 range:NSMakeRange(0, self.content.length)];
    if (result.numberOfRanges > 1) {
        NSRange range = [result rangeAtIndex:1];
        if (range.length != 0) {
            return [self.content stringByReplacingCharactersInRange:range withString:@""];
        }
    }
    return self.content;
}

@end
