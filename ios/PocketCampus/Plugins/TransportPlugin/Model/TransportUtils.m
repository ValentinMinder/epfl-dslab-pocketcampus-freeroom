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

#import "TransportUtils.h"

#import "TransportModelAdditions.h"

NSString* const kTransportDepartureTimeNowString = @"Now";

@implementation TransportUtils

+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval {
    return [self durationgStringForInterval:interval accessibilityOriented:NO];
}

+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval accessibilityOriented:(BOOL)accessibilityOriented {
    NSCalendar* systemCalendar = [NSCalendar currentCalendar];
    NSDate* date1 = [NSDate new];
    NSDate* date2 = [[NSDate alloc] initWithTimeInterval:interval sinceDate:date1];
    unsigned int unitFlags = NSHourCalendarUnit | NSMinuteCalendarUnit | NSDayCalendarUnit | NSMonthCalendarUnit;
    NSDateComponents* conversionInfo = [systemCalendar components:unitFlags fromDate:date1  toDate:date2  options:0];
    return [NSString stringWithFormat:@"%d%@%02d%@", (int)[conversionInfo hour], accessibilityOriented ? @"h " : @":", (int)[conversionInfo minute], accessibilityOriented ? @"min" : @""];
}

+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp {
    return [self hourMinutesStringForTimestamp:timestamp accessibilityOriented:NO];
}

+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp accessibilityOriented:(BOOL)accessibilityOriented {
    static NSDateFormatter* dateFormatter = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        dateFormatter = [NSDateFormatter new];
        dateFormatter.timeZone = [NSTimeZone systemTimeZone];
        dateFormatter.locale = [NSLocale systemLocale];
        dateFormatter.timeStyle = NSDateFormatterShortStyle;
        dateFormatter.dateStyle = NSDateFormatterNoStyle;
    });
    dateFormatter.dateFormat = accessibilityOriented ? @"HH'h'mm" : @"HH:mm"; //h to pronouce have VoiceOver pronouce "hour"
    return [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:timestamp]];
}

+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp {
    return [self automaticHoursMinutesLeftStringForTimestamp:timestamp accessibilityOriented:NO];
}

+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp accessibilityOriented:(BOOL)accessibilityOriented {
    NSDate* nowDate = [NSDate date];
    NSTimeInterval seconds = timestamp - [nowDate timeIntervalSince1970];
    if (seconds < 0.0) {
        if (seconds > -60.0) { //might still consider that it (train, bus, ...) is not left yet
            return accessibilityOriented ? NSLocalizedStringFromTable(@"Now", @"PocketCampus", nil) : kTransportDepartureTimeNowString;
        } else {
            return accessibilityOriented ? NSLocalizedStringFromTable(@"LeftAlready", @"TransportPlugin", nil) : @"Left"; //already left, should not show this result
        }
    }
    double minutesLeft = floor((seconds/60.0)+0.5);
    if (minutesLeft == 0.0) {
        return accessibilityOriented ? NSLocalizedStringFromTable(@"Now", @"PocketCampus", nil) : kTransportDepartureTimeNowString;
    }
    
    double hoursLeft = floor(minutesLeft/60.0);
    if (hoursLeft > 0.0) {
        return [NSString stringWithFormat:@"%1.0lfh%2.0lf%@",hoursLeft, minutesLeft, accessibilityOriented ? @"min" : @"'"];
    }
    
    return [NSString stringWithFormat:@"%2.0lf%@", minutesLeft, accessibilityOriented ? @"min" : @"'"];
}


+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalMin {
    return [self automaticTimeStringForTimestamp:timestamp maxIntervalForMinutesLeftString:maxIntervalMin accessibilityOriented:NO];
}

+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalMin accessibilityOriented:(BOOL)accessibilityOriented {
    NSDate* nowDate = [NSDate dateWithTimeIntervalSinceNow:0]; //timestamp GMT
    
    /*NSTimeZone* zone = [NSTimeZone systemTimeZone];
     
     NSTimeInterval nowTimestampLocal = [nowDate timeIntervalSince1970] + [zone secondsFromGMTForDate:nowDate]; //timestamp local
     
     NSDate* nowLocalDate = [NSDate dateWithTimeIntervalSince1970:nowTimestampLocal];
     
     NSDate* depDate = [NSDate dateWithTimeIntervalSince1970:timestamp];
     
     NSTimeInterval depTimestampLocal = timestamp + [zone secondsFromGMTForDate:depDate];
     
     NSDate* depLocalDate = [NSDate dateWithTimeIntervalSince1970:depTimestampLocal];
     
     NSLog(@"now : %@, arr : %@",nowLocalDate, depLocalDate);*/
    
    if (timestamp - [nowDate timeIntervalSince1970] > maxIntervalMin*60) {
        return [self hourMinutesStringForTimestamp:timestamp accessibilityOriented:accessibilityOriented];
    } else {
        return [self automaticHoursMinutesLeftStringForTimestamp:timestamp accessibilityOriented:accessibilityOriented];
    }
}

+ (NSTimeInterval)secondsBetweenNowAndTimestamp:(NSTimeInterval)timestamp {
    NSDate* nowDate = [NSDate dateWithTimeIntervalSinceNow:0];
    return (timestamp - [nowDate timeIntervalSince1970]);
}


+ (NSArray*)nextRedundantDeparturesFromMessyResult:(QueryTripsResult*)queryTripResult purgeAlreadyLeft:(BOOL)purgeAlreadyLeft {
    
    @try {
        
        if (!queryTripResult.connections) {
            return nil;
        }
        
        NSArray* queryTripResultConnections = purgeAlreadyLeft ? queryTripResult.nonLeftTrips : queryTripResult.connections;
        
        /* check for direct trips first */
        
        NSMutableArray* directTripsConnections = [NSMutableArray array]; //First and only TransportConnection of each direct trip from result
        for (TransportTrip* trip in queryTripResultConnections) {
            if (trip.parts && trip.parts.count == 1) {
                [directTripsConnections addObject:trip.parts[0]];
                //NSLog(@"found direct");
            }
        }
        if (directTripsConnections.count >= 3) { //if at least 3 direct connections in QueryTripsResult 
            NSMutableDictionary* connectionsForLine = [NSMutableDictionary dictionary];
            for (TransportConnection* directConnection in directTripsConnections) {
                if (!directConnection.line.name) {
                    continue;
                }
                NSString* lineNicerName = directConnection.line.shortName;
                if (!connectionsForLine[lineNicerName]) {
                    connectionsForLine[lineNicerName] = [NSMutableArray arrayWithObject:directConnection];
                    //NSLog(@"found new direct potentialRepeatingConnection line : %@", lineNicerName);
                } else {
                    NSMutableArray* lineDirectConnections = connectionsForLine[lineNicerName];
                    [lineDirectConnections addObject:directConnection];
                    //NSLog(@"found existing direct potentialRepeatingConnection line : %@", lineNicerName);
                }
            }
            
            NSUInteger maxConnections = 0;
            NSMutableArray* linesConnections = [NSMutableArray array];
            for (NSArray* lineDirectConnections in [connectionsForLine allValues]) {
                if (lineDirectConnections.count >= maxConnections && lineDirectConnections.count > 1) {
                    maxConnections = lineDirectConnections.count;
                    [linesConnections addObject:lineDirectConnections];
                }
            }
            
            [linesConnections sortUsingComparator:^NSComparisonResult(NSArray* line1Connections, NSArray* line2Connections) {
                if (line1Connections.count < line2Connections.count) {
                    return NSOrderedDescending; //inversed, so that connections that appear more often are at the beginning the array
                } else if (line1Connections.count > line2Connections.count) {
                    return NSOrderedAscending;
                } else {
                    return NSOrderedSame;
                }
            }];
            
            NSArray* maxLineDirectConnections;
            
            if (linesConnections.count == 0) {
                return nil;
            }
            
            if (linesConnections.count == 1) {
                maxLineDirectConnections = linesConnections[0];
            }
            
            if (linesConnections.count > 1) {
                maxLineDirectConnections = linesConnections[0];
                NSArray* line2Connections = linesConnections[1];
                if (maxLineDirectConnections.count < 2*line2Connections.count) { //means number of connections of line 1 is not really dominant compared to second line that appears the most often => both line are considered important thus nil is returned
                    return nil;
                }
            }
            
            TransportTrip* firstTrip = queryTripResultConnections[0];
            TransportConnection* firstConnection = firstTrip.parts[0];
            if (firstConnection.isFeetConnection) {
                firstConnection = firstTrip.parts[1];
            }
            TransportConnection* firstConnectionFromResult = maxLineDirectConnections[0];
            if ((firstConnectionFromResult.departureTime/1000.0) < (firstConnection.departureTime/1000.0) + 5.0*60.0) { //returning direct must arrive at most 5 minutes later than the non-direct that arrives the first
                return maxLineDirectConnections;
            }
            
        }
        
        
        /* now dealing with non-direct trips if no good direct trip result */
        
        NSMutableDictionary* repeatingConnectionsForLines = [NSMutableDictionary dictionary];
        for (TransportTrip* trip in queryTripResultConnections) {
            if (trip.parts && trip.parts.count >= 2) {
                
                TransportConnection* potentialFeetConnection = trip.parts[0];
                TransportConnection* potentialRepeatingConnection = trip.parts[1];
                
                if (!potentialFeetConnection.isFeetConnection) {
                    potentialRepeatingConnection = potentialFeetConnection;
                }
                
                if (potentialRepeatingConnection.line.name) {
                    NSString* lineNicerName = potentialFeetConnection.line.shortName;
                    //NSLog(@"Treating line : %@", lineNicerName);
                    if (!repeatingConnectionsForLines[lineNicerName]) {
                        repeatingConnectionsForLines[lineNicerName] = [NSMutableArray arrayWithObject:potentialRepeatingConnection];
                        //NSLog(@"found new potentialRepeatingConnection line : %@", lineNicerName);
                    } else {
                        NSMutableArray* potentialRepeatingConnections = repeatingConnectionsForLines[lineNicerName];
                        [potentialRepeatingConnections addObject:potentialRepeatingConnection];
                        //NSLog(@"found existing potentialRepeatingConnection line : %@", lineNicerName);
                    }
                }
                
            }
        }
        
        NSUInteger maxConnections = 0;
        NSMutableArray* linesConnections = [NSMutableArray array];
        for (NSArray* lineConnections in [repeatingConnectionsForLines allValues]) {
            if (lineConnections.count >= maxConnections && lineConnections.count > 1) {
                maxConnections = lineConnections.count;
                [linesConnections addObject:lineConnections];
                
            }
        }
        
        [linesConnections sortUsingComparator:^NSComparisonResult(NSArray* line1Connections, NSArray* line2Connections) {
            if (line1Connections.count < line2Connections.count) {
                return NSOrderedDescending; //inversed, so that connections that appear more often are at the beginning the array
            } else if (line1Connections.count > line2Connections.count) {
                return NSOrderedAscending;
            } else {
                return NSOrderedSame;
            }
        }];
        
        
        NSArray* mostRepeatingLineConnections;
        
        if (linesConnections.count == 0) {
            return nil;
        }
        
        if (linesConnections.count == 1) {
            mostRepeatingLineConnections = linesConnections[0];
        }
        
        if (linesConnections.count > 1) {
            mostRepeatingLineConnections = linesConnections[0];
            NSArray* line2Connections = linesConnections[1];
            if (mostRepeatingLineConnections.count < 2*line2Connections.count) { //means number of connections of line 1 is not really dominant compared to second line that appears the most often => both line are considered important thus nil is returned
                return nil;
            }
        }
        
        if (maxConnections > 2) {
            TransportTrip* firstTrip = queryTripResultConnections[0];
            TransportConnection* firstConnection = firstTrip.parts[0];
            if (firstConnection.isFeetConnection) {
                firstConnection = firstTrip.parts[1];
            }
            TransportConnection* firstConnectionFromResult = mostRepeatingLineConnections[0];
            //NSLog(@"firstTrip : %@ - %@", [self hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0], [self hourMinutesStringForTimestamp:firstConnection.arrivalTime/1000.0]);
            //NSLog(@"result : %@ - %@", [self hourMinutesStringForTimestamp:firstConnectionFromResult.departureTime/1000.0], [self hourMinutesStringForTimestamp:firstConnectionFromResult.arrivalTime/1000.0]);
            if ((firstConnectionFromResult.departureTime/1000.0) < (firstConnection.departureTime/1000.0) + 5.0*60.0) { //returning direct must leave at most 5 minutes later than the non-direct that arrives the first
                return mostRepeatingLineConnections;
            }        
        }
        
        return nil;
        
    }
    @catch (NSException *exception) {
        return nil;
    }
}

@end
