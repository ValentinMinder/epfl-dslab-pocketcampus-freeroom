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


#import "MapUtils.h"

// Convert decimal angle (degrees) to sexagesimal angle (degrees, minutes
// and seconds dd.mmss,ss)
double decToSexAngle(double dec) {
    int deg = (int)floor(dec);
    int min = (int)floor((dec - deg) * 60);
    double sec = (((dec - deg) * 60) - min) * 60;
    
    // Output: dd.mmss(,)ss
    return deg + ((double) min / 100.0) + (sec / 10000.0);
}

// Convert sexagesimal angle (degrees, minutes and seconds dd.mmss,ss) to
// seconds
double sexAngleToSeconds(double dms) {
    double deg = 0, min = 0, sec = 0;
    deg = floor(dms);
    min = floor((dms - deg) * 100);
    sec = (((dms - deg) * 100) - min) * 100;
    
    // Result in degrees sex (dd.mmss)
    return sec + (min * 60) + (deg * 3600);
}


double WGStoCHx(double lat, double lng) {
    // Converts degrees dec to sex
    lat = decToSexAngle(lat);
    lng = decToSexAngle(lng);
    
    // Converts degrees to seconds (sex)
    lat = sexAngleToSeconds(lat);
    lng = sexAngleToSeconds(lng);
    
    // Axiliary values (% Bern)
    double lat_aux = (lat - 169028.66) / 10000.0;
    double lng_aux = (lng - 26782.5) / 10000.0;
    
    // Process X
    double x = ((200147.07 + (308807.95 * lat_aux)
                 + (3745.25 * pow(lng_aux, 2)) + (76.63 * pow(lat_aux,
                                                              2))) - (194.56 * pow(lng_aux, 2) * lat_aux))
    + (119.79 * pow(lat_aux, 3));
    
    return x;
}

double WGStoCHy(double lat, double lng) {
    // Converts degrees dec to sex
    lat = decToSexAngle(lat);
    lng = decToSexAngle(lng);
    
    // Converts degrees to seconds (sex)
    lat = sexAngleToSeconds(lat);
    lng = sexAngleToSeconds(lng);
    
    // Axiliary values (% Bern)
    double lat_aux = (lat - 169028.66) / 10000.0;
    double lng_aux = (lng - 26782.5) / 10000.0;
    
    // Process Y
    double y = (600072.37 + (211455.93 * lng_aux))
    - (10938.51 * lng_aux * lat_aux)
    - (0.36 * lng_aux * pow(lat_aux, 2))
    - (44.54 * pow(lng_aux, 3));
    
    return y;
}

/*
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 */

double tilex2long(int x, int z) {
	return x / pow(2.0, z) * 360.0 - 180;
}

double tiley2lat(int y, int z) {
	double n = M_PI - 2.0 * M_PI * y / pow(2.0, z);
	return 180.0 / M_PI * atan(0.5 * (exp(n) - exp(-n)));
}

@implementation MapUtils

+ (UIImage*)mapControlOverylabBoxImage {
    UIImage* image = [UIImage imageNamed:@"MapControlsOverlayBox"];
    image = [image resizableImageWithCapInsets:UIEdgeInsetsMake(0.0, 30.0, 0.0, 30.0)];
    return image;
}

+ (NSArray*)mapItemsThatShouldBeDisplayed:(NSArray*)allMapItems forQuery:(NSString*)query {
    [PCUtils throwExceptionIfObject:allMapItems notKindOfClass:[NSArray class]];
    [PCUtils throwExceptionIfObject:query notKindOfClass:[NSString class]];
    
    NSString* lowerQuery = [query lowercaseString];
    NSString* lowerQueryWithoutSpace = [lowerQuery stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    NSMutableArray* mapItemsToDisplay = [NSMutableArray array];
    
    NSError* error = NULL;
    
    for (MapItem* mapItem in allMapItems) {
        if (![mapItem isKindOfClass:[MapItem class]]) {
            continue;
        }
        NSString* lowerTitle = [mapItem.title lowercaseString];
        NSString* lowerTitleWithoutSpace = [lowerTitle stringByReplacingOccurrencesOfString:@" " withString:@""];
        
        //NSLog(@"lower title : %@, lowerTitleWithoutSpace : %@", lowerTitle, lowerQueryWithoutSpace);
        
        NSRange titleRange = NSMakeRange(0, [lowerTitle length]);
        
        if ([lowerTitle isEqualToString:lowerQuery] || [lowerTitleWithoutSpace isEqualToString:lowerQueryWithoutSpace]) {
            [mapItemsToDisplay addObject:mapItem];
            return mapItemsToDisplay;
        }
        
        { //separation block
            NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:[NSString stringWithFormat:@"^Auditoire %@$", lowerQuery] options:NSRegularExpressionCaseInsensitive error:&error];
            NSRegularExpression* regexWithoutSpace = [NSRegularExpression regularExpressionWithPattern:[NSString stringWithFormat:@"^Auditoire %@$", lowerQueryWithoutSpace] options:NSRegularExpressionCaseInsensitive error:&error];
            if ([regex numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0 || [regexWithoutSpace numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0) {
                [mapItemsToDisplay addObject:mapItem];
                return mapItemsToDisplay;
            }
        }
        
    }
    
    mapItemsToDisplay = [allMapItems mutableCopy];
    
    return mapItemsToDisplay;
}

+ (id<MKAnnotation>)annotationThatShouldBeSelectedOnMapView:(MKMapView*)mapView forQuery:(NSString*)query {
    if (mapView == nil || ![mapView isKindOfClass:[MKMapView class]]) {
        @throw [NSException exceptionWithName:@"bad mapView argument in annotationThatShouldBeSelectedOnMapView:" reason:@"mapView is not kind of class MKMapView" userInfo:nil];
    }
    if (query == nil || ![query isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument query in annotationThatShouldBeSelectedOnMapView:for:Query:" reason:@"query is not kind of class NSString" userInfo:nil];
    }
    
    for (id<MKAnnotation> annotation in mapView.annotations) {
        if ([[annotation.title lowercaseString] isEqualToString:[query lowercaseString]]) {
            return annotation;
        }
        if ([annotation.title isEqualToString:@"Patrick Aebischer"] && [[query lowercaseString] rangeOfString:@"aebischer"].length != 0) {
            return annotation;
        }
             
    }
    
    return [mapView.annotations firstObject]; //otherwise select first one
}

+ (BOOL)mapViewHasMapItemAnnotations:(MKMapView*)mapView {
    for (id<MKAnnotation> annotation in mapView.annotations) {
        if ([annotation isKindOfClass:[MapItemAnnotation class]]) {
            return YES;
        }
    }
    return NO;
}

+ (NSArray*)mapItemAnnotations:(NSArray*)annotations {
    [PCUtils throwExceptionIfObject:annotations notKindOfClass:[NSArray class]];
    NSMutableArray* mapItemAnnotations = [NSMutableArray arrayWithCapacity:(annotations.count > 0 ? annotations.count-1 : annotations.count)]; //usually, all annotations except user location. Size will adapt if not the case
    for (id<MKAnnotation> annotation in annotations) {
        if ([annotation isKindOfClass:[MapItemAnnotation class]]) {
            [mapItemAnnotations addObject:annotation];
        }
    }
    return mapItemAnnotations;
}

+ (void)removeMapItemAnnotationsOnMapView:(MKMapView*)mapView; {
    for (id<MKAnnotation> annotation in mapView.annotations) {
        if ([annotation isKindOfClass:[MapItemAnnotation class]]) {
            [mapView removeAnnotation:annotation];
        }
    }
}

+ (BOOL)isRegion:(MKCoordinateRegion)region1 equalToRegion:(MKCoordinateRegion)region2 {
    if (fabs(region1.center.latitude - region2.center.latitude) > 0.0001) {
        return NO;
    }
    
    if (fabs(region1.center.longitude - region2.center.longitude) > 0.0001) {
        return NO;
    }
    
    if (fabs(region1.span.latitudeDelta - region2.span.latitudeDelta) > 0.0001) {
        return NO;
    }
    
    if (fabs(region1.span.longitudeDelta - region2.span.longitudeDelta) > 0.0001) {
        return NO;
    }
    
    return YES;
}

//size the mapView region to fit its annotations
+ (void)zoomMapView:(MKMapView*)mapView toFitMapItemAnnotationsAnimated:(BOOL)animated { 
    if (mapView == nil || ![mapView isKindOfClass:[MKMapView class]]) {
        @throw [NSException exceptionWithName:@"bad mapView argument in zoomMapView:toFitMapItemAnnotationsAnimated:" reason:@"mapView is not kind of class MKMapView" userInfo:nil];
    }
    NSArray* annotations = [self mapItemAnnotations:mapView.annotations];
    
    if (annotations.count > 0) {
        MKCoordinateRegion region = [self regionToFitMapItemAnnotations:annotations];
        [mapView setRegion:region animated:animated];
    }
}

+ (MKCoordinateRegion)regionToFitMapItemAnnotations:(NSArray*)annotations {
    NSUInteger count = annotations.count;
    if (count == 0) {
        return MKCoordinateRegionMake(CLLocationCoordinate2DMake(0, 0), MKCoordinateSpanMake(0, 0));
    }
    
    //convert NSArray of id <MKAnnotation> into an MKCoordinateRegion that can be used to set the map size
    //can't use NSArray with MKMapPoint because MKMapPoint is not an id
    MKMapPoint points[count]; //C array of MKMapPoint struct
    for( int i=0; i<count; i++ ) //load points C array by converting coordinates to points
    {
        CLLocationCoordinate2D coordinate = [(id <MKAnnotation>)annotations[i] coordinate];
        points[i] = MKMapPointForCoordinate(coordinate);
    }
    //create MKMapRect from array of MKMapPoint
    MKMapRect mapRect = [[MKPolygon polygonWithPoints:points count:count] boundingMapRect];
    //convert MKCoordinateRegion from MKMapRect
    MKCoordinateRegion region = MKCoordinateRegionForMapRect(mapRect);
    
    //add padding so pins aren't scrunched on the edges
    region.span.latitudeDelta  *= ANNOTATION_REGION_PAD_FACTOR;
    region.span.longitudeDelta *= ANNOTATION_REGION_PAD_FACTOR;
    //but padding can't be bigger than the world
    if( region.span.latitudeDelta > MAX_DEGREES_ARC ) { region.span.latitudeDelta  = MAX_DEGREES_ARC; }
    if( region.span.longitudeDelta > MAX_DEGREES_ARC ){ region.span.longitudeDelta = MAX_DEGREES_ARC; }
    
    //and don't zoom in stupid-close on small samples
    if( region.span.latitudeDelta  < MINIMUM_ZOOM_ARC ) { region.span.latitudeDelta  = MINIMUM_ZOOM_ARC; }
    if( region.span.longitudeDelta < MINIMUM_ZOOM_ARC ) { region.span.longitudeDelta = MINIMUM_ZOOM_ARC; }
    //and if there is a sample of 1 we want the max zoom-in instead of max zoom-out
    if( count == 1 )
    {
        region.span.latitudeDelta = MINIMUM_ZOOM_ARC;
        region.span.longitudeDelta = MINIMUM_ZOOM_ARC;
    }
    
    return region;
}

/**
 * Given a MKMapRect, this returns the zoomLevel based on 
 * the longitude width of the box.
 *
 * This is because the Mercator projection, when tiled,
 * normally operates with 2^zoomLevel tiles (1 big tile for
 * world at zoom 0, 2 tiles at 1, 4 tiles at 2, etc.)
 * and the ratio of the longitude width (out of 360º)
 * can be used to reverse this.
 *
 * This method factors in screen scaling for the iPhone 4:
 * the tile layer will use the *next* zoomLevel. (We are given
 * a screen that is twice as large and zoomed in once more
 * so that the "effective" region shown is the same, but
 * of higher resolution.)
 */
+ (NSUInteger)zoomLevelForMapRect:(MKMapRect)mapRect {
    MKCoordinateRegion region = MKCoordinateRegionForMapRect(mapRect);
    CGFloat lon_ratio = region.span.longitudeDelta/360.0;
    NSUInteger z = (NSUInteger)(log(1/lon_ratio)/log(2.0)-1.0);
    
    z += ([[UIScreen mainScreen] scale] - 1.0);
    return z;
}
/**
 * Similar to above, but uses a MKZoomScale to determine the
 * Mercator zoomLevel. (MKZoomScale is a ratio of screen points to
 * map points.)
 */
+ (NSUInteger)zoomLevelForZoomScale:(MKZoomScale)zoomScale {
    CGFloat realScale = zoomScale / [[UIScreen mainScreen] scale];
    NSUInteger z = (NSUInteger)(log(realScale)/log(2.0)+20.0);
    
    z += ([[UIScreen mainScreen] scale] - 1.0);
    return z;
}
/**
 * Shortcut to determine the number of tiles wide *or tall* the
 * world is, at the given zoomLevel. (In the Spherical Mercator
 * projection, the poles are cut off so that the resulting 2D
 * map is "square".)
 */
+ (NSUInteger)worldTileWidthForZoomLevel:(NSUInteger)zoomLevel {
    return (NSUInteger)(pow(2,zoomLevel));
}

/**
 * Given a MKMapRect, this reprojects the center of the mapRect
 * into the Mercator projection and calculates the rect's top-left point
 * (so that we can later figure out the tile coordinate).
 *
 * See http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Derivation_of_tile_names
 */
+ (CGPoint)mercatorTileOriginForMapRect:(MKMapRect)mapRect {
    MKCoordinateRegion region = MKCoordinateRegionForMapRect(mapRect);
    
    // Convert lat/lon to radians
    CGFloat x = (region.center.longitude) * (M_PI/180.0); // Convert lon to radians
    CGFloat y = (region.center.latitude) * (M_PI/180.0); // Convert lat to radians
    y = log(tan(y)+1.0/cos(y));
    
    // X and Y should actually be the top-left of the rect (the values above represent
    // the center of the rect)
    x = (1.0 + (x/M_PI)) / 2.0;
    y = (1.0 - (y/M_PI)) / 2.0;
    
    return CGPointMake(x, y);
}

+ (CH1903BBox)WGStoCH1903:(MKMapRect)mapRect {
    CLLocationCoordinate2D topLeftWGS = MKCoordinateForMapPoint(mapRect.origin);
    MKMapPoint bottomRightMapPoint = MKMapPointMake(mapRect.origin.x+mapRect.size.width, mapRect.origin.y+mapRect.size.height);
    CLLocationCoordinate2D bottomRightWGS = MKCoordinateForMapPoint(bottomRightMapPoint);
    
    CH1903BBox bbox;
    bbox.start_x = WGStoCHx(topLeftWGS.latitude, topLeftWGS.longitude);
    bbox.start_y = WGStoCHy(topLeftWGS.latitude, topLeftWGS.longitude);
    bbox.end_x = WGStoCHx(bottomRightWGS.latitude, bottomRightWGS.longitude);
    bbox.end_y = WGStoCHy(bottomRightWGS.latitude, bottomRightWGS.longitude);
    return bbox;
    
}

+ (CH1903BBox)tilePathToCH1903:(MKTileOverlayPath)tilePath tileSize:(CGSize)tileSize {
    double lat = tiley2lat((int)tilePath.y, (int)tilePath.z);
    double lon = tilex2long((int)tilePath.x, (int)tilePath.z);
    
    CLLocationDegrees latSpan = (360.0*cos(lat * M_PI / 180.0)) / (double)(pow(2,tilePath.z));
    CLLocationDegrees lonSpan = (360.0*cos(lon * M_PI / 180.0)) / (double)(pow(2,tilePath.z));
    
    lat -= latSpan/2.0;
    lon += lonSpan/2.0;
    
    MKCoordinateRegion region = MKCoordinateRegionMake(CLLocationCoordinate2DMake(lat, lon), MKCoordinateSpanMake(latSpan, lonSpan));
    
    MKMapRect mapRect = MKMapRectForCoordinateRegion(region);
    
    return [self WGStoCH1903:mapRect];
}

MKMapRect MKMapRectForCoordinateRegion(MKCoordinateRegion region) {
    MKMapPoint a = MKMapPointForCoordinate(CLLocationCoordinate2DMake(
                                                                      region.center.latitude + region.span.latitudeDelta / 2,
                                                                      region.center.longitude - region.span.longitudeDelta / 2));
    MKMapPoint b = MKMapPointForCoordinate(CLLocationCoordinate2DMake(
                                                                      region.center.latitude - region.span.latitudeDelta / 2,
                                                                      region.center.longitude + region.span.longitudeDelta / 2));
    return MKMapRectMake(MIN(a.x,b.x), MIN(a.y,b.y), ABS(a.x-b.x), ABS(a.y-b.y));
}

NSString* NSStringFromMKTileOverlayPath(MKTileOverlayPath path) {
    return [NSString stringWithFormat:@"x:%ld, y:%ld, z:%ld, contentScaleFactor:%f", path.x, path.y, path.z, path.contentScaleFactor];
}

@end
