//
//  EPFLTileOverlay.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#define MAX_LAYER_LEVEL 8
#define MIN_LAYER_LEVEL -4

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

@interface EPFLTileOverlay : NSObject<MKOverlay> {
    CGFloat alpha;
    NSInteger currentLayerLevel;
}

@property CGFloat alpha;
@property (readonly) NSInteger currentLayerLevel;
@property (nonatomic, assign) MKMapView* mapView;

- (NSString*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom;
- (NSString *)urlForPointWithX:(NSUInteger)x andY:(NSUInteger)y andZoomLevel:(NSUInteger)zoomLevel;

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;

- (NSInteger)convertYCoord:(NSInteger)y withZoom:(NSInteger)zoom;
- (NSString*)createCoordString:(NSInteger)coord;
- (NSInteger)randomizeTileServer;

- (void)increaseLayerLevel;
- (void)decreaseLayerLevel;
- (void)setLayerLevel:(NSInteger)newLevel;

@end
