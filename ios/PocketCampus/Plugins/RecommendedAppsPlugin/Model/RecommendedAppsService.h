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

- (void)getRecommendedAppsWithDelegate:(id)delegate;

@end

@protocol RecommendedAppsServiceDelegate <ServiceDelegate>

@optional

- (void)getRecommendedAppsDidReturn:(RecommendedAppsResponse*)response;
- (void)getRecommendedAppsFailed;

@end
