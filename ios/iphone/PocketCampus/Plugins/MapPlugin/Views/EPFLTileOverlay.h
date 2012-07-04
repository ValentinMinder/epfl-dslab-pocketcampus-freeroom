//
//  EPFLTileOverlay.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#define MAX_LAYER_LEVEL 8
#define MIN_LAYER_LEVEL -4
#define MIN_ZOOM_SCALE 0.03

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

#import "OverlayWithURLs.h"

@interface EPFLTileOverlay : NSObject<OverlayWithURLs> {
    NSInteger currentLayerLevel;
}

@property (readonly) NSInteger currentLayerLevel;
@property (nonatomic, assign) MKMapView* mapView;

- (NSInteger)convertYCoord:(NSInteger)y withZoom:(NSInteger)zoom;
- (NSString*)createCoordString:(NSInteger)coord;
- (NSInteger)randomizeTileServer;

- (void)increaseLayerLevel;
- (void)decreaseLayerLevel;
- (void)setLayerLevel:(NSInteger)newLevel;

@end
