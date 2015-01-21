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


#import "TransportService.h"

#import <CoreLocation/CoreLocation.h>

#import "PCPersistenceManager.h"

#import <float.h>

#import "NSOperationWithDelegate_Protected.h"

#pragma mark - TransportService private interface

NSString* const kTransportUserTransportStationsModifiedNotification = @"kTransportUserTransportStationsModifiedNotification";

@interface NearestUserTransportStationRequest : NSOperationWithDelegate

- (id)initWithTransportStations:(NSOrderedSet*)sations delegate:(id)delegate;

@end

@interface TransportService ()

@property (nonatomic, strong) NSOrderedSet* privateUserTransportStations;
@property (nonatomic, strong) TransportStation* privateUserManualDepartureTransportStation;

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
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(autocomplete:);
    operation.delegateDidReturnSelector = @selector(autocompleteFor:didReturn:);
    operation.delegateDidFailSelector = @selector(autocompleteFailedFor:);
    [operation addObjectArgument:constraint];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

- (void)getLocationsForNames:(NSArray*)names delegate:(id)delegate {
    if (![names isKindOfClass:[NSArray class]]) {
        @throw [NSException exceptionWithName:@"bad names" reason:@"names is either nil or not of class NSArray" userInfo:nil];
    }
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    operation.serviceClientSelector = @selector(getLocationsFromNames:);
    operation.delegateDidReturnSelector = @selector(locationsForNames:didReturn:);
    operation.delegateDidFailSelector = @selector(locationsFailedForNames:);
    [operation addObjectArgument:names];
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
    PCServiceRequest* operation = [[PCServiceRequest alloc] initWithThriftServiceClient:[self thriftServiceClientInstance] service:self delegate:delegate];
    [operation setQueuePriority:priority];
    operation.serviceClientSelector = @selector(getTrips:to:);
    operation.delegateDidReturnSelector = @selector(tripsFrom:to:didReturn:);
    operation.delegateDidFailSelector = @selector(tripsFailedFrom:to:);
    [operation addObjectArgument:from];
    [operation addObjectArgument:to];
    operation.returnType = ReturnTypeObject;
    [self.operationQueue addOperation:operation];
}

#pragma mark - Properties

static NSString* const kUserTransportStationsKey = @"userTransportStations";
static NSString* const kFavoriteTransportStationsOldKey = @"favoriteTransportStations";
static NSString* const kManualDepartureStationKey = @"manualDepartureStation";

- (void)initPersistedProperties {
    if (!self.privateUserTransportStations) {
        self.privateUserTransportStations = (NSOrderedSet*)[PCPersistenceManager objectForKey:kUserTransportStationsKey pluginName:@"transport"];
        if (!self.privateUserTransportStations) {
            NSArray* oldFavStations = (NSArray*)[PCPersistenceManager objectForKey:kFavoriteTransportStationsOldKey pluginName:@"transport"];;
            if (oldFavStations) {
                self.privateUserTransportStations = [NSOrderedSet orderedSetWithArray:oldFavStations]; //storage transition from old methods to new
            }
        }
    }
    if (!self.privateUserManualDepartureTransportStation) {
        self.privateUserManualDepartureTransportStation = (TransportStation*)[PCPersistenceManager objectForKey:kManualDepartureStationKey pluginName:@"transport"];
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
    [PCPersistenceManager saveObject:self.privateUserTransportStations forKey:kUserTransportStationsKey pluginName:@"transport"];
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
    [PCPersistenceManager saveObject:self.privateUserManualDepartureTransportStation forKey:kManualDepartureStationKey pluginName:@"transport"];
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

@interface NearestUserTransportStationRequest ()<CLLocationManagerDelegate, UIAlertViewDelegate>

- (id)initWithTransportStations:(NSOrderedSet*)sations delegate:(id)delegate;

@property (nonatomic, strong) NSOrderedSet* stations;
@property (nonatomic, strong) NSTimer* checkCancellationAndAdaptDesiredAccuracyTimer;
@property (nonatomic, strong) CLLocationManager* locationManager;
@property (nonatomic, strong) UIAlertView* authorizationBufferAlertView;
@property (nonatomic, copy) void (^authorizationBufferAlertViewCompletionBlock)(BOOL userAccepted);
@property (nonatomic) BOOL blockedByAuthStatus;
@property (nonatomic) BOOL delegateCallScheduled;
@property (nonatomic) int nbRounds;

@end

@implementation NearestUserTransportStationRequest

static NSTimeInterval const kLocationValidityInterval = 60.0; //nb seconds a cached location can be used / is considered that user has not moved
static NSString* const kLastLocationKey = @"lastLocation";

static NSInteger const kAuthorizationErrorCodeDeniedBufferAlert = 20;
static NSInteger const kAuthorizationErrorCodeDeniedSystem = 21;

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
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusRestricted) {
        CLSNSLog(@"-> User has denied system access to location, will return error to delegate.");
        [self locationManager:self.locationManager didFailWithError:[NSError errorWithDomain:@"" code:kAuthorizationErrorCodeDeniedSystem userInfo:nil]];
        return;
    }
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined) {
        CLSNSLog(@"-> Showing location authorization buffer alert...");
        
        __weak __typeof(self) welf = self;
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            [self showLocationBufferAlertWithCompletion:^(BOOL userAccepted) {
                if (userAccepted) {
                    CLSNSLog(@"-> User accepted buffert alert! Waiting for user to accept access to location...");
                    welf.blockedByAuthStatus = YES;
                    [welf.locationManager startUpdatingLocation];
                    if ([welf.locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]) {
                        //required in iOS 8 and above
                        [welf.locationManager requestWhenInUseAuthorization];
                    }
                } else {
                    CLSNSLog(@"-> User denied buffert alert. Will return LocationFailureReasonUserDeniedBufferAlert to delegate.");
                    [welf locationManager:welf.locationManager didFailWithError:[NSError errorWithDomain:@"" code:kAuthorizationErrorCodeDeniedBufferAlert userInfo:nil]];
                }
            }];
        });
        return; //self will be called (see delegate method) by CLLocationManager when user has accepted or rejected access to location
    }
    
    [self.locationManager startUpdatingLocation];
    
    CLLocation* lastLocation = (CLLocation*)[PCPersistenceManager objectForKey:kLastLocationKey pluginName:@"transport"];
    if ([self locationIsStillValid:lastLocation] && [self locationEnglobesOnlyOneStation:lastLocation]) {
        CLSNSLog(@"-> Last location still valid (%@), will return to delegate.", lastLocation.timestamp);
        [self returnLocationToDelegate:lastLocation];
        return;
    }
    
    self.blockedByAuthStatus = NO;

    self.locationManager.desiredAccuracy = [self minimumDistanceBetweenStations] > 1000 ? kCLLocationAccuracyHundredMeters : kCLLocationAccuracyBest; //improves reliability
    self.locationManager.distanceFilter =  kCLDistanceFilterNone;
    
    dispatch_async(dispatch_get_main_queue(), ^{ //timer must be scheduled on other thread not be blocked
        self.checkCancellationAndAdaptDesiredAccuracyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(checkCancellationAndAdaptDesiredAccuracy) userInfo:nil repeats:YES];
    });
}

- (BOOL)isConcurrent {
    return YES;
}

#pragma mark - Location buffer alert

- (void)showLocationBufferAlertWithCompletion:(void (^)(BOOL userAccepted))completion {
    self.authorizationBufferAlertViewCompletionBlock = completion;
    if (self.authorizationBufferAlertView) {
        return;
    }
    self.authorizationBufferAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedStringFromTable(@"LocationBufferAlertTitle", @"TransportPlugin", nil) message:NSLocalizedStringFromTable(@"LocationBufferAlertMessage", @"TransportPlugin", nil) delegate:self cancelButtonTitle:NSLocalizedStringFromTable(@"LocationBufferAlertRejectButtonTitle", @"TransportPlugin", nil) otherButtonTitles:NSLocalizedStringFromTable(@"LocationBufferAlertAcceptButtonTitle", @"TransportPlugin", nil), nil];
    [self.authorizationBufferAlertView show];
}

#pragma mark UIAlertViewDelegate

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (self.authorizationBufferAlertViewCompletionBlock) {
        self.authorizationBufferAlertViewCompletionBlock(buttonIndex != alertView.cancelButtonIndex);
    }
    self.authorizationBufferAlertView = nil;
    self.authorizationBufferAlertViewCompletionBlock = nil;
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
    self.authorizationBufferAlertViewCompletionBlock = nil;
    [self.authorizationBufferAlertView dismissWithClickedButtonIndex:self.authorizationBufferAlertView.cancelButtonIndex animated:NO];
    self.locationManager.delegate = nil;
    [self.locationManager stopUpdatingLocation];
    self.delegate = nil;
    self.executing = NO;
    self.finished = YES;
}

#pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    if (self.blockedByAuthStatus) {
        CLSNSLog(@"-> User has made a decision for location access (new CLAuthorizationStatus: %d). Restarting the request.", status);
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

    LocationFailureReason failureReason = 0;
    switch (error.code) {
        case kAuthorizationErrorCodeDeniedBufferAlert:
            failureReason = LocationFailureReasonUserDeniedBufferAlert;
            break;
        case kAuthorizationErrorCodeDeniedSystem:
            failureReason = LocationFailureReasonUserDeniedSystem;
            break;
        default:
            failureReason = LocationFailureReasonUnknown;
            break;
    }
    
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(nearestUserTransportStationFailed:)]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.delegate nearestUserTransportStationFailed:failureReason];
            [self cancelAll];
        });
    } else {
        [self cancelAll];
    }
    self.delegateCallScheduled = YES;
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
    
    CLAuthorizationStatus authStatus = [CLLocationManager authorizationStatus];
    if ([PCUtils isOSVersionSmallerThan:8.0]) {
        if (authStatus != kCLAuthorizationStatusAuthorized) {
            CLSLog(@"-> Will not handle location update because status is not authorized.");
            return;
        }
    } else {
        if (authStatus != kCLAuthorizationStatusAuthorizedWhenInUse && authStatus != kCLAuthorizationStatusAuthorizedAlways) {
            CLSLog(@"-> Will not handle location update because status is not authorized.");
            return;
        }
    }
    
    CLSLog(@"-> Handling location with accuracy : %lf | desired accuarcy : %lf", newLocation.horizontalAccuracy, self.locationManager.desiredAccuracy);
    
    if (![self locationIsStillValid:newLocation]) {
        CLSLog(@"-> Old location. Ignoring.");
        [PCPersistenceManager saveObject:nil forKey:kLastLocationKey pluginName:@"transport"];
        return;
    }
    
    if (newLocation.horizontalAccuracy <= 0.0) {
        CLSLog(@"-> Useless/invalid location (accuracy <= 0.0). Ignoring.");
        return;
    }
    
    if (self.locationManager.desiredAccuracy == kCLLocationAccuracyBest) {
        CLSLog(@"-> Waiting for best accuracy to be achieved : desired accuracy will be switched to 100m in %d seconds.", (3-self.nbRounds));
        return;
    }
    
    if (newLocation.horizontalAccuracy > self.locationManager.desiredAccuracy) {
        CLSLog(@"-> Location accuracy (%lf) not sufficient, %lf required.", newLocation.horizontalAccuracy, self.locationManager.desiredAccuracy);
        return;
    }
    
    if (![self locationEnglobesOnlyOneStation:newLocation]) {
        if (newLocation.horizontalAccuracy > self.locationManager.desiredAccuracy) { //second condition to prevent infinite waiting because accuracy cannot be achieved (desiredAccurary is deacreased by timer)
            CLSLog(@"-> Location not accurate enough. Ignoring.");
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
    [PCPersistenceManager saveObject:newLocation forKey:kLastLocationKey pluginName:@"transport"];
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
