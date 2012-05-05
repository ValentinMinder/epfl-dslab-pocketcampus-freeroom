//
//  MapUtils.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "map.h"

#import "MapItemAnnotation.h"

#define MINIMUM_ZOOM_ARC 0.0015
#define ANNOTATION_REGION_PAD_FACTOR 1.15
#define MAX_DEGREES_ARC 360

@interface MapUtils : NSObject

+ (NSArray*)mapItemAnnotationsThatShouldBeDisplayed:(NSArray*)annotations forQuery:(NSString*)query;
+ (id<MKAnnotation>)annotationThatShouldBeSelectedOnMapView:(MKMapView*)mapView forQuery:(NSString*)query;
+ (NSInteger)levelToSelectForRoomName:(NSString*)roomName; //returns INT_MAX if level cannot be determined
+ (NSArray*)mapItemAnnotations:(NSArray*)annotations;
+ (BOOL)mapViewHasMapItemAnnotations:(MKMapView*)mapView;
+ (void)removeMapItemAnnotationsOnMapView:(MKMapView*)mapView;
+ (void)zoomMapView:(MKMapView*)mapView toFitMapItemAnnotationsAnimated:(BOOL)animated;
+ (NSUInteger)zoomLevelForMapRect:(MKMapRect)mapRect;
+ (NSUInteger)zoomLevelForZoomScale:(MKZoomScale)zoomScale;
+ (NSUInteger)worldTileWidthForZoomLevel:(NSUInteger)zoomLevel;
+ (CGPoint)mercatorTileOriginForMapRect:(MKMapRect)mapRect;

@end
