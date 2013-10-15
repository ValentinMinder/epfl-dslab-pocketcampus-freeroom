//
//  RemoteOverlayRenderer.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 08.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

@import MapKit;

@protocol RemoteOverlayRendererDelegate;

@interface RemoteOverlayRenderer : MKOverlayRenderer

@property (weak) id<RemoteOverlayRendererDelegate> delegate;

- (void)cancelTilesDownload:(BOOL)willBeDeallocated;

@end

@protocol RemoteOverlayRendererDelegate <NSObject>

- (void)remoteOverlayRendererDidStartLoading:(RemoteOverlayRenderer*)overlayRenderer;
- (void)remoteOverlayRendererDidFinishLoading:(RemoteOverlayRenderer*)overlayRenderer;

@end
