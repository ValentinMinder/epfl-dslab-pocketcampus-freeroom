//
//  NextDeparturesCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "transport.h"

typedef enum {
    LoadingStateLoading,
    LoadingStateError,
    LoadingStateLoaded
} LoadingState;

@interface NextDeparturesCell : UITableViewCell {
    TransportStation* transportStation;
    LoadingState loadingState;
}

- (id)initWithQueryTripResult:(QueryTripsResult*)trip;
- (id)initWithDestinationStation:(TransportStation*)destinationStation loadingState:(LoadingState)loadingState;

@property (readonly) TransportStation* transportStation;
@property (readonly) LoadingState loadingState;

@end
