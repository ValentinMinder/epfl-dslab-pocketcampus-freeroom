//
//  NextDeparturesCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.03.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "NextDeparturesCell.h"

#import "TransportUtils.h"

#import "PCValues.h"

@implementation NextDeparturesCell

@synthesize transportStation, loadingState;

- (id)initWithQueryTripResult:(QueryTripsResult*)trip {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    if (self) {
        
        loadingState = LoadingStateLoaded;
        
        self.selectionStyle = UITableViewCellSelectionStyleGray;
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        if (trip.connections == nil) {
            return self;
        }
        
        transportStation = [trip.to retain];
        
        /* TEST */
        
        /*
        NSLog(@"---------------------- FROM : %@ TO : %@ ----------------------",trip.from.name, trip.to.name);
        
        NSArray* mess = [TransportUtils nextRedundantDeparturesFromMessyResult:trip];
        int i = 0;
        for(TransportConnection* connection in mess) {
            NSLog(@"CONNECTION %d : %@ from %@ (%@)", i, [TransportUtils nicerName:connection.line.name], connection.departure.name, [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime/1000.0) maxIntervalForMinutesLeftString:15.0]);
            i++;
        }
        NSLog(@"");
        for (TransportTrip* transTrip in trip.connections) {
            if (transTrip.parts == nil) {
                continue;
            }
            NSLog(@"TRIP : from %@ (%@) to %@ (%@):", transTrip.from.name, [TransportUtils automaticTimeStringForTimestamp:(transTrip.departureTime/1000.0) maxIntervalForMinutesLeftString:15.0], transTrip.to.name, [TransportUtils automaticTimeStringForTimestamp:(transTrip.arrivalTime/1000.0) maxIntervalForMinutesLeftString:15.0]);
            for (TransportConnection* connection in transTrip.parts) {
                NSLog(@"    Part : from %@ (%@) to %@ (%@)", connection.departure.name, [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime/1000.0) maxIntervalForMinutesLeftString:15.0], connection.arrival.name, [TransportUtils automaticTimeStringForTimestamp:(connection.arrivalTime/1000.0) maxIntervalForMinutesLeftString:15.0]);
                if (connection.line == nil) {
                    continue;
                }
                NSLog(@"    Line : %@", [TransportUtils nicerName:connection.line.name]);
            }
            
        }
        */
        /* END OF TEST */
        
        UIImageView* arrowImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"DestinationTransportArrow"]];
        arrowImageView.center = CGPointMake(16.0, 30.0);
        
        CGFloat fromStationLabelX = arrowImageView.frame.size.width+6.0;
        
        UILabel* fromStationLabel = [[UILabel alloc] initWithFrame:CGRectMake(fromStationLabelX, 2.0, 310 - fromStationLabelX, 30.0)];
        fromStationLabel.text = [TransportUtils nicerName:trip.to.name];
        fromStationLabel.font = [UIFont boldSystemFontOfSize:18];
        fromStationLabel.adjustsFontSizeToFitWidth = YES;
        fromStationLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
        fromStationLabel.textAlignment = UITextAlignmentLeft;
        
        //fromStationLabel.backgroundColor = [UIColor yellowColor];
        
        
        NSArray* connections = [TransportUtils nextRedundantDeparturesFromMessyResult:trip];
        
        
        NSString* timesString = @"";
        UIFont* timeFont = [UIFont systemFontOfSize:20.0];
        CGSize timeReqSize = [@"00:00" sizeWithFont:timeFont];
        for (int i = 0; i<connections.count && i < 3; i++) {
            TransportConnection* connection = [connections objectAtIndex:i];
            timesString = [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            if ([timesString isEqualToString:@"Now"]) {
                UIImageView* busImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"BusTransport"]];
                busImageView.center = CGPointMake((fromStationLabelX+8.0)+(i*(70.0)), 43.0);
                [self.contentView addSubview:busImageView];
                [busImageView release];
            } else {
                UILabel* timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(fromStationLabelX+(i*(timeReqSize.width+25.0)), 33.0, timeReqSize.width, 20.0)];
                //timeLabel.backgroundColor = [UIColor grayColor];
                timeLabel.text = timesString;
                timeLabel.textAlignment = UITextAlignmentLeft;
                timeLabel.textColor = [UIColor colorWithRed:0.22 green:0.33 blue:0.53 alpha:1.0]; //detailTextLabel color
                timeLabel.font = timeFont;
                [self.contentView addSubview:timeLabel];
                [timeLabel release];
            }
        }
        
        UILabel* lineNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(247.0, 0, 42.0, 60.0)];
        
        if (connections.count == 0) {
            lineNameLabel.frame = CGRectMake(0, 0, 0, 0);
            NSString* complexTripString = NSLocalizedStringFromTable(@"CellMultiplePossibilitiesTrip", @"TransportPlugin", nil);
            UIFont* font = [UIFont systemFontOfSize:16.0];
            CGSize reqSize = [complexTripString sizeWithFont:font];
            UILabel* complexTripLabel = [[UILabel alloc] initWithFrame:CGRectMake(fromStationLabelX, 30.0, reqSize.width, reqSize.height)];
            complexTripLabel.adjustsFontSizeToFitWidth = YES; //should not be necessary
            complexTripLabel.text = complexTripString;
            complexTripLabel.textAlignment = UITextAlignmentLeft;
            complexTripLabel.font = font;
            complexTripLabel.textColor = [UIColor colorWithWhite:0.5 alpha:1.0];
            [self.contentView addSubview:complexTripLabel];
            [complexTripLabel release];
        } else {
            TransportConnection* connection = [connections objectAtIndex:0]; //all have same line anyway
            NSString* lineName = [TransportUtils nicerName:connection.line.name];
            if (connection.line.name == nil) {
                lineNameLabel.frame = CGRectMake(0,0,0,0);
            } else if (lineName.length > 3) {
                lineNameLabel.frame = CGRectMake(0,0,0,0);
            } else {
                //lineNameLabel.backgroundColor = [UIColor grayColor];
                lineNameLabel.text = lineName;
                lineNameLabel.font = [UIFont systemFontOfSize:30];
                lineNameLabel.adjustsFontSizeToFitWidth = YES;
                lineNameLabel.textAlignment = UITextAlignmentCenter;
                lineNameLabel.textColor = [UIColor colorWithWhite:0.5 alpha:1.0];
                
                CGRect fromStationlabelFrame = fromStationLabel.frame;
                fromStationlabelFrame.size.width = 210.0;
                fromStationLabel.frame = fromStationlabelFrame;
                
            }
        }
        
        [self.contentView addSubview:arrowImageView];
        [self.contentView addSubview:fromStationLabel];
        [self.contentView addSubview:lineNameLabel];
        [arrowImageView release];
        [fromStationLabel release];
        [lineNameLabel release];
        
    }
    return self;
}

- (id)initWithDestinationStation:(TransportStation*)destinationStation loadingState:(LoadingState)loadingState_; {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    if (self) {
        
        loadingState = loadingState_;
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.accessoryType = UITableViewCellAccessoryNone;
        
        transportStation = [destinationStation retain];
        
        UIImageView* arrowImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"DestinationTransportArrow"]];
        arrowImageView.center = CGPointMake(16.0, 30.0);
        
        CGFloat fromStationLabelX = arrowImageView.frame.size.width+6.0;
        
        UILabel* fromStationLabel = [[UILabel alloc] initWithFrame:CGRectMake(fromStationLabelX, arrowImageView.frame.origin.y-3.0, 200.0, 30.0)];
        fromStationLabel.text = [TransportUtils nicerName:destinationStation.name];
        fromStationLabel.font = [UIFont boldSystemFontOfSize:18];
        fromStationLabel.adjustsFontSizeToFitWidth = YES;
        fromStationLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
        fromStationLabel.textAlignment = UITextAlignmentLeft;
        
        [self.contentView addSubview:fromStationLabel];
        [fromStationLabel release];
        
        [self.contentView addSubview:arrowImageView];
        [arrowImageView release];
        
        switch (loadingState) {
            case LoadingStateLoading:
                {
                    UIActivityIndicatorView* activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
                    activityIndicator.center = CGPointMake(290.0, 30.0);
                    [self.contentView addSubview:activityIndicator];
                    [activityIndicator startAnimating];
                    [activityIndicator release];
                }
                break;
            case LoadingStateError:
                {
                    UILabel* errorLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.contentView.frame.size.width - 80.0, 15.0, 70, 30.0)];
                    errorLabel.textColor = [UIColor redColor];
                    errorLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
                    errorLabel.font = [UIFont boldSystemFontOfSize:16.0];
                    errorLabel.textAlignment = UITextAlignmentCenter;
                    [self.contentView addSubview:errorLabel];
                    [errorLabel release];
                }
                break;
            default:
                break;
        }
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)dealloc
{
    [transportStation release];
    [super dealloc];
}

@end
