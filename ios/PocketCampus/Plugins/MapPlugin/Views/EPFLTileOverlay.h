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




//  Created by Loïc Gardiol on 23.04.12.


#define DEFAULT_LAYER_LEVEL 1
#define MAX_LAYER_LEVEL 8
#define MIN_LAYER_LEVEL -4
#define MAX_ALTITUDE_ALLOW_LAYER_CHANGE 1200
#define MAX_ALTITUDE 3800
#define MIN_ALTITUDE 0

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

#import "OverlayWithURLs.h"

@interface EPFLTileOverlay : NSObject<OverlayWithURLs>

@property (nonatomic, readonly) NSInteger currentLayerLevel;
@property (nonatomic, weak) MKMapView* mapView;

- (NSInteger)convertYCoord:(NSInteger)y withZoom:(NSInteger)zoom;
- (NSString*)createCoordString:(NSInteger)coord;
- (NSInteger)randomizeTileServer;

- (void)increaseLayerLevel;
- (void)decreaseLayerLevel;
- (void)setLayerLevel:(NSInteger)newLevel;

- (BOOL)shouldAllowLayerChange;

@end
