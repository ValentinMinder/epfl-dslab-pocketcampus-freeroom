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

//  Created by Loïc Gardiol on 13.04.12.

@import Foundation;

@import MapKit;

#import "map.h"

#import "MapItemAnnotation.h"

#define MINIMUM_ZOOM_ARC 0.00085
#define ANNOTATION_REGION_PAD_FACTOR 1.15
#define MAX_DEGREES_ARC 360

typedef struct {
    double start_x;
    double start_y;
    double end_x;
    double end_y;
} CH1903BBox;

@interface MapUtils : NSObject

+ (UIImage*)mapControlOverylabBoxImage;
+ (NSArray*)mapItemsThatShouldBeDisplayed:(NSArray*)allMapItems forQuery:(NSString*)query;
+ (id<MKAnnotation>)annotationThatShouldBeSelectedOnMapView:(MKMapView*)mapView forQuery:(NSString*)query;
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
+ (CH1903BBox)tilePathToCH1903:(MKTileOverlayPath)tilePath tileSize:(CGSize)tileSize;

double tilex2long(int x, int z);
double tiley2lat(int y, int z);

MKMapRect MKMapRectForCoordinateRegion(MKCoordinateRegion region);

NSString* NSStringFromMKTileOverlayPath(MKTileOverlayPath path);

@end
