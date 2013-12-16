//
//  CustomOverlayRenderer.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "RemoteOverlayRenderer.h"

#import "OverlayWithURLs.h"

#import <AFHTTPRequestOperation.h>

//static NSTimeInterval kTilesValiditySeconds = 604800.0; //seconds = 4 weeks

@interface RemoteOverlayRenderer ()

@property (nonatomic, strong) NSOperationQueue* operationQueue;

@property (nonatomic, strong) NSTimer* callDelegateTimer;

@property (strong) NSCache* tilesCache;  //key : - (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale, value : UIImage of corresponding tile
@property BOOL willBeDeallocated;

@end


@implementation RemoteOverlayRenderer

- (id)initWithOverlay:(id <MKOverlay>)overlay {
    self = [super initWithOverlay:overlay];
    if (self) {
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
        self.tilesCache = [NSCache new];
        self.callDelegateTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(callDelegateAccordingToRequestsState) userInfo:nil repeats:YES];
        self.willBeDeallocated = NO;
    }
    return self;
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
        if ([self.tilesCache objectForKey:key]) { //tile has already been downloaded and is in memory
            return YES;
        }
        
        NSString* urlString = [key copy];
        
        NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:urlString] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
        
        AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
        operation.responseSerializer = [AFImageResponseSerializer serializer];

        RemoteOverlayRenderer* weakSelf __weak = self;
        [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* responseImage) {
            if (responseImage) {
                @synchronized (weakSelf) {
                    [weakSelf.tilesCache setObject:responseImage forKey:[weakSelf keyWithMapRect:mapRect andZoomScale:zoomScale]];
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
        UIImage* image = [self.tilesCache objectForKey:key];
        
        if (!image) {
            [self canDrawMapRect:mapRect zoomScale:zoomScale];
            return;
        }
        
        if (self.willBeDeallocated) {
            return;
        }
        
        UIGraphicsPushContext(context);
        [image drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:0.85];
        UIGraphicsPopContext();
    }
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
