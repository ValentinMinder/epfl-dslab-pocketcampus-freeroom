//
//  TransportService.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "TransportService.h"

#import "ObjectArchiver.h"

@implementation TransportService

static TransportService* instance __weak = nil;

static NSString* kFavoriteTransportStationsKey = @"favoriteTransportStations";
static NSString* kManualDepartureStationKey = @"manualDepartureStation";

- (id)init {
    @synchronized(self) {
        if (instance) {
            @throw [NSException exceptionWithName:@"Double instantiation attempt" reason:@"PushNotifService cannot be instancied more than once at a time, use sharedInstance instead" userInfo:nil];
        }
        self = [super initWithServiceName:@"transport"];
        if (self) {
            instance = self;
        }
        return self;
    }
}

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

- (id)thriftServiceClientInstance {
    return [[[TransportServiceClient alloc] initWithProtocol:[self thriftProtocolInstance]] autorelease];
}

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
    [operationQueue addOperation:operation];
    [operation release];
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
    [operationQueue addOperation:operation];
    [operation release];
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
    [operationQueue addOperation:operation];
    [operation release];
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
    [operationQueue addOperation:operation];
    [operation release];
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
    operation.customTimeout = 40.0;
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
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
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
    ServiceRequest* operation = [[ServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getTripsFromStationsIDs::);
    operation.delegateDidReturnSelector = @selector(tripsFromStationID:toStationID:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFromStationID:toStationID:);
    [operation addObjectArgument:fromStationID];
    [operation addObjectArgument:toStationID];
    operation.returnType = ReturnTypeObject;
    [operationQueue addOperation:operation];
    [operation release];
}

/* User defaults */

- (NSArray*)userFavoriteTransportStations { //NSArray of TransportStation
    return (NSArray*)[ObjectArchiver objectForKey:kFavoriteTransportStationsKey andPluginName:@"transport"];
}

- (BOOL)saveUserFavoriteTransportStations:(NSArray*)favStations { //NSArray of TransportStation
    return [ObjectArchiver saveObject:favStations forKey:kFavoriteTransportStationsKey andPluginName:@"transport"];
}

- (TransportStation*)userManualDepartureStation {
    return (TransportStation*)[ObjectArchiver objectForKey:kManualDepartureStationKey andPluginName:@"transport"];
}

- (BOOL)saveUserManualDepartureStation:(TransportStation*)station {
    return [ObjectArchiver saveObject:station forKey:kManualDepartureStationKey andPluginName:@"transport"];
}

/* location utilities */

- (BOOL)appHasAccessToLocation {
    CLAuthorizationStatus status =  [CLLocationManager authorizationStatus];
    if (status != kCLAuthorizationStatusAuthorized || ![CLLocationManager locationServicesEnabled]) {
        return NO;
    }
    return YES;
}

- (void)nearestFavoriteTransportStationWithDelegate:(id)delegate {
    if (delegate == nil) {
        @throw [NSException exceptionWithName:@"bad delegate" reason:@"delegate cannot be nul" userInfo:nil];
    }
    NSArray* stations = [self userFavoriteTransportStations];
    
    if (stations.count == 0) {
        [delegate nearestFavoriteTransportStationDidReturn:nil];
    } else if (stations.count == 1) {
        [delegate nearestFavoriteTransportStationDidReturn:[stations objectAtIndex:0]];
    } else {
        NearestFavoriteStationRequest* operation = [[NearestFavoriteStationRequest alloc] initWithTransportStations:(NSArray*)[self userFavoriteTransportStations] delegate:delegate];
        [operationQueue addOperation:operation];
        [operation release];
    }
}

- (void)dealloc
{
    @synchronized(self) {
        instance = nil;
    }
    [super dealloc];
}

@end

/*---------------------------------------------------*/

@implementation NearestFavoriteStationRequest

static int kLocationValidity = 30; //nb seconds a cached location can be used / is considered that user has not moved
static NSString* kLastLocationKey = @"lastLocation";

@synthesize stations, checkCancellationAndAdaptDesiredAccuracyTimer;

- (id)initWithTransportStations:(NSArray*)stations_ delegate:(id)delegate_
{
    self = [super init];
    if (self) {
        self.delegate = delegate_;
        self.stations = stations_;
        locationManager = [[CLLocationManager alloc] init];
        locationManager.purpose = NSLocalizedStringFromTable(@"LocationNeedJustification", @"TransportPlugin", nil);
        blockedByAuthStatus = NO;
        executing = NO;
        finished = NO;
        delegateCallScheduled = NO;
        self.checkCancellationAndAdaptDesiredAccuracyTimer = nil;
        nbRounds = 0;
    }
    return self;
}

/* NSOperation methods */

- (void)main {
    //[self retain]; //to prevent release of the operation even when main finishes (must wait for location to be precised)
    
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    
    [self willChangeValueForKey:@"isExecuting"];
    executing = YES;
    [self didChangeValueForKey:@"isExecuting"];
    
    if (![stations isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad stations" reason:@"bad stations in NearestFavoriteStationRequest" userInfo:nil];
    }
    
    locationManager.delegate = self;
    [locationManager startUpdatingLocation];
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusRestricted) {
        NSLog(@"-> User has denied access to location, will return error to delegate.");
        [self locationManager:locationManager didFailWithError:[NSError errorWithDomain:@"" code:kCLErrorDenied userInfo:nil]];
        return;
    }
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined) {
        NSLog(@"-> Waiting for user to accept access to location...");
        blockedByAuthStatus = YES;
        return; //self will be called (see delegate method) by CLLocationManager when user has accepted or rejected access to location
    }
    
    blockedByAuthStatus = NO;
    
    CLLocationDistance minDistance = [self minimumDistanceBetweenStations];
    
    if (minDistance > 1000) {
        locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters; //improves reliability
    } else {
        locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    }
    
    locationManager.distanceFilter =  kCLDistanceFilterNone;
    
    dispatch_async(dispatch_get_main_queue(), ^{ //timer must be scheduled on other thread not be blocked
        self.checkCancellationAndAdaptDesiredAccuracyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(checkCancellationAndAdaptDesiredAccuracy) userInfo:nil repeats:YES];
    });
    
    CLLocation* lastLocation = (CLLocation*)[ObjectArchiver objectForKey:kLastLocationKey andPluginName:@"transport"];
    if ([self locationIsStillValid:lastLocation] && [self locationEnglobesOnlyOneFavoriteStation:lastLocation]) {
        NSLog(@"-> Last location still valid (%@), will return to delegate.", lastLocation.timestamp);
        [self returnLocationToDelegate:lastLocation];
        return;
    }
    
    /*if ([self locationIsStillValid:locationManager.location] && [self locationEnglobesOnlyOneFavoriteStation:locationManager.location]) { //commented because CLLocationManange sometimes says location is fresh (timestamp diff 0) when it's not. Do not trust it !
        NSLog(@"-> Initial locationManager location still valid, will return to delegate.");
        [self handleLocationUpdate:locationManager.location];
        return;
    }*/
    
}

- (void)checkCancellationAndAdaptDesiredAccuracy {
    if ([self isCancelled]) {
        [self cancelAll];
        return;
    }
    nbRounds++;
    
    if (nbRounds == 10) { //enlarge desiredAccurary, should give a result much faster
        locationManager.desiredAccuracy = 5000.0; //5KM
        [self handleLocationUpdate:locationManager.location];
    } else if (nbRounds == 15) { //location timeout (15 seconds)
        [self locationManager:locationManager didFailWithError:[NSError errorWithDomain:@"" code:kCLErrorLocationUnknown userInfo:nil]]; //normally delegate method, but used to properly terminate location search and return error to delegate
    } else {
        /*CLLocationAccuracy accuracy = locationManager.desiredAccuracy;
        if (nbRounds % 4 == 0 && accuracy < kCLLocationAccuracyBest) { //don't want to wait longer with this accuracy level
            accuracy = 80.0;
        }
        accuracy = accuracy*2.0;*/
        if (locationManager.desiredAccuracy == kCLLocationAccuracyBest) {
            if (nbRounds == 3) { //do not wait longer than 3 seconds in this best accuracy mode
                locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
            }
        } else if (nbRounds % 2 == 0) {
            locationManager.desiredAccuracy *= 2.0;
        }
        
        [self handleLocationUpdate:locationManager.location];
    }
}

- (void)cancelAll {
    [self cancel];
    if (self.checkCancellationAndAdaptDesiredAccuracyTimer) {
        [self.checkCancellationAndAdaptDesiredAccuracyTimer invalidate];
    }
    locationManager.delegate = nil;
    [locationManager stopUpdatingLocation];
    self.delegate = nil;
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];
    executing = NO;
    finished = YES;
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
}

- (BOOL)isConcurrent {
    return YES;
}

- (BOOL)isExecuting {
    return executing;
}

- (BOOL)isFinished {
    return finished;
}

/* CLLocationManagerDelegate delegation */

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    if (blockedByAuthStatus) {
        NSLog(@"-> User has made a decision for location access. Restarting the request.");
        [self main];
    }
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    [self handleLocationUpdate:newLocation];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    if(delegateCallScheduled) {
        return; //delegate call has already been putted in main loop
    }
    
    if ([self isCancelled])
    {
        [self cancelAll];
        return;
    }
    locationManager.delegate = nil;
    switch (error.code) {
        case kCLErrorDenied:
            if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestFavoriteTransportStationFailed:)]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate nearestFavoriteTransportStationFailed:LocationFailureReasonUserDenied];
                    [self cancelAll];
                });
            } else {
                [self cancelAll];
            }
            delegateCallScheduled = YES;
            break;
        default:
            if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestFavoriteTransportStationFailed:)]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate nearestFavoriteTransportStationFailed:LocationFailureReasonUnknown];
                    [self cancelAll];
                });
            } else {
                [self cancelAll];
            }
            delegateCallScheduled = YES;
            break;
    }
}


/* utilities */

- (void)handleLocationUpdate:(CLLocation*)newLocation {
    if(delegateCallScheduled) {
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
    
    NSLog(@"-> Handling location with accuracy : %lf | desired accuarcy : %lf", newLocation.horizontalAccuracy, locationManager.desiredAccuracy);
    
    if (![self locationIsStillValid:newLocation]) {
        NSLog(@"-> Old location. Ignoring.");
        [ObjectArchiver saveObject:nil forKey:kLastLocationKey andPluginName:@"transport"];
        return;
    }
    
    if (newLocation.horizontalAccuracy <= 0.0) {
        NSLog(@"-> Useless/invalid location (accuracy <= 0.0). Ignoring.");
        return;
    }
    
    if (locationManager.desiredAccuracy == kCLLocationAccuracyBest) {
        NSLog(@"-> Waiting for best accuracy to be achieved : desired accuracy will be switched to 100m in %d seconds.", (3-nbRounds));
        return;
    }
    
    if (newLocation.horizontalAccuracy > locationManager.desiredAccuracy) {
        NSLog(@"-> Location accuracy (%lf) not sufficient, %lf required.", newLocation.horizontalAccuracy, locationManager.desiredAccuracy);
        return;
    }
    
    if (![self locationEnglobesOnlyOneFavoriteStation:newLocation]) {
        if (newLocation.horizontalAccuracy > locationManager.desiredAccuracy) { //second condition to prevent infinite waiting because accuracy cannot be achieved (desiredAccurary is deacreased by timer)
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
    [ObjectArchiver saveObject:newLocation forKey:kLastLocationKey andPluginName:@"transport"];
    [self returnLocationToDelegate:newLocation];
    
}

- (void)returnLocationToDelegate:(CLLocation*)validLocation {
    
    TransportStation* retStation = [[self nearestStationFromLocation:validLocation] retain];
    if ([self isCancelled])
    {
        [retStation release],
        [self cancelAll];
        return;
    }
    
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestFavoriteTransportStationDidReturn:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate nearestFavoriteTransportStationDidReturn:retStation];
            [retStation release];
            [self cancelAll];
        });
    } else {
        [retStation release];
        [self cancelAll];
    }
    delegateCallScheduled = YES;
}

- (BOOL)locationIsStillValid:(CLLocation*)location {
    if (location == nil || location.timestamp == nil) {
        return NO;
    }
    if (location.horizontalAccuracy < 0) { //from documentation, means invalid location
        return NO;
    }
    if (abs((int)[location.timestamp timeIntervalSinceNow]) > kLocationValidity) {
        return NO;
    }
    return YES;
}

//Return YES if location parameter accuracy englobes only 1 favorite station. NO otherwise.
- (BOOL)locationEnglobesOnlyOneFavoriteStation:(CLLocation*)newLocation {
    if (newLocation == nil) {
        return NO;
    }
    CLRegion* userRegion = [[CLRegion alloc] initCircularRegionWithCenter:newLocation.coordinate radius:newLocation.horizontalAccuracy identifier:@"userRegion"]; //+1.0 to be sure this station will be included in containsCoordinate check
    int nbFavStationsInUserRegion = 0;
    for (TransportStation* station in stations) {
        if ([userRegion containsCoordinate:CLLocationCoordinate2DMake(station.latitude/1000000.0, station.longitude/1000000.0)]) {
            nbFavStationsInUserRegion++;
        }
    }
    [userRegion release];
    return (nbFavStationsInUserRegion < 2);
}

//does not take accuracy of location into parameter
- (TransportStation*)nearestStationFromLocation:(CLLocation*)newLocation {
    TransportStation* retStation = nil;
    CLLocationDistance minDistance = DBL_MAX;
    for (TransportStation* station in stations) {
        CLLocation* location = [[CLLocation alloc] initWithLatitude:station.latitude/1000000.0 longitude:station.longitude/1000000.0];
        CLLocationDistance distance = [location distanceFromLocation:newLocation];
        if ([location distanceFromLocation:newLocation] < minDistance) {
            retStation = station;
            minDistance = distance;
        }
        [location release];
    }
    return retStation;
}

- (CLLocationDistance)minimumDistanceBetweenStations {
    CLLocationDistance minDistance = DBL_MAX; //in meters
    
    for (TransportStation* station1 in stations) {
        if (![station1 isKindOfClass:[TransportStation class]]) {
            @throw [NSException exceptionWithName:@"bad station" reason:@"station1 is not of kind TransportStation in NearestFavoriteStationRequest" userInfo:nil];
        }
        for (TransportStation* station2 in stations) {
            if (![station2 isKindOfClass:[TransportStation class]]) {
                @throw [NSException exceptionWithName:@"bad station" reason:@"station2 is not of kind TransportStation in NearestFavoriteStationRequest" userInfo:nil];
            }
            if (station1 != station2) {
                CLLocation* location1 = [[CLLocation alloc] initWithLatitude:station1.latitude/1000000.0 longitude:station1.longitude/1000000.0];
                CLLocation* location2 = [[CLLocation alloc] initWithLatitude:station2.latitude/1000000.0 longitude:station2.longitude/1000000.0];
                CLLocationDistance distance = [location1 distanceFromLocation:location2];
                [location1 release];
                [location2 release];
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
    }
    return minDistance;
}

- (void)dealloc
{
    NSLog(@"-> NearestFavoriteStationRequest released");
    [locationManager release];
    [stations release];
    [checkCancellationAndAdaptDesiredAccuracyTimer release];
    [super dealloc];
}

@end
