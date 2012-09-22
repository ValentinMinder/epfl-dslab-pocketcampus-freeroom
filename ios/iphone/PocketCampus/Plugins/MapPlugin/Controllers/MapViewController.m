//
//  MapViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 12.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "GANTracker.h"

#import "MapViewController.h"

#import "MapItemAnnotation.h"

#import "MapUtils.h"

#import "PCUtils.h"

#import "MapController.h"

#import "CustomOverlayView.h"

#import "PCUnkownPersonViewController.h"

static int MAX_DISPLAYED_ANNOTATIONS = 70;
static NSString* kMapItemAnnotationIdentifier = @"mapItemAnnotation";

@implementation MapViewController

@synthesize mapView, searchBar, myLocationButton, floorDownButton, floorLabel, floorUpButton, othersButton, toolbar, overlaysLoadingIndicator;

- (id)init
{
    self = [super initWithNibName:@"MapView" bundle:nil];
    if (self) {
        // Custom initialization
        self.title = [MapController localizedName];
        mapService = [[MapService sharedInstanceToRetain] retain];
        directoryService = nil; //will be instancied if needed
        personToDisplay = nil;
        epflTileOverlay = [[EPFLTileOverlay alloc] init];
        epflLayersOverlay = [[EPFLLayersOverlay alloc] init];
        tileOverlayView = [[CustomOverlayView alloc] initWithOverlay:epflTileOverlay];
        layersOverlayView = [[CustomOverlayView alloc] initWithOverlay:epflLayersOverlay];
        internetConnectionAlert = nil;
        initialQuery = nil;
        initialQueryManualPinLabelText = nil;
        if ([PCUtils isOSVersionSmallerThan:6.0]) {
            epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.006544, 0.007316));
        } else {
            epflRegion = MKCoordinateRegionMake(CLLocationCoordinate2DMake(46.518747, 6.565683), MKCoordinateSpanMake(0.012285, 0.013733));
        }
        searchBarState = SearchBarStateHidden;
        navBarLoadingIndicator = nil;
        annotationsToAdd = nil;
        showBuildingsInterior = YES; //default
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query {
    self = [self init];
    if (self) {
        initialQuery = [query retain];
    }
    return self;
}

- (id)initWithInitialQuery:(NSString*)query pinTextLabel:(NSString*)pinTextLabel {
    self = [self initWithInitialQuery:query];
    if (self) {
        initialQueryManualPinLabelText = [pinTextLabel retain];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    if (initialQuery)
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/results" withError:NULL];
    else
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map" withError:NULL];
    UITapGestureRecognizer* mapTap = [[UITapGestureRecognizer alloc] initWithTarget:searchBar action:@selector(resignFirstResponder)];
    mapTap.cancelsTouchesInView = NO;
    mapTap.delegate = self;
    [mapView addGestureRecognizer:mapTap];
    [mapTap release];
    tileOverlayView.delegate = self;
    //layersOverlayView.delegate = self;
    [mapView setRegion:epflRegion animated:NO];
    mapView.accessibilityIdentifier = @"EPFLMapView";
    floorDownButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorDown", @"MapPlugin", nil);
    floorUpButton.accessibilityLabel = NSLocalizedStringFromTable(@"FloorUp", @"MapPlugin", nil);
    othersButton.accessibilityLabel = NSLocalizedStringFromTable(@"Others", @"MapPlugin", nil);
    epflTileOverlay.mapView = mapView;
    epflLayersOverlay.mapView = mapView;
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(toogleSearchBar)] autorelease];
    searchBarHiddenFrame = searchBar.frame; //y = -44.0
    CGRect tmpFrame = searchBar.frame;
    tmpFrame.origin.y = 0.0;
    searchBarVisibleFrame = tmpFrame;
    searchBar.placeholder = NSLocalizedStringFromTable(@"SearchPlaceholder", @"MapPlugin", nil);
    searchBar.isAccessibilityElement = YES;
    searchBar.accessibilityIdentifier = @"SearchBar";
    searchActivityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    searchActivityIndicator.center = CGPointMake(searchBar.frame.size.width-43.0, (searchBar.frame.size.height/2.0));
    searchActivityIndicator.hidesWhenStopped = YES;
    [searchBar addSubview:searchActivityIndicator];
    [searchActivityIndicator release];
    if (initialQuery) {
        /*searchBar.text = initialQuery;
        [self setSearchBarState:SearchBarStateVisible];*/
        self.navigationItem.rightBarButtonItem = nil;
        navBarLoadingIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        navBarLoadingIndicator.center = CGPointMake(296.0, 21.0);
        [self.navigationController.navigationBar addSubview:navBarLoadingIndicator];
        [navBarLoadingIndicator release];
        [NSTimer scheduledTimerWithTimeInterval:0.5 target:navBarLoadingIndicator selector:@selector(startAnimating) userInfo:nil repeats:NO];
        [self startSearchForQuery:initialQuery];
    }
    [self mapView:mapView regionDidChangeAnimated:NO]; //to refresh UI controls and add overlays
    [self updateFloorLabel];
}

- (void)viewWillDisappear:(BOOL)animated {
    if (navBarLoadingIndicator) {
        [navBarLoadingIndicator removeFromSuperview];
        navBarLoadingIndicator = nil;
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
     // Release any retained subviews of the main view
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    [tileOverlayView didReceiveMemoryWarning];
    [layersOverlayView didReceiveMemoryWarning];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation //<= iOS5
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* MapService access */

- (void)startSearchForQuery:(NSString*)query {
    if (mapService == nil) {
        return;
    }
    [MapUtils removeMapItemAnnotationsOnMapView:mapView];
    [mapService searchFor:query delegate:self];
    [searchActivityIndicator startAnimating];
    [searchBar resignFirstResponder];
}

/* UIGestureRecognizerDelegate delegation */

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

/* SearchBar animations */

- (void)toogleSearchBar {
    if (searchBarState == SearchBarStateVisible) {
        [self setSearchBarState:SearchBarStateHidden];
        [MapUtils removeMapItemAnnotationsOnMapView:mapView];
    } else {
        [self setSearchBarState:SearchBarStateVisible];
    }
}

- (void)hideSearchBar {
    [self setSearchBarState:SearchBarStateHidden];
}

- (void)setSearchBarState:(SearchBarState)newState {
    if (searchBarState == newState) {
        return;
    }
    typedef void (^b1_t)(void);
    
    b1_t animBlock;
    
    switch (newState) {
        case SearchBarStateHidden:
        {
            searchBarState = SearchBarStateHidden;
            if (mapService != nil) {
                [mapService cancelOperationsForDelegate:self];
            }
            searchBar.text = @"";
            UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(toogleSearchBar)];
            [self.navigationItem setRightBarButtonItem:button animated:YES];
            [button release];
            [searchActivityIndicator stopAnimating];
            [searchBar resignFirstResponder];
            animBlock = ^{
                searchBar.frame = searchBarHiddenFrame;
                //mapView.frame = CGRectMake(mapView.frame.origin.x, mapView.frame.origin.y-searchBar.frame.size.height, mapView.frame.size.width, mapView.frame.size.height+searchBar.frame.size.height);
            };
        }
            break;
        case SearchBarStateVisible:
        {
            [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/click/search" withError:NULL];
            searchBarState = SearchBarStateVisible;
            UIBarButtonItem* button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(toogleSearchBar)];
            [self.navigationItem setRightBarButtonItem:button animated:YES];
            [button release];
            [searchBar becomeFirstResponder];
            animBlock = ^{
                searchBar.frame = searchBarVisibleFrame;
                //mapView.frame = CGRectMake(mapView.frame.origin.x, mapView.frame.origin.y+searchBar.frame.size.height, mapView.frame.size.width, mapView.frame.size.height-searchBar.frame.size.height);
            };
        }
            break;
        default:
            NSLog(@"!! Unsupported SearchBarState");
            break;
    }
    
    [UIView transitionWithView:searchBar duration:0.25 options:UIViewAnimationTransitionNone animations:animBlock completion:NULL];
}

/* IBActions */

- (IBAction)myLocationPressed {    
    if (mapView.userTrackingMode == MKUserTrackingModeNone) {
        [[GANTracker sharedTracker] trackPageview:@"/v3r1/map/click/mylocation" withError:NULL];
        [mapView setUserTrackingMode:MKUserTrackingModeFollow animated:YES];
        mapView.showsUserLocation = YES;
        [mapView setRegion:MKCoordinateRegionMake(mapView.userLocation.coordinate, MKCoordinateSpanMake(0.003, 0.003)) animated:YES];
    } else if (mapView.userTrackingMode == MKUserTrackingModeFollow) {
        [mapView setUserTrackingMode:MKUserTrackingModeFollowWithHeading animated:YES];
        if (mapView.userTrackingMode != MKUserTrackingModeFollowWithHeading) { //means heading not supported
            [mapView setUserTrackingMode:MKUserTrackingModeNone];
            mapView.showsUserLocation = NO;
        }
    } else {
        [mapView setUserTrackingMode:MKUserTrackingModeNone animated:YES];
        mapView.showsUserLocation = NO;
    }
}

- (IBAction)othersPressed {
    //NSLog(@"MapView region : %lf %lf, %lf %lf", mapView.region.center.latitude, mapView.region.center.longitude, mapView.region.span.latitudeDelta, mapView.region.span.longitudeDelta);
    NSString* localizedStringFromBuildings;
    if (showBuildingsInterior) {
        localizedStringFromBuildings = NSLocalizedStringFromTable(@"HideBuildingsInterior", @"MapPlugin", nil);
    } else {
        localizedStringFromBuildings = NSLocalizedStringFromTable(@"ShowBuildingsInterior", @"MapPlugin", nil);
    }
    othersActionSheet = [[UIActionSheet alloc] initWithTitle:@"" delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel", @"PocketCampus", nil) destructiveButtonTitle:nil otherButtonTitles:NSLocalizedStringFromTable(@"CenterOnEPFL", @"MapPlugin", nil), localizedStringFromBuildings, nil];
    [othersActionSheet showFromToolbar:toolbar];
}

- (IBAction)floorDownPressed {
    for (id<MKAnnotation> annotation in mapView.annotations) {
        [mapView deselectAnnotation:annotation animated:YES];
    }
    [epflTileOverlay decreaseLayerLevel];
    [epflLayersOverlay decreaseLayerLevel];
    [self updateFloorLabel];
}

- (IBAction)floorUpPressed {
    for (id<MKAnnotation> annotation in mapView.annotations) {
        [mapView deselectAnnotation:annotation animated:YES];
    }
    [epflTileOverlay increaseLayerLevel];
    [epflLayersOverlay increaseLayerLevel];
    [self updateFloorLabel];
}

- (void)updateFloorLabel {
    if (epflTileOverlay.currentLayerLevel == MIN_LAYER_LEVEL) {
        floorDownButton.enabled = NO;
    } else if (epflTileOverlay.currentLayerLevel == MAX_LAYER_LEVEL) {
        floorUpButton.enabled = NO;
    } else {
        MKZoomScale zoomScale = mapView.bounds.size.width / mapView.visibleMapRect.size.width;
        if ([epflTileOverlay canDrawMapRect:mapView.visibleMapRect zoomScale:zoomScale]) {
            floorDownButton.enabled = YES;
            floorUpButton.enabled = YES;
        }
    }
    floorLabel.text = [NSString stringWithFormat:@"%@ %d", NSLocalizedStringFromTable(@"Floor", @"MapPlugin", nil), epflTileOverlay.currentLayerLevel];
}

/* UIActionSheetDelegate delegation */

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: //Center of EPFL
        {
            [mapView setUserTrackingMode:MKUserTrackingModeNone animated:NO];
            myLocationButton.tintColor = [UIColor whiteColor];
            [mapView setRegion:epflRegion animated:YES];
            break;
        }
        case 1: //show/hide buildings interior
        {
            showBuildingsInterior = !showBuildingsInterior;
            [self mapView:mapView regionDidChangeAnimated:NO]; //to refresh layer visibility
            break;
        }
        default:
            break;
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    [othersActionSheet release];
    othersActionSheet = nil;
}

/* UISearchBarDelegate delegation */

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar_ {
    [self startSearchForQuery:searchBar.text];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    [MapUtils removeMapItemAnnotationsOnMapView:mapView];
}


/* MKMapViewDelegate delegation */

- (void)mapView:(MKMapView *)mapView didChangeUserTrackingMode:(MKUserTrackingMode)mode animated:(BOOL)animated {
    if (mode == MKUserTrackingModeNone) {
        myLocationButton.image = [UIImage imageNamed:@"LocationArrow"];
        myLocationButton.tintColor = [UIColor whiteColor];
    } else if (mode == MKUserTrackingModeFollow) {
        myLocationButton.tintColor = [UIColor colorWithRed:0.7215 green:0.6745 blue:0.9921 alpha:1.0];
    } else if (mode == MKUserTrackingModeFollowWithHeading) {
        myLocationButton.image = [UIImage imageNamed:@"Compass"];
    } else {
        //no other mode
    }
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id <MKOverlay>)overlay {
    if ([overlay isKindOfClass:[EPFLTileOverlay class]]) { 
        return tileOverlayView;
    } else if ([overlay isKindOfClass:[EPFLLayersOverlay class]]) {
        return layersOverlayView;
    } else {
        //other, not managed
    }
    return nil;
}

- (MKAnnotationView *)mapView:(MKMapView *)theMapView viewForAnnotation:(id <MKAnnotation>)annotation {
    if (![annotation isKindOfClass:[MapItemAnnotation class]]) {
        return nil;
    }
    
    MKPinAnnotationView* pin = (MKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:kMapItemAnnotationIdentifier]; //cast ok we know we only use MKPinAnnotationView
    if (pin == nil) {
        pin = [[[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:kMapItemAnnotationIdentifier] autorelease];
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
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotations:mapView.annotations];
    if (mapItemAnnotations.count == 1) {
        [mapView selectAnnotation:[mapItemAnnotations objectAtIndex:0] animated:YES];
    }
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view {
    if ([view isKindOfClass:[MKAnnotationView class]]) {
        view.rightCalloutAccessoryView = nil;
        NSString* roomName = view.annotation.subtitle;
        if (roomName == nil || roomName.length == 0) {
            roomName = view.annotation.title;
        }
        if (directoryService) {
            [directoryService cancelOperationsForDelegate:self];
        }
        if (roomName != nil && !roomName.length == 0) {
            //both title and subtitle are indicated, might be a person => look in directory
            if (!initialQuery) {
                if (!directoryService) {
                    directoryService = [[DirectoryService sharedInstanceToRetain] retain];
                }
                [directoryService searchPersons:view.annotation.title delegate:self];
            }
            int level = [MapUtils levelToSelectForRoomName:roomName];
            if (level != INT_MAX) {
                [epflTileOverlay setLayerLevel:level];
                [epflLayersOverlay setLayerLevel:level];
                [self updateFloorLabel];
            }
        }
    }
}

- (void)mapView:(MKMapView *)mapView_ regionDidChangeAnimated:(BOOL)animated {
    
    //NSLog(@"%lf, %lf", mapView.region.span.latitudeDelta, mapView.region.span.longitudeDelta);

    MKZoomScale zoomScale = mapView.bounds.size.width / mapView.visibleMapRect.size.width;
    
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
    
    if (![epflTileOverlay canDrawMapRect:mapView.visibleMapRect zoomScale:zoomScale] || !showBuildingsInterior) {
        if ([mapView.overlays count] > 0) {
            [mapView removeOverlay:epflTileOverlay];
            //[mapView removeOverlay:epflLayersOverlay];
            floorDownButton.enabled = NO;
            floorLabel.hidden = YES;
            floorUpButton.enabled = NO;
        }
    } else if (showBuildingsInterior) {
        if ([mapView.overlays count] == 0) {
            [mapView addOverlay:epflTileOverlay];
            //[mapView addOverlay:epflLayersOverlay];
            floorDownButton.enabled = YES;
            floorLabel.hidden = NO;
            floorUpButton.enabled = YES;
        }
    } else {
        //nothing
    }
    
    if (annotationsToAdd) {
        [mapView addAnnotations:annotationsToAdd];
        [annotationsToAdd release];
        annotationsToAdd = nil;
    }
    
}



/* MapServiceDelegate delegation */

- (void)searchMapFor:(NSString*)query didReturn:(NSArray*)results{
    
    /* TEST */
    /*
    for (MapItem* item in results) {
        NSLog(@"%@", [item descriptionObject]);
    }
    */
    /* END OF TEST */
    
    [searchActivityIndicator stopAnimating];
    [NSTimer scheduledTimerWithTimeInterval:1.5 target:navBarLoadingIndicator selector:@selector(stopAnimating) userInfo:nil repeats:NO];
    
    [mapView setUserTrackingMode:MKUserTrackingModeNone];
    
    if (results.count == 0) { //no result
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"NoResult", @"MapPlugin", nil) message:@"" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    NSArray* mapItemAnnotations = [MapUtils mapItemAnnotationsThatShouldBeDisplayed:[self mapItemAnnotationsForMapItems:results] forQuery:query];
    
    if (mapItemAnnotations.count > MAX_DISPLAYED_ANNOTATIONS) {
        NSLog(@"-> Search for %@ returned too many results (%d)", query, mapItemAnnotations.count);
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"TooManyResults", @"MapPlugin", nil) message:NSLocalizedStringFromTable(@"NarrowSearch", @"MapPlugin", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        [alert release];
        return;
    }
    
    MKCoordinateRegion reqRegion = [MapUtils regionToFitMapItemAnnotations:mapItemAnnotations];
    
    [annotationsToAdd release];
    annotationsToAdd = [mapItemAnnotations retain];
    
    if ([MapUtils isRegion:mapView.region equalToRegion:[mapView regionThatFits:reqRegion]]) {
        [self mapView:mapView regionDidChangeAnimated:NO]; //force this call to redraw annotations (not called because region has not changed)
    } else {
        [mapView setRegion:reqRegion animated:YES];
    }
    
    //[MapUtils zoomMapView:mapView toFitMapItemAnnotationsAnimated:YES];
}

- (void)searchMapFailedFor:(NSString *)query {
    [searchActivityIndicator stopAnimating];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerError", @"PocketCampus", nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
    [alert release];
}

- (void)serviceConnectionToServerTimedOut {
    [navBarLoadingIndicator stopAnimating];
    [searchActivityIndicator stopAnimating];
    [internetConnectionAlert release];
    internetConnectionAlert = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil) message:NSLocalizedStringFromTable(@"ConnectionToServerTimedOut", @"PocketCampus", nil) delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [internetConnectionAlert show];    
}

/* DirectoryServiceDelegate delegation */

- (void)searchDirectoryFor:(NSString *)searchPattern didReturn:(NSArray *)results {
    if (mapView.selectedAnnotations.count == 0 || results.count == 0) {
        [self searchDirectoryFailedFor:searchPattern];
        return;
    }
    
    id<MKAnnotation> selectedAnnotation = [mapView.selectedAnnotations objectAtIndex:0];
    
    MKAnnotationView* selectedAnnotationView = [mapView viewForAnnotation:selectedAnnotation];
    
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
    [personToDisplay release];
    personToDisplay = [person retain];

}

- (void)searchDirectoryFailedFor:(NSString *)searchPattern {
    if (mapView.selectedAnnotations.count == 0) {
        return;
    }
    id<MKAnnotation> selectedAnnotation = [mapView.selectedAnnotations objectAtIndex:0];

    MKAnnotationView* selectedAnnotationView = [mapView viewForAnnotation:selectedAnnotation];
    
    selectedAnnotationView.rightCalloutAccessoryView = nil;
    [personToDisplay release];
    personToDisplay = nil;
    
}

- (void)profilePictureFor:(NSString *)sciper didReturn:(NSData *)data {
    if (personToDisplay && [personToDisplay.sciper isEqualToString:sciper] && [self.navigationController.topViewController isKindOfClass:[PCUnkownPersonViewController class]]) {
        [(PCUnkownPersonViewController*)self.navigationController.visibleViewController setProfilePictureData:data];
    }
}

- (void)profilePictureFailedFor:(NSString *)sciper {
    if (personToDisplay && [personToDisplay.sciper isEqualToString:sciper] && [self.navigationController.topViewController isKindOfClass:[PCUnkownPersonViewController class]]) {
        [(PCUnkownPersonViewController*)self.navigationController.visibleViewController setProfilePictureData:NULL]; //no picture => will show default icon
    }
}

- (void)annotationAccessoryTapped:(UIButton*)button {
    if (![button isKindOfClass:[UIButton class]] || !personToDisplay) {
        return;
    }
    [directoryService getProfilePicture:personToDisplay.sciper delegate:self];
    PCUnkownPersonViewController* personViewController = [[PCUnkownPersonViewController alloc] initWithDelegate:nil];
    [personViewController setPerson:personToDisplay];
    [self.navigationController pushViewController:personViewController animated:YES];
    [personViewController release];
}

/* UIAlertViewDelegate */

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (alertView == internetConnectionAlert) {
        [internetConnectionAlert release];
        internetConnectionAlert = nil;
        if (initialQuery && self.navigationController.visibleViewController == self) { //leave map if initial search query was not successful
            [self.navigationController popViewControllerAnimated:YES];
        }
    }
}

/* CustomOverlayViewDelegate */

- (void)customOverlayViewDidStartLoading:(CustomOverlayView *)overlayView {
    if (overlaysLoadingIndicator.isAnimating) {
        return;
    }
    [overlaysLoadingIndicator startAnimating];
}

- (void)customOverlayViewDidFinishLoading:(CustomOverlayView *)overlayView {
    if (!overlaysLoadingIndicator.isAnimating) {
        return;
    }
    [overlaysLoadingIndicator stopAnimating];
}

/* Uitilities */

- (NSArray*)mapItemAnnotationsForMapItems:(NSArray*)mapItems {
    if (mapItems == nil || ![mapItems isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad mapItems argument in mapItemAnnotationsForMapItems:" reason:@"mapItems is not kind of class NSArray" userInfo:nil];
    }
    NSMutableArray* annotations = [NSMutableArray arrayWithCapacity:mapItems.count];
    for(MapItem* item in mapItems) {
        if (initialQuery != nil && initialQueryManualPinLabelText != nil && ![initialQueryManualPinLabelText isEqualToString:item.title]) {
            item = [[[MapItem alloc] initWithTitle:initialQueryManualPinLabelText description:item.title latitude:item.latitude longitude:item.longitude layerId:item.layerId itemId:item.itemId] autorelease];
        }
        MapItemAnnotation* annotation = [[MapItemAnnotation alloc] initWithMapItem:item];
        [annotations addObject:annotation];
        [annotation release];
    }
    return annotations;
}

- (void)dealloc
{
    mapView.delegate = nil;
    [mapService release];
    [directoryService release];
    [personToDisplay release];
    [epflTileOverlay release];
    [epflLayersOverlay release];
    [tileOverlayView cancelTilesDownload:YES];
    tileOverlayView.delegate = nil;
    [tileOverlayView release];
    [layersOverlayView cancelTilesDownload:YES];
    layersOverlayView.delegate = nil;
    [layersOverlayView release];
    if (internetConnectionAlert) {
        internetConnectionAlert.delegate = nil;
    }
    [internetConnectionAlert release];
    [initialQuery release];
    [initialQueryManualPinLabelText release];
    [annotationsToAdd release];
    [super dealloc];
}

@end
