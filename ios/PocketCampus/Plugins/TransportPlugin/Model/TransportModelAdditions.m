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
