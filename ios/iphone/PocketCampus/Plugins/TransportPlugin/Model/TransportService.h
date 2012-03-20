//
//  TransportService.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "Service.h"

#import "transport.h"

@interface TransportService : Service<ServiceProtocol>

/*
- (NSArray *) autocomplete: (NSString *) constraint;  // throws TException
- (NSArray *) getLocationsFromIDs: (NSArray *) ids;  // throws TException
- (NSArray *) getLocationsFromNames: (NSArray *) names;  // throws TException
- (QueryDepartureResult *) nextDepartures: (NSString *) IDStation;  // throws TException
- (QueryTripsResult *) getTrips: (NSString *) from : (NSString *) to;  // throws TException
- (QueryTripsResult *) getTripsAtTime: (NSString *) from : (NSString *) to : (timestamp) time : (BOOL) isDeparture;  // throws TException
- (QueryTripsResult *) getTripsFromStationsIDs: (NSString *) fromID : (NSString *) toID;  // throws TException
*/

- (void)autocomplete:(NSString*)constraint delegate:(id)delegate;
- (void)getLocationsForIDs:(NSArray*)ids delegate:(id)delegate;
- (void)getLocationsForNames:(NSArray*)names delegate:(id)delegate;
- (void)nextDeparturesForStationID:(NSString*)stationID delegate:(id)delegate;
- (void)getTripsFrom:(NSString*)from to:(NSString*)to delegate:(id)delegate;
- (void)getTripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture delegate:(id)delegate;
- (void)getTripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID delegate:(id)delegate;

@end

@protocol TransportServiceDelegate <ServiceDelegate>

- (void)autocompleteFor:(NSString*)constraint didReturn:(NSArray*)results;
- (void)autocompleteFailedFor:(NSString*)constraint;
- (void)locationsForIDs:(NSArray*)ids didReturn:(NSArray*)locations;
- (void)locationsFailedForIDs:(NSArray*)ids;
- (void)locationsForNames:(NSArray*)names didReturn:(NSArray*)locations;
- (void)locationsFailedForNames:(NSArray*)names;
- (void)nextDeparturesForStationID:(NSString*)stationID didReturn:(QueryDepartureResult*)departureResult;
- (void)nextDeparturesFailedForStationID:(NSString*)stationID;
- (void)tripsFrom:(NSString*)from to:(NSString*)to didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to;
- (void)tripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture;
- (void)tripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID didReturn:(QueryTripsResult*)tripResult;
- (void)tripsFailedFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID;

@end