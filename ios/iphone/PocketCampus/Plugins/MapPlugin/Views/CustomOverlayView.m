//
//  CustomOverlayView.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "CustomOverlayView.h"

#import "ASIDownloadCache.h"

#import "ObjectArchiver.h"

#import "MapUtils.h"

#import "OverlayWithURLs.h"

@implementation CustomOverlayView

@synthesize tilesDataTmp, requestsQueue, isCancellingAll, delegate;

- (id)initWithOverlay:(id <MKOverlay>)overlay {
    self = [super initWithOverlay:overlay];
    if (self) {
        //self.tilesDataTmp = (NSMutableDictionary*)[ObjectArchiver objectForKey:[(id<OverlayWithURLs>)self.overlay identifier] andPluginName:@"map" nilIfDiffIntervalLargerThan:5184000]; //seconds = 60 days
        if (self.tilesDataTmp == nil) {
            self.tilesDataTmp = [NSMutableDictionary dictionary]; //retained in prop
        }
        self.requestsQueue = [ASINetworkQueue queue]; //retained in prop
        self.requestsQueue.shouldCancelAllRequestsOnFailure = NO;
        self.isCancellingAll = NO;
    }
    return self;
}

- (void)didReceiveMemoryWarning {
    @synchronized(self) {
        [self.tilesDataTmp removeAllObjects];
    }
}

- (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    return [(id<OverlayWithURLs>)self.overlay urlForMapRect:mapRect andZoomScale:zoomScale];
}

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    @synchronized (self) {
        if (self.isCancellingAll) {
            return NO;
        }
        if (![(id<OverlayWithURLs>)self.overlay canDrawMapRect:mapRect zoomScale:zoomScale]) {
            return NO;
        }
        
        NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        
        
        if ([self.tilesDataTmp objectForKey:key] != nil) { //tile has already been downloaded and is in memory
            return YES;
        }
        
        NSString* urlString = [key copy];
        
        
        ASIHTTPRequest* request = [ASIHTTPRequest requestWithURL:[NSURL URLWithString:urlString]];
        [urlString release];
        /*request.downloadCache = [ASIDownloadCache sharedCache];
        request.cachePolicy = ASIOnlyLoadIfNotCachedCachePolicy;*/
        request.numberOfTimesToRetryOnTimeout = 3;

        if ([request.downloadCache isCachedDataCurrentForRequest:request]) {
            [request startSynchronous];
            if (request.error != nil) {
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

        
        if (self.requestsQueue.operationCount == 0) {
            [self.requestsQueue go];
            if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidStartLoading:)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidStartLoading:) withObject:self waitUntilDone:NO];
            }
        }

        [self.requestsQueue addOperation:request];
        
        return NO;
    }
    
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
        /*
        NSString *urlString = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        NSString* fileName = [urlString lastPathComponent];   
        */
        /* END OF TEST */

        UIImage* image = [UIImage imageWithData:imageData];
        
        /*if ([urlString rangeOfString:@".png"].location != NSNotFound) {
            image = [self addText:image text:fileName];
        }*/
        
        UIGraphicsPushContext(context);
        [image drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:1.0];
        UIGraphicsPopContext();
    }
}
 
/* TEST */
-(UIImage *)addText:(UIImage *)img text:(NSString *)text1{
    int w = img.size.width;
    int h = img.size.height;
    //lon = h - lon;
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(NULL, w, h, 8, 4 * w, colorSpace, kCGImageAlphaPremultipliedFirst);
    
    CGContextDrawImage(context, CGRectMake(0, 0, w, h), img.CGImage);
    CGContextSetRGBFillColor(context, 0.0, 0.0, 1.0, 1);
	
    char* text	= (char *)[text1 cStringUsingEncoding:NSASCIIStringEncoding];// "05/05/09";
    CGContextSelectFont(context, "Arial", 18, kCGEncodingMacRoman);
    CGContextSetTextDrawingMode(context, kCGTextFill);
    CGContextSetRGBFillColor(context, 0, 0, 0, 1);
	
    
    //rotate text
    //CGContextSetTextMatrix(context, CGAffineTransformMakeRotation( -M_PI ));
	
    CGContextShowTextAtPoint(context, 4, 52, text, strlen(text));
	
	
    CGImageRef imageMasked = CGBitmapContextCreateImage(context);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    UIImage* image = [UIImage imageWithCGImage:imageMasked];
    
    CGImageRelease(imageMasked);
	
    return image;
}
/* END OF TEST */

- (void)requestDidFinishLoad:(ASIHTTPRequest *)request {
    @synchronized (self) {
        if (self.isCancellingAll) {
            return;
        }
        if (request.responseStatusCode == 404) {
            if (self.requestsQueue.operationCount == 0) {
                if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidFinishLoading:)]) {
                    [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidFinishLoading:) withObject:self waitUntilDone:NO];
                }
            }
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
        if (self.requestsQueue.operationCount == 0) {
            if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidFinishLoading:)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidFinishLoading:) withObject:self waitUntilDone:NO];
            }
        }
    }
    
}

-(void)requestDidFail:(ASIHTTPRequest *)request {
    @synchronized (self) {
        if (self.isCancellingAll) {
            return;
        }
        
        if (self.requestsQueue.operationCount == 0) {
            if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidFinishLoading:)]) {
                [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidFinishLoading:) withObject:self waitUntilDone:NO];
            }
        }
    }
}

- (void)cancelTilesDownload {
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidFinishLoading:)]) {
        [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidFinishLoading:) withObject:self waitUntilDone:NO];
    }
    [self.requestsQueue cancelAllOperations];
    for (ASIHTTPRequest* request in self.requestsQueue.operations) {
        request.delegate = nil;
    }
}

- (void)dealloc {
    @synchronized (self) {
        self.isCancellingAll = YES;
        self.delegate = nil;
        [self.requestsQueue setSuspended:YES];
        [self cancelTilesDownload];
        [requestsQueue release];
    }
    [ObjectArchiver saveObject:tilesDataTmp forKey:[(id<OverlayWithURLs>)self.overlay identifier] andPluginName:@"map"];
    [tilesDataTmp release];
    [super dealloc];
}

@end

