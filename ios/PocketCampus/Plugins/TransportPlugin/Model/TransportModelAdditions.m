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






//  Created by Loïc Gardiol on 23.11.13.



#import "TransportModelAdditions.h"

#import <objc/runtime.h>

static NSCache* shortNameForTransportStationName;

@implementation TransportStation (Additions)

- (NSString*)shortName {
    shortNameForTransportStationName = shortNameForTransportStationName ?: [NSCache new];
    if (!shortNameForTransportStationName[self.name]) {
        shortNameForTransportStationName[self.name] = [self computeShortName];
    }
    return shortNameForTransportStationName[self.name];
}

- (NSString*)computeShortName {
    if ([self.name isEqualToString:@"Ecublens VD, EPFL"]) {
        return @"EPFL";
    }
    
    if ([self.name isEqualToString:@"Ecublens VD, EPFL Piccard"]) {
        return @"EPFL Piccard";
    }
    
    if ([self.name isEqualToString:@"Ecublens VD, UNIL-Sorge"]) {
        return @"UNIL-Sorge";
    }
    
    if ([self.name isEqualToString:@"Lausanne, Vigie"]) {
        return @"Vigie";
    }
    
    if ([self.name isEqualToString:@"Chavannes-p.-R., UNIL-Dorigny"]) {
        return @"UNIL-Dorigny";
    }
    
    if ([self.name isEqualToString:@"Chavannes-p.-R., UNIL-Mouline"]) {
        return @"UNIL-Mouline";
    }
    
    return self.name;
}

- (BOOL)isEqual:(id)object {
    if (self == object) {
        return YES;
    }
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    return [self isEqualToTransportStation:object];
}

- (BOOL)isEqualToTransportStation:(TransportStation*)transportStation {
    return self.id == transportStation.id;
}

- (NSUInteger)hash {
    NSUInteger hash = 0;
    hash += self.id;
    return hash;
}

@end


@implementation TransportConnection (Additions)

- (BOOL)isFeetConnection {
    int64_t duration = (self.arrivalTime/1000) - (self.departureTime/1000);
    return ( (duration <= (6.0 * 60.0)) && !self.line.name); //feet connections are generally 3min longs (or less) and have nil line name
}

- (BOOL)hasLeft {
    NSTimeInterval interval = (self.departureTime/1000.0) - [[NSDate date] timeIntervalSince1970];
    return interval < -60.0; //up to 1min in the past, consider that the transport might still be there
}

@end

static NSCache* shortNameForTransportLineName;
static NSCache* veryShortNameForTransportLineName;

@implementation TransportLine (Additions)

- (NSString*)shortName {
    shortNameForTransportLineName = shortNameForTransportLineName ?: [NSCache new];
    if (!shortNameForTransportLineName[self.name]) {
        NSString* shortName = [self computeShortName];
        if (!shortName) {
            return nil;
        }
        shortNameForTransportLineName[self.name] = shortName;
    }
    return shortNameForTransportLineName[self.name];
}

- (NSString*)veryShortName {
    veryShortNameForTransportLineName = veryShortNameForTransportLineName ?: [NSCache new];
    if (!veryShortNameForTransportLineName[self.name]) {
        NSString* veryShortName = self.shortName.length <= 3 ? self.shortName : [[self.shortName substringToIndex:2] stringByAppendingString:@".."];
        if (!veryShortName) {
            return nil;
        }
        veryShortNameForTransportLineName[self.name] = veryShortName;
    }
    return veryShortNameForTransportLineName[self.name];
}

//private
- (NSString*)computeShortName {
    
    NSString* currentName = self.name;
    
    if ([currentName isEqualToString:@"UMetm1"]) {
        return @"M1";
    }
    
    if ([currentName isEqualToString:@"UMm1"]) {
        return @"M1";
    }
    
    if ([currentName isEqualToString:@"UMetm2"]) {
        return @"M2";
    }
    
    if ([currentName isEqualToString:@"UMm2"]) {
        return @"M2";
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
                return [NSString stringWithFormat:@"B%@",[currentName substringWithRange:range]];
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
        regex = [NSRegularExpression regularExpressionWithPattern:@"^S(S\\d\\d)" options:NSRegularExpressionCaseInsensitive error:&error];
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

@implementation TransportTrip (Additions)

- (TransportConnection*)firstConnection {
    if (!self.parts.count) {
        return nil;
    }
    if (self.parts.count > 1 && [(TransportConnection*)[self.parts firstObject] isFeetConnection]) {
        return (TransportConnection*)(self.parts[1]);
    }
    return (TransportConnection*)[self.parts firstObject];
    
}

- (NSUInteger)numberOfChanges {
    if (!self.parts.count) {
        return 0;
    }
    
    NSUInteger nbChanges = 0;
    for (TransportConnection* connection in self.parts) {
        if (!connection.isFeetConnection) {
            nbChanges++;
        }
    }
    return nbChanges == 0 ? nbChanges : nbChanges-1; //les piquets et les potaux...
}

@end

@implementation QueryTripsResult (Additions)

- (NSArray*)nonLeftTrips {
    NSMutableArray* purgedTrips = [NSMutableArray arrayWithArray:self.connections];
    for  (TransportTrip* trip in self.connections) {
        if (trip.firstConnection.hasLeft) {
            [purgedTrips removeObject:trip];
        }
    }
    return purgedTrips;
}

@end
