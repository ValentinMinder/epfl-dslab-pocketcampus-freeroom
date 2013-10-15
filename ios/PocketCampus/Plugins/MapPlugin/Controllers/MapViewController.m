//
//  MapViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MapViewController.h"

#import <MapKit/MapKit.h>

#import "MapService.h"

#import "DirectoryService.h"

#import "EPFLTileOverlay.h"

#import "EPFLLayersOverlay.h"

#import "RemoteOverlayRenderer.h"

#import "MapItemAnnotation.h"

#import "MapUtils.h"

#import "MapController.h"

#import "NSTimer+Blocks.h"

#import "DirectoryPersonViewController.h"

#import <QuartzCore/QuartzCore.h>

#import "EPFLTileOverlay2.h"

typedef enum  {
    SearchStateReady = 0, //no search yet, bar empty and ready for a new search
    SearchStateLoading,
    SearchStateResults,
} SearchState;

typedef enum  {
    MapControlsStateAllAvailable = 0,
    MapControlsStateNoFloorControl
} MapControlsState;

static int kMaxDisplayedAnnotations = 70;
static NSString* kMapItemAnnotationIdentifier = @"mapItemAnnotation";

static const CGFloat kSearchBarHeightPortrait = 44.0;
static const CGFloat kSearchBarHeightLandscape = 32.0;

@interface MapViewController ()<MKMapViewDelegate, UIGestureRecognizerDelegate, UISearchBarDelegate, UIGestureRecognizerDelegate, UIAlertViewDelegate, MapServiceDelegate, RemoteOverlayRendererDelegate, UIPopoverControllerDelegate>

/* Outlets */

@property (nonatomic, strong) IBOutlet MKMapView* mapView;
@property (nonatomic, strong) IBOutlet UIToolbar* toolBar;

@property (nonatomic, strong) MapService* mapService;

@property (nonatomic, strong) EPFLTileOverlay* epflTileOverlay;
//@property (nonatomic, strong) EPFLTileOverlay2* epflTileOverlay2;
@property (nonatomic, strong) EPFLLayersOverlay* epflLayersOverlay;
@property (nonatomic, strong) RemoteOverlayRenderer* tilesOverlayRenderer;
//@property (nonatomic, strong) MKOverlayRenderer* tilesOverlayRenderer2;
@property (nonatomic, strong) RemoteOverlayRenderer* layersOverlayRenderer; //not initialized for now (disabled feature)

@property (nonatomic, strong) NSString* initialQuery;
@property (nonatomic, strong) NSString* initialQueryManualPinLabelText;

@property (nonatomic, readonly) MKCoordinateRegion epflRegion;

@property (nonatomic, strong) UISearchBar* searchBar;
@property (nonatomic, strong) UIBarButtonItem* searchBarItem;
@property (nonatomic, strong) UIBarButtonItem* resultsListButton;
@property (nonatomic, strong) UIBarButtonItem* loadingBarItem;
@property (nonatomic) SearchState searchState;

@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* overlaysLoadingIndicator;
@property (nonatomic, strong) UIBarButtonItem* myLocationButton;
@property (nonatomic, strong) UIBarButtonItem* floorDownButton;
@property (nonatomic, strong) UIBarButtonItem* floorLabelItem;
@property (nonatomic, strong) UIBarButtonItem* floorUpButton;
@property (nonatomic, strong) UIBarButtonItem* centerOnEPFLButton;
@property (nonatomic) MapControlsState mapControlsState;

@property (nonatomic, strong) UIPopoverController* personPopOverController;
@property (nonatomic, strong) UIAlertView* noResultAlert;
@property (nonatomic, strong) UIAlertView* internetConnectionAlert;

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
        self.mapService = [MapService sharedInstanceToRetain];
        self.epflTileOverlay = [[EPFLTileOverlay alloc] init];
        //self.epflTileOverlay2 = [[EPFLTileOverlay2 alloc] init];
        self.epflLayersOverlay = nil; //[[EPFLLayersOverlay alloc] init];
        self.tilesOverlayRenderer = [[RemoteOverlayRenderer alloc] initWithOverlay:self.epflTileOverlay];
        //self.tilesOverlayRenderer2 = [[MKTileOverlayRenderer alloc] initWithOverlay:self.epflTileOverlay2];
        _searchState = -1; //set to "illegal" value so that first call to setSearchState is not discared (as default value 0)
        _mapControlsState = -1; //set to "illegal" value so that first call to setMapControlState is not discareded (as default value 0)
    }
    return self;

}

- (id)initWithInitialQuery:(NSString*)query {
    self = [self init];
    if (self) {
        if (![query isKindOfClass:[NSString class]] || query.length == 0) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"query must be of class NSString and of length > 0" userInfo:nil];
        }
        self.initialQuery = query;
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel {
    self = [self initWithInitialQuery:query];
    if (self) {
        if (![pinTextLabel isKindOfClass:[NSString class]] || pinTextLabel.length == 0) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"pinTextLabel must be of class NSString and of length > 0" userInfo:nil];
        }
        self.initialQueryManualPinLabelText = pinTextLabel;
    }
    return self;
}

#pragma mark - View events

- (void)viewDidLoad
{
    [super viewDidLoad];
    //self.mapView.mapType = MKMapTypeHybrid;
    self.tilesOverlayRenderer.delegate = self;
    //self.layersOverlayView.delegate = self;
    [self.mapView setRegion:self.epflRegion animated:NO];
    self.epflTileOverlay.mapView = self.mapView;
    self.epflLayersOverlay.mapView = self.mapView;
    
    self.searchState = SearchStateReady; //will set nav bar elements, see implementation
    self.mapControlsState = MapControlsStateAllAvailable;
    
    UITapGestureRecognizer* mapTap = [[UITapGestureRecognizer alloc] initWithTarget:self.searchBar action:@selector(resignFirstResponder)];
    mapTap.cancelsTouchesInView = NO;
    mapTap.delegate = self;
    [self.mapView addGestureRecognizer:mapTap];
    
    if (self.initialQuery) {
        /*searchBar.text = initialQuery;
         [self setSearchBarState:SearchBarStateVisible];*/
        self.title = self.initialQuery;
        [self startSearchForQuery:self.initialQuery];
    }
    [self mapView:self.mapView regionDidChangeAnimated:NO]; //to refresh UI controls and add overlays
    [self updateControls];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"Map"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"Map"];
    
    if (self.initialQueryWithFullControls && !self.initialQuery) {
        [self startSearchForQuery:self.initialQueryWithFullControls];
    }
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

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (self.initialQuery) {
        [[PCGAITracker sharedTracker] trackScreenWithName:@"/map/searchResults"];
    } else {
        [[PCGAITracker sharedTracker] trackScreenWithName:@"/map"];
    }
    [self mapView:self.mapView regionDidChangeAnimated:NO];
}

- (NSUInteger)supportedInterfaceOrientations
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Properties override

- (MKCoordinateRegion)epflRegion {
    if ([PCUtils isIdiomPad]) {
        if ([[UIDevice currentDevice] orientation] == UIDeviceOrientationPortrait) {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519113, 6.566634), MKCoordinateSpanMake(0.014175, 0.016479));
        } else {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519113, 6.566634), MKCoordinateSpanMake(0.010395, 0.021973));
        }
    } else {
        if ([[UIDevice currentDevice] orientation] == UIDeviceOrientationPortrait) {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.012285, 0.013733));
        } else {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.012285, 0.013733));
        }
    }
}

#pragma mark - startSearchForQuery

- (void)startSearchForQuery:(NSString*)query {
    if (!self.mapService) {
        return;
    }
    [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
    [self.mapService searchFor:query delegate:self];
    [self setSearchState:SearchStateLoading animated:YES];
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

#pragma mark - Buttons and bars management

- (void)setSearchState:(SearchState)searchState {
    [self setSearchState:searchState animated:NO];
}

- (void)setSearchState:(SearchState)searchState animated:(BOOL)animated {
    if (_searchState == searchState) {
        return;
    }
    _searchState = searchState;
    
    if (!self.searchBarItem) {
        if (!self.searchBar) {
            self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0.0, 0, 210.0, kSearchBarHeightPortrait)];
            self.searchBar.delegate = self;
            self.searchBar.barStyle = UISearchBarStyleDefault;
            self.searchBar.barTintColor = [UIColor clearColor];
            self.searchBar.translucent = YES;
            self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchPlaceholder", @"MapPlugin", nil);
            self.searchBar.autocapitalizationType = UITextAutocapitalizationTypeNone;
            self.searchBar.autocorrectionType = UITextAutocorrectionTypeNo;
            self.searchBar.isAccessibilityElement = YES;
            self.searchBar.accessibilityIdentifier = @"SearchBar";
        }
        UIView* searchBarContainerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.searchBar.frame.size.width, self.searchBar.frame.size.height)];
        //searchBarContainerView.backgroundColor = [UIColor yellowColor];
        [searchBarContainerView addSubview:self.searchBar];
        
        self.searchBarItem = [[UIBarButtonItem alloc] initWithCustomView:searchBarContainerView];
    }
    
    if (!self.loadingBarItem) {
        UIActivityIndicatorView* loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [loadingIndicator startAnimating];
        self.loadingBarItem = [[UIBarButtonItem alloc] initWithCustomView:loadingIndicator];
    }
    
    if (!self.resultsListButton) {
        self.resultsListButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ListBarButton"] style:UIBarButtonItemStylePlain target:nil action:nil];
#warning TODO set target to show results list
    }
    
    NSArray* items = nil;
    
    if (self.initialQuery) {
        if (searchState == SearchStateLoading) {
            items = @[self.loadingBarItem];
        } else {
            items = @[];
        }
    } else {
        CGRect searchBarTargetFrame;
        CGRect searchBarContainerViewTargetFrame;
        if ([PCUtils isIdiomPad]) {
            searchBarTargetFrame = CGRectMake(10.0, 0, 270.0, kSearchBarHeightPortrait);
            searchBarContainerViewTargetFrame = CGRectMake(0, 0, searchBarTargetFrame.size.width, searchBarTargetFrame.size.height);
            switch (searchState) {
                case SearchStateReady:
                    items = @[self.searchBarItem];
                    break;
                case SearchStateLoading:
                {
                    UIBarButtonItem* space1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
                    space1.width = 3.0;
                    UIBarButtonItem* space2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
                    space2.width = 22.0;
                    items = @[space1, self.loadingBarItem, space2, self.searchBarItem];
                    break;
                }
                case SearchStateResults:
                    items = @[self.resultsListButton, self.searchBarItem];
                    break;
                default:
                    break;
            }
        } else {
            switch (searchState) {
                case SearchStateReady:
                    items = @[self.searchBarItem];
                    searchBarTargetFrame = CGRectMake(-2.0, 0, 264.0, kSearchBarHeightPortrait);
                    searchBarContainerViewTargetFrame = CGRectMake(0, 0, searchBarTargetFrame.size.width-11.0, searchBarTargetFrame.size.height);
                    break;
                case SearchStateLoading:
                {
                    UIBarButtonItem* space1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
                    space1.width = 3.0;
                    UIBarButtonItem* space2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
                    space2.width = 22.0;
                    items = @[space1, self.loadingBarItem, space2, self.searchBarItem];
                    searchBarTargetFrame = CGRectMake(-10.0, 0, 220.0, kSearchBarHeightPortrait);
                    searchBarContainerViewTargetFrame = CGRectMake(0, 0, searchBarTargetFrame.size.width+(2*searchBarTargetFrame.origin.x), searchBarTargetFrame.size.height);
                    break;
                }
                case SearchStateResults:
                    items = @[self.resultsListButton, self.searchBarItem];
                    searchBarTargetFrame = CGRectMake(-10.0, 0, 220.0, kSearchBarHeightPortrait);
                    searchBarContainerViewTargetFrame = CGRectMake(0, 0, searchBarTargetFrame.size.width+(2*searchBarTargetFrame.origin.x), searchBarTargetFrame.size.height);
                    break;
                default:
                    break;
            }
        }
        if (!items) {
            return;
        }
        [UIView animateWithDuration:animated ? 0.25 : 0.0 animations:^{
            self.searchBar.frame = searchBarTargetFrame;
            self.searchBar.superview.frame = searchBarContainerViewTargetFrame;
        }];
    }
    BOOL searchBarWasFirstResponder = self.searchBar.isFirstResponder;
    [self.navigationItem setRightBarButtonItems:items animated:NO];
    if (searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    }
}

- (void)setMapControlsState:(MapControlsState)mapControlsState {
    if (_mapControlsState == mapControlsState) {
        return;
    }
    _mapControlsState = mapControlsState;

    if (!self.myLocationButton) {
        self.myLocationButton = [[MKUserTrackingBarButtonItem alloc] initWithMapView:self.mapView];
    }
    
    if (!self.floorDownButton) {
        self.floorDownButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"FloorDown"] style:UIBarButtonItemStylePlain target:self action:@selector(floorDownPressed)];
    }
    
    if (!self.floorLabelItem) {
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 60.0, 20.0)];
        label.adjustsFontSizeToFitWidth = YES;
        //label.backgroundColor = [UIColor yellowColor];
        label.textAlignment = NSTextAlignmentCenter;
        label.font = [UIFont systemFontOfSize:16.0];
        label.textColor = [UIColor darkGrayColor];
        self.floorLabelItem = [[UIBarButtonItem alloc] initWithCustomView:label];
    }
    
    if (!self.floorUpButton) {
        self.floorUpButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"FloorUp"] style:UIBarButtonItemStylePlain target:self action:@selector(floorUpPressed)];
    }
    
    if (!self.centerOnEPFLButton) {
        self.centerOnEPFLButton = [[UIBarButtonItem alloc] initWithTitle:@"EPFL" style:UIBarButtonItemStylePlain target:self action:@selector(centerOnEPFLPressed)];
    }
    
    NSArray* items = nil;
    if ([PCUtils isIdiomPad]) {
        
        UIBarButtonItem* fspace1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:NULL];
        UIBarButtonItem* fspace2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:NULL];
        UIBarButtonItem* space1 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
        space1.width = 30.0;
        UIBarButtonItem* space2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:NULL];
        space2.width = 30.0;
        
        switch (mapControlsState) {
            case MapControlsStateAllAvailable:
            {
                items = @[self.navigationItem.leftBarButtonItem, fspace1, self.floorDownButton, self.floorLabelItem, self.floorUpButton, fspace2, self.myLocationButton, space1, self.centerOnEPFLButton, space2];
                break;
            }
            case MapControlsStateNoFloorControl:
            {
                items = @[self.navigationItem.leftBarButtonItem, fspace1, self.myLocationButton, space1, self.centerOnEPFLButton, space2];
                break;
            }
            default:
                break;
        }
        if (items) {
            self.navigationItem.leftBarButtonItems = items;
        }
    } else {
        switch (mapControlsState) {
            case MapControlsStateAllAvailable:
            {
                UIBarButtonItem* fspaceLeft = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:NULL];
                UIBarButtonItem* fspaceRight = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:NULL];
                items = @[self.myLocationButton, fspaceLeft, self.floorDownButton, self.floorLabelItem, self.floorUpButton, fspaceRight, self.centerOnEPFLButton];
                break;
            }
            case MapControlsStateNoFloorControl:
            {
                UIBarButtonItem* fspaceLeft = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:NULL];
                items = @[self.myLocationButton, fspaceLeft, self.centerOnEPFLButton];
                break;
            }
            default:
                break;
        }
        if (items) {
            self.toolBar.items = items;
        }
    }
}

/*- (void)setMyLocationButtonSateForTrackingMode:(MKUserTrackingMode)mode {
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
}*/

#pragma mark - Actions

- (void)myLocationPressed {
    if (self.mapView.userTrackingMode == MKUserTrackingModeNone) {
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

- (void)floorDownPressed {
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) { //copy in case they are modified in the meantime (highly unlikely though)
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay decreaseLayerLevel];
    [self.epflLayersOverlay decreaseLayerLevel];
    [self updateControls];
}

- (void)floorUpPressed {
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) {
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay increaseLayerLevel];
    [self.epflLayersOverlay increaseLayerLevel];
    [self updateControls];
}

- (void)centerOnEPFLPressed {
    [self.mapView setRegion:self.epflRegion animated:YES];
}

- (void)updateControls {
    if (self.epflTileOverlay.currentLayerLevel == MIN_LAYER_LEVEL) {
        self.floorDownButton.enabled = NO;
        self.floorUpButton.enabled = YES;
    } else if (self.epflTileOverlay.currentLayerLevel == MAX_LAYER_LEVEL) {
        self.floorDownButton.enabled = YES;
        self.floorUpButton.enabled = NO;
    } else {
        self.floorDownButton.enabled = YES;
        self.floorUpButton.enabled = YES;
    }
    ((UILabel*)(self.floorLabelItem.customView)).text = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"Floor", @"MapPlugin", nil), self.epflTileOverlay.currentLayerLevel];
}

- (void)setLayersLevel:(NSInteger)level {
    if (level == self.epflTileOverlay.currentLayerLevel) {
        return;
    }
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) {
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay setLayerLevel:level];
    [self.epflLayersOverlay setLayerLevel:level];
    [self updateControls];
}

#pragma mark - UISearchBarDelegate

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    [self startSearchForQuery:self.searchBar.text];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    if (self.searchState == SearchStateLoading) {
        [self.mapService cancelOperationsForDelegate:self];
        [self setSearchState:SearchStateReady animated:YES];
    } else if (self.searchState == SearchStateResults) {
        [self setSearchState:SearchStateReady animated:YES];
        [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
    }
}

#pragma mark - MKMapViewDelegate

/*- (void)mapView:(MKMapView *)mapView didChangeUserTrackingMode:(MKUserTrackingMode)mode animated:(BOOL)animated {
    [self setMyLocationButtonSateForTrackingMode:mode];
}*/

- (MKOverlayRenderer*)mapView:(MKMapView *)mapView rendererForOverlay:(id<MKOverlay>)overlay {
    if ([overlay isKindOfClass:[EPFLTileOverlay class]]) {
        return self.tilesOverlayRenderer;
    } else if ([overlay isKindOfClass:[EPFLLayersOverlay class]]) {
        return self.layersOverlayRenderer;
    } else if ([overlay isKindOfClass:[EPFLTileOverlay2 class]]) {
        //return self.tilesOverlayRenderer2;
    }
    //other, not managed
    return nil;
}

- (MKAnnotationView *)mapView:(MKMapView *)theMapView viewForAnnotation:(id <MKAnnotation>)annotation {
    if (![annotation isKindOfClass:[MapItemAnnotation class]]) {
        return nil;
    }
    
    MapItem* mapItem = [(MapItemAnnotation*)annotation mapItem];
    
    
    MKPinAnnotationView* pin = (MKPinAnnotationView *)[self.mapView dequeueReusableAnnotationViewWithIdentifier:kMapItemAnnotationIdentifier]; //cast ok we know we only use MKPinAnnotationView
    pin.rightCalloutAccessoryView = nil;
    if (!pin) {
        pin = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:kMapItemAnnotationIdentifier];
        pin.pinColor = MKPinAnnotationColorRed;
        pin.animatesDrop = YES;
        pin.canShowCallout = YES;
        pin.enabled = YES;
    } else {
        pin.annotation = annotation;
    }
    
    if ([mapItem.category isEqualToString:kPersonsMapItemCategoryName] && !self.initialQuery) {
        UIButton* disclosureButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
        [disclosureButton addTarget:self action:@selector(annotationAccessoryTapped:) forControlEvents:UIControlEventTouchUpInside];
        pin.rightCalloutAccessoryView = disclosureButton;
    } else {
        pin.rightCalloutAccessoryView = nil;
    }
    
    return pin;
    
}

- (void)mapView:(MKMapView *)mapView didAddAnnotationViews:(NSArray *)views {
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotations:self.mapView.annotations];
    if (mapItemAnnotations.count == 1) {
        [self.mapView selectAnnotation:mapItemAnnotations[0] animated:YES];
    }
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view {
    if (![view.annotation isKindOfClass:[MapItemAnnotation class]]) {
        return;
    }
    MapItem* mapItem = [((MapItemAnnotation*)(view.annotation)) mapItem];
    [self.epflTileOverlay setLayerLevel:mapItem.floor];
    [self.epflLayersOverlay setLayerLevel:mapItem.floor];
    [self updateControls];

}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated {
    
    MKZoomScale zoomScale = self.mapView.bounds.size.width / self.mapView.visibleMapRect.size.width;
    
    //NSLog(@"%lf", self.mapView.camera.altitude);
    
    if ([self.epflTileOverlay canDrawMapRect:self.mapView.visibleMapRect zoomScale:zoomScale]) {
        if (self.mapView.overlays.count == 0) {
            [self.mapView addOverlay:self.epflTileOverlay];
            //[self.mapView addOverlay:self.epflTileOverlay2];
            //[mapView removeOverlay:epflLayersOverlay];
        }
        if ([self.epflTileOverlay shouldAllowLayerChange]) {
            self.mapControlsState = MapControlsStateAllAvailable;
        } else {
            [self setLayersLevel:DEFAULT_LAYER_LEVEL]; //back to default floor because other floors might display nothing at low zoom scale
            self.mapControlsState = MapControlsStateNoFloorControl;
        }
    } else {
        if (self.mapView.overlays.count > 0) {
            [self.mapView removeOverlay:self.epflTileOverlay];
            //[mapView removeOverlay:epflLayersOverlay];
            self.mapControlsState = MapControlsStateNoFloorControl;
        }
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
    
    [self setSearchState:SearchStateResults animated:YES];
    
    if (results.count == 0) { //no result
        self.noResultAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"NoResult", @"MapPlugin", nil) message:@"" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [self.noResultAlert show];
        [self setSearchState:SearchStateReady animated:YES];
        return;
    }
    
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotationsThatShouldBeDisplayed:[self mapItemAnnotationsForMapItems:results] forQuery:query];
    
    if (mapItemAnnotations.count > kMaxDisplayedAnnotations) {
        NSLog(@"-> Search for %@ returned too many results (%d)", query, mapItemAnnotations.count);
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"TooManyResults", @"MapPlugin", nil) message:NSLocalizedStringFromTable(@"NarrowSearch", @"MapPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    [self.mapView showAnnotations:mapItemAnnotations animated:YES];
    
}

- (void)searchMapFailedFor:(NSString *)query {
    [self setSearchState:SearchStateReady animated:YES];
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerTimedOut {
    if (self.searchState == SearchStateLoading) {
        [self setSearchState:SearchStateReady animated:YES];
    }
    self.internetConnectionAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [self.internetConnectionAlert show];
}

#pragma mark - Directory related

- (void)annotationAccessoryTapped:(UIButton*)button {
    if (![button isKindOfClass:[UIButton class]] || self.mapView.selectedAnnotations.count == 0) {
        return;
    }
    NSString* annotationTitle = [(MapItemAnnotation*)(self.mapView.selectedAnnotations[0]) title]; //annotation title is actually person's firstname lastname
    
    DirectoryPersonViewController* personViewController = [[DirectoryPersonViewController alloc] initAndLoadPersonWithFullName:annotationTitle];
    personViewController.allowShowOfficeOnMap = NO; //prevent loop
    
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

- (void)remoteOverlayRendererDidStartLoading:(RemoteOverlayRenderer *)overlayView {
    if (self.overlaysLoadingIndicator.isAnimating) {
        return;
    }
    [self.overlaysLoadingIndicator startAnimating];
}

- (void)remoteOverlayRendererDidFinishLoading:(RemoteOverlayRenderer *)overlayView {
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
            item = [[MapItem alloc] initWithTitle:self.initialQueryManualPinLabelText description:item.title latitude:item.latitude longitude:item.longitude layerId:item.layerId itemId:item.itemId floor:item.floor category:item.category];
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
    [[MainController publicController] removePluginStateObserver:self];
    self.mapView.delegate = nil;
    [self.tilesOverlayRenderer cancelTilesDownload:YES];
    self.tilesOverlayRenderer.delegate = nil;
    [self.layersOverlayRenderer cancelTilesDownload:YES];
    self.layersOverlayRenderer.delegate = nil;
    self.internetConnectionAlert.delegate = nil;
}



@end
