//
//  TransportNextDeparturesCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum {
    TransportNextDeparturesCellStateLoading = 0,
    TransportNextDeparturesCellStateLoaded,
    TransportNextDeparturesCellStateError
} TransportNextDeparturesCellState;

@class TransportStation;
@class QueryTripsResult;

@interface TransportNextDeparturesCell : UITableViewCell

- (id)initWithReuseIdentifier:(NSString*)identifier;

/*
 * Use this property to temporarily indicate the destination when the QueryTripsResult is not available yet
 * The cell will display destinationStation.name
 * IMPORTANT: assigning new value nils tripResult
 * Default: nil
 */
@property (nonatomic, strong) TransportStation* destinationStation;

/*
 * Use this property in conjunction with destinationStation to indicate the state
 * If TransportNextDeparturesCellStateLoading, the cell displays a loading indication
 * If TransportNextDeparturesCellStateError, the cell displays the message "Error"
 * Otherwise, shows nothing in particular (you should not set TransportNextDeparturesCellStateLoaded manually)
 * Default: TransportNextDeparturesCellStateLoading
 */
@property (nonatomic) TransportNextDeparturesCellState state;

/*
 * Assigning a non-nil QueryTripResult results in self.state set to TransportNextDeparturesCellStateLoaded
 * and self.destinationStation set to tripResult.to
 * The cell then shows the content of tripResult
 * Default: nil
 */
@property (nonatomic, strong) QueryTripsResult* tripResult;

@end
