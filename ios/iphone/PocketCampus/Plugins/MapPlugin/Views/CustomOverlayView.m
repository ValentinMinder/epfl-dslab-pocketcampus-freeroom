//
//  CustomOverlayView.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CustomOverlayView.h"

#import "ASIDownloadCache.h"

#import "EPFLTileOverlay.h"

#import "ObjectArchiver.h"

#import "MapUtils.h"

@implementation CustomOverlayView

@synthesize tilesDataTmp, requests, isCancellingAll;

- (id)initWithOverlay:(id <MKOverlay>)overlay {
    self = [super initWithOverlay:overlay];
    if (self) {
        
        self.tilesDataTmp = [NSMutableDictionary dictionary];
        self.requests = [NSMutableArray array];
        self.isCancellingAll = NO;
        
        [NSTimer scheduledTimerWithTimeInterval:10.0 target:self selector:@selector(test) userInfo:nil repeats:NO];
    }
    return self;
}

- (void)test {
    //[self.tilesDataTmp removeAllObjects];
}

- (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    /*EPFLTileOverlay* epflTileOverlay = (EPFLTileOverlay*)self.overlay;
    return [NSString stringWithFormat:@"%lf%lf%lf%lf%f%d", mapRect.origin.x, mapRect.origin.y, mapRect.size.width, mapRect.size.height, zoomScale, epflTileOverlay.currentLayerLevel];*/
    NSUInteger zoomLevel = [MapUtils zoomLevelForZoomScale:zoomScale];
    CGPoint mercatorPoint = [MapUtils mercatorTileOriginForMapRect:mapRect];
    NSUInteger tileX = floor(mercatorPoint.x * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
    NSUInteger tileY = floor(mercatorPoint.y * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
    NSString* urlString = [(EPFLTileOverlay*)self.overlay urlForPointWithX:tileX andY:tileY andZoomLevel:zoomLevel];
    return urlString;
}

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    if (self.isCancellingAll) {
        return NO;
    }
    if (![(EPFLTileOverlay*)self.overlay canDrawMapRect:mapRect zoomScale:zoomScale]) {
        return NO;
    }
    
    NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
    
    if ([self.tilesDataTmp objectForKey:key] != nil) {
        return YES;
    }
    
    NSString* urlString = [[key copy] autorelease];
    
    ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
    [request setDownloadCache:[ASIDownloadCache sharedCache]];
    [request setCachePolicy:ASIOnlyLoadIfNotCachedCachePolicy];
    
    if ([[request downloadCache] isCachedDataCurrentForRequest:request]) {
        [request startSynchronous];
        NSError *error = request.error;
        if (error) {
            return NO;
        }
        [self.tilesDataTmp setObject:request.responseData forKey:key];
        return YES;
    }
    
    request.delegate = self;
    request.didFinishSelector = @selector(requestDidFinishLoad:);
    request.didFailSelector = @selector(requestDidFail:);

    NSDictionary *metaData = [NSDictionary dictionaryWithObjectsAndKeys:
                              [NSNumber numberWithDouble:mapRect.origin.x], @"mr_origin_x",
                              [NSNumber numberWithDouble:mapRect.origin.y], @"mr_origin_y",
                              [NSNumber numberWithDouble:mapRect.size.width], @"mr_size_w",
                              [NSNumber numberWithDouble:mapRect.size.height], @"mr_size_h",
                              [NSNumber numberWithFloat:zoomScale], @"zoomScale",
                              nil];
    request.userInfo = metaData;
    
    if (self.isCancellingAll) {
        return NO;
    }
    [self.requests addObject:request];
    [request startAsynchronous];
    
    return NO;
}

- (void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context {
    @synchronized(self) {
        if (self.isCancellingAll) {
            return;
        }
        NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        NSData* imageData = [self.tilesDataTmp objectForKey:key];
        
        if (imageData == nil) {
            [self canDrawMapRect:mapRect zoomScale:zoomScale];
            return;
        }
        if (self.isCancellingAll) {
            return;
        }
        
        /*TEST*/
        /*NSUInteger zoomLevel = [MapUtils zoomLevelForZoomScale:zoomScale];
        CGPoint mercatorPoint = [MapUtils mercatorTileOriginForMapRect:mapRect];
        NSUInteger tileX = floor(mercatorPoint.x * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
        NSUInteger tileY = floor(mercatorPoint.y * [MapUtils worldTileWidthForZoomLevel:zoomLevel]);
        
        NSString *urlString = [(EPFLTileOverlay*)self.overlay urlForPointWithX:tileX andY:tileY andZoomLevel:zoomLevel];
        
        NSString* fileName = [urlString lastPathComponent];
        
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 50, 50)];
        label.text = fileName;
        label.font = [UIFont systemFontOfSize:20.0];
        
        NSLog(@"%@ - key : %@", fileName, key);*/
        /* END OF TEST */

        UIImage* image = [UIImage imageWithData:imageData];
        EPFLTileOverlay* overlay = (EPFLTileOverlay*)self.overlay;
        UIGraphicsPushContext(context);
        [image drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:overlay.alpha];
        UIGraphicsPopContext();
    }
}

- (void)requestDidFinishLoad:(ASIHTTPRequest *)request {
    if (self.isCancellingAll) {
        return;
    }
    if (request.responseData.length < 800) { //bytes, means it's not an image, but a 404 error page        
        return;
    }
    
    NSNumber* mr_origin_x = [(NSDictionary *)[request userInfo] objectForKey:@"mr_origin_x"];
    NSNumber* mr_origin_y = [(NSDictionary *)[request userInfo] objectForKey:@"mr_origin_y"];
    NSNumber* mr_size_w = [(NSDictionary *)[request userInfo] objectForKey:@"mr_size_w"];
    NSNumber* mr_size_h = [(NSDictionary *)[request userInfo] objectForKey:@"mr_size_h"];
    
    MKMapRect mapRect = MKMapRectMake([mr_origin_x doubleValue],[mr_origin_y doubleValue],[mr_size_w doubleValue],[mr_size_h doubleValue]);
    
    NSNumber* zoomScaleNumber = [(NSDictionary *)[request userInfo] objectForKey:@"zoomScale"];
    MKZoomScale zoomScale = [zoomScaleNumber floatValue];
    
    
    [self.tilesDataTmp setObject:request.responseData forKey:[self keyWithMapRect:mapRect andZoomScale:zoomScale]];
    [self setNeedsDisplayInMapRect:mapRect zoomScale:zoomScale];
    [self.requests removeObject:request];
}

-(void)requestDidFail:(ASIHTTPRequest *)request {
    if (self.isCancellingAll) {
        return;
    }
    [self.requests removeObject:request];
}

- (void)cancelTilesDownload {
    [ASIHTTPRequest cancelPreviousPerformRequestsWithTarget:self];
}

- (void)dealloc {
    [self cancelTilesDownload];
    self.isCancellingAll = YES;
    for (ASIHTTPRequest* request in self.requests) {
        request.delegate = nil;
    }
    [self.tilesDataTmp release];
    [self.requests release];
    [super dealloc];
}

@end

