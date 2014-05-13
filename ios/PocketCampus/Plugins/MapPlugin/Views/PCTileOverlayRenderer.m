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

#import "PCTileOverlayRenderer.h"

#import "PCTileOverlay.h"

#import "PCScreenTileOverlay.h"

#import <AFNetworking/AFNetworking.h>

@interface PCTileOverlayRenderer ()

@property (nonatomic, readwrite) PCTileOverlay* pcTileOverlay;
@property (nonatomic, readwrite) PCTileOverlay<PCScreenTileOverlay>* pcScreenTileOverlay;

@property (nonatomic, strong) NSCache* tilesCache;

@property (nonatomic, strong) NSOperationQueue* operationQueue;

@end

@implementation PCTileOverlayRenderer

#pragma mark - Init

- (instancetype)initWithPCTileOverlay:(PCTileOverlay*)overlay {
    self = [super initWithTileOverlay:overlay];
    if (self) {
        self.tilesCache = [NSCache new];
        self.pcTileOverlay = overlay;
        self.alpha = overlay.desiredAlpha;
        [self.pcTileOverlay addObserver:self forKeyPath:NSStringFromSelector(@selector(floorLevel)) options:0 context:nil];
    }
    return self;
}

- (instancetype)initWithScreenPCTileOverlay:(PCTileOverlay<PCScreenTileOverlay>*)overlay {
    self = [self initWithPCTileOverlay:overlay];
    if (self) {
        self.operationQueue = [NSOperationQueue new];
        self.operationQueue.maxConcurrentOperationCount = NSOperationQueueDefaultMaxConcurrentOperationCount;
        self.pcScreenTileOverlay = overlay;
    }
    return self;
}

#pragma mark - Observation

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.pcTileOverlay && [keyPath isEqualToString:NSStringFromSelector(@selector(floorLevel))]) {
        [self reloadData];
    }
}

#pragma mark - MKTileOverlayRenderer

- (void)reloadData {
    [self.operationQueue cancelAllOperations];
    [super reloadData];
}

#pragma mark - MKOverlayRenderer overrides

- (BOOL)canDrawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale {
    if (!self.pcScreenTileOverlay) {
        return [super canDrawMapRect:mapRect zoomScale:zoomScale];
    }
    
    if (![self.pcScreenTileOverlay shouldDrawMapRect:mapRect zoomScale:zoomScale]) {
        return NO;
    }
    
    NSURL* url = [self.pcScreenTileOverlay URLForCurrentlyVisibleMapRectAndZoomScale];
    
    if (self.tilesCache[url.absoluteString]) {
        return YES;
    }
    
    NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
    
    AFHTTPRequestOperation* operation = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    operation.responseSerializer = [AFImageResponseSerializer serializer];
    
    __weak __typeof(self) welf = self;
    [operation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, UIImage* responseImage) {
        if (responseImage) {
            welf.tilesCache[url.absoluteString] = responseImage;
            [welf setNeedsDisplayInMapRect:mapRect zoomScale:zoomScale];
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        //too bad...
    }];
    
    [self.operationQueue addOperation:operation];
    
    return NO;
}

- (void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context {
    if (!self.pcScreenTileOverlay) {
        return [super drawMapRect:mapRect zoomScale:zoomScale inContext:context];
    }
    @synchronized (self) {
        NSURL* url = [self.pcScreenTileOverlay URLForCurrentlyVisibleMapRectAndZoomScale];
        UIImage* image = self.tilesCache[url.absoluteString];
        if (!image) {
            [self canDrawMapRect:mapRect zoomScale:zoomScale];
            return;
        }
        UIImage* croppedImage = [self.pcScreenTileOverlay croppedImageFromCurrentlyVisibleMapRectImage:image forMapRect:mapRect zoomScale:zoomScale];
        UIGraphicsPushContext(context);
        [croppedImage drawInRect:[self rectForMapRect:mapRect] blendMode:kCGBlendModeNormal alpha:self.pcScreenTileOverlay.desiredAlpha];
        CGContextSetRGBStrokeColor(context, 1.0, 0.5, 1.0, 1.0);
        UIGraphicsPopContext();
    }
}

#pragma mark - Dealloc

- (void)dealloc {
    @try {
        [self.pcTileOverlay removeObserver:self forKeyPath:NSStringFromSelector(@selector(floorLevel))];
    }
    @catch (NSException *exception) {}
}

@end
