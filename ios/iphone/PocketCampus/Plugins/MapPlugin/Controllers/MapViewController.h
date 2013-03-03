//
//  MapViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <MapKit/MapKit.h>

#import "map.h"

#import "MapService.h"

#import "DirectoryService.h"

#import "EPFLTileOverlay.h"

#import "EPFLLayersOverlay.h"

#import "CustomOverlayView.h"

typedef enum {
    SearchBarStateVisible,
    SearchBarStateHidden
} SearchBarState;

@interface MapViewController : UIViewController<MKMapViewDelegate, UIGestureRecognizerDelegate, UIActionSheetDelegate, UISearchBarDelegate, UIGestureRecognizerDelegate, UIAlertViewDelegate, MapServiceDelegate, CustomOverlayViewDelegate, DirectoryServiceDelegate, UIPopoverControllerDelegate>

- (id)initWithInitialQuery:(NSString*)query;
- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel;

- (void)startSearchForQuery:(NSString*)query;

@end
