//
//  ConnectionSummaryCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "transport.h"

#import "TransportUtils.h"

@interface ConnectionSummaryCell : UITableViewCell {
    UILabel* depLabel;
    UILabel* arrLabel;
    UILabel* durationLabel;
    UILabel* nbChangeLabel;
    UILabel* firstLineLabel;
}

- (id)initWithTransportTrip:(TransportTrip*)trip reuseIdentifier:(NSString*)identifier;
- (void)setTransportTrip:(TransportTrip*)trip;

@end
