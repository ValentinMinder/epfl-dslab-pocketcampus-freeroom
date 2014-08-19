//
//  RecommendedAppsService.h
//  PocketCampus
//
//  Created by Loïc Gardiol (loic.gardiol@gmail.com)
//

#import <Foundation/Foundation.h>

#import "Service.h"

#import "recommendedapps.h"

@interface RecommendedAppsService : Service <ServiceProtocol>

- (void)getRecommendedApps:(RecommendedAppsRequest*)request delegate:(id)delegate;

@end

@protocol RecommendedAppsServiceDelegate <ServiceDelegate>

@optional

- (void)getRecommendedAppsForRequest:(RecommendedAppsRequest*)request didReturn:(RecommendedAppsResponse*)response;
- (void)getRecommendedAppsFailedForRequest:(RecommendedAppsRequest*)request;

@end
