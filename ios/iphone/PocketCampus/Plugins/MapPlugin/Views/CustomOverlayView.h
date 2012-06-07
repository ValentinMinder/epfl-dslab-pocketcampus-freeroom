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

@protocol CustomOverlayViewDelegate;

@interface CustomOverlayView : MKOverlayView

@property (retain) NSMutableDictionary* tilesDataTmp;  //key : - (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale, value : NSData of corresponding tile image data
@property (retain) NSMutableArray* requests;
@property BOOL isCancellingAll;
@property (nonatomic, assign) id<CustomOverlayViewDelegate> delegate;

- (void)cancelTilesDownload;

@end

@protocol CustomOverlayViewDelegate <NSObject>

- (void)customOverlayViewDidStartLoading:(CustomOverlayView*)overlayView;
- (void)customOverlayViewDidFinishLoading:(CustomOverlayView*)overlayView;

@end