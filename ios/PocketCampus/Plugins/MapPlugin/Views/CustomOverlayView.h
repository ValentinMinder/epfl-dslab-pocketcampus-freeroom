//
//  CustomOverlayView.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

#import "ASIHTTPRequest.h"

#import "ASINetworkQueue.h"

@protocol CustomOverlayViewDelegate;

@interface CustomOverlayView : MKOverlayView

@property (weak) id<CustomOverlayViewDelegate> delegate;

- (void)didReceiveMemoryWarning;
- (void)cancelTilesDownload:(BOOL)willBeDeallocated;

@end

@protocol CustomOverlayViewDelegate <NSObject>

- (void)customOverlayViewDidStartLoading:(CustomOverlayView*)overlayView;
- (void)customOverlayViewDidFinishLoading:(CustomOverlayView*)overlayView;

@end