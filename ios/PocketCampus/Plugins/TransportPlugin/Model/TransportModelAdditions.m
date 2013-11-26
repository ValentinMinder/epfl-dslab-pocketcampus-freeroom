//
//  TransportModelAdditions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportModelAdditions.h"

@implementation TransportStation (Additions)

- (NSString*)shortName {
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
    
    NSString* currentName = self.name;
    
    if ([currentName isEqualToString:@"UMetm1"]) {
        return @"M1";
    }
    
    if ([currentName isEqualToString:@"UMm1"]) {
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
    
    if ([currentName isEqualToString:@"UMm2"]) {
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
