//
//  ConnectionPartsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 11.04.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "ConnectionPartsListViewController.h"

#import "TransportUtils.h"

@implementation ConnectionPartsListViewController

- (id)initWithTransportTrip:(TransportTrip*)trip_;
{
    self = [super initWithNibName:@"ConnectionPartsListView" bundle:nil];
    if (self) {
        trip = [trip_ retain];
        NSString* from = [TransportUtils nicerName:trip.from.name];
        NSString* to = [TransportUtils nicerName:trip.to.name];
        self.title = [NSString stringWithFormat:@"%@ ➔ %@", from, to];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    UIScrollView* scrollView = [[UIScrollView alloc] initWithFrame:self.view.frame];
    
    CGFloat currentY = 30.0; //start with amrgin from top
    
    for (TransportConnection* connection in trip.parts) {
        
        if ([TransportUtils isFeetConnection:connection]) {
            currentY += 6.0;
            [scrollView addSubview:[self feetConnectionStartingY:currentY]];
            //currentY += 4.0;
            continue;
        }
        
        UIView* departure = [self partViewForTransportConnection:connection part:PartDeparture startingY:currentY];
        currentY += departure.frame.size.height + 0.0;
        UIView* line = nil;
        
        NSString* lineName = @"|";
        if (connection.line != nil && connection.line.name != nil) {
            //lineName = [TransportUtils nicerName:connection.line.name];
            lineName = [TransportUtils nicerName:connection.line.name];
        }
        line = [self longLineViewWithTitle:lineName startingY:currentY];
        
        currentY += line.frame.size.height + 0.0;
        UIView* arrival = [self partViewForTransportConnection:connection part:PartArrival startingY:currentY];
        currentY += arrival.frame.size.height + 15.0;
        
        [scrollView addSubview:departure];
        [scrollView addSubview:line];
        [scrollView addSubview:arrival];
    }
    
    scrollView.showsVerticalScrollIndicator = NO;
    
    if (currentY+20.0 <= self.view.frame.size.height) {
        scrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height+1.0); //so that you can move even if it's smaller
    } else {
        scrollView.contentSize = CGSizeMake(self.view.frame.size.width, currentY+20.0);
    }
    
    
    
    [self.view addSubview:scrollView];
    [scrollView release];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

/* drawing utilities */

- (UIView*)partViewForTransportConnection:(TransportConnection*)connection part:(PartType)partType startingY:(CGFloat)startingY {
    if(connection == nil) {
        return [[[UIView alloc] initWithFrame:CGRectMake(0, startingY, 0, 0)] autorelease];
    }
    
    UIView* container = [[UIView alloc] initWithFrame:CGRectMake(10.0, startingY, 300.0, 25.0)];
    
    UILabel* timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 0.0, 80.0, container.frame.size.height)];
    timeLabel.backgroundColor = [UIColor clearColor];
    timeLabel.textColor = [UIColor whiteColor];
    timeLabel.textAlignment = UITextAlignmentRight;
    timeLabel.shadowColor = [UIColor colorWithWhite:0.0 alpha:0.5];
    timeLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    
    UILabel* stationLabel = [[UILabel alloc] initWithFrame:CGRectMake(100.0, 0.0, 200.0, container.frame.size.height)];
    stationLabel.backgroundColor = [UIColor clearColor];
    stationLabel.textColor = [UIColor whiteColor];
    stationLabel.textAlignment = UITextAlignmentLeft;
    stationLabel.adjustsFontSizeToFitWidth = YES;
    stationLabel.shadowColor = [UIColor colorWithWhite:0.0 alpha:0.5];
    stationLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    
    if (partType == PartDeparture && connection.departureTime != 0) {
        timeLabel.text = [TransportUtils hourMinutesStringForTimestamp:connection.departureTime/1000.0];
    } else if(partType == PartArrival && connection.arrivalTime != 0) {
        timeLabel.text = [TransportUtils hourMinutesStringForTimestamp:connection.arrivalTime/1000.0];
    } else {
        /*stationLabel.frame = CGRectMake(0.0, 0.0, container.frame.size.width, container.frame.size.height); //center station name as no info to give on time
        stationLabel.textAlignment = UITextAlignmentCenter;*/
        //nothing
    }

    
    if (partType == PartDeparture && connection.departure != nil && connection.departure.name != nil) {
        stationLabel.text = [TransportUtils nicerName:connection.departure.name];
    } else if(partType == PartArrival && connection.arrival != nil && connection.arrival.name != nil) {
        stationLabel.text = [TransportUtils nicerName:connection.arrival.name];
    } else {
        //too bad...
    }
    
    [container addSubview:timeLabel];
    [container addSubview:stationLabel];
    [timeLabel release];
    [stationLabel release];
    
    return [container autorelease];
}

- (UIView*)longLineViewWithTitle:(NSString*)title startingY:(CGFloat)startingY {
    UIView* container = [[UIView alloc] initWithFrame:CGRectMake(0.0, startingY, 320.0, 90.0)];
    container.backgroundColor = [UIColor clearColor];
    UIView* line1 = [[UIView alloc] initWithFrame:CGRectMake(100.0, 0.0, 2.0, 32.0)];
    line1.backgroundColor = [UIColor whiteColor];
    
    UILabel* titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(67.0, line1.frame.origin.y+line1.frame.size.height, 70.0, 26.0)];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:13.0];
    titleLabel.textAlignment = UITextAlignmentCenter;
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.text = title;
    titleLabel.shadowColor = [UIColor colorWithWhite:0.0 alpha:0.3];
    titleLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    
    UIView* line2 = [[UIView alloc] initWithFrame:CGRectMake(100.0, titleLabel.frame.origin.y+titleLabel.frame.size.height, 2.0, 32.0)];
    line2.backgroundColor = [UIColor whiteColor];
    
    [container addSubview:line1];
    [container addSubview:titleLabel];
    [container addSubview:line2];
    [line1 release];
    [titleLabel release];
    [line2 release];
    
    return [container autorelease];
}

- (UIView*)feetConnectionStartingY:(CGFloat)startingY {
    UIImageView* person = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"FeetConnection"]];
    person.center = CGPointMake(100.0, startingY-(person.frame.size.height/2.0));
    return [person autorelease];
}

- (void)dealloc
{
    [trip release];
    [super dealloc];
}

@end
