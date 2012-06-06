//
//  EPFLLayersOverlay.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.06.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "EPFLLayersOverlay.h"

@implementation EPFLLayersOverlay

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
    }
    
    return self;
}

- (NSString *)urlForPointWithX:(NSUInteger)x andY:(NSUInteger)y andZoomLevel:(NSUInteger)zoomLevel {
    
    NSUInteger newY = [self convertYCoord:y withZoom:zoomLevel];
    
    NSString *returnString = [self urlForEpflLayerWithX:x andY:newY andZoom:zoomLevel];//zoomLevel];
    
    //    NSLog(@"EPFLUrl called, returned string: %@", returnString);
    
    return returnString;
}


- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    // Limit this overlay to only display tiles over the general Swiss area.
    // Roughly within (48, 4), (44, 10), in degrees.
    
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

- (NSString*)urlForEpflLayerWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* baseURLWithBBoxEmptyParameter = @"http://plan.epfl.ch/wms_themes?FORMAT=image%2Fpng&LAYERS=events_surface,events_line,events_label,parkings_publicsall,arrets_metroall,informationall,evenements_scenesall,evenements_nourritureall,evenements_boissonsall,evenements_entreesall,evenements_informationsall,evenements_presseall,evenements_infirmerieall,locaux_hall,locaux_labelsall,batiments_routes_labels&TRANSPARENT=true&LOCALID=-1&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application%2Fvnd.ogc.se_inimage&SRS=EPSG%3A21781&BBOX=";
     //TODO
    return nil;
    
}

@end
