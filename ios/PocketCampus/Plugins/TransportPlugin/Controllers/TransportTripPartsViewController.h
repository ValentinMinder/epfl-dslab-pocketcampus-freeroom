//
//  TransportTripPartsViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

@class TransportTrip;

@interface TransportTripPartsViewController : UIViewController

- (id)initWithTransportTrip:(TransportTrip*)trip;

@property (nonatomic, readonly, strong) TransportTrip* trip;

@end
