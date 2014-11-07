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

//  Created by Loïc Gardiol on 07.05.14.

#import "PCTileOverlay.h"

@implementation PCTileOverlay

#pragma mark - Init

- (instancetype)init {
    self = [super init];
    if (self) {
        self.floorLevel = self.defaultFloorLevel;
        self.allowStaleCachedTilesDataUsageWhenNoInternetConnection = YES;
    }
    return self;
}

- (instancetype)initWithURLTemplate:(NSString *)URLTemplate {
    self = [super initWithURLTemplate:URLTemplate];
    if (self) {
        self.floorLevel = self.defaultFloorLevel;
        self.allowStaleCachedTilesDataUsageWhenNoInternetConnection = YES;
    }
    return self;
}

#pragma mark - MKTileOverlay overrides

- (void)loadTileAtPath:(MKTileOverlayPath)path result:(void (^)(NSData *, NSError *))result {
    if (self.tilesDataCacheValidityInterval <= 0.0) {
        [super loadTileAtPath:path result:result];
        return;
    }
    NSInteger floorLevel = self.floorLevel;
    NSData* cachedData = [self cachedTileDataForTileOverlayPath:path floorLevel:floorLevel];
    if (!cachedData || ![cachedData isKindOfClass:[NSData class]]) {
        __weak __typeof(self) welf = self;
        [super loadTileAtPath:path result:^void(NSData* data, NSError* error) {
            if (welf && data && !error) {
                [welf saveTileData:data forTileOverlayPath:path floorLevel:floorLevel];
            }
            if (welf) {
                result(data, error);
            }
        }];
        return;
    }
    result(cachedData, nil);
}

#pragma mark - Public

- (void)setFloorLevel:(NSInteger)floorLevel {
    NSInteger oldVal = _floorLevel;
    NSInteger newVal;
    if (floorLevel > self.maxFloorLevel) {
        newVal = self.maxFloorLevel;
    } else if (floorLevel < self.minFloorLevel) {
        newVal = self.minFloorLevel;
    } else {
        newVal = floorLevel;
    }
    if (newVal != oldVal) {
        [self willChangeValueForKey:NSStringFromSelector(@selector(floorLevel))];
        _floorLevel = newVal;
        [self didChangeValueForKey:NSStringFromSelector(@selector(floorLevel))];
    }
}

//KVO override
+ (BOOL)automaticallyNotifiesObserversOfFloorLevel {
    //so that we can manage notificaiton manually,
    //only notifying when level has actually changed (not only setter called)
    return NO;
}

- (void)increaseFloorLevel {
    self.floorLevel = (self.floorLevel+1);
}

- (void)decreaseFloorLevel {
    self.floorLevel = (self.floorLevel-1);
}

#pragma mark Default implementations

- (NSInteger)defaultFloorLevel {
    return 0;
}

- (NSInteger)maxFloorLevel {
    return 100;
}

- (NSInteger)minFloorLevel {
    return 100;
}

- (CLLocationDistance)floorLevelsMaxAltitude {
    return 10000.0;
}

- (CGFloat)desiredAlpha {
    return 1.0;
}

- (MKOverlayLevel)desiredLevelForMapView {
    return MKOverlayLevelAboveRoads;
}

#pragma mark - Private

- (NSData*)cachedTileDataForTileOverlayPath:(MKTileOverlayPath)tilePath floorLevel:(NSInteger)floorLevel {
    NSString* key = [self keyForTileOverlayPath:tilePath floorLevel:floorLevel];
    NSData* cachedIfValid = (NSData*)[PCPersistenceManager objectForKey:key pluginName:@"map" nilIfDiffIntervalLargerThan:self.tilesDataCacheValidityInterval isCache:YES];
    if ([PCUtils hasDeviceInternetConnection]) {
        return cachedIfValid;
    }
    if (self.allowStaleCachedTilesDataUsageWhenNoInternetConnection) {
        return (NSData*)[PCPersistenceManager objectForKey:key pluginName:@"map" isCache:YES];
    }
    return cachedIfValid;
}

- (void)saveTileData:(NSData*)data forTileOverlayPath:(MKTileOverlayPath)tilePath floorLevel:(NSInteger)floorLevel {
    NSString* key = [self keyForTileOverlayPath:tilePath floorLevel:floorLevel];
    [PCPersistenceManager saveObject:data forKey:key pluginName:@"map" isCache:YES];
    
}

- (NSString*)keyForTileOverlayPath:(MKTileOverlayPath)tilePath floorLevel:(NSInteger)floorLevel {
    if (!self.overlayIdentifier) {
        [NSException raise:@"Illegal state" format:@"self.overlayIdentifier MUST be set to use tiles caching"];
    }
    return [NSString stringWithFormat:@"%@_cached_tile_%ld_%ld_%ld_%ld_%f", self.overlayIdentifier, floorLevel, tilePath.x, tilePath.y, tilePath.z, tilePath.contentScaleFactor];
}

@end
