//
//  TransportTripCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 27.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TransportTrip;

@interface TransportTripCell : UITableViewCell

- (id)initWithReuseIdentifier:(NSString*)reuseIdentifier;

@property (nonatomic, strong) TransportTrip* trip;

@end
