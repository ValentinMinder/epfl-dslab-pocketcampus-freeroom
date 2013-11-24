//
//  TransportModelAdditions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "transport.h"

@interface TransportStation (Additions)

/*
 * Returns a shortened name for some common stations
 * (e.g. Ecublens VD, EPFL => EPFL)
 * For other stations, returns original name
 */
@property (nonatomic, readonly) NSString* shortName;

- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToTransportStation:(TransportStation*)transportStation;
- (NSUInteger)hash;

@end
