//
//  ConnectionPartsListViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "transport.h"

typedef enum {
    PartDeparture = 1,
    PartArrival = 2
} PartType;

@interface ConnectionPartsListViewController : UIViewController {
    TransportTrip* trip;
}

- (id)initWithTransportTrip:(TransportTrip*)trip;

@end
