//
//  ConnectionSummaryCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "ConnectionSummaryCell.h"

#import "PCValues.h"

@implementation ConnectionSummaryCell


- (id)initWithTransportTrip:(TransportTrip*)trip reuseIdentifier:(NSString*)identifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    if (self) {
        
        if (trip == nil || ![trip isKindOfClass:[TransportTrip class]]) {
            @throw [NSException exceptionWithName:@"bad trip argument in initWithTransportTrip" reason:@"trip is not kind of class TransportTrip" userInfo:nil];
        }
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        self.backgroundColor = [UIColor clearColor];
        
        depLabel = [[UILabel alloc] initWithFrame:CGRectMake(2.0, 2.0, 50.0, 35.0)];
        depLabel.backgroundColor = [UIColor clearColor];
        depLabel.textAlignment = UITextAlignmentCenter;
        depLabel.textColor = [UIColor whiteColor];
        depLabel.font = [UIFont systemFontOfSize:16.0];
        
        TransportConnection* firstConnection;
        
        if (trip.parts == nil || trip.parts.count == 0) {
            depLabel.text = [TransportUtils hourMinutesStringForTimestamp:trip.departureTime/1000.0];
        } else if ([TransportUtils isFeetConnection:[trip.parts objectAtIndex:0]] && trip.parts.count > 1) {
            firstConnection = [trip.parts objectAtIndex:1];
            depLabel.text = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
        } else {
            firstConnection = [trip.parts objectAtIndex:0];
            depLabel.text = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
        }
        
        [self.contentView addSubview:depLabel];
        
        arrLabel = [[UILabel alloc] initWithFrame:CGRectMake(55.0, 2.0, 50.0, 35.0)];
        arrLabel.backgroundColor = [UIColor clearColor];
        arrLabel.text = [TransportUtils hourMinutesStringForTimestamp:trip.arrivalTime/1000.0];
        arrLabel.textAlignment = UITextAlignmentCenter;
        arrLabel.textColor = [UIColor whiteColor];
        arrLabel.font = [UIFont systemFontOfSize:16.0];
        [self.contentView addSubview:arrLabel];
        
        
        durationLabel = [[UILabel alloc] initWithFrame:CGRectMake(115.0, 2.0, 50.0, 35.0)];
        durationLabel.backgroundColor = [UIColor clearColor];
        durationLabel.text = [TransportUtils durationgStringForInterval:((trip.arrivalTime/1000.0) - (trip.departureTime/1000.0))];
        durationLabel.textAlignment = UITextAlignmentCenter;
        durationLabel.textColor = [UIColor whiteColor];
        durationLabel.font = [UIFont systemFontOfSize:16.0];
        [self.contentView addSubview:durationLabel];    
        
        nbChangeLabel = [[UILabel alloc] initWithFrame:CGRectMake(180.0, 2.0, 35.0, 35.0)];
        nbChangeLabel.backgroundColor = [UIColor clearColor];
        nbChangeLabel.text = [NSString stringWithFormat:@"%d", [TransportUtils numberOfChangeForTrip:trip]];
        nbChangeLabel.textAlignment = UITextAlignmentCenter;
        nbChangeLabel.textColor = [UIColor whiteColor];
        nbChangeLabel.font = [UIFont systemFontOfSize:16.0];
        [self.contentView addSubview:nbChangeLabel];
        
        
        firstLineLabel = [[UILabel alloc] initWithFrame:CGRectMake(230.0, 2.0, 50.0, 35.0)];
        firstLineLabel.backgroundColor = [UIColor clearColor];
        firstLineLabel.text = [TransportUtils firstLineNameForTrip:trip];
        firstLineLabel.textAlignment = UITextAlignmentCenter;
        firstLineLabel.textColor = [UIColor whiteColor];
        firstLineLabel.font = [UIFont systemFontOfSize:16.0];
        [self.contentView addSubview:firstLineLabel];
        
        
        [depLabel release];
        [arrLabel release];
        [durationLabel release];
        [nbChangeLabel release];
        [firstLineLabel release];
        
    }
    return self;
}

- (void)setTransportTrip:(TransportTrip*)trip {
    if (trip == nil || ![trip isKindOfClass:[TransportTrip class]]) {
        @throw [NSException exceptionWithName:@"bad trip argument in initWithTransportTrip" reason:@"trip is not kind of class TransportTrip" userInfo:nil];
    }
    
    TransportConnection* firstConnection;
    
    if (trip.parts == nil || trip.parts.count == 0) {
        depLabel.text = [TransportUtils hourMinutesStringForTimestamp:trip.departureTime/1000.0];
    } else if ([TransportUtils isFeetConnection:[trip.parts objectAtIndex:0]] && trip.parts.count > 1) {
        firstConnection = [trip.parts objectAtIndex:1];
        depLabel.text = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
    } else {
        firstConnection = [trip.parts objectAtIndex:0];
        depLabel.text = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
    }
    
    arrLabel.text = [TransportUtils hourMinutesStringForTimestamp:trip.arrivalTime/1000.0];
    durationLabel.text = [TransportUtils durationgStringForInterval:((trip.arrivalTime/1000.0) - (trip.departureTime/1000.0))];
    nbChangeLabel.text = [NSString stringWithFormat:@"%d", [TransportUtils numberOfChangeForTrip:trip]];
    firstLineLabel.text = [TransportUtils firstLineNameForTrip:trip];
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    [self highlight:selected];
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    [super setHighlighted:highlighted animated:animated];
    // Configure the view for the highlighted state
    [self highlight:highlighted];
    
}

- (void)highlight:(BOOL)highlight {
    
    if (highlight) {
        self.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.2];
    } else {
        self.backgroundColor = [UIColor clearColor];
    }
    
}


@end
