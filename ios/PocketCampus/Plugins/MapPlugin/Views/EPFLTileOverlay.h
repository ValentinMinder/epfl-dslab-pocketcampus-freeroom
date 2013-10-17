//
//  EPFLTileOverlay.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

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
