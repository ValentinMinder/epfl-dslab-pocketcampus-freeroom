//
//  NewsService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 05.05.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

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
- (void)getNewsItemContentForId:(Id)newsItemId delegate:(id)delegate;
- (void)getFeedUrlsForLanguage:(NSString*)language delegate:(id)delegate;
- (void)getFeedsForLanguage:(NSString*)language delegate:(id)delegate;

@end

@protocol NewsServiceDelegate <ServiceDelegate>

@optional
- (void)newsItemsForLanguage:(NSString*)language didReturn:(NSArray*)newsItems;
- (void)newsItemsFailedForLanguage:(NSString*)language;
- (void)newsItemContentForId:(Id)newsItemId didReturn:(NSString*)content;
- (void)newsItemContentFailedForId:(Id)newsItemId;
- (void)feedUrlsForLanguage:(NSString*)language didReturn:(NSDictionary*)feedUrls;
- (void)feedUrlsFailedForLanguage:(NSString*)language;
- (void)feedsForLanguage:(NSString*)language didReturn:(NSArray*)feeds;
- (void)feedsFailedForLanguage:(NSString*)language;

@end
