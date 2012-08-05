//
//  TransportUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 24.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportUtils.h"

@implementation TransportUtils


+ (NSString*)durationgStringForInterval:(NSTimeInterval)interval {
    NSCalendar* systemCalendar = [NSCalendar currentCalendar];
    NSDate* date1 = [[[NSDate alloc] init] autorelease];
    NSDate* date2 = [[[NSDate alloc] initWithTimeInterval:interval sinceDate:date1] autorelease]; 
    unsigned int unitFlags = NSHourCalendarUnit | NSMinuteCalendarUnit | NSDayCalendarUnit | NSMonthCalendarUnit;
    NSDateComponents* conversionInfo = [systemCalendar components:unitFlags fromDate:date1  toDate:date2  options:0];
    return [NSString stringWithFormat:@"%d:%02d", [conversionInfo hour], [conversionInfo minute]];
}

+ (NSString*)hourMinutesStringForTimestamp:(NSTimeInterval)timestamp {
    NSDateFormatter* dateFormatter = [[[NSDateFormatter alloc] init] autorelease];
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

+ (NSArray*)connectionsWithoutAlreadyLeft:(NSArray*)connections {
    NSMutableArray* purgedConnections = [NSMutableArray arrayWithCapacity:connections.count]; //often, no connection to remove
    for (TransportConnection* connection in connections) {
        NSString* timeString = [self  automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
        if (![timeString isEqualToString:@"Left"]) {
            [purgedConnections addObject:connection];
        }
    }
    return purgedConnections;
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

+ (NSArray*)nextRedundantDeparturesFromMessyResult:(QueryTripsResult*)queryTripResult {
    
    @try {
        
        if (queryTripResult.connections == nil) {
            @throw [NSException exceptionWithName:@"bad argument in nextRedundantDeparturesFromMessyResult:" reason:@"queryTripResult.connections is nil" userInfo:nil];
        }
        
        /* check for direct trips first */
        
        NSMutableArray* directTripsConnections = [NSMutableArray array]; //First and only TransportConnection of each direct trip from result
        for (TransportTrip* trip in queryTripResult.connections) {
            if (trip.parts != nil && trip.parts.count == 1) {
                [directTripsConnections addObject:[trip.parts objectAtIndex:0]];
                //NSLog(@"found direct");
            }
        }
        if (directTripsConnections.count >= 3) { //if at least 3 direct connections in QueryTripsResult 
            NSMutableDictionary* connectionsForLine = [NSMutableDictionary dictionary];
            for (TransportConnection* directConnection in directTripsConnections) {
                if (directConnection.line == nil || directConnection.line.name == nil) {
                    continue;
                }
                NSString* lineNicerName = [[self class] nicerName:directConnection.line.name];
                if ([connectionsForLine objectForKey:lineNicerName] == nil) {
                    [connectionsForLine setObject:[NSMutableArray arrayWithObject:directConnection] forKey:lineNicerName];
                    //NSLog(@"found new direct potentialRepeatingConnection line : %@", lineNicerName);
                } else {
                    NSMutableArray* lineDirectConnections = [connectionsForLine objectForKey:lineNicerName];
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
            
            [linesConnections sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
                NSArray* line1Connections = (NSArray*)obj1;
                NSArray* line2Connections = (NSArray*)obj2;
                
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
                maxLineDirectConnections = [linesConnections objectAtIndex:0];
            }
            
            if (linesConnections.count > 1) {
                maxLineDirectConnections = [linesConnections objectAtIndex:0];
                NSArray* line2Connections = [linesConnections objectAtIndex:1];
                if (maxLineDirectConnections.count < 2*line2Connections.count) { //means number of connections of line 1 is not really dominant compared to second line that appears the most often => both line are considered important thus nil is returned
                    return nil;
                }
            }
            
            TransportTrip* firstTrip = [queryTripResult.connections objectAtIndex:0];
            TransportConnection* firstConnection = [firstTrip.parts objectAtIndex:0];
            if ([self isFeetConnection:firstConnection]) {
                firstConnection = [firstTrip.parts objectAtIndex:1];
            }
            TransportConnection* firstConnectionFromResult = [maxLineDirectConnections objectAtIndex:0];
            if ((firstConnectionFromResult.departureTime/1000.0) < (firstConnection.departureTime/1000.0) + 5.0*60.0) { //returning direct must arrive at most 5 minutes later than the non-direct that arrives the first
                return maxLineDirectConnections;
            }
            
        }
        
        
        /* now dealing with non-direct trips if no good direct trip result */
        
        NSMutableDictionary* repeatingConnectionsForLines = [NSMutableDictionary dictionary];
        for (TransportTrip* trip in queryTripResult.connections) {
            if (trip.parts != nil && trip.parts.count >= 2) {
                
                TransportConnection* potentialFeetConnection = [trip.parts objectAtIndex:0];
                TransportConnection* potentialRepeatingConnection = [trip.parts objectAtIndex:1];
                
                if (![self isFeetConnection:potentialFeetConnection]) {
                    potentialRepeatingConnection = potentialFeetConnection;
                }
                
                if (potentialRepeatingConnection.line != nil && potentialRepeatingConnection.line.name != nil) {
                    NSString* lineNicerName = [self nicerName:potentialRepeatingConnection.line.name];
                    //NSLog(@"Treating line : %@", lineNicerName);
                    if ([repeatingConnectionsForLines objectForKey:lineNicerName] == nil) {
                        [repeatingConnectionsForLines setObject:[NSMutableArray arrayWithObject:potentialRepeatingConnection] forKey:lineNicerName];
                        //NSLog(@"found new potentialRepeatingConnection line : %@", lineNicerName);
                    } else {
                        NSMutableArray* potentialRepeatingConnections = [repeatingConnectionsForLines objectForKey:lineNicerName];
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
        
        [linesConnections sortUsingComparator:^NSComparisonResult(id obj1, id obj2) {
            NSArray* line1Connections = (NSArray*)obj1;
            NSArray* line2Connections = (NSArray*)obj2;
            
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
            mostRepeatingLineConnections = [linesConnections objectAtIndex:0];
        }
        
        if (linesConnections.count > 1) {
            mostRepeatingLineConnections = [linesConnections objectAtIndex:0];
            NSArray* line2Connections = [linesConnections objectAtIndex:1];
            if (mostRepeatingLineConnections.count < 2*line2Connections.count) { //means number of connections of line 1 is not really dominant compared to second line that appears the most often => both line are considered important thus nil is returned
                return nil;
            }
        }
        
        if (maxConnections > 2) {
            TransportTrip* firstTrip = [queryTripResult.connections objectAtIndex:0];
            TransportConnection* firstConnection = [firstTrip.parts objectAtIndex:0];
            if ([self isFeetConnection:firstConnection]) {
                firstConnection = [firstTrip.parts objectAtIndex:1];
            }
            TransportConnection* firstConnectionFromResult = [mostRepeatingLineConnections objectAtIndex:0];
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

+ (int)numberOfChangeForTrip:(TransportTrip*)trip {
    if (trip == nil || ![trip isKindOfClass:[TransportTrip class]]) {
        @throw [NSException exceptionWithName:@"bad argument in numberOfChangeForTrip" reason:@"trip is not kind of class TransportTrip" userInfo:nil];
    }
    if (trip.parts == nil || trip.parts.count == 0) {
        return 0;
    }
    
    int nbChange = 0;
    for (TransportConnection* part in trip.parts) {
        if (![self isFeetConnection:part]) {
            nbChange++;
        }
    }
    return nbChange-1; //les piquets et les potaux...
}

+ (NSString*)firstLineNameForTrip:(TransportTrip*)trip {
    if (trip == nil || ![trip isKindOfClass:[TransportTrip class]]) {
        @throw [NSException exceptionWithName:@"bad argument in firstLineNameForTrip" reason:@"trip is not kind of class TransportTrip" userInfo:nil];
    }
    
    if (trip.parts == nil || trip.parts.count == 0) {
        return @"";
    }
    
    TransportConnection* firstSignificantConnection = [trip.parts objectAtIndex:0];
    if (![self isFeetConnection:firstSignificantConnection]) {
        return [self nicerName:firstSignificantConnection.line.name];
    }
    
    if (trip.parts.count > 1) {
        firstSignificantConnection = [trip.parts objectAtIndex:1];
        if (firstSignificantConnection.line != nil && firstSignificantConnection.line.name != nil) {
            return [self nicerName:firstSignificantConnection.line.name];
        }
    }
    return @"";
}

+ (BOOL)isFeetConnection:(TransportConnection*)part {
    if (part == nil || ![part isKindOfClass:[TransportConnection class]]) {
        @throw [NSException exceptionWithName:@"bad argument in isFeetConnection" reason:@"part is not kind of class TransportConnection" userInfo:nil];
    }
    return ( ( (part.arrivalTime/1000) - (part.departureTime/1000) <= (3*60) ) && (part.line == nil || part.line.name == nil) ); //feet connections are generally 3min longs (or less) and have nil line name
}


+ (NSString*)nicerName:(NSString*)currentName;{
    if (currentName == nil || ![currentName isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument in nicerName:" reason:@"argument currentName is not kind of class NSString" userInfo:nil];
    }
    
    if ([currentName isEqualToString:@"UMetm1"]) {
        return @"M1";
    }
    
    if ([currentName isEqualToString:@"Ecublens VD, EPFL"]) {
        return @"EPFL";
    }
    
    if ([currentName isEqualToString:@"Ecublens VD, EPFL Piccard"]) {
        return @"EPFL Piccard";
    }
    
    if ([currentName isEqualToString:@"Ecublens VD, UNIL-Sorge"]) {
        return @"UNIL-Sorge";
    }
    
    if ([currentName isEqualToString:@"UMetm2"]) {
        return @"M2";
    }
    
    if ([currentName isEqualToString:@"Lausanne, Vigie"]) {
        return @"Vigie";
    }
    
    if ([currentName isEqualToString:@"Chavannes-p.-R., UNIL-Dorigny"]) {
        return @"UNIL-Dorigny";
    }
    
    if ([currentName isEqualToString:@"Chavannes-p.-R., UNIL-Mouline"]) {
        return @"UNIL-Mouline";
    }
    
    NSError* error = NULL;
    NSRange currentNameRange = NSMakeRange(0, [currentName length]);
    
    /* From here : very repeated, ugly code. But not refractored for performances purposes */
    
    NSRegularExpression* regex;
    NSTextCheckingResult* result;
    NSRange range;
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^BBus(\\d*)" options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^S(S\\d) " options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^R(IR)\\d*" options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^R(RE)\\d*" options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^(ICN)*" options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    {
        regex = [NSRegularExpression regularExpressionWithPattern:@"^I(IC)*" options:NSRegularExpressionCaseInsensitive error:&error];
        result = [regex firstMatchInString:currentName options:0 range:currentNameRange];
        if (result.numberOfRanges > 1) {
            range = [result rangeAtIndex:1];
            if (range.length != 0) {
                return [currentName substringWithRange:range];
            }
        }
    }
    
    return currentName;
    
}

@end
