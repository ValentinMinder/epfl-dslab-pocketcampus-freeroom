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

@interface CustomOverlayView : MKOverlayView

@property (retain) NSMutableDictionary* tilesDataTmp;  //key : - (NSString*)keyWithMapRect:(MKMapRect)mapRect andZoomScale:(MKZoomScale)zoomScale, value : NSData of corresponding tile image data
@property (retain) NSMutableArray* requests;
@property BOOL isCancellingAll;

- (void)cancelTilesDownload;

@end