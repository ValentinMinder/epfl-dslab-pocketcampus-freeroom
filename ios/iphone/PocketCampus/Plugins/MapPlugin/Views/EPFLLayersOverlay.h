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

@interface EPFLLayersOverlay : NSObject<OverlayWithURLs> {
    NSInteger currentLayerLevel;
}

@property (nonatomic, assign) MKMapView* mapView;

- (void)increaseLayerLevel;
- (void)decreaseLayerLevel;
- (void)setLayerLevel:(NSInteger)newLevel;

@end
