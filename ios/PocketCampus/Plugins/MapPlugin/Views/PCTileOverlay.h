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

@import MapKit;

@interface PCTileOverlay : MKTileOverlay

/**
 * Set this property to a positive value to enable tiles persistence caching.
 * loadTileAtPath:result: automatically looks for a tile that is still valid
 * and directly returns it in result(...) if found.
 * 0.0 or negative value means no caching.
 * IMPORTANT: self.overlayIdentifier MUST be set and unique
 * Default: 0.0
 */
@property (nonatomic) NSTimeInterval tilesDataCacheValidityInterval;

@property (nonatomic, strong) NSString* overlayIdentifier;

/**
 * Default: YES
 */
@property (nonatomic) BOOL allowStaleCachedTilesDataUsageWhenNoInternetConnection;

/*
 * Setter ensures that value stays withing [self.minFloorLevel, self.maxFloorLevel] bounds
 */
@property (nonatomic) NSInteger floorLevel;

- (void)increaseFloorLevel;
- (void)decreaseFloorLevel;

/* 
 * Subclasses should override this getters
 * and return wished value
 */

/**
 * Default: 0
 */
@property (nonatomic, readonly) NSInteger defaultFloorLevel;

/**
 * Default: 100
 */
@property (nonatomic, readonly) NSInteger maxFloorLevel;

/**
 * Default: -100
 */
@property (nonatomic, readonly) NSInteger minFloorLevel;

/**
 * Default: 10'000.0
 */
@property (nonatomic, readonly) CLLocationDistance floorLevelsMaxAltitude;


/**
 * PCTileOverlayRenderer will use this value for its alpha rendering
 * Default: 1.0
 */
@property (nonatomic, readonly) CGFloat desiredAlpha;

/**
 * Should return level at which overlay should be added
 * Default: MKOverlayLevelAboveRoads
 */
@property (nonatomic, readonly) MKOverlayLevel desiredLevelForMapView;

@end
