//
//  TransportService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportService.h"

@implementation TransportService

static TransportService* instance = nil;

+ (id)sharedInstanceToRetain {
    if (instance != nil) {
        return instance;
    }
    @synchronized(self) {
        if (instance == nil) {
            instance = [[[self class] alloc] initWithServiceName:@"transport"];
            [instance setThriftClient:[[[TransportServiceClient alloc] initWithProtocol:instance.thriftProtocol] autorelease]];
        }
    }
    return [instance autorelease];;
}

- (void)autocomplete:(NSString*)constraint delegate:(id)delegate {
    if (![constraint isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad constraint" reason:@"constraint is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(autocomplete:);
    operation.delegateDidReturnSelector = @selector(autocompleteFor:didReturn:);
    operation.delegateDidFailSelector = @selector(autocompleteFailedFor::);
    [operation addObjectArgument:constraint];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getLocationsForIDs:(NSArray*)ids delegate:(id)delegate {
    if (![ids isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad ids" reason:@"ids is either nil or not of class NSArray" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getLocationsFromIDs:);
    operation.delegateDidReturnSelector = @selector(locationsForIDs:didReturn:);
    operation.delegateDidFailSelector = @selector(locationsFailedForIDs:);
    [operation addObjectArgument:ids];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getLocationsForNames:(NSArray*)names delegate:(id)delegate {
    if (![names isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad names" reason:@"names is either nil or not of class NSArray" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getLocationsFromNames:);
    operation.delegateDidReturnSelector = @selector(locationsForNames:didReturn:);
    operation.delegateDidFailSelector = @selector(locationsFailedForNames:);
    [operation addObjectArgument:names];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

/*- (void)nextDeparturesFromStationID:(NSString*)stationID1 toStationID:(NSString*)stationID2 delegate:(id)delegate {
    if (![stationID1 isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad stationID1" reason:@"stationID1 is either nil or not of class NSString" userInfo:nil];
    }
    if (![stationID2 isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad stationID2" reason:@"stationID2 is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(nextDepartures::);
    operation.delegateDidReturnSelector = @selector(nextDeparturesFromStationID:toStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(nextDeparturesFailedFromStationID:toStationID:);
    [operation addObjectArgument:stationID1];
    [operation addObjectArgument:stationID2];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}*/

- (void)nextDeparturesForStationID:(NSString*)stationID delegate:(id)delegate {
    if (![stationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad IDStation" reason:@"IDStation is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(nextDepartures:);
    operation.delegateDidReturnSelector = @selector(nextDeparturesForStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(nextDeparturesFailedForStationID:);
    [operation addObjectArgument:stationID];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getTripsFrom:(NSString*)from to:(NSString*)to delegate:(id)delegate {
    if (![from isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'from' argument" reason:@"'from' argument is either nil or not of class NSString" userInfo:nil];
    }
    if (![to isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'to' argument" reason:@"'to' argument is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getTrips::);
    operation.delegateDidReturnSelector = @selector(tripsFrom:to:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFrom:to:);
    [operation addObjectArgument:from];
    [operation addObjectArgument:to];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getTripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture delegate:(id)delegate {
    //time and isDeperture not verifiable (primitives)
    if (![from isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'from' argument" reason:@"'from' argument is either nil or not of class NSString" userInfo:nil];
    }
    if (![to isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'to' argument" reason:@"'to' argument is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getTripsAtTime::::);
    operation.delegateDidReturnSelector = @selector(tripsFrom:to:atTimestamp:isDeparture:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFrom:to:atTimestamp:isDeparture:);
    [operation addObjectArgument:from];
    [operation addObjectArgument:to];
    [operation addLongLongArgument:time];
    [operation addBoolArgument:isDeparture];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)getTripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID delegate:(id)delegate {
    if (![fromStationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad fromStationID" reason:@"fromStationID is either nil or not of class NSString" userInfo:nil];
    }
    if (![toStationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad toStationID" reason:@"toStationID is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:self.thriftClient delegate:delegate];
    operation.serviceClientSelector = @selector(getTripsFromStationsIDs::);
    operation.delegateDidReturnSelector = @selector(tripsFromStationID:toStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFromStationID:toStationID:);
    [operation addObjectArgument:fromStationID];
    [operation addObjectArgument:toStationID];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

- (void)dealloc
{
    instance = nil;
    [super dealloc];
}

@end
