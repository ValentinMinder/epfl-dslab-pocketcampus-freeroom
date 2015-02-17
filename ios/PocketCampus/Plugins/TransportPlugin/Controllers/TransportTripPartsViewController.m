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




//  Created by Loïc Gardiol on 11.04.12.


#import "TransportTripPartsViewController.h"

#import "TransportService.h"

#import "TransportUtils.h"

typedef enum {
    PartTypeDeparture = 1,
    PartTypeArrival = 2
} PartType;

@interface TransportTripPartsViewController ()

@property (nonatomic, strong) IBOutlet UIScrollView* scrollView;

@property (nonatomic, readwrite, strong) TransportTrip* trip;

@end

@implementation TransportTripPartsViewController

#pragma mark - Init

- (id)initWithTransportTrip:(TransportTrip*)trip;
{
    [PCUtils throwExceptionIfObject:trip notKindOfClass:[TransportTrip class]];
    self = [super initWithNibName:@"TransportTripPartsView" bundle:nil];
    if (self) {
        self.gaiScreenName = @"/transport/trips/parts";
        self.trip = trip;
        self.title = [NSString stringWithFormat:@"%@ ➝ %@", self.trip.from.shortName, self.trip.to.shortName];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //self.scrollView.contentInset = [PCUtils edgeInsetsForViewController:self];
    self.scrollView.scrollIndicatorInsets = self.scrollView.contentInset;
    
    CGFloat currentY = 30.0; //start with margin from top
    
    for (TransportConnection* connection in self.trip.parts) {
        
        if (connection.isFeetConnection) {
            currentY += 6.0;
            [self.scrollView addSubview:[self feetConnectionViewWithStartingY:currentY]];
            //currentY += 4.0;
            continue;
        }
        
        UIView* departure = [self partViewForTransportConnection:connection part:PartTypeDeparture startingY:currentY];
        currentY += departure.frame.size.height + 0.0;
        UIView* line = nil;
        
        NSString* lineName = NSLocalizedStringFromTable(@"(unknown)", @"TransportPlugin", nil);
        if (connection.line != nil && connection.line.name != nil) {
            lineName = connection.line.shortName;
        }
        line = [self longLineViewWithTitle:lineName startingY:currentY];
        
        currentY += line.frame.size.height + 0.0;
        UIView* arrival = [self partViewForTransportConnection:connection part:PartTypeArrival startingY:currentY];
        currentY += arrival.frame.size.height + 15.0;
        
        [self.scrollView addSubview:departure];
        [self.scrollView addSubview:line];
        [self.scrollView addSubview:arrival];
    }
    
    self.scrollView.showsVerticalScrollIndicator = NO;
    
    if (currentY+20.0 <= self.view.frame.size.height) {
        self.scrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height+1.0); //so that you can move even if it's smaller
    } else {
        self.scrollView.contentSize = CGSizeMake(self.view.frame.size.width, currentY+20.0);
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (NSUInteger)supportedInterfaceOrientations //iOS 6
{
    return UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Drawing utilities

- (UIColor*)elementsColor1 {
    return [UIColor colorWithWhite:0.2 alpha:1.0];
}

- (UIView*)partViewForTransportConnection:(TransportConnection*)connection part:(PartType)partType startingY:(CGFloat)startingY {
    if(!connection) {
        return [[UIView alloc] initWithFrame:CGRectMake(0, startingY, 0, 0)];
    }
    
    if (partType == 0) {
        @throw [NSException exceptionWithName:@"bad partType" reason:@"partType cannot be 0 in partViewForTransportConnection:part:startingY:" userInfo:nil];
    }
    
    UIView* container = [[UIView alloc] initWithFrame:CGRectMake(10.0, startingY, 300.0, 25.0)];
    
    UILabel* timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0.0, 0.0, 64.0, container.frame.size.height)];
    timeLabel.backgroundColor = [UIColor clearColor];
    timeLabel.textColor = [self elementsColor1];
    timeLabel.textAlignment = NSTextAlignmentRight;
    
    UILabel* platformLabel = [[UILabel alloc] initWithFrame:CGRectMake(71.0, 0.0, 40.0, 25.0)];
    platformLabel.backgroundColor = [UIColor clearColor];
    platformLabel.textColor = [self elementsColor1];
    platformLabel.font = [UIFont systemFontOfSize:13.0];
    platformLabel.adjustsFontSizeToFitWidth = YES;
    platformLabel.textAlignment = NSTextAlignmentCenter;
    
    UILabel* stationLabel = [[UILabel alloc] initWithFrame:CGRectMake(120.0, 0.0, 185.0, container.frame.size.height)];
    stationLabel.backgroundColor = [UIColor clearColor];
    stationLabel.textColor = [self elementsColor1];
    stationLabel.textAlignment = NSTextAlignmentLeft;
    stationLabel.adjustsFontSizeToFitWidth = YES;
    
    platformLabel.text = @"•";
    
    if (partType == PartTypeDeparture && connection.departureTime != 0) {
        timeLabel.text = [TransportUtils hourMinutesStringForTimestamp:connection.departureTime/1000.0];
        if (connection.departurePosition) {
            platformLabel.text = [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"PlatformShort", @"TransportPlugin", nil), connection.departurePosition];
        }
    } else if(partType == PartTypeArrival && connection.arrivalTime != 0) {
        timeLabel.text = [TransportUtils hourMinutesStringForTimestamp:connection.arrivalTime/1000.0];
        if (connection.arrivalPosition) {
            platformLabel.text = [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"PlatformShort", @"TransportPlugin", nil), connection.arrivalPosition];
        }
    } else {
        /*stationLabel.frame = CGRectMake(0.0, 0.0, container.frame.size.width, container.frame.size.height); //center station name as no info to give on time
        stationLabel.textAlignment = UITextAlignmentCenter;*/
        //nothing
    }

    
    if (partType == PartTypeDeparture && connection.departure != nil && connection.departure.name != nil) {
        stationLabel.text = connection.departure.shortName;
    } else if(partType == PartTypeArrival && connection.arrival != nil && connection.arrival.name != nil) {
        stationLabel.text = connection.arrival.shortName;
    } else {
        //cannot happen
    }
    
    [container addSubview:timeLabel];
    [container addSubview:platformLabel];
    [container addSubview:stationLabel];
    
    return container;
}

- (UIView*)longLineViewWithTitle:(NSString*)title startingY:(CGFloat)startingY {
    UIView* container = [[UIView alloc] initWithFrame:CGRectMake(0.0, startingY, 320.0, 90.0)];
    container.backgroundColor = [UIColor clearColor];
    UIView* line1 = [[UIView alloc] initWithFrame:CGRectMake(100.0, 0.0, 1.0, 32.0)];
    line1.backgroundColor = [self elementsColor1];
    
    UILabel* titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(67.0, line1.frame.origin.y+line1.frame.size.height, 70.0, 26.0)];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont systemFontOfSize:13.0];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.textColor = [self elementsColor1];
    titleLabel.text = title;
    
    UIView* line2 = [[UIView alloc] initWithFrame:CGRectMake(100.0, titleLabel.frame.origin.y+titleLabel.frame.size.height, 1.0, 32.0)];
    line2.backgroundColor = [self elementsColor1];
    
    [container addSubview:line1];
    [container addSubview:titleLabel];
    [container addSubview:line2];
    
    return container;
}

- (UIView*)feetConnectionViewWithStartingY:(CGFloat)startingY {
    UIImageView* person = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"TransportFeetConnection"]];
    person.center = CGPointMake(102.0, startingY-(person.frame.size.height/2.0));
    person.alpha = 0.8;
    return person;
}

@end
