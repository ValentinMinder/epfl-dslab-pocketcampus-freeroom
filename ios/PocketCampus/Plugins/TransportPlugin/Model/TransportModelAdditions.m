//
//  TransportModelAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportModelAdditions.h"

#import <objc/runtime.h>

@implementation TransportStation (Additions)

- (NSString*)shortName {
    static NSString* const kShortNameKey = @"shortName";
    NSString* shortName = objc_getAssociatedObject(self, (__bridge const void *)(kShortNameKey));
    if (!shortName) {
        shortName = [self computeShortName];
        objc_setAssociatedObject(self, (__bridge const void *)(kShortNameKey), shortName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return shortName;
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
    return ( (duration <= (3*60)) && !self.line.name); //feet connections are generally 3min longs (or less) and have nil line name
}

@end

@implementation TransportLine (Additions)

- (NSString*)shortName {
    static NSString* const kShortNameKey = @"shortName";
    NSString* shortName = objc_getAssociatedObject(self, (__bridge const void *)(kShortNameKey));
    if (!shortName) {
        shortName = [self computeShortName];
        objc_setAssociatedObject(self, (__bridge const void *)(kShortNameKey), shortName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
    return shortName;
}

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
    return nbChanges-1; //les piquets et les potaux...
}

- (BOOL)isLeft {
    NSTimeInterval interval = (self.departureTime/1000.0) - [[NSDate date] timeIntervalSince1970];
    return interval < -60.0; //up to 1min in the past, consider that the transport might still be there
}

@end

@implementation QueryTripsResult (Additions)

- (NSArray*)nonLeftTrips {
    NSMutableArray* purgedTrips = [NSMutableArray arrayWithArray:self.connections];
    for  (TransportTrip* trip in self.connections) {
        if (trip.isLeft) {
            [purgedTrips removeObject:trip];
        }
    }
    return purgedTrips;
}

@end
