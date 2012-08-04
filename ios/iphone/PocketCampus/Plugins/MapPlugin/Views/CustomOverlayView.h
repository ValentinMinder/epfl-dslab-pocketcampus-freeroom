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

@interface CustomOverlayView : MKOverlayView {
    NSMutableDictionary* tilesDataTmp;
    ASINetworkQueue* requestsQueue;
}

@property (retain) NSMutableDictionary* tilesDataTmp;  //key : - (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale, value : NSData of corresponding tile image data
@property (retain) ASINetworkQueue* requestsQueue;
@property BOOL isCancellingAll;
@property (assign) id<CustomOverlayViewDelegate> delegate;

- (void)didReceiveMemoryWarning;
- (void)cancelTilesDownload;

@end

@protocol CustomOverlayViewDelegate <NSObject>

- (void)customOverlayViewDidStartLoading:(CustomOverlayView*)overlayView;
- (void)customOverlayViewDidFinishLoading:(CustomOverlayView*)overlayView;

@end