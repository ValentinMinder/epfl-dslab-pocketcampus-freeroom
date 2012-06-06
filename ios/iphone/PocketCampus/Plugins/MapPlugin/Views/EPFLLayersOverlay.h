//
//  EPFLLayersOverlay.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.06.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <MapKit/MapKit.h>

#import "OverlayWithURLs.h"

@interface EPFLLayersOverlay : NSObject<OverlayWithURLs>

@property (nonatomic, assign) MKMapView* mapView;

@end
