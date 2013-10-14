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

#import "PCUtils.h"

@implementation EPFLLayersOverlay

static int MAX_LAYER_LEVEL = 8;
static int MIN_LAYER_LEVEL = -4;
static double MIN_ZOOM_SCALE_OVERLAY = 0.1;

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
    
    if (![PCUtils isRetinaDevice]) {
        zoomScale *= 2.0;
    }
    
    //NSString* url = [self urlForEpflLayerWithCH1903StartX:bbox.start_x startY:bbox.start_y endX:bbox.end_x endY:bbox.end_y width:mapRect.size.width*zoomScale height:mapRect.size.height*zoomScale];
    
    /*NSLog(@"------------");
    NSLog(@"zoomScale : %lf", zoomScale);
    NSLog(@"start_x : %lf", bbox.start_x);
    NSLog(@"start_y : %lf", bbox.start_y);
    
    NSLog(@"end_x : %lf", bbox.end_x);
    NSLog(@"end_y : %lf", bbox.end_y);*/
    
    double width;
    double height;
    
    if (zoomScale > 0.75) { //showing each room name
        if ([PCUtils isRetinaDevice]) {
            width = mapRect.size.width*zoomScale;
            height = mapRect.size.height*zoomScale;
        } else { //room names text is too small and unclear on non-retina to be read
            width = 0.0;
            height = 0.0;
        }
    } else {
        width = abs(bbox.start_x-bbox.end_x)*5.0*zoomScale;
        height = abs(bbox.start_y-bbox.end_y)*5.0*zoomScale;
    }
    
    //NSLog(@"width : %lf", width);
    //NSLog(@"height : %lf", height);
    
    NSString* url = [self urlForEpflLayerWithCH1903StartX:bbox.start_x startY:bbox.start_y endX:bbox.end_x endY:bbox.end_y width:width height:height];
    
    //NSLog(@"url : %@", url);
    
    return url;
}


- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    // Limit this overlay to only display tiles over the general Swiss area.
    // Roughly within (48, 4), (44, 10), in degrees.
    
    //NSLog(@"ZoomScale : %lf", zoomScale);
    
    if (![PCUtils isRetinaDevice]) {
        zoomScale *= 2.0;
        if (zoomScale > 0.75) {
            return NO;
        }
    }
    
    if (zoomScale < MIN_ZOOM_SCALE_OVERLAY) {
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
    
    NSString* urlString = [NSString stringWithFormat:@"%@%lf,%lf,%lf,%lf&WIDTH=%.0lf&HEIGHT=%.0lf&LAYERS=locaux_labels%d,locaux_h%d,batiments_routes_labels,parkings_publicsall,informationall", baseURLWithBBoxEmptyParameter, startY, endX, endY, startX, width, height, currentLayerLevel, currentLayerLevel];
    NSLog(@"%@", urlString);
    return urlString;
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
            [customOverlayView cancelTilesDownload:NO];
            [customOverlayView setNeedsDisplayInMapRect:MKMapRectWorld];
        }
    }
}

- (void)dealloc
{
    [super dealloc];
}

@end
