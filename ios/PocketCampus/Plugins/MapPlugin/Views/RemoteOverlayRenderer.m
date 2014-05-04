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

//  Created by Loïc Gardiol on 08.10.13.

#import "RemoteOverlayRenderer.h"

#import "OverlayWithURLs.h"

#import <AFHTTPRequestOperation.h>

//static NSTimeInterval kTilesValiditySeconds = 604800.0; //seconds = 4 weeks

@interface RemoteOverlayRenderer ()

@property (nonatomic, readwrite) PCRemoteOverlayRendererTileMode tileMode;

@property (nonatomic, strong) NSOperationQueue* operationQueue;

@property (nonatomic, strong) NSTimer* callDelegateTimer;

@property (strong) NSCache* tilesCache;  //key : - (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale, value : UIImage of corresponding tile
@property BOOL willBeDeallocated;

@end


@implementation RemoteOverlayRenderer

#pragma mark - Init

- (id)initWithOverlay:(id <MKOverlay>)overlay tileMode:(PCRemoteOverlayRendererTileMode)tileMode {
    self = [super initWithOverlay:overlay];
    if (self) {
        self.tileMode = tileMode;
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
        self.tilesCache = [NSCache new];
        self.callDelegateTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(callDelegateAccordingToRequestsState) userInfo:nil repeats:YES];
        self.willBeDeallocated = NO;
    }
    return self;
}

- (id)initWithOverlay:(id <MKOverlay>)overlay {
    return [self initWithOverlay:overlay tileMode:PCRemoteOverlayRendererTileModeNormal];
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
        if (self.tileMode == PCRemoteOverlayRendererTileModeSingleTilePerRenderingMapViewVisibleMapRect) {
            if (!self.renderingMapView) {
                [NSException raise:@"Illegal state" format:@"renderingView cannot be nil if tileMode is PCRemoteOverlayRendererTileModeSingleTilePerRenderingMapViewVisibleMapRect"];
            }
            mapRect = self.renderingMapView.visibleMapRect;
        }
        
        
        NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        if (self.tilesCache[key]) { //tile has already been downloaded and is in memory
            return YES;
        }
        
        NSString* urlString = [key copy];
        
        NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
        
        AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
        operation.responseSerializer = [AFImageResponseSerializer serializer];

        RemoteOverlayRenderer* weakSelf __weak = self;
        [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* responseImage) {
            if (weakSelf.willBeDeallocated) {
                return;
            }
            if (responseImage) {
                @synchronized (weakSelf) {
                    [weakSelf.tilesCache setObject:responseImage forKey:[weakSelf keyWithMapRect:mapRect andZoomScale:zoomScale]];
                    if (weakSelf.willBeDeallocated) {
                        return;
                    }
                }
                if (weakSelf.willBeDeallocated) {
                    return;
                }
                [weakSelf setNeedsDisplayInMapRect:mapRect zoomScale:zoomScale];
            }
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            //too bad...
        }];
        if (!self.willBeDeallocated) {
            [self.operationQueue addOperation:operation];
        }
        return NO;
    }
}

- (void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context {
    @synchronized(self) {
        if (self.willBeDeallocated) {
            return;
        }
        NSString* key = [self keyWithMapRect:mapRect andZoomScale:zoomScale];
        UIImage* image = self.tilesCache[key];
        
        if (!image) {
            [self canDrawMapRect:mapRect zoomScale:zoomScale];
            return;
        }
        
        if (self.willBeDeallocated) {
            return;
        }
        
        image = [self imageWithBorderFromImage:image];
        
        UIGraphicsPushContext(context);
        [image drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:0.85];
        CGContextSetRGBStrokeColor(context, 1.0, 0.5, 1.0, 1.0);
        UIGraphicsPopContext();
    }
}

- (UIImage*)imageWithBorderFromImage:(UIImage*)source;
{
    CGSize size = [source size];
    size.width *= 2.0;
    size.height *= 2.0;
    UIGraphicsBeginImageContext(size);
    CGRect rect = CGRectMake(0, 0, size.width, size.height);
    [source drawInRect:rect blendMode:kCGBlendModeNormal alpha:1.0];
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetRGBStrokeColor(context, 1.0, 0.5, 1.0, 1.0);
    CGContextSetLineWidth(context, 0.5);
    CGContextStrokeRect(context, rect);
    UIImage *testImg =  UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return testImg;
}


- (void)callDelegateAccordingToRequestsState {
    if (self.operationQueue.operationCount == 0) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(remoteOverlayRendererDidFinishLoading:)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(remoteOverlayRendererDidFinishLoading:) withObject:self waitUntilDone:NO];
        }
    } else {
        if (self.delegate && [self.delegate respondsToSelector:@selector(remoteOverlayRendererDidStartLoading:)]) {
            [(NSObject*)self.delegate performSelectorOnMainThread:@selector(remoteOverlayRendererDidStartLoading:) withObject:self waitUntilDone:NO];
        }
    }
}

- (void)cancelTilesDownload:(BOOL)willBeDeallocated_ {
    self.willBeDeallocated = willBeDeallocated_;
    
    @synchronized(self) {
        [self.operationQueue cancelAllOperations];
    }
}

- (void)dealloc {
    [self.callDelegateTimer invalidate];
    [self.operationQueue cancelAllOperations];
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}


@end
