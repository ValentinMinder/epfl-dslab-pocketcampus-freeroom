//
//  PCScreenTileOverlay.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.05.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

@import MapKit;

@protocol PCScreenTileOverlay <NSObject>

@property (nonatomic, weak, readonly) MKMapView* mapView;

@required

- (instancetype)initWithMapView:(MKMapView*)mapView;

/**
 * Returns wether NOW mapMap is in a state where fetching/drawing mapRect with zoomScale is possible
 * Typically NO if mapRect is outside visibleMapRect or zoomScale not equal to mapView current's zoomScale
 */
- (BOOL)shouldDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;

- (NSURL*)URLForCurrentlyVisibleMapRectAndZoomScale;

/**
 * Should return empty image if zoomScale is not equal to mapView current zoom scale, partial or empty image if mapRect is partially or completely outside
 * or mapView visibleMapRect respectively
 */
- (UIImage*)croppedImageFromCurrentlyVisibleMapRectImage:(UIImage*)image forMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale;

@end
