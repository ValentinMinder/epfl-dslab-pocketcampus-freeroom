//
//  MapUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

#import "map.h"

#import "MapItemAnnotation.h"

#define MINIMUM_ZOOM_ARC 0.0015
#define ANNOTATION_REGION_PAD_FACTOR 1.15
#define MAX_DEGREES_ARC 360

typedef struct {
    double start_x;
    double start_y;
    double end_x;
    double end_y;
} CH1903BBox;

@interface MapUtils : NSObject

+ (NSArray*)mapItemAnnotationsThatShouldBeDisplayed:(NSArray*)annotations forQuery:(NSString*)query;
+ (id<MKAnnotation>)annotationThatShouldBeSelectedOnMapView:(MKMapView*)mapView forQuery:(NSString*)query;
+ (NSInteger)levelToSelectForRoomName:(NSString*)roomName; //returns INT_MAX if level cannot be determined
+ (NSArray*)mapItemAnnotations:(NSArray*)annotations;
+ (BOOL)mapViewHasMapItemAnnotations:(MKMapView*)mapView;
+ (void)removeMapItemAnnotationsOnMapView:(MKMapView*)mapView;
+ (BOOL)isRegion:(MKCoordinateRegion)region1 equalToRegion:(MKCoordinateRegion)region2;
+ (void)zoomMapView:(MKMapView*)mapView toFitMapItemAnnotationsAnimated:(BOOL)animated;
+ (MKCoordinateRegion)regionToFitMapItemAnnotations:(NSArray*)annotations; //returns 0 values if no annotation
+ (NSUInteger)zoomLevelForMapRect:(MKMapRect)mapRect;
+ (NSUInteger)zoomLevelForZoomScale:(MKZoomScale)zoomScale;
+ (NSUInteger)worldTileWidthForZoomLevel:(NSUInteger)zoomLevel;
+ (CGPoint)mercatorTileOriginForMapRect:(MKMapRect)mapRect;

+ (CH1903BBox)WGStoCH1903:(MKMapRect)mapRect;

@end
