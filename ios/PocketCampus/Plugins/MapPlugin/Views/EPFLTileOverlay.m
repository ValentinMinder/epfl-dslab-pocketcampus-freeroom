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

static NSInteger const kDefaultFloorLevel = 1;
static NSInteger const kMaxFloorLevel = 8;
static NSInteger const kMinFloorLevel = -4;
static double const kFloorLevelsMaxAltitude = 1200.0;

static NSString* const kTileServersBaseURLStringWithFormat = @"http://plan-epfl-tile%d.epfl.ch";
static NSInteger const kNbTileServers = 5;

static NSString* const kURLEnding = @".png";
static NSInteger const kMinZ = 12;
static NSInteger const kMaxZ = 30;

static NSTimeInterval const kTilesCacheValidityInterval = 259200.0; //2 days

#import "EPFLTileOverlay.h"

@interface EPFLTileOverlay ()

@property (nonatomic) CGFloat alpha;

@end

@implementation EPFLTileOverlay

#pragma mark - Init

- (instancetype)init {
    self = [super init];
    if (self) {
        self.alpha = 0.85;
        self.tilesDataCacheValidityInterval = kTilesCacheValidityInterval;
        self.overlayIdentifier = NSStringFromClass(self.class);
    }
    return self;
}

#pragma mark - PCTileOverlay overrides

- (NSInteger)defaultFloorLevel {
    return kDefaultFloorLevel;
}

- (NSInteger)maxFloorLevel {
    return kMaxFloorLevel;
}

- (NSInteger)minFloorLevel {
    return kMinFloorLevel;
}

- (CLLocationDistance)floorLevelsMaxAltitude {
    return kFloorLevelsMaxAltitude;
}

- (CGFloat)desiredAlpha {
    return 0.85;
}

- (MKOverlayLevel)desiredLevelForMapView {
    return MKOverlayLevelAboveRoads;
}

#pragma mark - MKTileOverlay overrides

- (NSURL*)URLForTilePath:(MKTileOverlayPath)path {
    NSString* serverBaseURLString = [self serverBaseURLStringForTilePath:path];
    NSString* pathOnServer = [NSString stringWithFormat:@"/batiments%d-merc/%d/%@/%@%@", (int)self.floorLevel, (int)path.z, [self createStringCoordForTileCoord:path.x], [self createStringCoordForTileCoord:path.y], kURLEnding];
    NSURL* url = [NSURL URLWithString:pathOnServer relativeToURL:[NSURL URLWithString:serverBaseURLString]];
    return url;
}

- (BOOL)isGeometryFlipped {
    return YES;
}

- (NSInteger)minimumZ {
    return kMinZ;
}

- (NSInteger)maximumZ {
    return kMaxZ;
}

#pragma mark - Private

- (NSString*)serverBaseURLStringForTilePath:(MKTileOverlayPath)tilePath {
    // we want to load balance on the kNbTileServers, but always use the same server for
    // a specfic tile, so client caching is preserved.
    int tileServerIndex = (tilePath.x+tilePath.y+tilePath.z) % kNbTileServers;
    return [NSString stringWithFormat:kTileServersBaseURLStringWithFormat, tileServerIndex];
}

- (NSString*)createStringCoordForTileCoord:(NSInteger)coord {
    NSString* coordString = [NSString stringWithFormat:@"%09d", (int)coord];
    NSString* firstSubString = [[coordString substringToIndex:3] stringByAppendingString:@"/"];
    NSString* secondSubString = [[[coordString substringFromIndex:3] substringToIndex:3] stringByAppendingString:@"/"];
    NSString* thirdSubString = [coordString substringFromIndex:6];
    NSString* returnString = [NSString stringWithFormat:@"%@%@%@", firstSubString, secondSubString, thirdSubString];
    return returnString;
}

@end
