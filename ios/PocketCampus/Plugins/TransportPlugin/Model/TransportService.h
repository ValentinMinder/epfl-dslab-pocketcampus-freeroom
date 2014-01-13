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




//  Created by Loïc Gardiol on 09.03.12.


#import "Service.h"

#import "transport.h"

#import "TransportModelAdditions.h"

extern NSString* const kTransportUserTransportStationsModifiedNotification;

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

#pragma mark - Service methods

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
