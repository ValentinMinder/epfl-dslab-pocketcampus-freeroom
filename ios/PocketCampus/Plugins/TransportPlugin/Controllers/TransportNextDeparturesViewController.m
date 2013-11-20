//
//  TransportNextDeparturesViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 20.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportNextDeparturesViewController.h"

#import "TransportService.h"

typedef enum {
    FavStationsStateEmpty,
    FavStationsStateNeedTwo,
    FavStationsStateLoading,
    FavStationsStateOK,
    FavStationsStateError,
    FavStationsStateUnset
} FavStationsState;

typedef enum {
    LocationStateManualSelection,
    LocationStateLocated,
    LocationStateLoading,
    LocationStateErrorUserDenied,
    LocationStateErrorTimeout,
    LocationStateErrorUnknown,
    LocationStateUnset
} LocationState;

typedef enum {
    SchedulesStateLoaded,
    SchedulesStateLoading,
    SchedulesStateError,
    SchedulesStateUnset
} SchedulesState;

@interface TransportNextDeparturesViewController ()<TransportServiceDelegate>

@property (nonatomic, strong) IBOutlet UIImageView* locationArrow;
@property (nonatomic, strong) IBOutlet UILabel* fromLabel;
@property (nonatomic, strong) IBOutlet UILabel* fromValueLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* locationActivityIndicator;
@property (nonatomic, strong) IBOutlet UIButton* infoButton;
@property (nonatomic, strong) IBOutlet UITableView* tableView;
@property (nonatomic, strong) IBOutlet UILabel* welcomeTouchInfoInstructionsLabel;
@property (nonatomic, strong) IBOutlet UILabel* connectionErrorLabel;
@property (nonatomic, strong) IBOutlet UIToolbar* toolbar;

@property (nonatomic, strong) TransportService* transportService;
@property (nonatomic, strong) NSArray* favStations;
@property (nonatomic, strong) NSMutableDictionary* tripResults; //key : destination station name, value : QueryTripResult
@property (nonatomic) LocationState locationState;
@property (nonatomic) SchedulesState schedulesState;
@property (nonatomic, strong) TransportStation* departureStation;
@property (nonatomic) FavStationsState favStationsState;
@property (nonatomic, strong) NSTimer* refreshTimer;
@property (nonatomic) BOOL isRefreshing;
@property (nonatomic, strong) NSDate* lastRefreshTimestamp;
@property (nonatomic) BOOL needToRefresh;

@end

@implementation TransportNextDeparturesViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithNibName:@"TransportNextDeparturesView" bundle:nil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

#pragma mark - TransportServiceDelegate

- (void)serviceConnectionToServerTimedOut {
#warning TODO
}

@end
