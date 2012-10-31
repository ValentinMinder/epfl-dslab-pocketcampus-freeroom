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

- (id)initWithQueryTripResult:(QueryTripsResult*)trip redundantConnections:(NSArray*)redundantConnections {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    if (self) {
        
        loadingState = LoadingStateLoaded;
        
        self.selectionStyle = UITableViewCellSelectionStyleGray;
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        if (trip.connections == nil || trip.connections.count == 0) {//should not happen
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
        
        fromStationLabel = [[UILabel alloc] initWithFrame:CGRectMake(fromStationLabelX, 2.0, 310 - fromStationLabelX, 30.0)];
        fromStationLabel.text = [TransportUtils nicerName:transportStation.name];
        fromStationLabel.font = [UIFont boldSystemFontOfSize:18];
        fromStationLabel.adjustsFontSizeToFitWidth = YES;
        fromStationLabel.textAlignment = UITextAlignmentLeft;
        
        //fromStationLabel.backgroundColor = [UIColor yellowColor];
        
        NSString* timesString = @"";
        UIFont* timeFont;
        CGSize timeReqSize; 
        bottomLabels = [[NSMutableArray arrayWithCapacity:3] retain];
        
        if (redundantConnections != nil && redundantConnections.count > 0) {
            
            timeFont = [UIFont systemFontOfSize:20.0];
            timeReqSize = [@"00:00" sizeWithFont:timeFont];
            
            for (int i = 0; i<redundantConnections.count && i < 3; i++) {
                TransportConnection* connection = [redundantConnections objectAtIndex:i];
                timesString = [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
                if ([timesString isEqualToString:@"Now"]) {
                    UIImageView* busImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"BusTransport"]];
                    busImageView.center = CGPointMake(40.0+(i*(70.0)), 43.0);
                    [self.contentView addSubview:busImageView];
                    [busImageView release];
                } else {
                    UILabel* timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(30.0+(i*(timeReqSize.width+25.0)), 33.0, timeReqSize.width, 20.0)];
                    //timeLabel.backgroundColor = [UIColor grayColor];
                    timeLabel.text = timesString;
                    timeLabel.textAlignment = UITextAlignmentLeft;
                    timeLabel.font = timeFont;
                    [self.contentView addSubview:timeLabel];
                    [bottomLabels addObject:timeLabel];
                    [timeLabel release];
                }
            }
            
            lineNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(247.0, 0, 42.0, 60.0)];
            
            TransportConnection* connection = [redundantConnections objectAtIndex:0]; //all have same line anyway
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
                
                CGRect fromStationlabelFrame = fromStationLabel.frame;
                fromStationlabelFrame.size.width = 210.0;
                fromStationLabel.frame = fromStationlabelFrame;
                
            }
            
            [self.contentView addSubview:lineNameLabel];
            
        } else { //redundantConnections = nil or is empty OR user has disabled "best result" setting
            
            timeFont = [UIFont systemFontOfSize:17.0];
            timeReqSize = [@"00:00 (M1)" sizeWithFont:timeFont];
            
            NSArray* connections = [TransportUtils connectionsWithoutAlreadyLeft:trip.connections];
            
            for (int i = 0; i<connections.count && i < 3; i++) {
                TransportTrip* transportTrip = [connections objectAtIndex:i]; 
                
                TransportConnection* firstConnection = nil;
                
                if (transportTrip.parts == nil || transportTrip.parts.count == 0) {
                    timesString = [TransportUtils automaticHoursMinutesLeftStringForTimestamp:transportTrip.departureTime/1000.0];
                } else {
                    if ([TransportUtils isFeetConnection:[transportTrip.parts objectAtIndex:0]] && transportTrip.parts.count > 1) {
                        firstConnection = [transportTrip.parts objectAtIndex:1];
                    } else {
                        firstConnection = [transportTrip.parts objectAtIndex:0];
                    }
                    timesString = [TransportUtils automaticTimeStringForTimestamp:(firstConnection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
                }

                
                NSString* lineName;
                
                if (!firstConnection) {
                    lineName = @"";
                } else {
                    lineName = [TransportUtils nicerName:firstConnection.line.name];
                }
                
                if ([timesString isEqualToString:@"Now"]) {
                    UIImageView* busImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"BusTransport"]];
                    busImageView.center = CGPointMake(40.0+(i*100.0), 43.0);
                    [self.contentView addSubview:busImageView];
                    [busImageView release];
                    
                    UILabel* lineLabel = [[[UILabel alloc] initWithFrame:CGRectMake(53.0+(i*100.0), 33.0, 30.0, 20.0)] autorelease];
                    lineLabel.text = [NSString stringWithFormat:@"(%@)", lineName];
                    lineLabel.adjustsFontSizeToFitWidth = YES;
                    lineLabel.minimumFontSize = 13.0;
                    lineLabel.textAlignment = UITextAlignmentLeft;
                    lineLabel.font = timeFont;
                    [self.contentView addSubview:lineLabel];
                    [bottomLabels addObject:lineLabel];
                } else {
                    UILabel* timeLabel = [[[UILabel alloc] initWithFrame:CGRectMake(30.0+(i*(timeReqSize.width+10.0)), 33.0, timeReqSize.width, 20.0)] autorelease];
                    timeLabel.text = [NSString stringWithFormat:@"%@ (%@)",timesString, lineName];
                    timeLabel.adjustsFontSizeToFitWidth = YES;
                    timeLabel.minimumFontSize = 13.0;
                    timeLabel.textAlignment = UITextAlignmentLeft;
                    timeLabel.font = timeFont;
                    [self.contentView addSubview:timeLabel];
                    [bottomLabels addObject:timeLabel];
                    
                }
            }
            
            lineNameLabel = nil;
            
        }
        
        
        [self.contentView addSubview:arrowImageView];
        [self.contentView addSubview:fromStationLabel];
        [arrowImageView release];
        
    }
    return self;
}

- (id)initWithDestinationStation:(TransportStation*)destinationStation loadingState:(LoadingState)loadingState_; {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    if (self) {
        bottomLabels = nil;
        lineNameLabel = nil;
        
        loadingState = loadingState_;
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.accessoryType = UITableViewCellAccessoryNone;
        
        transportStation = [destinationStation retain];
        
        UIImageView* arrowImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"DestinationTransportArrow"]];
        arrowImageView.center = CGPointMake(16.0, 30.0);
        
        fromStationLabel = [[UILabel alloc] initWithFrame:CGRectMake(32.0, 15.0, 200.0, 30.0)];
        fromStationLabel.text = [TransportUtils nicerName:destinationStation.name];
        fromStationLabel.font = [UIFont boldSystemFontOfSize:18];
        fromStationLabel.adjustsFontSizeToFitWidth = YES;
        fromStationLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
        fromStationLabel.textAlignment = UITextAlignmentLeft;
        
        [self.contentView addSubview:fromStationLabel];
        
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
    [self setHighlighted:selected animated:animated];
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    if (loadingState != LoadingStateLoaded) {
        return;
    }
    if (highlighted) {
        UIColor* white = [UIColor whiteColor];
        fromStationLabel.textColor = white;
        lineNameLabel.textColor = white;
        for (UILabel* label in bottomLabels) {
            label.textColor = white; //detailTextLabel color
        }
    } else {
        if (animated) {
            [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(unhighlight) userInfo:nil repeats:NO];
        } else {
            [self unhighlight];
        }
    }
}

- (void)unhighlight {
    fromStationLabel.textColor = [UIColor colorWithWhite:0.4 alpha:1.0];
    lineNameLabel.textColor = [UIColor colorWithWhite:0.5 alpha:1.0];
    for (UILabel* label in bottomLabels) {
        label.textColor = [UIColor colorWithRed:0.22 green:0.33 blue:0.53 alpha:1.0]; //detailTextLabel color
        
    }
}

- (void)dealloc
{
    [fromStationLabel release];
    [bottomLabels release];
    [lineNameLabel release];
    [transportStation release];
    [super dealloc];
}

@end
