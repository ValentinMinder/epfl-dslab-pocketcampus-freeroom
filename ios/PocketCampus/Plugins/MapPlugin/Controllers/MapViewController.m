/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

//  Created by Loïc Gardiol on 12.04.12.

@import MapKit;
@import QuartzCore;

#import "MapViewController.h"

#import "MapService.h"

#import "EPFLTileOverlay.h"

#import "EPFLLayersOverlay.h"

#import "PCTileOverlayRenderer.h"

#import "MapItemAnnotation.h"

#import "MapUtils.h"

#import "MapController.h"

#import "NSTimer+Blocks.h"

#import "DirectoryPersonViewController.h"

#import "MapResultsListViewController.h"

#import "MapRecentSearchesListViewController.h"

#import "MapLayersListSelectionViewController.h"

#import "LGAUserTrackingBarButtonItem.h"

@interface MapViewControllerInitialState : NSObject

@property (nonatomic, strong) NSSet* layerIdsToDisplay;
@property (nonatomic, strong) MapItem* mapItem;
@property (nonatomic, strong) NSString* query;
@property (nonatomic, strong) NSString* queryManualPinLabelText;

@end

@implementation MapViewControllerInitialState

@end

typedef enum  {
    SearchStateReady = 0, //no search yet, bar empty and ready for a new search
    SearchStateLoading,
    SearchStateResults,
} SearchState;

typedef enum  {
    MapControlsStateAllAvailable = 0,
    MapControlsStateNoFloorControl
} MapControlsState;

static int const kMaxDisplayedAnnotations = 70;
static NSString* const kMapItemAnnotationIdentifier = @"mapItemAnnotation";

static CGFloat const kSearchBarHeightPortrait = 44.0;
static CGFloat const kSearchBarHeightLandscape __unused = 32.0;

@interface MapViewController ()<MKMapViewDelegate, UIGestureRecognizerDelegate, UISearchBarDelegate, UIGestureRecognizerDelegate, UIAlertViewDelegate, MapServiceDelegate, UIPopoverControllerDelegate>


@property (nonatomic, strong) IBOutlet MKMapView* mapView;
@property (nonatomic, strong) IBOutlet UIToolbar* toolBar;

@property (nonatomic, strong) MapService* mapService;

@property (nonatomic, strong) EPFLTileOverlay* epflTileOverlay;
@property (nonatomic, strong) EPFLLayersOverlay* epflLayersOverlay;

@property (nonatomic, strong) PCTileOverlayRenderer* epflLayersOverlayRenderer;

@property (nonatomic, strong) NSArray* mapItemsAllResults; //raw result from map service for a search. Nil if searchState is != SearchStateResults

@property (nonatomic, strong) NSDictionary* mapLayerForLayerId; //key: @(MapLayer.layerId), value: MapLayer object

@property (nonatomic, strong) NSTimer* getMapLayersTimer;
@property (nonatomic) BOOL getMapLayersRequestInProgress;

@property (nonatomic, strong) MapViewControllerInitialState* initialState;

@property (nonatomic, readonly) MKCoordinateRegion epflRegion;

@property (nonatomic, copy) NSArray* leftBarButtonItemsAtLoad;

@property (nonatomic, strong) UISearchBar* searchBar;
@property (nonatomic, strong) UIBarButtonItem* searchBarItem;
@property (nonatomic, strong) UIBarButtonItem* resultsListButton;
@property (nonatomic, strong) UIBarButtonItem* loadingBarItem;
@property (nonatomic, strong) UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) UIBarButtonItem* layersListButton;
@property (nonatomic) SearchState searchState;

@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* overlaysLoadingIndicator;
@property (nonatomic, strong) UIBarButtonItem* myLocationButton;
@property (nonatomic, strong) UIBarButtonItem* floorDownButton;
@property (nonatomic, strong) UIBarButtonItem* floorLabelItem;
@property (nonatomic, strong) UIBarButtonItem* floorUpButton;
@property (nonatomic, strong) UIBarButtonItem* centerOnEPFLButton;
@property (nonatomic) MapControlsState mapControlsState;

@property (nonatomic, strong) MapRecentSearchesListViewController* recentSearchesListViewController;
@property (nonatomic, strong) UIPopoverController* recentSearchesListPopoverController;
@property (nonatomic, strong) UIPopoverController* personPopOverController;
@property (nonatomic, strong) UIPopoverController* resultsListPopOverController;
@property (nonatomic, strong) UIPopoverController* mapLayersPopover;
@property (nonatomic, strong) UIAlertView* noResultAlert;
@property (nonatomic, strong) UIAlertView* internetConnectionAlert;
@property (nonatomic, strong) UIAlertView* tooManyResultsAlert;

@property (nonatomic) BOOL searchBarShouldBeginEditing;
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
        self.gaiScreenName = @"/map";
        self.mapService = [MapService sharedInstanceToRetain];
        //self.epflLayersOverlay.tileSize = CGSizeMake(1024.0, 1024.0);
        _searchState = -1; //set to "illegal" value so that first call to setSearchState is not discared (as default value 0)
        _mapControlsState = -1; //set to "illegal" value so that first call to setMapControlState is not discareded (as default value 0)
        self.searchBarShouldBeginEditing = YES;
    }
    return self;

}

- (instancetype)initWithMapLayerIdsToDisplay:(NSSet*)layerIds {
    [PCUtils throwExceptionIfObject:layerIds notKindOfClass:[NSSet class]];
    self = [self init];
    if (self) {
        MapViewControllerInitialState* state = [MapViewControllerInitialState new];
        state.layerIdsToDisplay = layerIds;
        self.initialState = state;
    }
    return self;
}

- (instancetype)initWithInitialMapItem:(MapItem*)mapItem {
    [PCUtils throwExceptionIfObject:mapItem notKindOfClass:[MapItem class]];
    self = [self init];
    if (self) {
        MapViewControllerInitialState* state = [MapViewControllerInitialState new];
        state.mapItem = mapItem;
        self.initialState = state;
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query {
    self = [self init];
    if (self) {
        if (![query isKindOfClass:[NSString class]] || query.length == 0) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"query must be of class NSString and of length > 0" userInfo:nil];
        }
        MapViewControllerInitialState* state = [MapViewControllerInitialState new];
        state.query = query;
        self.initialState = state;
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel {
    self = [self initWithInitialQuery:query];
    if (self) {
        if (![pinTextLabel isKindOfClass:[NSString class]] || pinTextLabel.length == 0) {
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"pinTextLabel must be of class NSString and of length > 0" userInfo:nil];
        }
        self.initialState.queryManualPinLabelText = pinTextLabel;
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.leftItemsSupplementBackButton = YES;
    
    self.epflTileOverlay = [[EPFLTileOverlay alloc] init];
    self.epflLayersOverlay = [[EPFLLayersOverlay alloc] initWithMapView:self.mapView];
    
    self.leftBarButtonItemsAtLoad = self.navigationItem.leftBarButtonItems;
    self.mapView.pitchEnabled = NO; //epflLayersOverlay are fucked up otherwise
    self.mapView.rotateEnabled = NO; //for some reason, allows higher zoom on iOS 7, thus allowing to see room names! Amazing!
    [self.mapView setRegion:self.epflRegion animated:NO];
    
    self.searchState = SearchStateReady; //will set nav bar elements, see implementation
    
    [self manageRecentSearchesControllerVisibilityAnimated:NO];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(recentSearchesChanged) name:kMapRecentSearchesModifiedNotification object:self.mapService];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(selectedLayersChanged) name:kMapSelectedMapLayerIdsModifiedNotificaiton object:self.mapService];
    
    self.mapControlsState = MapControlsStateAllAvailable;
    
    UITapGestureRecognizer* mapTap = [[UITapGestureRecognizer alloc] initWithTarget:self.searchBar action:@selector(resignFirstResponder)];
    mapTap.cancelsTouchesInView = NO;
    mapTap.delegate = self;
    [self.mapView addGestureRecognizer:mapTap];
    
    if (self.initialState) {
        if (self.initialState.mapItem) {
            self.title = [PCUtils isIdiomPad] ? nil : self.initialState.mapItem.title;
            self.navigationItem.leftItemsSupplementBackButton = YES;
            [self searchMapFor:self.initialState.mapItem.title didReturn:@[self.initialState.mapItem]];
        } else if (self.initialState.query) {
            self.title = [PCUtils isIdiomPad] ? nil : self.initialState.query;
            self.navigationItem.leftItemsSupplementBackButton = YES;
            [self startSearchForQuery:self.initialState.query];
        }
    }
    
    [[MainController publicController] addPluginStateObserver:self selector:@selector(willLoseForeground) notification:PluginWillLoseForegroundNotification pluginIdentifierName:@"Map"];
    [[MainController publicController] addPluginStateObserver:self selector:@selector(didEnterForeground) notification:PluginDidEnterForegroundNotification pluginIdentifierName:@"Map"];
    
    self.layersListButton.enabled = NO;
    [self startGetMapLayersRequestIfNecessary];
    
    if (self.initialQueryWithFullControls && !self.initialState) {
        [self startSearchForQuery:self.initialQueryWithFullControls];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self mapView:self.mapView regionDidChangeAnimated:NO]; //to refresh UI controls and add overlays
    [self updateControls];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.epflLayersOverlayRenderer cancelScreenTileDownload];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.searchState = self.searchState;
}

- (NSUInteger)supportedInterfaceOrientations
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Notifications listening

- (void)willLoseForeground {
    self.searchBarWasFirstResponder = [self.searchBar isFirstResponder];
    [self.searchBar resignFirstResponder];
}

- (void)didEnterForeground {
    if (self.searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    }
}

- (void)recentSearchesChanged {
    [self manageRecentSearchesControllerVisibilityAnimated:YES];
}

- (void)selectedLayersChanged {
    if ([PCUtils isIdiomPad] || !self.presentedViewController) {
        //done on viewWillAppear on iPhone
        [self mapView:self.mapView regionDidChangeAnimated:NO]; //force update layers
    }
}

#pragma mark - Properties override

- (MKCoordinateRegion)epflRegion {
    if ([PCUtils isIdiomPad]) {
        if ([[UIDevice currentDevice] orientation] == UIDeviceOrientationPortrait) {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.520674, 6.566849), MKCoordinateSpanMake(0.011292, 0.013128));
        } else {
            return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519803, 6.566633), MKCoordinateSpanMake(0.008335, 0.017620));
        }
    } else {
        return MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.519766, 6.566893), MKCoordinateSpanMake(0.014206, 0.013108));
    }
}

#pragma mark - Data fetch

- (void)startGetMapLayersRequestIfNecessary {
    if (self.mapLayerForLayerId) {
        [self.getMapLayersTimer invalidate];
        self.getMapLayersTimer = nil;
        return;
    }
    if (!self.getMapLayersTimer) {
        self.getMapLayersTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(startGetMapLayersRequestIfNecessary) userInfo:nil repeats:YES];
    }
    if (self.getMapLayersRequestInProgress) {
        return;
    }
    self.getMapLayersRequestInProgress = YES;
    [self.mapService getLayersWithDelegate:self];
}

- (void)startSearchForQuery:(NSString*)query {
    if (!self.mapService) {
        return;
    }
    [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
    [self.mapService searchFor:query delegate:self];
    self.searchState = SearchStateLoading;
    [self.searchBar resignFirstResponder];
}

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

#pragma mark - Buttons bars and recent searches UI management

- (void)setSearchState:(SearchState)searchState {
    _searchState = searchState;
    
    if (self.searchState != SearchStateResults) {
        if (self.resultsListPopOverController.isPopoverVisible) {
            [self.resultsListPopOverController dismissPopoverAnimated:NO];
        }
        self.mapItemsAllResults = nil;
    }
    
    if (!self.searchBarItem) {
        if (!self.searchBar) {
            self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(0.0, 0, 210.0, kSearchBarHeightPortrait)];
            self.searchBar.delegate = self;
            self.searchBar.barStyle = UISearchBarStyleDefault;
            self.searchBar.translucent = YES;
            self.searchBar.barTintColor = [UIColor clearColor];
            [self.searchBar setBackgroundViewTransparent];
            self.searchBar.placeholder = NSLocalizedStringFromTable(@"SearchPlaceholder", @"MapPlugin", nil);
            self.searchBar.autocapitalizationType = UITextAutocapitalizationTypeNone;
            self.searchBar.autocorrectionType = UITextAutocorrectionTypeNo;
        }
        UIView* searchBarContainerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.searchBar.frame.size.width, self.searchBar.frame.size.height)];
        searchBarContainerView.backgroundColor = [UIColor clearColor];
        [searchBarContainerView addSubview:self.searchBar];
        
        self.searchBarItem = [[UIBarButtonItem alloc] initWithCustomView:searchBarContainerView];
    }
    
    if (!self.loadingIndicator) {
        self.loadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        self.loadingIndicator.translatesAutoresizingMaskIntoConstraints = NO;
        [self.loadingIndicator addConstraints:[NSLayoutConstraint width:20.0 height:20.0 constraintsForView:self.loadingIndicator]];
        [self.searchBarItem.customView addSubview:self.loadingIndicator];
        [self.searchBarItem.customView addConstraints:[NSLayoutConstraint constraintsToSuperview:self.searchBarItem.customView forView:self.loadingIndicator edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, kNoInsetConstraint, kNoInsetConstraint, -30.0)]];
        [self.searchBarItem.customView addConstraint:[NSLayoutConstraint constraintForCenterYtoSuperview:self.searchBarItem.customView forView:self.loadingIndicator constant:0.0]];
    }
    
    if (!self.loadingBarItem) {
        UIActivityIndicatorView* loadingIndicatorForBarItem = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [loadingIndicatorForBarItem startAnimating];
        self.loadingBarItem = [[UIBarButtonItem alloc] initWithCustomView:loadingIndicatorForBarItem];
    }
    
    if (!self.resultsListButton) {
        self.resultsListButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"ListBarButton"] style:UIBarButtonItemStylePlain target:self action:@selector(resultsListPressed)];
        self.resultsListButton.accessibilityLabel = NSLocalizedStringFromTable(@"AllSearchResults", @"MapPlugin", nil);
    }
    self.resultsListButton.accessibilityHint = [NSString stringWithFormat:NSLocalizedStringFromTable(@"ShowsAllSearchResultsForSearchWithFormat", @"MapPlugin", nil), self.searchBar.text];
    
    if (!self.layersListButton) {
        self.layersListButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"MapLayersBarButton"] style:UIBarButtonItemStylePlain target:self action:@selector(layersListPressed)];
        self.layersListButton.accessibilityLabel = NSLocalizedStringFromTable(@"MapLayersAccessibility", @"MapPlugin", nil);
    }
    
    NSArray* items = nil;
    
    if (self.initialState) {
        if (searchState == SearchStateLoading) {
            items = @[self.loadingBarItem];
        } else {
            items = @[];
        }
    } else {
        
        CGRect searchBarTargetFrame;
        CGRect searchBarContainerViewTargetFrame;
        
        [self.loadingIndicator stopAnimating];
        items = @[self.layersListButton, self.searchBarItem];
        CGFloat searchBarTargetWidth = 0.66 * self.view.frame.size.width;
        if ([PCUtils isIdiomPad] && searchBarTargetWidth > 272.0) {
            searchBarTargetWidth = 272.0;
        }
        searchBarTargetFrame = CGRectMake(0, 0, searchBarTargetWidth, kSearchBarHeightPortrait);
        searchBarContainerViewTargetFrame = searchBarTargetFrame;
        switch (searchState) {
            case SearchStateReady:
                [self.loadingIndicator stopAnimating];
                self.searchBar.showsSearchResultsButton = NO;
                break;
            case SearchStateLoading:
            {
                [self.loadingIndicator startAnimating];
                self.searchBar.showsSearchResultsButton = NO;
                break;
            }
            case SearchStateResults:
                [self.loadingIndicator stopAnimating];
                self.searchBar.showsSearchResultsButton = self.mapItemsAllResults.count > 1;
                break;
            default:
                break;
        }
        
        if (!items) {
            return;
        }
        self.searchBar.frame = searchBarTargetFrame;
        self.searchBar.superview.frame = searchBarContainerViewTargetFrame;
    }
    BOOL searchBarWasFirstResponder = self.searchBar.isFirstResponder;
    self.navigationItem.rightBarButtonItems = items;
    if (searchBarWasFirstResponder) {
        [self.searchBar becomeFirstResponder];
    } else {
        [self.searchBar resignFirstResponder];
    }
}

- (void)setMapControlsState:(MapControlsState)mapControlsState {
    if (_mapControlsState == mapControlsState) {
        return;
    }
    _mapControlsState = mapControlsState;

    if (!self.myLocationButton) {
        self.myLocationButton = [[LGAUserTrackingBarButtonItem alloc] initWithMapView:self.mapView];
    }
    
    if (!self.floorDownButton) {
        self.floorDownButton = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"FloorDown"] style:UIBarButtonItemStylePlain target:self action:@selector(floorDownPressed)];
        self.floorDownButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorDown", @"MapPlugin", nil);
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
        self.floorUpButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorUp", @"MapPlugin", nil);
    }
    
    if (!self.centerOnEPFLButton) {
        self.centerOnEPFLButton = [[UIBarButtonItem alloc] initWithTitle:@"EPFL" style:UIBarButtonItemStylePlain target:self action:@selector(centerOnEPFLPressed)];
        self.centerOnEPFLButton.accessibilityLabel = NSLocalizedStringFromTable(@"CenterOnEPFL", @"MapPlugin", nil);
        self.centerOnEPFLButton.accessibilityHint = NSLocalizedStringFromTable(@"CentersMapToSeelWholeEPFLCampus", @"MapPlugin", nil);
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
                items = @[fspace1, self.floorDownButton, self.floorLabelItem, self.floorUpButton, fspace2, self.myLocationButton, space1, self.centerOnEPFLButton, space2];
                break;
            }
            case MapControlsStateNoFloorControl:
            {
                items = @[fspace1, self.myLocationButton, space1, self.centerOnEPFLButton, space2];
                break;
            }
            default:
                break;
        }
        if (items) {
            if (self.leftBarButtonItemsAtLoad) {
                items = [self.leftBarButtonItemsAtLoad arrayByAddingObjectsFromArray:items];
            }
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
            //solves an API bug (apparently) that is occuring when changing toolBar items multiple times in the same run loop
            //timer schedules change for next run loop and solves the problem
            [NSTimer scheduledTimerWithTimeInterval:0.0 block:^{
                self.toolBar.items = items;
            } repeats:NO];
        }
    }
}

- (void)manageRecentSearchesControllerVisibilityAnimated:(BOOL)animated {
    if (self.searchState == SearchStateReady && self.searchBar.isFirstResponder && [self.mapService recentSearches].count > 0) { //recent searches view management
        if (!self.recentSearchesListViewController) {
            MapViewController* weakSelf __weak = self;
            self.recentSearchesListViewController = [[MapRecentSearchesListViewController alloc] initWithUserSelectedRecentSearchBlock:^(NSString *searchPattern) {
                weakSelf.searchBar.text = searchPattern;
                [weakSelf startSearchForQuery:searchPattern];
            }];
            self.recentSearchesListViewController.showClearButtonWithinTableView = [PCUtils isIdiomPad] ? NO : YES;
        }
        if ([PCUtils isIdiomPad]) {
            if (!self.recentSearchesListPopoverController) {
                self.recentSearchesListPopoverController = [[UIPopoverController alloc] initWithContentViewController:[[PCNavigationController alloc] initWithRootViewController:self.recentSearchesListViewController]];
                self.recentSearchesListPopoverController.passthroughViews = @[self.searchBar];
                self.recentSearchesListPopoverController.delegate = self;
            }
            if (!self.recentSearchesListPopoverController.isPopoverVisible) {
                [self.recentSearchesListPopoverController togglePopoverFromBarButtonItem:self.searchBarItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:animated];
            }
        } else {
            self.searchBar.showsCancelButton = YES;
            if (!self.recentSearchesListViewController.parentViewController) {
                [self addChildViewController:self.recentSearchesListViewController];
                [self.view addSubview:self.recentSearchesListViewController.view];
                self.recentSearchesListViewController.view.translatesAutoresizingMaskIntoConstraints = NO;
                self.recentSearchesListViewController.tableView.contentInset = UIEdgeInsetsMake(self.topLayoutGuide.length, 0, self.recentSearchesListViewController.tableView.contentInset.bottom, 0); //216.0 is keyboard height, no way to get it dynmically
                self.recentSearchesListViewController.tableView.scrollIndicatorInsets = self.recentSearchesListViewController.tableView.contentInset;
                [self.view addConstraints:[NSLayoutConstraint constraintsToSuperview:self.view forView:self.recentSearchesListViewController.view edgeInsets:UIEdgeInsetsMake(0, 0, 0, 0)]];
                self.recentSearchesListViewController.view.alpha = 0.0;
                [UIView animateWithDuration:animated ? 0.25 : 0.0 animations:^() {
                    self.recentSearchesListViewController.view.alpha = 1.0;
                } completion:NULL];
            }
        }
    } else {
        self.searchBar.showsCancelButton = NO;
        if ([PCUtils isIdiomPad]) {
            if (self.recentSearchesListPopoverController.isPopoverVisible) {
                [self.recentSearchesListPopoverController dismissPopoverAnimated:animated];
            }
        } else {
            if (self.recentSearchesListViewController.parentViewController) {
                [self.recentSearchesListViewController removeFromParentViewController];
                [UIView animateWithDuration:animated ? 0.25 : 0.0 animations:^() {
                    self.recentSearchesListViewController.view.alpha = 0.0;
                } completion:^(BOOL finished) {
                    [self.recentSearchesListViewController.view removeFromSuperview];
                    self.recentSearchesListViewController.view.alpha = 1.0;
                }];
            }
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

- (void)resultsListPressed {
    if (self.presentedViewController) {
        [self dismissViewControllerAnimated:NO completion:NULL];
    }
    
    __weak __typeof(self) welf = self;
    MapResultsListViewController* resultsViewController = [[MapResultsListViewController alloc] initWithMapItems:self.mapItemsAllResults selectedInitially:nil userValidatedSelectionBlock:^(NSArray *newlySelected) {
        [welf.mapView removeAnnotations:[MapUtils mapItemAnnotations:welf.mapView.annotations]];
        
        void (^block)() = ^ {
            NSArray* mapItemsAnnotations = [welf mapItemAnnotationsForMapItems:newlySelected];
            if (mapItemsAnnotations.count > 0) {
                [welf.mapView showAnnotations:mapItemsAnnotations animated:YES];
                if (mapItemsAnnotations.count == 1) {
                    [welf.mapView selectAnnotation:mapItemsAnnotations[0] animated:YES];
                }
            }
        };
        
        if ([PCUtils isIdiomPad]) {
            [welf.resultsListPopOverController togglePopoverFromBarButtonItem:welf.resultsListButton permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
            block();
        } else {
            [welf dismissViewControllerAnimated:YES completion:block];
        }
    }];
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:resultsViewController];
    if ([PCUtils isIdiomPad]) {
        if (self.resultsListPopOverController.isPopoverVisible) {
            [self.resultsListPopOverController dismissPopoverAnimated:YES];
            self.resultsListPopOverController = nil;
        } else {
            self.resultsListPopOverController = [[UIPopoverController alloc] initWithContentViewController:navController];
            self.resultsListPopOverController.delegate = self;
            [self trackAction:@"ShowResultsList"];
            [self.resultsListPopOverController togglePopoverFromBarButtonItem:self.searchBarItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
        }
    } else {
        [self trackAction:@"ShowResultsList"];
        [self presentViewController:navController animated:YES completion:NULL];
    }
}

- (void)layersListPressed {
    if (!self.mapLayerForLayerId) {
        return;
    }
    
    if (self.resultsListPopOverController.isPopoverVisible) {
        [self.resultsListPopOverController dismissPopoverAnimated:NO];
        self.resultsListPopOverController = nil;
    }
    
    __weak __typeof(self) welf = self;
    MapLayersListSelectionViewController* layersViewController = [[MapLayersListSelectionViewController alloc] initWithAllSelectableMapLayers:self.mapLayerForLayerId.allValues doneButtonTappedBlock:^{
        if ([PCUtils isIdiomPad]) {
            [welf.mapLayersPopover togglePopoverFromBarButtonItem:welf.layersListButton permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
        } else {
            [welf dismissViewControllerAnimated:YES completion:NULL];
        }
    }];
    
    PCNavigationController* navController = [[PCNavigationController alloc] initWithRootViewController:layersViewController];
    if ([PCUtils isIdiomPad]) {
        if (self.mapLayersPopover.isPopoverVisible) {
            [self.mapLayersPopover dismissPopoverAnimated:YES];
            self.mapLayersPopover = nil;
        } else {
            self.mapLayersPopover = [[UIPopoverController alloc] initWithContentViewController:navController];
            self.mapLayersPopover.delegate = self;
            [self trackAction:@"ShowLayersList"];
            [self.mapLayersPopover togglePopoverFromBarButtonItem:self.layersListButton permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
        }
        
    } else {
        [self trackAction:@"ShowLayersList"];
        [self presentViewController:navController animated:YES completion:NULL];
    }
}

- (void)floorDownPressed {
    [self trackAction:@"DecreaseFloor"];
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) { //copy in case they are modified in the meantime (highly unlikely though)
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay decreaseFloorLevel];
    [self.epflLayersOverlay decreaseFloorLevel];
    [self updateControls];
}

- (void)floorUpPressed {
    [self trackAction:@"IncreaseFloor"];
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) {
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    [self.epflTileOverlay increaseFloorLevel];
    [self.epflLayersOverlay increaseFloorLevel];
    [self updateControls];
}

- (void)centerOnEPFLPressed {
    [self trackAction:@"CenterOnCampus"];
    [self.mapView setRegion:self.epflRegion animated:YES];
}

- (void)updateControls {
    if (self.epflTileOverlay.floorLevel == self.epflTileOverlay.minFloorLevel) {
        self.floorDownButton.enabled = NO;
        self.floorUpButton.enabled = YES;
    } else if (self.epflTileOverlay.floorLevel == self.epflTileOverlay.maxFloorLevel) {
        self.floorDownButton.enabled = YES;
        self.floorUpButton.enabled = NO;
    } else {
        self.floorDownButton.enabled = YES;
        self.floorUpButton.enabled = YES;
    }
    ((UILabel*)(self.floorLabelItem.customView)).text = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"Floor", @"MapPlugin", nil), (int)self.epflTileOverlay.floorLevel];
}

- (void)setFloorsLevel:(NSInteger)level {
    if (level == self.epflTileOverlay.floorLevel) {
        return;
    }
    for (id<MKAnnotation> annotation in [self.mapView.annotations copy]) {
        [self.mapView deselectAnnotation:annotation animated:YES];
    }
    self.epflTileOverlay.floorLevel = level;
    self.epflLayersOverlay.floorLevel = level;
    [self updateControls];
}

#pragma mark - UISearchBarDelegate

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self trackAction:@"Search" contentInfo:self.searchBar.text];
    [self startSearchForQuery:self.searchBar.text];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    [self.searchBar resignFirstResponder];
    [self manageRecentSearchesControllerVisibilityAnimated:YES];
}

- (void)searchBarResultsListButtonClicked:(UISearchBar *)searchBar {
    [self resultsListPressed];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    if (!self.searchBar.isFirstResponder) {
        self.searchBarShouldBeginEditing = NO;
    }
    switch (self.searchState) {
        case SearchStateReady:
            [self manageRecentSearchesControllerVisibilityAnimated:YES];
            break;
        case SearchStateLoading:
            [self.mapService cancelOperationsForDelegate:self];
            self.searchState = SearchStateReady;
            break;
        case SearchStateResults:
            self.searchState = SearchStateReady;
            [MapUtils removeMapItemAnnotationsOnMapView:self.mapView];
            break;
        default:
            break;
    }
}

- (BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar {
    // So that user can press clear button to just clear results from map
    // but not bring the keyboard
    // http://stackoverflow.com/a/3852509/1423774
    BOOL boolToReturn = self.searchBarShouldBeginEditing;
    self.searchBarShouldBeginEditing = YES;
    if (!boolToReturn) {
        //otherwise, cancel stays but disabled
        self.searchBar.showsCancelButton = NO;
    }
    return boolToReturn;
}

- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar {
    [self manageRecentSearchesControllerVisibilityAnimated:YES];
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar {
    [self manageRecentSearchesControllerVisibilityAnimated:YES];
}

#pragma mark - MKMapViewDelegate

/*- (void)mapView:(MKMapView *)mapView didChangeUserTrackingMode:(MKUserTrackingMode)mode animated:(BOOL)animated {
    [self setMyLocationButtonSateForTrackingMode:mode];
}*/

- (MKOverlayRenderer*)mapView:(MKMapView *)mapView rendererForOverlay:(id<MKOverlay>)overlay {
    if (overlay == self.epflTileOverlay) {
        return [[PCTileOverlayRenderer alloc] initWithPCTileOverlay:self.epflTileOverlay];
    }
    if (overlay == self.epflLayersOverlay) {
        if (!self.epflLayersOverlayRenderer) {
            self.epflLayersOverlayRenderer = [[PCTileOverlayRenderer alloc] initWithScreenPCTileOverlay:self.epflLayersOverlay];
        }
        return self.epflLayersOverlayRenderer;
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
    
    pin.accessibilityValue = annotation.subtitle.length > 0 ? [NSString stringWithFormat:@"%@\n%@", annotation.title, annotation.subtitle] : annotation.title;
    
    if ([mapItem.category isEqualToString:kMapItemCategoryPerson] && !self.initialState) {
        UIButton* disclosureButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
        [disclosureButton addTarget:self action:@selector(annotationAccessoryTapped:) forControlEvents:UIControlEventTouchUpInside];
        pin.rightCalloutAccessoryView = disclosureButton;
        pin.rightCalloutAccessoryView.accessibilityHint = NSLocalizedStringFromTable(@"ShowsPersonsDetails", @"MapPlugin", nil);
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
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        // if update layers immediately, sometimes prevents annotation from being displayed
        self.epflTileOverlay.floorLevel = mapItem.floor;
        self.epflLayersOverlay.floorLevel = mapItem.floor;
        [self updateControls];
    });
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated {
    
    NSInteger zoomLevel = [MapUtils zoomLevelForMapRect:self.mapView.visibleMapRect];
    
    BOOL shouldShowOverlay = (zoomLevel >= self.epflTileOverlay.minimumZ) && (zoomLevel <= self.epflTileOverlay.maximumZ);
    
    CLLocationDistance altitude = self.mapView.camera.altitude/cos(self.mapView.camera.pitch*M_PI/180.0);
    BOOL shouldAllowFloorLevelChange = altitude <= self.epflTileOverlay.floorLevelsMaxAltitude;
    
    if (shouldShowOverlay) {
        
        if (self.mapLayerForLayerId && (self.mapService.selectedMapLayerIds.count > 0 || self.initialState.layerIdsToDisplay.count > 0)) {
            NSMutableSet* namesForQueryToDisplay = [NSMutableSet set];
            
            for (NSNumber* nsLayerId in self.mapLayerForLayerId) {
                if ([self.mapService.selectedMapLayerIds containsObject:nsLayerId] || [self.initialState.layerIdsToDisplay containsObject:nsLayerId]) {
                    MapLayer* layer = self.mapLayerForLayerId[nsLayerId];
                    if (layer.nameForQueryAllFloors) {
                        [namesForQueryToDisplay addObject:layer.nameForQueryAllFloors];
                    }
                }
            }
            
            self.epflLayersOverlay.mapLayersNamesForQueryToDisplay = namesForQueryToDisplay;
        } else {
            self.epflLayersOverlay.mapLayersNamesForQueryToDisplay = nil;
        }
        
        if (self.mapView.overlays.count == 0) {
            [self.mapView addOverlay:self.epflTileOverlay level:self.epflTileOverlay.desiredLevelForMapView];
            [self.mapView addOverlay:self.epflLayersOverlay level:self.epflLayersOverlay.desiredLevelForMapView];
        } else {
            [self.epflLayersOverlayRenderer reloadData];
        }
        
        if (shouldAllowFloorLevelChange) {
            self.mapControlsState = MapControlsStateAllAvailable;
        } else {
            [self setFloorsLevel:self.epflTileOverlay.defaultFloorLevel]; //back to default floor because other floors might display nothing at low zoom scale
            self.mapControlsState = MapControlsStateNoFloorControl;
        }
    } else {
        if (self.mapView.overlays.count > 0) {
            [self.mapView removeOverlay:self.epflTileOverlay];
            [self.mapView removeOverlay:self.epflLayersOverlay];
            self.mapControlsState = MapControlsStateNoFloorControl;
        }
    }
}

- (void)mapView:(MKMapView *)mapView didChangeUserTrackingMode:(MKUserTrackingMode)mode animated:(BOOL)animated {
    if (mode == MKUserTrackingModeFollow) {
        [self trackAction:@"CenterOnSelf"];
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
    
//#warning REMOVE
    //return;
    
    if (results.count == 0) { //no result
        self.noResultAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"NoResult", @"MapPlugin", nil) message:@"" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [self.noResultAlert show];
        self.searchState = SearchStateReady;
        return;
    }
    
    if (!self.initialState) {
        [self.mapService addOrPromoteRecentSearch:query];
    }
    
    self.mapItemsAllResults = results;
    
    NSArray* mapItemsToDisplay = [MapUtils mapItemsThatShouldBeDisplayed:results forQuery:query];
    
    NSArray* mapItemAnnotations = [self mapItemAnnotationsForMapItems:mapItemsToDisplay];
    
    self.searchState = SearchStateResults;
    
    if (mapItemAnnotations.count > kMaxDisplayedAnnotations) {
        CLSNSLog(@"-> Search for %@ returned too many results (%u), ask user to see list", query, (unsigned int)mapItemAnnotations.count);
        self.tooManyResultsAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"TooManyResults", @"MapPlugin", nil) message:NSLocalizedStringFromTable(@"CannotDisplayOnMap", @"MapPlugin", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:NSLocalizedStringFromTable(@"PickFromList", @"MapPlugin", nil), nil];
        [self.tooManyResultsAlert show];
    } else {
        [self.mapView showAnnotations:mapItemAnnotations animated:YES];
    }
}

- (void)searchMapFailedFor:(NSString *)query {
    self.searchState = SearchStateReady;
    [PCUtils showServerErrorAlert];
}

- (void)getLayersDidReturn:(MapLayersResponse *)response {
    switch (response.statusCode) {
        case MapStatusCode_OK:
        {
            self.getMapLayersRequestInProgress = NO;
            self.mapLayerForLayerId = response.layers;
            self.layersListButton.enabled = (self.mapLayerForLayerId != nil);
            [self mapView:self.mapView regionDidChangeAnimated:NO];
            break;
        }
        default:
            [self getLayersFailed];
            break;
    }
}

- (void)getLayersFailed {
    self.getMapLayersRequestInProgress = NO;
    self.mapLayerForLayerId = nil;
}

- (void)serviceConnectionToServerFailed {
    if (self.searchState == SearchStateLoading) {
        self.searchState = SearchStateReady;
        self.internetConnectionAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutAlert", @"PocketCampus", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [self.internetConnectionAlert show];
    } else {
        //layers, this is the only request we do to map server
        self.getMapLayersRequestInProgress = NO;
    }
}

#pragma mark - Directory related

- (void)annotationAccessoryTapped:(UIButton*)button {
    if (![button isKindOfClass:[UIButton class]] || self.mapView.selectedAnnotations.count == 0) {
        return;
    }
    NSString* annotationTitle = [(MapItemAnnotation*)(self.mapView.selectedAnnotations[0]) title]; //annotation title is actually person's firstname lastname
    
    DirectoryPersonViewController* personViewController = [[DirectoryPersonViewController alloc] initAndLoadPersonWithFullName:annotationTitle];
    personViewController.allowShowOfficeOnMap = NO; //prevent loop
    [self trackAction:@"PinViewMoreInfo" contentInfo:annotationTitle];
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

#pragma mark - UIPopoverControllerDelegate

- (void)popoverControllerDidDismissPopover:(UIPopoverController *)popoverController {
    if (popoverController == self.personPopOverController) {
        self.personPopOverController = nil;
    } else if (popoverController == self.resultsListPopOverController) {
        self.resultsListPopOverController = nil;
    } else if (popoverController == self.recentSearchesListPopoverController) {
        [self.searchBar resignFirstResponder];
    } else if (popoverController == self.mapLayersPopover) {
        self.mapLayersPopover = nil;
    }
}

#pragma mark - UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView willDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.noResultAlert && !self.initialState) { //initial query case is treated in alertView:didDismissWithButtonIndex: (nect method)
        //user likely wants to retype, so focus in search bar
        [self.searchBar becomeFirstResponder];
    }
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == self.internetConnectionAlert) {
        self.internetConnectionAlert = nil;
        if (self.initialState && self.navigationController.visibleViewController == self) { //leave map if initial search query was not successful
            [self.navigationController popViewControllerAnimated:YES];
        }
    } else if (alertView == self.noResultAlert) {
        self.noResultAlert = nil;
        if (self.initialState && self.navigationController.visibleViewController == self) { //leave map if initial search query was not successful
            [self.navigationController popViewControllerAnimated:YES];
        }
    } else if (alertView == self.tooManyResultsAlert) {
        self.tooManyResultsAlert = nil;
        if (buttonIndex != alertView.cancelButtonIndex) { //only two buttons => "results list" button
            [self resultsListPressed];
        }
    } else {
        //no other alerts
    }
}

#pragma mark - CustomOverlayViewDelegate

/*- (void)remoteOverlayRendererDidStartLoading:(RemoteOverlayRenderer *)overlayView {
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
}*/

#pragma mark - Utilities

- (NSArray*)mapItemAnnotationsForMapItems:(NSArray*)mapItems {
    if (!mapItems || ![mapItems isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad mapItems argument in mapItemAnnotationsForMapItems:" reason:@"mapItems is not kind of class NSArray" userInfo:nil];
    }
    NSMutableArray* annotations = [NSMutableArray arrayWithCapacity:mapItems.count];
    for (MapItem* item __strong in mapItems) {
        if (self.initialState.query && self.initialState.queryManualPinLabelText && ![self.initialState.queryManualPinLabelText isEqualToString:item.title]) {
            item = [[MapItem alloc] initWithTitle:self.initialState.queryManualPinLabelText description:item.title latitude:item.latitude longitude:item.longitude layerId:item.layerId itemId:item.itemId floor:item.floor category:item.category];
        }
        
        MapItemAnnotation* annotation = nil;
        
        for (MapItemAnnotation* annot in self.mapView.annotations) {
            if (![annot isKindOfClass:[MapItemAnnotation class]]) {
                continue;
            }
            if ([annot.mapItem isEqual:item]) {
                annotation = annot;
                break;
            }
        }
        if (!annotation) {
            annotation = [[MapItemAnnotation alloc] initWithMapItem:item];
        }
        [annotations addObject:annotation];
    }
    return annotations;
}

#pragma mark - dealloc

- (void)dealloc
{
    [self.getMapLayersTimer invalidate];
    [self.mapService cancelOperationsForDelegate:self];
    [[MainController publicController] removePluginStateObserver:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.mapView.delegate = nil;
    [self.mapView removeOverlays:self.mapView.overlays]; // http://stackoverflow.com/a/22244049/1423774
    [self.epflLayersOverlayRenderer cancelScreenTileDownload];
    self.internetConnectionAlert.delegate = nil;
}



@end
