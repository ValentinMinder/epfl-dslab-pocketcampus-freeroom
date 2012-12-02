//
//  MapUtils.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 13.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

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


@implementation MapUtils

+ (UIImage*)mapControlOverylabBoxImage {
    UIImage* image = [UIImage imageNamed:@"MapControlsOverlayBox"];
    image = [image resizableImageWithCapInsets:UIEdgeInsetsMake(0.0, 30.0, 0.0, 30.0)];
    return image;
}

+ (NSArray*)mapItemAnnotationsThatShouldBeDisplayed:(NSArray*)annotations forQuery:(NSString*)query {
    if (annotations == nil || ![annotations isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad argument annotations in mapItemAnnotationsThatShouldBeDisplayed:for:Query:" reason:@"annotations is not kind of class NSAarray" userInfo:nil];
    }
    
    if (query == nil || ![query isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad argument query in mapItemAnnotationsThatShouldBeDisplayed:for:Query:" reason:@"query is not kind of class NSString" userInfo:nil];
    }
    
    NSString* lowerQuery = [query lowercaseString];
    NSString* lowerQueryWithoutSpace = [lowerQuery stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    NSMutableArray* retAnnotations = [NSMutableArray array];
    
    NSError* error = NULL;
    
    for (id<MKAnnotation> annotation in annotations) {
        if ([annotation isKindOfClass:[MapItemAnnotation class]]) {
            MapItemAnnotation* mapItemAnnotation = (MapItemAnnotation*)annotation;
            if(mapItemAnnotation.title == nil) {
                continue;
            }
            NSString* lowerTitle = [mapItemAnnotation.title lowercaseString];
            NSString* lowerTitleWithoutSpace = [lowerTitle stringByReplacingOccurrencesOfString:@" " withString:@""];
            
            //NSLog(@"lower title : %@, lowerTitleWithoutSpace : %@", lowerTitle, lowerQueryWithoutSpace);
            
            NSRange titleRange = NSMakeRange(0, [lowerTitle length]);
            
            if ([lowerTitle isEqualToString:lowerQuery] || [lowerTitleWithoutSpace isEqualToString:lowerQueryWithoutSpace]) {
                [retAnnotations addObject:mapItemAnnotation];
                return retAnnotations;
            }
            
            { //separation block
                NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:[NSString stringWithFormat:@"^Auditoire %@$", lowerQuery] options:NSRegularExpressionCaseInsensitive error:&error];
                NSRegularExpression* regexWithoutSpace = [NSRegularExpression regularExpressionWithPattern:[NSString stringWithFormat:@"^Auditoire %@$", lowerQueryWithoutSpace] options:NSRegularExpressionCaseInsensitive error:&error];
                if ([regex numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0 || [regexWithoutSpace numberOfMatchesInString:lowerTitle options:0 range:titleRange] > 0) {
                    [retAnnotations addObject:mapItemAnnotation];
                    return retAnnotations;
                }
            }
            
        }
    }
    
    //TODO rest
    
    retAnnotations = [annotations mutableCopy];
    
    return [retAnnotations autorelease];
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
    
    return [mapView.annotations objectAtIndex:0]; //otherwise select first one
}

+ (NSInteger)levelToSelectForRoomName:(NSString*)roomName {
    NSString* roomNameWithoutSpace = [roomName stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSError* error = nil;
    
    { //Separation block
        NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:@"^\\D*(\\d{2,})" options:NSRegularExpressionCaseInsensitive error:&error]; //Example : BC329
        if (error != nil) {
            return 1;
        }
        NSTextCheckingResult* result = [regex firstMatchInString:roomNameWithoutSpace options:0 range:NSMakeRange(0, [roomNameWithoutSpace length])];
        if (result.numberOfRanges > 1) {
            NSRange range = [result rangeAtIndex:1];
            if (range.length != 0) {
                char levelChar = [[roomNameWithoutSpace substringWithRange:range] characterAtIndex:0];
                NSInteger level = atoi(&levelChar);
                return level;
            }
        }
    }
    
    { //Separation block
        NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern:@"^\\D*(\\d)$" options:NSRegularExpressionCaseInsensitive error:&error]; //Example CO1
        if (error != nil) {
            return 1;
        }
        NSTextCheckingResult* result = [regex firstMatchInString:roomNameWithoutSpace options:0 range:NSMakeRange(0, [roomNameWithoutSpace length])];
        if (result.numberOfRanges > 1) {
            return 1; //assume rooms with only 1 digit are always on first floor
        }
    }
    
    if ([roomName isEqualToString:@"RLC"]) {
        return 1;
    }
    
    if ([roomName isEqualToString:@"Cafeteria BC"]) {
        return 4;
    }
    
    if ([roomName isEqualToString:@"Cafeteria MX"]) {
        return 3;
    }
    
    if ([roomName isEqualToString:@"L'Atlantide"]) {
        return 2;
    }
    
    if ([roomName isEqualToString:@"Le Corbusier"]) {
        return 0;
    }
    
    if ([roomName isEqualToString:@"Le Parmentier"]) {
        return 2;
    }
    
    if ([roomName isEqualToString:@"Le Vinci"]) {
        return 2;
    }
    
    if ([[roomName lowercaseString] isEqualToString:@"satellite"]) {
        return 2;
    }
    
    return INT_MAX; //means level could not determined
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
    if (annotations == nil || ![annotations isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad annotations argument in mapItemAnnotations:" reason:@"annotations is not kind of class NSArray" userInfo:nil];
    }
    NSMutableArray* mapItemAnnotations = [NSMutableArray arrayWithCapacity:annotations.count-1]; //usually, all annotations except user location. Size will adapt if not the case
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
    int count = annotations.count;
    if (count == 0) {
        return MKCoordinateRegionMake(CLLocationCoordinate2DMake(0, 0), MKCoordinateSpanMake(0, 0));
    }
    
    //convert NSArray of id <MKAnnotation> into an MKCoordinateRegion that can be used to set the map size
    //can't use NSArray with MKMapPoint because MKMapPoint is not an id
    MKMapPoint points[count]; //C array of MKMapPoint struct
    for( int i=0; i<count; i++ ) //load points C array by converting coordinates to points
    {
        CLLocationCoordinate2D coordinate = [(id <MKAnnotation>)[annotations objectAtIndex:i] coordinate];
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

@end
