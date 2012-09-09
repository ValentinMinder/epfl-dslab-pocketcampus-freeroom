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

static NSTimeInterval TILES_VALIDITY = 604800.0; //seconds = 4 weeks

@implementation CustomOverlayView

@synthesize tilesDataTmp, willBeDeallocated, delegate;

- (id)initWithOverlay:(id <MKOverlay>)overlay {
    self = [super initWithOverlay:overlay];
    if (self) {
        self.tilesDataTmp = [NSMutableDictionary dictionary]; //retained in prop
        requests = [[NSMutableArray array] retain];
        callDelegateTimer = [[NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(callDelegateAccordingToRequestsState) userInfo:nil repeats:YES] retain];
        self.willBeDeallocated = NO;
    }
    return self;
}

- (void)didReceiveMemoryWarning {
    @synchronized(self) {
        NSLog(@"CustomOverlayView didReceiveMemoryWarning. Removing tilesDataTmp objects...");
        [self.tilesDataTmp removeAllObjects];
    }
}

- (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale {
    return [(id<OverlayWithURLs>)self.overlay urlForMapRect:mapRect andZoomScale:zoomScale];
}

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    @synchronized (self) {
        if (self.willBeDeallocated) {
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
        request.downloadCache = [ASIDownloadCache sharedCache];
        request.secondsToCache = TILES_VALIDITY;
        request.cacheStoragePolicy = ASICachePermanentlyCacheStoragePolicy;
        request.cachePolicy = ASIAskServerIfModifiedWhenStaleCachePolicy;
        
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
        
        if (self.willBeDeallocated) {
            return NO;
        }
        
        [requests addObject:request];
        [request startAsynchronous];
        return NO;
    }
    
}

- (void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context {
    @synchronized(self) {
        if (self.willBeDeallocated) {
            return;
        }
        NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        NSData* imageData = [self.tilesDataTmp objectForKey:key];
        
        if (imageData == nil) {
            [self canDrawMapRect:mapRect zoomScale:zoomScale];
            return;
        }
        if (self.willBeDeallocated) {
            return;
        }
        
        UIImage* image = [[UIImage alloc] initWithData:imageData];
        
        /*TEST*/
        /*
        NSString *urlString = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        NSString* fileName = [urlString lastPathComponent];   
        */
        
        /*if ([urlString rangeOfString:@".png"].location != NSNotFound) {
            image = [self addText:image text:fileName];
        }*/
        
        //NSString* dir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
        
        //[imageData writeToFile:[dir stringByAppendingPathComponent:[NSString stringWithFormat:@"%d.png", [urlString hash]]] atomically:NO];
        
        /* END OF TEST */
        
        UIGraphicsPushContext(context);
        [image drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:1.0];
        UIGraphicsPopContext();
        [image release];
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
    request.delegate = nil;
    [request retain]; //released at end of method
    [requests removeObject:request];
    if (self.willBeDeallocated) {
        [request release];
        return;
    }
    
    if (request.responseStatusCode == 404) {
        [request release];
        return;
    }
    
    NSNumber* mr_origin_x = [(NSDictionary *)[request userInfo] objectForKey:@"mr_origin_x"];
    NSNumber* mr_origin_y = [(NSDictionary *)[request userInfo] objectForKey:@"mr_origin_y"];
    NSNumber* mr_size_w = [(NSDictionary *)[request userInfo] objectForKey:@"mr_size_w"];
    NSNumber* mr_size_h = [(NSDictionary *)[request userInfo] objectForKey:@"mr_size_h"];
    
    MKMapRect mapRect = MKMapRectMake([mr_origin_x doubleValue],[mr_origin_y doubleValue],[mr_size_w doubleValue],[mr_size_h doubleValue]);
    
    NSNumber* zoomScaleNumber = [(NSDictionary *)[request userInfo] objectForKey:@"zoomScale"];
    MKZoomScale zoomScale = [zoomScaleNumber floatValue];
    
    @synchronized (self) {
        [self.tilesDataTmp setObject:request.responseData forKey:[self keyWithMapRect:mapRect andZoomScale:zoomScale]];
    }
    [self setNeedsDisplayInMapRect:mapRect zoomScale:zoomScale];
    [request release];
}

- (void)requestDidFail:(ASIHTTPRequest*)request {
    request.delegate = nil;
    [requests removeObject:request];
    if (self.willBeDeallocated) {
        return;
    }
}


- (void)callDelegateAccordingToRequestsState {
    if ([requests count] == 0) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidFinishLoading:)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidFinishLoading:) withObject:self waitUntilDone:NO];
        }
    } else {
        if (self.delegate && [self.delegate respondsToSelector:@selector(customOverlayViewDidStartLoading:)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(customOverlayViewDidStartLoading:) withObject:self waitUntilDone:NO];
        }
    }
}

- (void)cancelTilesDownload:(BOOL)willBeDeallocated_ {
    self.willBeDeallocated = willBeDeallocated_;

    for (ASIHTTPRequest* request in [[requests copy] autorelease]) {
        request.delegate = nil;
        [request cancel];
    }
    @synchronized(self) {
        [requests removeAllObjects];
    }
}

- (void)dealloc {
    [callDelegateTimer invalidate];
    [callDelegateTimer release];
    [requests release];
    [tilesDataTmp release];
    [super dealloc];
}

@end

