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




//  Created by Loïc Gardiol on 05.05.12.


#import <Foundation/Foundation.h>

#import "Service.h"

#import "news.h"

@interface NewsService : Service <ServiceProtocol>

/*
 THRIFT METHODS
 - (NSArray *) getNewsItems: (NSString *) language;  // throws TException
 - (NSString *) getNewsItemContent: (Id) newsItemId;  // throws TException
 - (NSDictionary *) getFeedUrls: (NSString *) language;  // throws TException
 - (NSArray *) getFeeds: (NSString *) language;  // throws TException
 */

- (void)getNewsItemsForLanguage:(NSString*)language delegate:(id)delegate;
- (void)getNewsItemContentForId:(int64_t)newsItemId delegate:(id)delegate;
- (void)getFeedUrlsForLanguage:(NSString*)language delegate:(id)delegate;
- (void)getFeedsForLanguage:(NSString*)language delegate:(id)delegate;

/* Cached versions */

- (NSArray*)getFromCacheNewsItemsForLanguage:(NSString*)language;

@end

@protocol NewsServiceDelegate <ServiceDelegate>

@optional
- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems;
- (void)newsItemsFailedForLanguage:(NSString*)language;
- (void)newsItemContentForId:(int64_t)newsItemId didReturn:(NSString*)content;
- (void)newsItemContentFailedForId:(int64_t)newsItemId;
- (void)feedUrlsForLanguage:(NSString*)language didReturn:(NSDictionary*)feedUrls;
- (void)feedUrlsFailedForLanguage:(NSString*)language;
- (void)feedsForLanguage:(NSString*)language didReturn:(NSArray*)feeds;
- (void)feedsFailedForLanguage:(NSString*)language;

@end
