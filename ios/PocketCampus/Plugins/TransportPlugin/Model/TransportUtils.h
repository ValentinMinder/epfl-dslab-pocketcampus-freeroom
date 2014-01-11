

//  Created by Loïc Gardiol on 24.03.12.


#import <Foundation/Foundation.h>

#import "transport.h"

@interface TransportUtils : NSObject

+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval;
+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp;
+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp;
+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalSeconds;
+ (NSTimeInterval)secondsBetweenNowAndTimestamp:(NSTimeInterval)timestamp;

/*
 * Returns array of TransportConnection of redundant (and most direct) departures (metro line for ex.) in the query trip result, if some are found. nil otherwise
 * If purgeAlreadyLeft is YES, input of algo is done with queryTripResult.nonLeftTrips
 */
+ (NSArray*)nextRedundantDeparturesFromMessyResult:(QueryTripsResult*)queryTripResult purgeAlreadyLeft:(BOOL)purgeAlreadyLeft;

@end
