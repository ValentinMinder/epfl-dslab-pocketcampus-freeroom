//
//  MapViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapViewController.h"

#import "MapItemAnnotation.h"

#import "MapUtils.h"

#import "PCUtils.h"

#import "PCValues.h"

#import "MapController.h"

#import "CustomOverlayView.h"

#import "PCUnkownPersonViewController.h"

#import "UIActionSheet+Additions.h"

#import "GANTracker.h"

#import <QuartzCore/QuartzCore.h>

static int kMaxDisplayedAnnotations = 70;
static NSString* kMapItemAnnotationIdentifier = @"mapItemAnnotation";


@interface MapViewController ()

/* Outlets */

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

@property (nonatomic, strong) MapService* mapService;
@property (nonatomic, strong) DirectoryService* directoryService;
@property (nonatomic, strong) Person* personToDisplay;
@property (nonatomic, strong) EPFLTileOverlay* epflTileOverlay;
@property (nonatomic, strong) EPFLLayersOverlay* epflLayersOverlay;
@property (nonatomic, strong) CustomOverlayView* tileOverlayView;
@property (nonatomic, strong) CustomOverlayView* layersOverlayView;
@property (nonatomic, strong) UIAlertView* noResultAlert;
@property (nonatomic, strong) UIAlertView* internetConnectionAlert;
@property (nonatomic, strong) NSString* initialQuery;
@property (nonatomic, strong) NSString* initialQueryManualPinLabelText;
@property (nonatomic) MKCoordinateRegion epflRegion;
@property (nonatomic) SearchBarState searchBarState;
@property (nonatomic) CGRect searchBarHiddenFrame;
@property (nonatomic) CGRect searchBarVisibleFrame;
@property (nonatomic, strong) UIActivityIndicatorView* navBarLoadingIndicator;
@property (nonatomic, strong) UIActivityIndicatorView* searchActivityIndicator;
@property (nonatomic, strong) NSArray* annotationsToAdd;
@property (nonatomic, strong) UIPopoverController* personPopOverController;
@property (nonatomic, strong) UIActionSheet* othersActionSheet;
@property (nonatomic) BOOL showBuildingsInterior;
@property (nonatomic) BOOL searchBarWasFirstResponder;

@end

@implementation MapViewController

#pragma mark - Init

- (id)init
{
    if ([PCUtils isIdiomPad]) {
        self = [super initWithNibName:@"MapView-pad" bundle:nil];
    } else {
        self = [super initWithNibName:@"MapView-phone" bundle:nil];
    }
    if (self) {
        if (![PCUtils isIdiomPad]) {
            self.title = [MapController localizedName];
        }
        self.mapService = [MapService sharedInstanceToRetain];
        self.epflTileOverlay = [[EPFLTileOverlay alloc] init];
        self.epflLayersOverlay = nil; //[[EPFLLayersOverlay alloc] init];
        self.tileOverlayView = [[CustomOverlayView alloc] initWithOverlay:self.epflTileOverlay];
        self.layersOverlayView = nil; //[[CustomOverlayView alloc] initWithOverlay:epflLayersOverlay];
        if ([PCUtils isIdiomPad]) {
            self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519113, 6.566634), MKCoordinateSpanMake(0.014175, 0.016479));
        } else {
            if ([PCUtils isOSVersionSmallerThan:6.0]) {
                self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.006544, 0.007316));
            } else {
                self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.012285, 0.013733));
            }
        }
        _searchBarState = SearchBarStateHidden;
        self.showBuildingsInterior = YES; //default
    }
    return self;

}

- (id)initWithInitialQuery:(NSString*)query {
    self = [self init];
    if (self) {
        self.initialQuery = query;
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel {
    self = [self initWithInitialQuery:query];
    if (self) {
        self.initialQueryManualPinLabelText = query;
    }
    return self;
}

#pragma mark - Properties override

- (MKCoordinateRegion)epflRegion {
    
    if ([PCUtils isIdiomPad]) {
        if ([[UIDevice currentDevice] orientation] == UIDeviceOrientationPortrait) {
            self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519113, 6.566634), MKCoordinateSpanMake(0.014175, 0.016479));
        } else {
            self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519113, 6.566634), MKCoordinateSpanMake(0.010395, 0.021973));
        }
    } else {
        if ([PCUtils isOSVersionSmallerThan:6.0]) {
            self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.006544, 0.007316));
        } else {
            self.epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.012285, 0.013733));
        }
    }
    
    return _epflRegion;
}

#pragma mark - View events

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    if (self.initialQuery)
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/results" withError:NULL];
    else
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map" withError:NULL];
    UITapGestureRecognizer* mapTap = [[UITapGestureRecognizer alloc] initWithTarget:self.searchBar action:@selector(resignFirstResponder)];
    mapTap.cancelsTouchesInView = NO;
    mapTap.delegate = self;
    [self.mapView addGestureRecognizer:mapTap];
    self.tileOverlayView.delegate = self;
    //layersOverlayView.delegate = self;
    [self.mapView setRegion:_epflRegion animated:NO];
    self.mapView.accessibilityIdentifier = @"EPFLMapView";
    self.floorDownButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorDown", @"MapPlugin", nil);
    self.floorUpButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorUp", @"MapPlugin", nil);
    self.eyeButton.accessibilityLabel = NSLocalizedStringFromTable(@"Eye", @"MapPlugin", nil);
    self.epflTileOverlay.mapView = self.mapView;
    self.epflLayersOverlay.mapView = self.mapView;
    
    self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchPlaceholder", @"MapPlugin", nil);
    self.searchBar.isAccessibilityElement = YES;
    self.searchBar.accessibilityIdentifier = @"SearchBar";
    self.searchActivityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    if ([PCUtils isIdiomPad] && ![PCUtils isOSVersionSmallerThan:6.0]) {
        self.searchActivityIndicator.center = CGPointMake(self.searchBar.frame.size.width-45.0, (self.searchBar.frame.size.height/2.0)+1.0);
    } else {
        self.searchActivityIndicator.center = CGPointMake(self.searchBar.frame.size.width-45.0, (self.searchBar.frame.size.height/2.0));
    }
    self.searchActivityIndicator.hidesWhenStopped = YES;
    [self.searchBar addSubview:self.searchActivityIndicator];
    
    
    if ([PCUtils isIdiomPad]) {
        self.searchBar.tintColor = [UIColor clearColor];
        self.searchBar.backgroundColor = [UIColor clearColor];
        self.searchBar.frame = CGRectMake(3.0, -1.0, self.searchBar.frame.size.width, self.searchBar.frame.size.height);
        self.searchBar.tintColor = [UIColor clearColor];
        [[self.searchBar.subviews objectAtIndex:0] removeFromSuperview];
        UIView* searchBarContainerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.searchBar.frame.size.width, self.searchBar.frame.size.height)];
        searchBarContainerView.autoresizingMask = UIViewAutoresizingNone;
        [searchBarContainerView addSubview:self.searchBar];
        UIBarButtonItem* searchBarItem = [[UIBarButtonItem alloc] initWithCustomView:searchBarContainerView];
        self.navigationItem.rightBarButtonItem = searchBarItem;
    } else {
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(toogleSearchBar)];
        self.searchBarHiddenFrame = self.searchBar.frame; //y = -44.0
        CGRect tmpFrame = self.searchBar.frame;
        tmpFrame.origin.y = 0.0;
        self.searchBarVisibleFrame = tmpFrame;
    }
    
    [self.myLocationButton setBackgroundImage:[MapUtils mapControlOverylabBoxImage] forState:UIControlStateNormal];
    
    self.floorManagementBackground.image = [MapUtils mapControlOverylabBoxImage];
    
    [self.eyeButton setBackgroundImage:[MapUtils mapControlOverylabBoxImage] forState:UIControlStateNormal];
    
    if (self.initialQuery) {
        /*searchBar.text = initialQuery;
         [self setSearchBarState:SearchBarStateVisible];*/
        self.navigationItem.rightBarButtonItem = nil;
        self.title = self.initialQuery;
        self.navBarLoadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        self.navBarLoadingIndicator.center = CGPointMake(self.navigationController.navigationBar.frame.size.width - 24.0, 21.0);
        [self.navigationController.navigationBar addSubview:self.navBarLoadingIndicator];
        [NSTimer scheduledTimerWithTimeInterval:0.5 target:self.navBarLoadingIndicator selector:@selector(startAnimating) userInfo:nil repeats:NO];
        [self startSearchForQuery:self.initialQuery];
    }
    [self mapView:self.mapView regionDidChangeAnimated:NO]; //to refresh UI controls and add overlays
    [self updateFloorLabel];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"Map"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"Map"];
}

- (void)willLoseForeground {
    self.searchBarWasFirstResponder = [self.searchBar isFirstResponder];
    [self.searchBar resignFirstResponder];
}

- (void)didEnterForeground {
    if (self.searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    if (self.navBarLoadingIndicator) {
        [self.navBarLoadingIndicator removeFromSuperview];
        self.navBarLoadingIndicator = nil;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    [self.tileOverlayView didReceiveMemoryWarning];
    [self.layersOverlayView didReceiveMemoryWarning];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    if ([PCUtils isIdiomPad]) {
        return YES;
    } else {
        return (interfaceOrientation == UIInterfaceOrientationPortrait);
    }
}

#pragma mark - startSearchForQuery

- (void)startSearchForQuery:(NSString*)query {
    if (!self.mapService) {
        return;
    }
    [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
    [self.mapService searchFor:query delegate:self];
    [self.searchActivityIndicator startAnimating];
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

#pragma mark SearchBar animations

- (void)toogleSearchBar {
    if (self.searchBarState == SearchBarStateVisible) {
        [self setSearchBarState:SearchBarStateHidden];
        [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
    } else {
        [self setSearchBarState:SearchBarStateVisible];
    }
}

- (void)hideSearchBar {
    [self setSearchBarState:SearchBarStateHidden];
}

- (void)setSearchBarState:(SearchBarState)newState {
    if (self.searchBarState == newState || [PCUtils isIdiomPad]) {
        return;
    }
    typedef void (^b1_t)(void);
    
    b1_t animBlock;
    
    switch (newState) {
        case SearchBarStateHidden:
        {
            _searchBarState = SearchBarStateHidden;
            if (self.mapService != nil) {
                [self.mapService cancelOperationsForDelegate:self];
            }
            self.searchBar.text = @"";
            UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(toogleSearchBar)];
            [self.navigationItem setRightBarButtonItem:button animated:YES];
            [self.searchActivityIndicator stopAnimating];
            [self.searchBar resignFirstResponder];
            animBlock = ^{
                self.searchBar.frame = self.searchBarHiddenFrame;
                //mapView.frame = CGRectMake(mapView.frame.origin.x, mapView.frame.origin.y-searchBar.frame.size.height, mapView.frame.size.width, mapView.frame.size.height+searchBar.frame.size.height);
            };
        }
            break;
        case SearchBarStateVisible:
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/click/search" withError:NULL];
            _searchBarState = SearchBarStateVisible;
            UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(toogleSearchBar)];
            [self.navigationItem setRightBarButtonItem:button animated:YES];
            [self.searchBar becomeFirstResponder];
            animBlock = ^{
                self.searchBar.frame = self.searchBarVisibleFrame;
                //mapView.frame = CGRectMake(mapView.frame.origin.x, mapView.frame.origin.y+searchBar.frame.size.height, mapView.frame.size.width, mapView.frame.size.height-searchBar.frame.size.height);
            };
        }
            break;
        default:
            NSLog(@"!! Unsupported SearchBarState");
            break;
    }
    
    [UIView transitionWithView:self.searchBar duration:0.25 options:UIViewAnimationOptionTransitionNone animations:animBlock completion:NULL];
}

#pragma mark - myLocationButton states edition

- (void)setMyLocationButtonSateForTrackingMode:(MKUserTrackingMode)mode {
    switch (mode) {
        case MKUserTrackingModeNone:
            [self.myLocationButton setImage:[UIImage imageNamed:@"LocationArrowInactive"] forState:UIControlStateNormal];
            break;
        case MKUserTrackingModeFollow:
            [self.myLocationButton setImage:[UIImage imageNamed:@"LocationArrowActive"] forState:UIControlStateNormal];
            break;
        case MKUserTrackingModeFollowWithHeading:
            [self.myLocationButton setImage:[UIImage imageNamed:@"Compass"] forState:UIControlStateNormal];
            break;
        default:
            @throw [NSException exceptionWithName:@"unspported MKUserTrackingMode" reason:@"setMyLocationButtonSateForTrackingMode received unsupported mode" userInfo:nil];
            break;
    }
}

#pragma mark - IBActions

- (IBAction)myLocationPressed {
    if (self.mapView.userTrackingMode == MKUserTrackingModeNone) {
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/click/mylocation" withError:NULL];
        [self.mapView setUserTrackingMode:MKUserTrackingModeFollow animated:YES];
        self.mapView.showsUserLocation = YES;
        [self.mapView setRegion:MKCoordinateRegionMake(self.mapView.userLocation.coordinate, MKCoordinateSpanMake(0.003, 0.003)) animated:YES];
    } else if (self.mapView.userTrackingMode == MKUserTrackingModeFollow) {
        [self.mapView setUserTrackingMode:MKUserTrackingModeFollowWithHeading animated:YES];
        if (self.mapView.userTrackingMode != MKUserTrackingModeFollowWithHeading) { //means heading not supported
            [self.mapView setUserTrackingMode:MKUserTrackingModeNone];
            self.mapView.showsUserLocation = NO;
        }
    } else {
        [self.mapView setUserTrackingMode:MKUserTrackingModeNone animated:YES];
        self.mapView.showsUserLocation = NO;
    }
}

- (IBAction)eyePressed {
    if (!self.othersActionSheet) {
        //NSLog(@"MapView region : %lf %lf, %lf %lf", mapView.region.center.latitude, mapView.region.center.longitude, mapView.region.span.latitudeDelta, mapView.region.span.longitudeDelta);
        NSString* localizedStringFromBuildings;
        if (self.showBuildingsInterior) {
            localizedStringFromBuildings = NSLocalizedStringFromTable(@"HideBuildingsInterior", @"MapPlugin", nil);
        } else {
            localizedStringFromBuildings = NSLocalizedStringFromTable(@"ShowBuildingsInterior", @"MapPlugin", nil);
        }
        self.othersActionSheet = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"CenterOnEPFL", @"MapPlugin", nil), localizedStringFromBuildings, nil];
    }
    
    [self.othersActionSheet toggleFromRect:self.eyeButton.frame inView:self.mapView animated:YES];
}

- (IBAction)floorDownPressed {
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) { //copy in case they are modified in the meantime (highly unlikely though)
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay decreaseLayerLevel];
    [self.epflLayersOverlay decreaseLayerLevel];
    [self updateFloorLabel];
}

- (IBAction)floorUpPressed {
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) {
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay increaseLayerLevel];
    [self.epflLayersOverlay increaseLayerLevel];
    [self updateFloorLabel];
}

- (void)updateFloorLabel {
    if (self.epflTileOverlay.currentLayerLevel == MIN_LAYER_LEVEL) {
        self.floorDownButton.enabled = NO;
        self.floorUpButton.enabled = YES;
    } else if (self.epflTileOverlay.currentLayerLevel == MAX_LAYER_LEVEL) {
        self.floorDownButton.enabled = YES;
        self.floorUpButton.enabled = NO;
    } else {
        MKZoomScale zoomScale = self.mapView.bounds.size.width / self.mapView.visibleMapRect.size.width;
        if ([self.epflTileOverlay canDrawMapRect:self.mapView.visibleMapRect zoomScale:zoomScale]) {
            self.floorManagementSuperview.hidden = NO;
            self.floorDownButton.enabled = YES;
            self.floorUpButton.enabled = YES;
        }
    }
    self.floorLabel.text = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"Floor", @"MapPlugin", nil), self.epflTileOverlay.currentLayerLevel];
}

#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: //Center of EPFL
        {
            [self.mapView setUserTrackingMode:MKUserTrackingModeNone animated:NO];
            [self.mapView setRegion:self.epflRegion animated:YES];
            break;
        }
        case 1: //show/hide buildings interior
        {
            self.showBuildingsInterior = !self.showBuildingsInterior;
            [self mapView:self.mapView regionDidChangeAnimated:NO]; //to refresh layer visibility
            break;
        }
        default:
            break;
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    self.othersActionSheet = nil;
}

#pragma mark - UISearchBarDelegate

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    [self startSearchForQuery:self.searchBar.text];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    if (self.searchActivityIndicator.isAnimating) {
        [self.mapService cancelOperationsForDelegate:self];
        [self.searchActivityIndicator stopAnimating];
    }
    [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
}

#pragma mark - MKMapViewDelegate

- (void)mapView:(MKMapView *)mapView didChangeUserTrackingMode:(MKUserTrackingMode)mode animated:(BOOL)animated {
    [self setMyLocationButtonSateForTrackingMode:mode];
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id <MKOverlay>)overlay {
    if ([overlay isKindOfClass:[EPFLTileOverlay class]]) {
        return self.tileOverlayView;
    } else if ([overlay isKindOfClass:[EPFLLayersOverlay class]]) {
        return self.layersOverlayView;
    } else {
        //other, not managed
    }
    return nil;
}

- (MKAnnotationView *)mapView:(MKMapView *)theMapView viewForAnnotation:(id <MKAnnotation>)annotation {
    if (![annotation isKindOfClass:[MapItemAnnotation class]]) {
        return nil;
    }
    
    MKPinAnnotationView* pin = (MKPinAnnotationView *)[self.mapView dequeueReusableAnnotationViewWithIdentifier:kMapItemAnnotationIdentifier]; //cast ok we know we only use MKPinAnnotationView
    if (pin == nil) {
        pin = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:kMapItemAnnotationIdentifier];
        pin.pinColor = MKPinAnnotationColorPurple;
        pin.animatesDrop = YES;
        pin.canShowCallout = YES;
        pin.enabled = YES;
        pin.rightCalloutAccessoryView = nil;
    } else {
        pin.annotation = annotation;
        pin.rightCalloutAccessoryView = nil;
    }
    return pin;
    
}

- (void)mapView:(MKMapView *)mapView_ didAddAnnotationViews:(NSArray *)views {
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotations:self.mapView.annotations];
    if (mapItemAnnotations.count == 1) {
        [self.mapView selectAnnotation:[mapItemAnnotations objectAtIndex:0] animated:YES];
    }
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view {
    if ([view isKindOfClass:[MKAnnotationView class]]) {
        view.rightCalloutAccessoryView = nil;
        NSString* roomName = view.annotation.subtitle;
        if (roomName == nil || roomName.length == 0) {
            roomName = view.annotation.title;
        }
        if (self.directoryService) {
            [self.directoryService cancelOperationsForDelegate:self];
        }
        if (roomName != nil && !roomName.length == 0) {
            //both title and subtitle are indicated, might be a person => search in directory
            if (!self.initialQuery) {
                if (!self.directoryService) {
                    self.directoryService = [DirectoryService sharedInstanceToRetain];
                }
                [self.directoryService searchPersons:view.annotation.title delegate:self];
            }
            int level = [MapUtils levelToSelectForRoomName:roomName];
            if (level != INT_MAX) {
                [self.epflTileOverlay setLayerLevel:level];
                [self.epflLayersOverlay setLayerLevel:level];
                [self updateFloorLabel];
            }
        }
    }
}

- (void)mapView:(MKMapView *)mapView didDeselectAnnotationView:(MKAnnotationView *)view {
    if (![PCUtils isIdiomPad]) { // on iPad, released on popover dismissed
        self.personToDisplay = nil;
    }
}

- (void)mapView:(MKMapView *)mapView_ regionDidChangeAnimated:(BOOL)animated {
    
    //NSLog(@"%lf, %lf", mapView.region.span.latitudeDelta, mapView.region.span.longitudeDelta);
    
    MKZoomScale zoomScale = self.mapView.bounds.size.width / self.mapView.visibleMapRect.size.width;
    
    /*if ([epflTileOverlay canDrawMapRect:mapView.visibleMapRect zoomScale:zoomScale]) { //is normally a delegate method, but used here to know whether layer UI controls should be shown
     if ([mapView.overlays count] == 0) {
     if (showBuildingsInterior) {
     [mapView addOverlay:epflTileOverlay];
     }
     //[mapView addOverlay:epflLayersOverlay];
     floorDownButton.enabled = YES;
     floorLabel.hidden = NO;
     floorUpButton.enabled = YES;
     } else {
     [mapView removeOverlay:epflTileOverlay];
     }
     } else {
     if ([mapView.overlays count] > 0) {
     [mapView removeOverlay:epflTileOverlay];
     //[mapView removeOverlay:epflLayersOverlay];
     floorDownButton.enabled = NO;
     floorLabel.hidden = YES;
     floorUpButton.enabled = NO;
     }
     }*/
    
    if (![self.epflTileOverlay canDrawMapRect:self.mapView.visibleMapRect zoomScale:zoomScale] || !self.showBuildingsInterior) {
        if ([self.mapView.overlays count] > 0) {
            [self.mapView removeOverlay:self.epflTileOverlay];
            //[mapView removeOverlay:epflLayersOverlay];
            self.floorManagementSuperview.hidden = YES;
        }
    } else if (self.showBuildingsInterior) {
        if ([self.mapView.overlays count] == 0) {
            [self.mapView addOverlay:self.epflTileOverlay];
            //[mapView addOverlay:epflLayersOverlay];
            self.floorManagementSuperview.hidden = NO;
        }
    } else {
        //nothing
    }
    
    if (self.annotationsToAdd) {
        [self.mapView addAnnotations:self.annotationsToAdd];
        self.annotationsToAdd = nil;
    }
    
}


#pragma mark - MapServiceDelegate

- (void)searchMapFor:(NSString*)query didReturn:(NSArray*)results{
    
    /* TEST */
    /*
     for (MapItem* item in results) {
     NSLog(@"%@", [item descriptionObject]);
     }
     */
    /* END OF TEST */
    
    [self.searchActivityIndicator stopAnimating];
    [NSTimer scheduledTimerWithTimeInterval:1.5 target:self.navBarLoadingIndicator selector:@selector(stopAnimating) userInfo:nil repeats:NO];
    
    [self.mapView setUserTrackingMode:MKUserTrackingModeNone];
    
    if (results.count == 0) { //no result
        self.noResultAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"NoResult", @"MapPlugin", nil) message:@"" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [self.noResultAlert show];
        return;
    }
    
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotationsThatShouldBeDisplayed:[self mapItemAnnotationsForMapItems:results] forQuery:query];
    
    if (mapItemAnnotations.count > kMaxDisplayedAnnotations) {
        NSLog(@"-> Search for %@ returned too many results (%d)", query, mapItemAnnotations.count);
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"TooManyResults", @"MapPlugin", nil) message:NSLocalizedStringFromTable(@"NarrowSearch", @"MapPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    MKCoordinateRegion reqRegion = [MapUtils regionToFitMapItemAnnotations:mapItemAnnotations];
    
    self.annotationsToAdd = mapItemAnnotations;
    
    if ([MapUtils isRegion:self.mapView.region equalToRegion:[self.mapView regionThatFits:reqRegion]]) {
        [self mapView:self.mapView regionDidChangeAnimated:NO]; //force this call to redraw annotations (not called because region has not changed)
    } else {
        [self.mapView setRegion:reqRegion animated:YES];
    }
    
    //[MapUtils zoomMapView:mapView toFitMapItemAnnotationsAnimated:YES];
}

- (void)searchMapFailedFor:(NSString *)query {
    [self.searchActivityIndicator stopAnimating];
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerTimedOut {
    [self.navBarLoadingIndicator stopAnimating];
    [self.searchActivityIndicator stopAnimating];
    self.internetConnectionAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [self.internetConnectionAlert show];
}

#pragma mark - DirectoryServiceDelegate

- (void)searchDirectoryFor:(NSString *)searchPattern didReturn:(NSArray *)results {
    if (self.mapView.selectedAnnotations.count == 0 || results.count == 0) {
        [self searchDirectoryFailedFor:searchPattern];
        return;
    }
    
    id<MKAnnotation> selectedAnnotation = [self.mapView.selectedAnnotations objectAtIndex:0];
    
    MKAnnotationView* selectedAnnotationView = [self.mapView viewForAnnotation:selectedAnnotation];
    
    Person* person = [results objectAtIndex:0];
    
    NSString* firstAndLastName = [NSString stringWithFormat:@"%@ %@", person.firstName, person.lastName];
    
    if (![firstAndLastName isEqualToString:selectedAnnotation.title]) {
        [self searchDirectoryFailedFor:searchPattern];
        return;
    }
    
    UIButton* disclosureButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
    [disclosureButton addTarget:self action:@selector(annotationAccessoryTapped:) forControlEvents:UIControlEventTouchUpInside];
    disclosureButton.titleLabel.text = firstAndLastName;
    selectedAnnotationView.rightCalloutAccessoryView = disclosureButton;
    self.personToDisplay = person;
}

- (void)searchDirectoryFailedFor:(NSString *)searchPattern {
    if (self.mapView.selectedAnnotations.count == 0) {
        return;
    }
    id<MKAnnotation> selectedAnnotation = [self.mapView.selectedAnnotations objectAtIndex:0];
    
    MKAnnotationView* selectedAnnotationView = [self.mapView viewForAnnotation:selectedAnnotation];
    
    selectedAnnotationView.rightCalloutAccessoryView = nil;
    self.personToDisplay = nil;
    
}

#pragma mark - Directory related (cont'd)

- (void)annotationAccessoryTapped:(UIButton*)button {
    if (![button isKindOfClass:[UIButton class]] || !self.personToDisplay) {
        return;
    }
    PCUnkownPersonViewController* personViewController = [[PCUnkownPersonViewController alloc] initWithDelegate:nil];
    [personViewController setPerson:self.personToDisplay];
    
    if ([PCUtils isIdiomPad]) {
        
        if (!self.personPopOverController) {
            personViewController.title = NSLocalizedStringFromTable(@"Details", @"MapPlugin", nil);
            
            UINavigationController* navController = [[UINavigationController alloc] initWithRootViewController:personViewController]; //to have title
            
            self.personPopOverController = [[UIPopoverController alloc] initWithContentViewController:navController];
            self.personPopOverController.popoverContentSize = CGSizeMake(320.0, 480.0);
            self.personPopOverController.delegate = self;
            id<MKAnnotation> annotation = self.mapView.selectedAnnotations[0];
            
            CGPoint annotationPoint = [self.mapView convertCoordinate:annotation.coordinate toPointToView:self.mapView];
            float boxDY = annotationPoint.y;
            float boxDX = annotationPoint.x;
            
            CGRect box;
            
            if ([PCUtils isOSVersionSmallerThan:6.0]) {
                box = CGRectMake(boxDX-4.0,boxDY-27.0,1,1);
            } else {
                box = CGRectMake(boxDX,boxDY-30.0,1,1);
            }
            
            
            [self.mapView deselectAnnotation:annotation animated:YES];
            
            [self.personPopOverController presentPopoverFromRect:box inView:self.mapView permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
            
        }
        
    } else {
        [self.navigationController pushViewController:personViewController animated:YES];
    }
}

- (void)popoverControllerDidDismissPopover:(UIPopoverController *)popoverController {
    self.personPopOverController = nil;
    self.personToDisplay = nil;
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.internetConnectionAlert) {
        self.internetConnectionAlert = nil;
        if (self.initialQuery && self.navigationController.visibleViewController == self) { //leave map if initial search query was not successful
            [self.navigationController popViewControllerAnimated:YES];
        }
    } else if (alertView == self.noResultAlert) {
        self.noResultAlert = nil;
        if (self.initialQuery && self.navigationController.visibleViewController == self) { //leave map if initial search query was not successful
            [self.navigationController popViewControllerAnimated:YES];
        }
    } else {
        //no other alerts
    }
}

#pragma mark - CustomOverlayViewDelegate

- (void)customOverlayViewDidStartLoading:(CustomOverlayView *)overlayView {
    if (self.overlaysLoadingIndicator.isAnimating) {
        return;
    }
    [self.overlaysLoadingIndicator startAnimating];
}

- (void)customOverlayViewDidFinishLoading:(CustomOverlayView *)overlayView {
    if (!self.overlaysLoadingIndicator.isAnimating) {
        return;
    }
    [self.overlaysLoadingIndicator stopAnimating];
}

#pragma mark - Utilities

- (NSArray*)mapItemAnnotationsForMapItems:(NSArray*)mapItems {
    if (!mapItems || ![mapItems isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad mapItems argument in mapItemAnnotationsForMapItems:" reason:@"mapItems is not kind of class NSArray" userInfo:nil];
    }
    NSMutableArray* annotations = [NSMutableArray arrayWithCapacity:mapItems.count];
    for (MapItem* item __strong in mapItems) {
        if (self.initialQuery && self.initialQueryManualPinLabelText && ![self.initialQueryManualPinLabelText isEqualToString:item.title]) {
            item = [[MapItem alloc] initWithTitle:self.initialQueryManualPinLabelText description:item.title latitude:item.latitude longitude:item.longitude layerId:item.layerId itemId:item.itemId];
        }
        MapItemAnnotation* annotation = [[MapItemAnnotation alloc] initWithMapItem:item];
        [annotations addObject:annotation];
    }
    return annotations;
}

#pragma mark - dealloc

- (void)dealloc
{
    [self.mapService cancelOperationsForDelegate:self];
    [self.directoryService cancelOperationsForDelegate:self];
    [[MainController publicController] removePluginStateObserver:self];
    self.mapView.delegate = nil;
    [self.tileOverlayView cancelTilesDownload:YES];
    self.tileOverlayView.delegate = nil;
    [self.layersOverlayView cancelTilesDownload:YES];
    self.layersOverlayView.delegate = nil;
    self.internetConnectionAlert.delegate = nil;
}



@end
