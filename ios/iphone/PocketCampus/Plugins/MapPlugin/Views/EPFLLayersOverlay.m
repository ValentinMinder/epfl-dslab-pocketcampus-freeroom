//
//  EPFLLayersOverlay.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.06.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "EPFLLayersOverlay.h"

#import "CustomOverlayView.h"

#import "MapUtils.h"

@implementation EPFLLayersOverlay

static int MAX_LAYER_LEVEL = 8;
static int MIN_LAYER_LEVEL = -4;

@synthesize boundingMapRect, coordinate, mapView;

- (id)init {
    self = [super init];
    
    if (self) {
        
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
        
        currentLayerLevel = 1;
    }
    
    return self;
}

- (NSString *)urlForMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    
    CH1903BBox bbox = [MapUtils WGStoCH1903:mapRect];
    
    return [self urlForEpflLayerWithCH1903StartX:bbox.start_x startY:bbox.start_y endX:bbox.end_x endY:bbox.end_y width:mapRect.size.width*zoomScale height:mapRect.size.height*zoomScale];
}


- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    // Limit this overlay to only display tiles over the general Swiss area.
    // Roughly within (48, 4), (44, 10), in degrees.
    
    if (zoomScale < 1.0) {
        return NO;
    }
    
    // Turn center to bounds
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
    NSNumber* newY = [NSNumber numberWithDouble:floor(4194303 / (pow(2, (22 - zoom)))) - y];
    return [newY intValue];
}

- (NSString*)urlForEpflLayerWithCH1903StartX:(double)startX startY:(double)startY endX:(double)endX endY:(double)endY width:(double)width height:(double)height  {
    NSString* baseURLWithBBoxEmptyParameter = @"http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&TRANSPARENT=false&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application%2Fvnd.ogc.se_inimage&SRS=EPSG%3A21781&BBOX=";
    
    return [NSString stringWithFormat:@"%@%lf,%lf,%lf,%lf&WIDTH=%.0lf&HEIGHT=%.0lf&LAYERS=locaux_labels%d,locaux_h%d", baseURLWithBBoxEmptyParameter, startY, endX, endY, startX, width, height, currentLayerLevel, currentLayerLevel];
    
}

- (NSString*)identifier {
    return @"EPFLLayers";
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
            [customOverlayView cancelTilesDownload];
            [customOverlayView setNeedsDisplayInMapRect:MKMapRectWorld];
        }
    }
}

- (void)dealloc
{
    [super dealloc];
}

@end
