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




//  Created by Loïc Gardiol on 23.04.12.


#import "EPFLTileOverlay.h"

#import "RemoteOverlayRenderer.h"

#import "MapUtils.h"

@interface EPFLTileOverlay ()

@property (nonatomic, readwrite) NSInteger currentLayerLevel;

@end

static NSString* const kURLEnding = @".png";

@implementation EPFLTileOverlay

@synthesize boundingMapRect, coordinate; //auto-synthesis does not synthesize properties of protocol

- (id)init {
    self = [super init];
    
    if (self) {
        self.currentLayerLevel = DEFAULT_LAYER_LEVEL;
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
    
    if (self.mapView.camera.altitude > MAX_ALTITUDE ||self.mapView.camera.altitude < MIN_ALTITUDE) {
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

/*
 * OLD
 */
/*- (NSString*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* baseUrl = [NSString stringWithFormat:@"%@%d%@", @"http://plan-epfl-tile", [self randomizeTileServer], @".epfl.ch/batiments"];
    NSString* layerLevel = [NSString stringWithFormat: @"%d%@", self.currentLayerLevel, @"/"];//-ch
    NSString* zoomLevel = [NSString stringWithFormat:@"%d/",zoom];
    NSString* xCoord = [self createCoordString:x];
    NSString* yCoord = [self createCoordString:y];
    //Build the final url string
    NSString* urlString = [NSString stringWithFormat:@"%@%@%@%@%@%@%@",baseUrl,layerLevel,zoomLevel,xCoord,@"/",yCoord,URL_ENDING];
    return urlString;
}*/

/*
 * Normal
 */
- (NSString*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* urlString = [NSString stringWithFormat:@"http://plan-epfl-tile1.epfl.ch/batiments%d-merc/%d/%@/%@%@", self.currentLayerLevel, zoom, [self createCoordString:x], [self createCoordString:y], kURLEnding];
    return urlString;
}

/*
 * Dev tiles
 */

/*- (NSString*)urlForEpflTilesWithX:(NSInteger)x andY:(NSInteger)y andZoom:(NSInteger)zoom {
    NSString* urlString = [NSString stringWithFormat:@"http://plan-dev.epfl.ch/tilecache/batiments%d-merc/%d/%@/%@%@", self.currentLayerLevel, zoom, [self createCoordString:x], [self createCoordString:y], URL_ENDING];
    return urlString;
}*/

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
    if (self.currentLayerLevel < MAX_LAYER_LEVEL) {
        //Redraw the overlay.
        [self setLayerLevel:(self.currentLayerLevel+1)];
    }
}

- (void)decreaseLayerLevel {
    if (self.currentLayerLevel > MIN_LAYER_LEVEL) {
        //Redraw the overlay.
        [self setLayerLevel:(self.currentLayerLevel-1)];
    }
}

- (void)setLayerLevel:(NSInteger)newLevel {
    if (newLevel < MIN_LAYER_LEVEL || newLevel > MAX_LAYER_LEVEL) {
        return;
    }
    self.currentLayerLevel = newLevel;
    //Redraw the overlay.

    if (self.mapView == nil) {
        NSLog(@"-> !! mapView property is nil, cannot setNeedsDisplay");
        return;
    }
    
    for(NSObject<MKOverlay>* overlay in self.mapView.overlays) {
        if([overlay isKindOfClass:self.class]){
            RemoteOverlayRenderer* remoteOverlayRenderer = (RemoteOverlayRenderer*)[self.mapView rendererForOverlay:overlay];
            [remoteOverlayRenderer cancelTilesDownload:NO];
            [remoteOverlayRenderer setNeedsDisplayInMapRect:MKMapRectWorld];
        }
    }
}

- (BOOL)shouldAllowLayerChange {
    CLLocationDistance altitude = self.mapView.camera.altitude/cos(self.mapView.camera.pitch*M_PI/180.0);
    return altitude < 1200.0;
}

- (NSString*)identifier {
    return @"EPFLTiles";
}


@end
