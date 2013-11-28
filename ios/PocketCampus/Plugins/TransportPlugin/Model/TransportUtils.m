//
//  TransportUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportUtils.h"

#import "TransportModelAdditions.h"

@implementation TransportUtils


+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval {
    NSCalendar* systemCalendar = [NSCalendar currentCalendar];
    NSDate* date1 = [NSDate new];
    NSDate* date2 = [[NSDate alloc] initWithTimeInterval:interval sinceDate:date1];
    unsigned int unitFlags = NSHourCalendarUnit | NSMinuteCalendarUnit | NSDayCalendarUnit | NSMonthCalendarUnit;
    NSDateComponents* conversionInfo = [systemCalendar components:unitFlags fromDate:date1  toDate:date2  options:0];
    return [NSString stringWithFormat:@"%d:%02d", [conversionInfo hour], [conversionInfo minute]];
}

+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp {
    NSDateFormatter* dateFormatter = [NSDateFormatter new];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    [dateFormatter setLocale:[NSLocale systemLocale]];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    [dateFormatter setDateFormat:@"HH:mm"];
    return [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:timestamp]];
}

+ (NSString*)automaticHoursMinutesLeftStringForTimestamp:(NSTimeInterval)timestamp {
    NSDate* nowDate = [NSDate date];
    NSTimeInterval seconds = timestamp - [nowDate timeIntervalSince1970];
    if (seconds < 0.0) {
        if (seconds > -60.0) { //might still consider that it (train, bus, ...) is not left yet
            return @"Now";
        } else {
            return @"Left"; //already left, should not show this result
        }
    }
    double minutesLeft = floor((seconds/60.0)+0.5);
    if (minutesLeft == 0.0) {
        return @"Now";
    }
    
    double hoursLeft = floor(minutesLeft/60.0);
    if (hoursLeft > 0.0) {
        return [NSString stringWithFormat:@"%1.0lfh%2.0lf'",hoursLeft, minutesLeft];
    }
    
    return [NSString stringWithFormat:@"%2.0lf'", minutesLeft];
}


+ (NSString*)automaticTimeStringForTimestamp:(NSTimeInterval)timestamp maxIntervalForMinutesLeftString:(NSTimeInterval)maxIntervalMinutes {
    NSDate* nowDate = [NSDate dateWithTimeIntervalSinceNow:0]; //timestamp GMT
    
    /*NSTimeZone* zone = [NSTimeZone systemTimeZone];
    
    NSTimeInterval nowTimestampLocal = [nowDate timeIntervalSince1970] + [zone secondsFromGMTForDate:nowDate]; //timestamp local
    
    NSDate* nowLocalDate = [NSDate dateWithTimeIntervalSince1970:nowTimestampLocal];
    
    NSDate* depDate = [NSDate dateWithTimeIntervalSince1970:timestamp];
    
    NSTimeInterval depTimestampLocal = timestamp + [zone secondsFromGMTForDate:depDate];
    
    NSDate* depLocalDate = [NSDate dateWithTimeIntervalSince1970:depTimestampLocal];
    
    NSLog(@"now : %@, arr : %@",nowLocalDate, depLocalDate);*/
    
    if (timestamp - [nowDate timeIntervalSince1970] > maxIntervalMinutes*60) {
        return [self hourMinutesStringForTimestamp:timestamp];
    } else {
        return [self automaticHoursMinutesLeftStringForTimestamp:timestamp];
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
                    connectionsForLine[lineNicerName] = @[directConnection];
                    //NSLog(@"found new direct potentialRepeatingConnection line : %@", lineNicerName);
                } else {
                    NSMutableArray* lineDirectConnections = connectionsForLine[lineNicerName];
                    [lineDirectConnections addObject:directConnection];
                    //NSLog(@"found existing direct potentialRepeatingConnection line : %@", lineNicerName);
                }
            }
            
            int maxConnections = 0;
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
            TransportConnection* firstConnectionFromResult = [maxLineDirectConnections objectAtIndex:0];
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
        
        int maxConnections = 0;
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
