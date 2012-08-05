//
//  EPFLTileOverlay.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "EPFLTileOverlay.h"

#import "CustomOverlayView.h"

#import "MapUtils.h"

@implementation EPFLTileOverlay

static NSString* URL_ENDING = @".png";

@synthesize boundingMapRect, coordinate, currentLayerLevel, mapView;

- (id)init {
    self = [super init];
    
    if (self) {
        currentLayerLevel = 1;
        
        // I am still not well-versed in map projections, but the Google Mercator projection
        // is slightly off from the "standard" Mercator projection, used by MapKit. (GMerc is used
        // by the demo tileserver to serve to the Google Maps API script in a user's
        // browser.)
        //
        // My understanding is that this is due to Google Maps' use of a Spherical Mercator
        // projection, where the poles are cut off -- the effective map ending at approx. +/- 85º.
        // MapKit does not(?), therefore, our origin point (top-left) must be moved accordingly.
        
        boundingMapRect = MKMapRectWorld;
        boundingMapRect.origin.x += 1048600.0;
        boundingMapRect.origin.y += 1048600.0;
        
        coordinate = CLLocationCoordinate2DMake(0, 0);
    }
    
    return self;
}

- (NSString *)urlForMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    NSUInteger zoomLevel = [MapUtils zoomLevelForZoomScale:zoomScale];
    CGPoint mercatorPoint = [MapUtils mercatorTileOriginForMapRect:mapRect];
    NSUInteger tileX = floor(mercatorPoint.x * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
    NSUInteger tileY = floor(mercatorPoint.y * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
    NSUInteger newY = [self convertYCoord:tileY withZoom:zoomLevel];
    
    NSString *returnString = [self urlForEpflTilesWithX:tileX andY:newY andZoom:zoomLevel];//zoomLevel];
    
    return returnString;
}

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    // Limit this overlay to only display tiles over the general Swiss area.
    // Roughly within (48, 4), (44, 10), in degrees.
    // Turn center to bounds
    
    if (zoomScale < MIN_ZOOM_SCALE) {
        return NO;
    }
    
    MKCoordinateRegion _region = MKCoordinateRegionForMapRect(mapRect);
    CLLocationDegrees top_bound = _region.center.latitude + (_region.span.latitudeDelta / 2.0);
    CLLocationDegrees bottom_bound = _region.center.latitude - (_region.span.latitudeDelta / 2.0);
    CLLocationDegrees left_bound = _region.center.longitude - (_region.span.longitudeDelta / 2.0);
    CLLocationDegrees right_bound = _region.center.longitude + (_region.span.longitudeDelta / 2.0);
    
    //The area outside Switzerland
    if ( (left_bound > 10.0f) ||   
        (right_bound < 4.0f) || 
        (top_bound < 44.0f) ||     
        (bottom_bound > 48.0f) ) {
        return NO;
    }
    return YES;
}

- (NSInteger)convertYCoord:(NSInteger)y withZoom:(NSInteger)zoom {
    return floor(4194303 / (pow(2, (22 - zoom)))) - y;
}

- (NSString*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* baseUrl = [NSString stringWithFormat:@"%@%d%@", @"http://plan-epfl-tile", [self randomizeTileServer], @".epfl.ch/batiments"];
    NSString* layerLevel = [NSString stringWithFormat: @"%d%@",currentLayerLevel,@"/"];//-ch
    NSString* zoomLevel = [NSString stringWithFormat:@"%d/",zoom];
    NSString* xCoord = [self createCoordString:x];
    NSString* yCoord = [self createCoordString:y];
    
    //Build the final url string
    NSString* urlString = [NSString stringWithFormat:@"%@%@%@%@%@%@%@",baseUrl,layerLevel,zoomLevel,xCoord,@"/",yCoord,URL_ENDING];
    return urlString;
}

- (NSString*)createCoordString:(NSInteger)coord {
    NSString* coordString = [NSString stringWithFormat:@"%09d",coord];
    NSString* firstSubString = [[coordString substringToIndex:3] stringByAppendingString:@"/"];
    NSString* secondSubString = [[[coordString substringFromIndex:3] substringToIndex:3] stringByAppendingString:@"/"];
    NSString* thirdSubString = [coordString substringFromIndex:6];
    NSString* returnString = [NSString stringWithFormat:@"%@%@%@", firstSubString, secondSubString, thirdSubString];
    return returnString;
}
//Returns a value between 0 and 4, commented because some urls don't have tiles
- (NSInteger)randomizeTileServer {
    //return rand() % 5;
    return 1;
}

- (void)increaseLayerLevel {
    if (currentLayerLevel < MAX_LAYER_LEVEL) {
        //Redraw the overlay.
        [self setLayerLevel:(currentLayerLevel+1)];
    }
}


- (void)decreaseLayerLevel {
    if (currentLayerLevel > MIN_LAYER_LEVEL) {
        //Redraw the overlay.
        [self setLayerLevel:(currentLayerLevel-1)];
    }
}

- (void)setLayerLevel:(NSInteger)newLevel {
    if (newLevel < MIN_LAYER_LEVEL || newLevel > MAX_LAYER_LEVEL) {
        return;
    }
    currentLayerLevel = newLevel;
    //Redraw the overlay.

    if (self.mapView == nil) {
        NSLog(@"-> !! mapView property is nil, cannot setNeedsDisplay");
        return;
    }
    
    for(NSObject<MKOverlay>* overlay in mapView.overlays) {
        if([overlay isKindOfClass:self.class]){
            CustomOverlayView* customOverlayView = (CustomOverlayView*)[mapView viewForOverlay:overlay];
            [customOverlayView cancelTilesDownload:NO];
            [customOverlayView setNeedsDisplayInMapRect:MKMapRectWorld];
        }
    }
}

- (NSString*)identifier {
    return @"EPFLTiles";
}

- (void)dealloc {
    [super dealloc];
}


@end
