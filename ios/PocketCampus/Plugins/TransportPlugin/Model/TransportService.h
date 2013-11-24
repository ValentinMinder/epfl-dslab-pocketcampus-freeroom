//
//  TransportService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <CoreLocation/CoreLocation.h>

#import "Service.h"

#import "transport.h"

#import "TransportModelAdditions.h"

static NSString* kUserTransportStationsModifiedNotificationName __unused = @"UserTransportStationsModifiedNotification";

@interface TransportService : Service<ServiceProtocol>

/*
 
 THRIFT SERVICE
 
- (NSArray *) autocomplete: (NSString *) constraint;  // throws TException
- (NSArray *) getLocationsFromIDs: (NSArray *) ids;  // throws TException
- (NSArray *) getLocationsFromNames: (NSArray *) names;  // throws TException
- (QueryTripsResult *) getTrips: (NSString *) from : (NSString *) to;  // throws TException
- (QueryTripsResult *) getTripsAtTime: (NSString *) from : (NSString *) to : (timestamp) time : (BOOL) isDeparture;  // throws TException
- (QueryTripsResult *) getTripsFromStationsIDs: (NSString *) fromID : (NSString *) toID;  // throws TException
*/

#pragma mark - Thrift requests

- (void)autocomplete:(NSString*)constraint delegate:(id)delegate;
- (void)getLocationsForIDs:(NSArray*)ids delegate:(id)delegate;
- (void)getLocationsForNames:(NSArray*)names delegate:(id)delegate;
- (void)getTripsFrom:(NSString*)from to:(NSString*)to delegate:(id)delegate priority:(NSInteger)priority; //priority between -8 (lowest) and +8 (highest). Off bounds will be set to closest matching. See NSOperationQueuePriority
- (void)getTripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture delegate:(id)delegate;
- (void)getTripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID delegate:(id)delegate;

#pragma mark - Location

- (void)nearestUserTransportStationWithDelegate:(id)delegate;

#pragma mark - User stations
/*
 * This property is persisted (uses storage)
 * nil if never set
 * A notification with name kUserTransportStationsModifiedNotificationName is posted when the set is modified
 */
@property (nonatomic, copy) NSOrderedSet* userTransportStations;

/*
 * This property is persisted (uses storage)
 * nil if never set
 * KVO compliant
 */
@property (nonatomic, copy) TransportStation* userManualDepartureStation;

@end

#pragma mark -  fTransportServiceDelegate

typedef enum {
    LocationFailureReasonUnset = 0,
    LocationFailureReasonUserDenied,
    LocationFailureReasonTimeout,
    LocationFailureReasonUnknown,
} LocationFailureReason;

@protocol TransportServiceDelegate <ServiceDelegate>

@optional
/* delegation for default transport service requests */
- (void)autocompleteFor:(NSString*)constraint didReturn:(NSArray*)results;
- (void)autocompleteFailedFor:(NSString*)constraint;
- (void)locationsForIDs:(NSArray*)ids didReturn:(NSArray*)locations;
- (void)locationsFailedForIDs:(NSArray*)ids;
- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations;
- (void)locationsFailedForNames:(NSArray*)names;
- (void)tripsFrom:(NSString*)from to:(NSString*)to didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to;
- (void)tripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture;
- (void)tripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID;

/* delegation for location utilities */

- (void)nearestUserTransportStationDidReturn:(TransportStation*)nearestStation;
- (void)nearestUserTransportStationFailed:(LocationFailureReason)reason;

@end
