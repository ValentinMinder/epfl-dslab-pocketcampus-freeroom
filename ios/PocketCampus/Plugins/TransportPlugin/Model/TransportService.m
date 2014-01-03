//
//  TransportService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportService.h"

#import <CoreLocation/CoreLocation.h>

#import "PCObjectArchiver.h"

#import <float.h>

#import "NSOperationWithDelegate_Protected.h"

#pragma mark - TransportService private interface

NSString* const kTransportUserTransportStationsModifiedNotification = @"kTransportUserTransportStationsModifiedNotification";

@interface TransportService ()

@property (nonatomic, strong) NSOrderedSet* privateUserTransportStations;
@property (nonatomic, strong) TransportStation* privateUserManualDepartureTransportStation;

@end

#pragma mark - NearestUserTransportStationRequest interface

@interface NearestUserTransportStationRequest : NSOperationWithDelegate<CLLocationManagerDelegate>

- (id)initWithTransportStations:(NSOrderedSet*)sations delegate:(id)delegate;

@property (nonatomic, strong) NSOrderedSet* stations;
@property (nonatomic, strong) NSTimer* checkCancellationAndAdaptDesiredAccuracyTimer;
@property (nonatomic, strong) CLLocationManager* locationManager;
@property (nonatomic) BOOL blockedByAuthStatus;
@property (nonatomic) BOOL delegateCallScheduled;
@property (nonatomic) int nbRounds;

@end


@implementation TransportService

static TransportService* instance __weak = nil;

#pragma mark - Init

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"PushNotifService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"transport" thriftServiceClientClassName:NSStringFromClass(TransportServiceClient.class)];
        if (self) {
            instance = self;
        }
        return self;
    }
}

#pragma mark - ServiceProtocol

+ (id)sharedInstanceToRetain {
    @synchronized (self) {
        if (instance) {
            return instance;
        }
#if __has_feature(objc_arc)
        return [[[self class] alloc] init];
#else
        return [[[[self class] alloc] init] autorelease];
#endif
    }
}

#pragma mark - Service methods

- (void)autocomplete:(NSString*)constraint delegate:(id)delegate {
    if (![constraint isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad constraint" reason:@"constraint is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(autocomplete:);
    operation.delegateDidReturnSelector = @selector(autocompleteFor:didReturn:);
    operation.delegateDidFailSelector = @selector(autocompleteFailedFor::);
    [operation addObjectArgument:constraint];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getLocationsForIDs:(NSArray*)ids delegate:(id)delegate {
    if (![ids isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad ids" reason:@"ids is either nil or not of class NSArray" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLocationsFromIDs:);
    operation.delegateDidReturnSelector = @selector(locationsForIDs:didReturn:);
    operation.delegateDidFailSelector = @selector(locationsFailedForIDs:);
    [operation addObjectArgument:ids];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getLocationsForNames:(NSArray*)names delegate:(id)delegate {
    if (![names isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad names" reason:@"names is either nil or not of class NSArray" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLocationsFromNames:);
    operation.delegateDidReturnSelector = @selector(locationsForNames:didReturn:);
    operation.delegateDidFailSelector = @selector(locationsFailedForNames:);
    [operation addObjectArgument:names];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)nextDeparturesForStationID:(NSString*)stationID delegate:(id)delegate {
    if (![stationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad IDStation" reason:@"IDStation is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(nextDepartures:);
    operation.delegateDidReturnSelector = @selector(nextDeparturesForStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(nextDeparturesFailedForStationID:);
    [operation addObjectArgument:stationID];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getTripsFrom:(NSString*)from to:(NSString*)to delegate:(id)delegate priority:(NSInteger)priority {
    if (![from isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'from' argument" reason:@"'from' argument is either nil or not of class NSString" userInfo:nil];
    }
    if (![to isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'to' argument" reason:@"'to' argument is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    [operation setQueuePriority:priority];
    operation.serviceClientSelector = @selector(getTrips::);
    operation.delegateDidReturnSelector = @selector(tripsFrom:to:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFrom:to:);
    [operation addObjectArgument:from];
    [operation addObjectArgument:to];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getTripsFrom:(NSString*)from to:(NSString*)to atTimestamp:(timestamp)time isDeparture:(BOOL)isDeparture delegate:(id)delegate {
    //time and isDeperture not verifiable (primitives)
    if (![from isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'from' argument" reason:@"'from' argument is either nil or not of class NSString" userInfo:nil];
    }
    if (![to isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad 'to' argument" reason:@"'to' argument is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstanceWithCustomTimeoutInterval:40.0] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTripsAtTime::::);
    operation.delegateDidReturnSelector = @selector(tripsFrom:to:atTimestamp:isDeparture:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFrom:to:atTimestamp:isDeparture:);
    [operation addObjectArgument:from];
    [operation addObjectArgument:to];
    [operation addLongLongArgument:time];
    [operation addBoolArgument:isDeparture];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getTripsFromStationID:(NSString*)fromStationID toStationID:(NSString*)toStationID delegate:(id)delegate {
    if (![fromStationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad fromStationID" reason:@"fromStationID is either nil or not of class NSString" userInfo:nil];
    }
    if (![toStationID isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:@"bad toStationID" reason:@"toStationID is either nil or not of class NSString" userInfo:nil];
    }
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTripsFromStationsIDs::);
    operation.delegateDidReturnSelector = @selector(tripsFromStationID:toStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFromStationID:toStationID:);
    [operation addObjectArgument:fromStationID];
    [operation addObjectArgument:toStationID];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}


#pragma mark - Properties

static NSString* const kUserTransportStationsKey = @"userTransportStations";
static NSString* const kFavoriteTransportStationsOldKey = @"favoriteTransportStations";
static NSString* const kManualDepartureStationKey = @"manualDepartureStation";

- (void)initPersistedProperties {
    if (!self.privateUserTransportStations) {
        self.privateUserTransportStations = (NSOrderedSet*)[PCObjectArchiver objectForKey:kUserTransportStationsKey andPluginName:@"transport"];
        if (!self.privateUserTransportStations) {
            NSArray* oldFavStations = (NSArray*)[PCObjectArchiver objectForKey:kFavoriteTransportStationsOldKey andPluginName:@"transport"];;
            if (oldFavStations) {
                self.privateUserTransportStations = [NSOrderedSet orderedSetWithArray:oldFavStations]; //storage transition from old methods to new
            }
        }
    }
    if (!self.privateUserManualDepartureTransportStation) {
        self.privateUserManualDepartureTransportStation = (TransportStation*)[PCObjectArchiver objectForKey:kManualDepartureStationKey andPluginName:@"transport"];
    }
}

- (NSOrderedSet*)userTransportStations {
    [self initPersistedProperties];
    return self.privateUserTransportStations;
}

- (void)setUserTransportStations:(NSOrderedSet*)userTransportStations {
    [self initPersistedProperties];
    if ([self.privateUserTransportStations isEqualToOrderedSet:userTransportStations]) {
        return;
    }
    self.privateUserTransportStations = [userTransportStations copy];
    [PCObjectArchiver saveObject:self.privateUserTransportStations forKey:kUserTransportStationsKey andPluginName:@"transport"];
    [[NSNotificationCenter defaultCenter] postNotificationName:kTransportUserTransportStationsModifiedNotification object:self];
}

- (TransportStation*)userManualDepartureStation {
    [self initPersistedProperties];
    return self.privateUserManualDepartureTransportStation;
}

- (void)setUserManualDepartureStation:(TransportStation *)userManualDepartureStation {
    if (userManualDepartureStation == self.privateUserManualDepartureTransportStation) {
        return;
    }
    [self initPersistedProperties];
    [self willChangeValueForKey:NSStringFromSelector(@selector(userManualDepartureStation))];
    self.privateUserManualDepartureTransportStation = userManualDepartureStation;
    [self didChangeValueForKey:NSStringFromSelector(@selector(userManualDepartureStation))];
    [PCObjectArchiver saveObject:self.privateUserManualDepartureTransportStation forKey:kManualDepartureStationKey andPluginName:@"transport"];
}

#pragma mark - Nearest TransportStation

- (void)nearestUserTransportStationWithDelegate:(id)delegate {
    if (delegate == nil) {
        @throw [NSException exceptionWithName:@"bad delegate" reason:@"delegate cannot be nul" userInfo:nil];
    }
    NSOrderedSet* stations = self.userTransportStations;
    
    if (stations.count < 2) {
        [delegate nearestUserTransportStationDidReturn:[stations firstObject]];
    } else {
        NearestUserTransportStationRequest* operation = [[NearestUserTransportStationRequest alloc] initWithTransportStations:stations delegate:delegate];
        [self.operationQueue addOperation:operation];
    }
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
}

@end

/*---------------------------------------------------*/


@implementation NearestUserTransportStationRequest

static NSTimeInterval const kLocationValidityInterval = 60.0; //nb seconds a cached location can be used / is considered that user has not moved
static NSString* const kLastLocationKey = @"lastLocation";

- (id)initWithTransportStations:(NSOrderedSet*)stations delegate:(id)delegate {
    [PCUtils throwExceptionIfObject:stations notKindOfClass:[NSOrderedSet class]];
    self = [super init];
    if (self) {
        self.stations = stations;
        self.delegate = delegate;
        self.locationManager = [CLLocationManager new];
    }
    return self;
}

#pragma mark - NSOperation overrides

- (void)main {
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    
    self.executing = YES;
    
    self.locationManager.delegate = self;
    [self.locationManager startUpdatingLocation];
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusRestricted) {
        NSLog(@"-> User has denied access to location, will return error to delegate.");
        [self locationManager:self.locationManager didFailWithError:[NSError errorWithDomain:@"" code:kCLErrorDenied userInfo:nil]];
        return;
    }
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined) {
        NSLog(@"-> Waiting for user to accept access to location...");
        self.blockedByAuthStatus = YES;
        return; //self will be called (see delegate method) by CLLocationManager when user has accepted or rejected access to location
    }
    
    CLLocation* lastLocation = (CLLocation*)[PCObjectArchiver objectForKey:kLastLocationKey andPluginName:@"transport"];
    if ([self locationIsStillValid:lastLocation] && [self locationEnglobesOnlyOneStation:lastLocation]) {
        NSLog(@"-> Last location still valid (%@), will return to delegate.", lastLocation.timestamp);
        [self returnLocationToDelegate:lastLocation];
        return;
    }
    
    self.blockedByAuthStatus = NO;

    self.locationManager.desiredAccuracy = [self minimumDistanceBetweenStations] > 1000 ? kCLLocationAccuracyHundredMeters : kCLLocationAccuracyBest; //improves reliability
    self.locationManager.distanceFilter =  kCLDistanceFilterNone;
    
    dispatch_sync(dispatch_get_main_queue(), ^{ //timer must be scheduled on other thread not be blocked
        self.checkCancellationAndAdaptDesiredAccuracyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(checkCancellationAndAdaptDesiredAccuracy) userInfo:nil repeats:YES];
    });
}

- (BOOL)isConcurrent {
    return YES;
}

#pragma mark - Timer call handling

- (void)checkCancellationAndAdaptDesiredAccuracy {
    if ([self isCancelled]) {
        [self cancelAll];
        return;
    }
    self.nbRounds++;
    
    if (self.nbRounds == 10) { //enlarge desiredAccurary, should give a result much faster
        self.locationManager.desiredAccuracy = 5000.0; //5KM
        [self handleLocationUpdate:self.locationManager.location];
    } else if (self.nbRounds == 15) { //location timeout (15 seconds)
        [self locationManager:self.locationManager didFailWithError:[NSError errorWithDomain:@"" code:kCLErrorLocationUnknown userInfo:nil]]; //normally delegate method, but used to properly terminate location search and return error to delegate
    } else {
        /*CLLocationAccuracy accuracy = locationManager.desiredAccuracy;
        if (nbRounds % 4 == 0 && accuracy < kCLLocationAccuracyBest) { //don't want to wait longer with this accuracy level
            accuracy = 80.0;
        }
        accuracy = accuracy*2.0;*/
        if (self.locationManager.desiredAccuracy == kCLLocationAccuracyBest) {
            if (self.nbRounds == 3) { //do not wait longer than 3 seconds in this best accuracy mode
                self.locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
            }
        } else if (self.nbRounds % 2 == 0) {
            self.locationManager.desiredAccuracy *= 2.0;
        }
        
        [self handleLocationUpdate:self.locationManager.location];
    }
}

- (void)cancelAll {
    [self cancel];
    if (self.checkCancellationAndAdaptDesiredAccuracyTimer) {
        [self.checkCancellationAndAdaptDesiredAccuracyTimer invalidate];
    }
    self.locationManager.delegate = nil;
    [self.locationManager stopUpdatingLocation];
    self.delegate = nil;
    self.executing = NO;
    self.finished = YES;
}

#pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    if (self.blockedByAuthStatus) {
        NSLog(@"-> User has made a decision for location access. Restarting the request.");
        [self main];
    }
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
    CLLocation* newLocation = [locations lastObject]; //docs says lastObject is newest, and array contains at least one location
    [self handleLocationUpdate:newLocation];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    if(self.delegateCallScheduled) {
        return; //delegate call has already been putted in main loop
    }
    
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    self.locationManager.delegate = nil;
    switch (error.code) {
        case kCLErrorDenied:
            if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestUserTransportStationFailed:)]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate nearestUserTransportStationFailed:LocationFailureReasonUserDenied];
                    [self cancelAll];
                });
            } else {
                [self cancelAll];
            }
            self.delegateCallScheduled = YES;
            break;
        default:
            if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestUserTransportStationFailed:)]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate nearestUserTransportStationFailed:LocationFailureReasonUnknown];
                    [self cancelAll];
                });
            } else {
                [self cancelAll];
            }
            self.delegateCallScheduled = YES;
            break;
    }
}

#pragma mark - Location and delegate handling

- (void)handleLocationUpdate:(CLLocation*)newLocation {
    if(self.delegateCallScheduled) {
        return; //delegate call has already been putted in main loop
    }
    
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    
    if ([CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorized) {
        return;
    }
    
    NSLog(@"-> Handling location with accuracy : %lf | desired accuarcy : %lf", newLocation.horizontalAccuracy, self.locationManager.desiredAccuracy);
    
    if (![self locationIsStillValid:newLocation]) {
        NSLog(@"-> Old location. Ignoring.");
        [PCObjectArchiver saveObject:nil forKey:kLastLocationKey andPluginName:@"transport"];
        return;
    }
    
    if (newLocation.horizontalAccuracy <= 0.0) {
        NSLog(@"-> Useless/invalid location (accuracy <= 0.0). Ignoring.");
        return;
    }
    
    if (self.locationManager.desiredAccuracy == kCLLocationAccuracyBest) {
        NSLog(@"-> Waiting for best accuracy to be achieved : desired accuracy will be switched to 100m in %d seconds.", (3-self.nbRounds));
        return;
    }
    
    if (newLocation.horizontalAccuracy > self.locationManager.desiredAccuracy) {
        NSLog(@"-> Location accuracy (%lf) not sufficient, %lf required.", newLocation.horizontalAccuracy, self.locationManager.desiredAccuracy);
        return;
    }
    
    if (![self locationEnglobesOnlyOneStation:newLocation]) {
        if (newLocation.horizontalAccuracy > self.locationManager.desiredAccuracy) { //second condition to prevent infinite waiting because accuracy cannot be achieved (desiredAccurary is deacreased by timer)
            NSLog(@"-> Location not accurate enough. Ignoring.");
            return;
        }
    }
    
    /* From this point, newLocation is considered valid, will return to delegate */
    
    /* DEV TEST */
    /*
    if (stations.count == 5) {
        NSString* message = [NSString stringWithFormat:@"accuracy : %lf, desired accuracy : %lf, timestamp delta from now : %d", newLocation.horizontalAccuracy, locationManager.desiredAccuracy, abs((int)[newLocation.timestamp timeIntervalSinceNow])];
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"LOCATION" message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
            //[alert show];
            [alert release];
        });
    }
    */
    /* END OF DEV TEST */
    
    NSLog(@"-> Location considered valid, will return to delegate.");
    [PCObjectArchiver saveObject:newLocation forKey:kLastLocationKey andPluginName:@"transport"];
    [self returnLocationToDelegate:newLocation];
    
}

- (void)returnLocationToDelegate:(CLLocation*)validLocation {
    
    TransportStation* retStation = [self nearestStationFromLocation:validLocation];
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestUserTransportStationDidReturn:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate nearestUserTransportStationDidReturn:retStation];
            [self cancelAll];
        });
    } else {
        [self cancelAll];
    }
    self.delegateCallScheduled = YES;
}

#pragma mark - Utils

- (BOOL)locationIsStillValid:(CLLocation*)location {
    if (location == nil || location.timestamp == nil) {
        return NO;
    }
    if (location.horizontalAccuracy < 0) { //from documentation, means invalid location
        return NO;
    }
    if (fabs([location.timestamp timeIntervalSinceNow]) > kLocationValidityInterval) {
        return NO;
    }
    return YES;
}

//Return YES if location parameter accuracy englobes only 1 user station. NO otherwise.
- (BOOL)locationEnglobesOnlyOneStation:(CLLocation*)newLocation {
    if (newLocation == nil) {
        return NO;
    }
    CLCircularRegion* userRegion = [[CLCircularRegion alloc] initWithCenter:newLocation.coordinate radius:newLocation.horizontalAccuracy identifier:@"userRegion"]; //+1.0 to be sure this station will be included in containsCoordinate check
    int nbFavStationsInUserRegion = 0;
    for (TransportStation* station in self.stations) {
        if ([userRegion containsCoordinate:CLLocationCoordinate2DMake(station.latitude/1000000.0, station.longitude/1000000.0)]) {
            nbFavStationsInUserRegion++;
        }
    }
    return (nbFavStationsInUserRegion < 2);
}

//does not take accuracy of location into parameter
- (TransportStation*)nearestStationFromLocation:(CLLocation*)newLocation {
    TransportStation* retStation = nil;
    CLLocationDistance minDistance = CGFLOAT_MAX;
    for (TransportStation* station in self.stations) {
        CLLocation* location = [[CLLocation alloc] initWithLatitude:station.latitude/1000000.0 longitude:station.longitude/1000000.0];
        CLLocationDistance distance = [location distanceFromLocation:newLocation];
        if ([location distanceFromLocation:newLocation] < minDistance) {
            retStation = station;
            minDistance = distance;
        }
    }
    return retStation;
}

- (CLLocationDistance)minimumDistanceBetweenStations {
    CLLocationDistance minDistance = CGFLOAT_MAX; //in meters
    for (TransportStation* station1 in self.stations) {
        for (TransportStation* station2 in self.stations) {
            [PCUtils throwExceptionIfObject:station1 notKindOfClass:[TransportStation class]];
            [PCUtils throwExceptionIfObject:station2 notKindOfClass:[TransportStation class]];
            if (station1 != station2) {
                CLLocation* location1 = [[CLLocation alloc] initWithLatitude:station1.latitude/1000000.0 longitude:station1.longitude/1000000.0];
                CLLocation* location2 = [[CLLocation alloc] initWithLatitude:station2.latitude/1000000.0 longitude:station2.longitude/1000000.0];
                CLLocationDistance distance = [location1 distanceFromLocation:location2];
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
    }
    return minDistance;
}

#pragma mark - Dealloc

- (void)dealloc
{
    self.locationManager.delegate = nil;
}

@end
