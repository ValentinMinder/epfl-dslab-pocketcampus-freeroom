//
//  NextDeparturesListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 23.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "TransportService.h"

#import "PCValues.h"

#import "FavoriteStationsViewController.h"

#import "TransportUtils.h"

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

@interface NextDeparturesListViewController : UIViewController<TransportServiceDelegate> {
    UIImageView* locationArrow;
    UILabel* fromLabel;
    UILabel* fromValueLabel;
    UIActivityIndicatorView* locationActivityIndicator;
    UIButton* infoButton;
    UITableView* tableView;
    UILabel* welcomeTouchInfoInstructionsLabel;
    UILabel* connectionErrorLabel;
    UIToolbar* toolbar;
    TransportService* transportService;
    NSArray* favStations;
    NSMutableDictionary* tripResults; //key : destination station name, value : QueryTripResult
    LocationState locationState;
    SchedulesState schedulesState;
    TransportStation* departureStation;
    FavStationsState favStationsState;
    NSTimer* refreshTimer;
    BOOL isRefreshing;
    NSDate* lastRefreshTimestamp;
    BOOL needToRefresh;
}

- (IBAction)presentFavoriteStationsViewController:(id)sender;
- (void)refresh;

@property (nonatomic, assign) IBOutlet UIImageView* locationArrow;
@property (nonatomic, assign) IBOutlet UILabel* fromLabel;
@property (nonatomic, assign) IBOutlet UILabel* fromValueLabel;
@property (nonatomic, assign) IBOutlet UIActivityIndicatorView* locationActivityIndicator;
@property (nonatomic, assign) IBOutlet UIButton* infoButton;
@property (nonatomic, assign) IBOutlet UITableView* tableView;
@property (nonatomic, assign) IBOutlet UILabel* welcomeTouchInfoInstructionsLabel;
@property (nonatomic, assign) IBOutlet UILabel* connectionErrorLabel;
@property (nonatomic, assign) IBOutlet UIToolbar* toolbar;

@end
