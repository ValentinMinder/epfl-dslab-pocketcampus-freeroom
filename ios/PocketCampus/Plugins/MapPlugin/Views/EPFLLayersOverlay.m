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

//  Created by Loïc Gardiol on 07.05.14.

#import "EPFLLayersOverlay.h"

#import "MapUtils.h"

#import <AFNetworking/AFNetworking.h>

static NSInteger const kDefaultFloorLevel = 1;
static NSInteger const kMaxFloorLevel = 8;
static NSInteger const kMinFloorLevel = -4;
static double const kFloorLevelsMaxAltitude = 1200.0;

@interface EPFLLayersOverlay ()

@property (nonatomic, weak, readwrite) MKMapView* mapView;

@end

@implementation EPFLLayersOverlay

#pragma mark - PCScreenTileOverlay protocol implementation

- (instancetype)initWithMapView:(MKMapView *)mapView {
    self = [super init];
    if (self) {
        self.mapView = mapView;
    }
    return self;
}

- (BOOL)shouldDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    MKZoomScale currentZoomScale = [self currentMapViewZoomScale];
    /*if (zoomScale != currentZoomScale) {
        NSLog(@"NO: zoomScale: %f, current: %f", zoomScale, currentZoomScale);
        return NO;
    }*/
    if (!MKMapRectIntersectsRect(self.mapView.completeVisibleMapRect, mapRect)) {
        return NO;
    }
    return YES;
}

- (NSURL*)URLForCurrentlyVisibleMapRectAndZoomScale {
    MKMapRect rect = self.mapView.completeVisibleMapRect;
    MKZoomScale zoomScale = [self currentMapViewZoomScale];
    NSString* urlString = [self urlForMapRect:rect andZoomScale:zoomScale];
    return [NSURL URLWithString:urlString];
}

- (UIImage*)croppedImageFromCurrentlyVisibleMapRectImage:(UIImage *)image forMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    [PCUtils throwExceptionIfObject:image notKindOfClass:[UIImage class]];
    MKCoordinateRegion region = MKCoordinateRegionForMapRect(mapRect);
    CGRect rect = [self.mapView convertRegion:region toRectToView:self.mapView];
    if (![self shouldDrawMapRect:mapRect zoomScale:zoomScale]) {
        return [self blankImageOfSize:CGSizeMake(rect.size.width, rect.size.height)];
    }
    
    MKCoordinateRegion visibleRegion = MKCoordinateRegionForMapRect(self.mapView.completeVisibleMapRect);
    CGRect visibleRect = [self.mapView convertRegion:visibleRegion toRectToView:self.mapView];
    
    CGRect croppedRect = CGRectIntersection(visibleRect, rect);
    
    //NSString* test = [NSString stringWithFormat:@"\n\n%@\n=>\n%@", NSStringFromCGRect(rect), NSStringFromCGRect(croppedRect)];
    
    CGImageRef cropppedImageRef = CGImageCreateWithImageInRect(image.CGImage, croppedRect);
    UIImage* croppedImage = [UIImage imageWithCGImage:cropppedImageRef];
    CGImageRelease(cropppedImageRef);
    
    CGPoint drawOrigin;
    drawOrigin.x = rect.origin.x < 0.0 ? fabsf(rect.origin.x) : 0.0;
    drawOrigin.y = rect.origin.y < 0.0 ? fabsf(rect.origin.y) : 0.0;
    
    UIGraphicsBeginImageContext(rect.size);
    [croppedImage drawAtPoint:drawOrigin];
    UIImage* finalImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return finalImage;
}

#pragma mark - PCTileOverlay overrides

- (NSInteger)defaultFloorLevel {
    return kDefaultFloorLevel;
}

- (NSInteger)maxFloorLevel {
    return kMaxFloorLevel;
}

- (NSInteger)minFloorLevel {
    return kMinFloorLevel;
}

- (CLLocationDistance)floorLevelsMaxAltitude {
    return kFloorLevelsMaxAltitude;
}

- (CGFloat)desiredAlpha {
    return 1.0;
}

- (MKOverlayLevel)desiredLevelForMapView {
    return MKOverlayLevelAboveLabels;
}

#pragma mark - MKTileOverlay overrides

/*- (NSURL*)URLForTilePath:(MKTileOverlayPath)path {
    CH1903BBox bbox = [MapUtils tilePathToCH1903:path tileSize:self.tileSize];
    
    NSString* test = [NSString stringWithFormat:@"%@ | BBOX startX:%lf startY:%lf, endX:%lf, endY:%lf", NSStringFromMKTileOverlayPath(path), bbox.start_x, bbox.start_y, bbox.end_x, bbox.end_y];

    
    double north = tiley2lat(path.y, path.z);
    double south = tiley2lat(path.y+1, path.z);
    
    double west = tilex2long(path.x, path.z);
    double east = tilex2long(path.x+1, path.z);
    
#warning WHY GO THROUGH MAP RECT ? => Directly convert north, south, west, east to CH coordinates, and feed them to urlForEpflLayerWithCH1903StartX. Easy.
    
    
    NSString* test2 = [NSString stringWithFormat:@"%lf %lf %lf %lf", west, north, east, south];
    
    NSString* urlString = [self urlForEpflLayerWithCH1903StartX:bbox.start_x startY:bbox.start_y endX:bbox.end_x endY:bbox.end_y width:1024.0 height:1024.0];
    
    return [NSURL URLWithString:urlString];
}

- (void)loadTileAtPath:(MKTileOverlayPath)path result:(void (^)(NSData *, NSError *))result {
    AFHTTPRequestOperationManager* manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFImageResponseSerializer serializer];
    NSURL* url = [self URLForTilePath:path];
    [manager GET:[url absoluteString] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        UIImage* image = responseObject;
        if (!image) {
            return;
        }
        //image = [self addBorderToImage:image];
        result(UIImagePNGRepresentation(image), nil);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Failure");
    }];
}*/

#pragma mark - Private

- (MKZoomScale)currentMapViewZoomScale {
    return self.mapView.bounds.size.width / self.mapView.visibleMapRect.size.width;
}

- (NSString *)urlForMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    
    CH1903BBox bbox = [MapUtils WGStoCH1903:mapRect];
    
    /*if (![PCUtils isRetinaDevice]) {
        zoomScale *= 2.0;
    }
    
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
    }*/
    
    CGRect visibleRect = self.mapView.bounds;
    CGFloat scale = 1.0;//[[UIScreen mainScreen] scale];
    CGFloat width = visibleRect.size.width * scale;
    CGFloat height = visibleRect.size.height * scale;
    
    NSString* url = [self urlForEpflLayerWithCH1903StartX:bbox.start_x startY:bbox.start_y endX:bbox.end_x endY:bbox.end_y width:width height:height];
    
    return url;
}


- (NSString*)urlForEpflLayerWithCH1903StartX:(double)startX startY:(double)startY endX:(double)endX endY:(double)endY width:(double)width height:(double)height  {
    
    NSString* baseURLWithBBoxEmptyParameter = @"http://plan.epfl.ch/wms_themes?FORMAT=image/png&LOCALID=-1&VERSION=1.1.1&REQUEST=GetMap&SRS=EPSG%3A21781&BBOX=";
    
    NSString* urlString = [NSString stringWithFormat:@"%@%lf,%lf,%lf,%lf&WIDTH=%.0lf&HEIGHT=%.0lf&LAYERS=locaux_labels_en%d,batiments_routes_labels,parkings_publicsall,informationall", baseURLWithBBoxEmptyParameter, startY, endX, endY, startX, width, height, (int)self.floorLevel];
    return urlString;
}

- (UIImage*)blankImageOfSize:(CGSize)size {
    UIGraphicsBeginImageContextWithOptions(size, NO, 0.0);
    UIImage* image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

#pragma mark - Tests

- (UIImage*)imageWithBorderFromImage:(UIImage*)source;
{
    CGSize size = [source size];
    //size.width *= 2.0;
    //size.height *= 2.0;
    UIGraphicsBeginImageContext(size);
    CGRect rect = CGRectMake(0, 0, size.width, size.height);
    [source drawInRect:rect blendMode:kCGBlendModeNormal alpha:1.0];
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetRGBStrokeColor(context, 1.0, 0.5, 1.0, 1.0);
    CGContextSetLineWidth(context, 2.0);
    CGContextStrokeRect(context, rect);
    UIImage *testImg =  UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return testImg;
}

- (UIImage *)addBorderToImage:(UIImage *)image {
	CGImageRef bgimage = [image CGImage];
	float width = CGImageGetWidth(bgimage);
	float height = CGImageGetHeight(bgimage);
	
    // Create a temporary texture data buffer
	void *data = malloc(width * height * 4);
	
	// Draw image to buffer
	CGContextRef ctx = CGBitmapContextCreate(data,
                                             width,
                                             height,
                                             8,
                                             width * 4,
                                             CGImageGetColorSpace(image.CGImage),
                                             kCGImageAlphaPremultipliedLast);
	CGContextDrawImage(ctx, CGRectMake(0, 0, (CGFloat)width, (CGFloat)height), bgimage);
	
	//Set the stroke (pen) color
	CGContextSetStrokeColorWithColor(ctx, [UIColor greenColor].CGColor);
    
	//Set the width of the pen mark
	CGFloat borderWidth = 2.0;
	CGContextSetLineWidth(ctx, borderWidth);
    
	//Start at 0,0 and draw a square
	CGContextMoveToPoint(ctx, 0.0, 0.0);
	CGContextAddLineToPoint(ctx, 0.0, height);
	CGContextAddLineToPoint(ctx, width, height);
	CGContextAddLineToPoint(ctx, width, 0.0);
	CGContextAddLineToPoint(ctx, 0.0, 0.0);
	
	//Draw it
	CGContextStrokePath(ctx);
    
    // write it to a new image
	CGImageRef cgimage = CGBitmapContextCreateImage(ctx);
	UIImage *newImage = [UIImage imageWithCGImage:cgimage];
	CFRelease(cgimage);
	CGContextRelease(ctx);
	
    // auto-released
	return newImage;
}

@end
