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

@interface MapViewController : UIViewController<MKMapViewDelegate, UIGestureRecognizerDelegate, UIActionSheetDelegate, UISearchBarDelegate, UIGestureRecognizerDelegate, UIAlertViewDelegate, MapServiceDelegate, CustomOverlayViewDelegate, DirectoryServiceDelegate, UIPopoverControllerDelegate> {
    UIButton* myLocationButton;
    UIView* floorManagementSuperview;
    UIImageView* floorManagementBackground;
    UIButton* floorDownButton;
    UILabel* floorLabel;
    UIButton* floorUpButton;
    UIButton* eyeButton;
    UIActionSheet* othersActionSheet;
    UIAlertView* internetConnectionAlert;
    MapService* mapService;
    DirectoryService* directoryService;
    Person* personToDisplay;
    NSString* initialQuery;
    NSString* initialQueryManualPinLabelText;
    MKMapView* mapView;
    EPFLTileOverlay* epflTileOverlay;
    EPFLLayersOverlay* epflLayersOverlay;
    CustomOverlayView* tileOverlayView;
    CustomOverlayView* layersOverlayView;
    BOOL overlaysVisible;
    MKCoordinateRegion epflRegion;
    UISearchBar* searchBar;
    UIActivityIndicatorView* searchActivityIndicator;
    SearchBarState searchBarState;
    CGRect searchBarHiddenFrame;
    CGRect searchBarVisibleFrame;
    UIActivityIndicatorView* overlaysLoadingIndicator;
    UIActivityIndicatorView* navBarLoadingIndicator;
    BOOL showBuildingsInterior;
    NSArray* annotationsToAdd;
    UIPopoverController* personPopOverController;
    BOOL searchBarWasFirstResponder;
}

@property (nonatomic, assign) IBOutlet MKMapView* mapView;
@property (nonatomic, assign) IBOutlet UISearchBar* searchBar;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* overlaysLoadingIndicator;
@property (nonatomic, assign) IBOutlet UIButton* myLocationButton;
@property (nonatomic, assign) IBOutlet UIView* floorManagementSuperview;
@property (nonatomic, assign) IBOutlet UIImageView* floorManagementBackground;
@property (nonatomic, assign) IBOutlet UIButton* floorDownButton;
@property (nonatomic, assign) IBOutlet UILabel* floorLabel;
@property (nonatomic, assign) IBOutlet UIButton* floorUpButton;
@property (nonatomic, assign) IBOutlet UIButton* eyeButton;

- (id)initWithInitialQuery:(NSString*)query;
- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel;

- (IBAction)myLocationPressed;
- (IBAction)floorDownPressed;
- (IBAction)floorUpPressed;
- (IBAction)eyePressed;


- (void)willLoseFocus;
- (void)didRegainActive;


@end
