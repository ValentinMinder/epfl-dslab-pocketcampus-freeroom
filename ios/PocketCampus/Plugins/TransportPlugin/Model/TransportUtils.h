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

//  Created by Loïc Gardiol on 24.03.12.

@import Foundation;

#import "transport.h"

extern NSString* const kTransportDepartureTimeNowString;

@interface TransportUtils : NSObject

+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval; //same as next with accessibilityOriented set to NO
+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval accessibilityOriented:(BOOL)accessibilityOriented;
+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp; //same as next with accessibilityOriented set to NO
+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp accessibilityOriented:(BOOL)accessibilityOriented;
+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp; //same as next with accessibilityOriented set to NO
+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp accessibilityOriented:(BOOL)accessibilityOriented;
+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalMin; //same as next with accessibilityOriented set to NO
+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalMin accessibilityOriented:(BOOL)accessibilityOriented;
+ (NSTimeInterval)secondsBetweenNowAndTimestamp:(NSTimeInterval)timestamp;

/*
 * Returns array of TransportConnection of redundant (and most direct) departures (metro line for ex.) in the query trip result, if some are found. nil otherwise
 * If purgeAlreadyLeft is YES, input of algo is done with queryTripResult.nonLeftTrips
 */
+ (NSArray*)nextRedundantDeparturesFromMessyResult:(QueryTripsResult*)queryTripResult purgeAlreadyLeft:(BOOL)purgeAlreadyLeft;

@end
