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

//  Created by Loïc Gardiol on 25.11.13.

#import "TransportNextDeparturesCell.h"

#import "TransportService.h"

#import "TransportUtils.h"

static NSTimeInterval kMaxIntervalForMinutesLeftString = 15.0;

@interface TransportNextDeparturesCell ()

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) IBOutlet UILabel* destinationLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) TransportConnection* transportConnection1;
@property (nonatomic, strong) IBOutlet UILabel* time1Label;
@property (nonatomic, strong) IBOutlet UILabel* platform1Label;
@property (nonatomic, strong) TransportConnection* transportConnection2;
@property (nonatomic, strong) IBOutlet UILabel* time2Label;
@property (nonatomic, strong) IBOutlet UILabel* platform2Label;
@property (nonatomic, strong) TransportConnection* transportConnection3;
@property (nonatomic, strong) IBOutlet UILabel* time3Label;
@property (nonatomic, strong) IBOutlet UILabel* platform3Label;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* destinationLabelCenterYConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint* time1LabelTopConstraint;

@property (nonatomic, strong) UIFont* originalLineLabelFont;
@property (nonatomic, strong) UIColor* originalLineLabelTextColor;

@end

@implementation TransportNextDeparturesCell

@synthesize destinationStation = _destinationStation;

@synthesize reuseIdentifier = _reuseIdentifier;

#pragma mark - Init

- (id)initWithReuseIdentifier:(NSString*)identifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"TransportNextDeparturesCell" owner:nil options:nil];
    self = (TransportNextDeparturesCell*)elements[0];
    if (self) {
        self.reuseIdentifier = identifier;
        self.destinationLabel.text = nil;
        self.time1Label.text = nil;
        self.time1Label.isAccessibilityElement = NO;
        self.time2Label.text = nil;
        self.time2Label.isAccessibilityElement = NO;
        self.time3Label.text = nil;
        self.time3Label.isAccessibilityElement = NO;
    }
    return self;
}


- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    return [self initWithReuseIdentifier:reuseIdentifier];
}

#pragma mark - Accessiblity

- (BOOL)isAccessibilityElement {
    return (self.destinationStation != nil);
}

- (NSString*)accessibilityLabel {
    NSString* string = [NSString stringWithFormat:NSLocalizedStringFromTable(@"NextDeparturesFromToWithFormat", @"TransportPlugin", nil), self.departureStation.shortName, self.destinationStation.shortName];
    switch (self.state) {
        case TransportNextDeparturesCellStateLoading:
            string = [string stringByAppendingString:NSLocalizedStringFromTable(@"Loading", @"PocketCampus", nil)];
            break;
        case TransportNextDeparturesCellStateError:
            string = [string stringByAppendingString:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil)];
            break;
        case TransportNextDeparturesCellStateLoaded:
            if (!self.tripResult) {
                //should not happen
                return nil;
            }
            
            //1
            if (self.time1Label.text) {
                NSString* depTimeString = [TransportUtils automaticTimeStringForTimestamp:(self.transportConnection1.departureTime)/1000.0 maxIntervalForMinutesLeftString:kMaxIntervalForMinutesLeftString accessibilityOriented:YES];
                string = [string stringByAppendingString:depTimeString];
                if (self.transportConnection1.line.veryShortName) {
                    NSString* lineString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"lineWithFormat", @"TransportPlugin", nil), self.transportConnection1.line.veryShortName];
                    string = [string stringByAppendingString:lineString];
                }
                if (self.platform1Label.text && self.transportConnection1.departurePosition) {
                    NSString* platString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"platformWithFormat", @"TransportPlugin", nil), self.transportConnection1.departurePosition];
                    string = [string stringByAppendingString:platString];
                }
            }
            
            //2
            if (self.time2Label.text) {
                if (self.time1Label.text) {
                    string = [string stringByAppendingString:@", "];
                }
                NSString* depTimeString = [TransportUtils automaticTimeStringForTimestamp:(self.transportConnection2.departureTime)/1000.0 maxIntervalForMinutesLeftString:kMaxIntervalForMinutesLeftString accessibilityOriented:YES];
                string = [string stringByAppendingString:depTimeString];
                if (self.transportConnection2.line.veryShortName) {
                    NSString* lineString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"lineWithFormat", @"TransportPlugin", nil), self.transportConnection2.line.veryShortName];
                    string = [string stringByAppendingString:lineString];
                }
                if (self.platform2Label.text && self.transportConnection2.departurePosition) {
                    NSString* platString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"platformWithFormat", @"TransportPlugin", nil), self.transportConnection2.departurePosition];
                    string = [string stringByAppendingString:platString];
                }
            }
            
            //3
            if (self.time3Label.text) {
                if (self.time2Label.text) {
                    string = [string stringByAppendingString:@", "];
                }
                NSString* depTimeString = [TransportUtils automaticTimeStringForTimestamp:(self.transportConnection3.departureTime)/1000.0 maxIntervalForMinutesLeftString:kMaxIntervalForMinutesLeftString accessibilityOriented:YES];
                string = [string stringByAppendingString:depTimeString];
                if (self.transportConnection3.line.veryShortName) {
                    NSString* lineString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"lineWithFormat", @"TransportPlugin", nil), self.transportConnection3.line.veryShortName];
                    string = [string stringByAppendingString:lineString];
                }
                if (self.platform3Label.text && self.transportConnection3.departurePosition) {
                    NSString* platString = [NSString stringWithFormat:NSLocalizedStringFromTable(@"platformWithFormat", @"TransportPlugin", nil), self.transportConnection3.departurePosition];
                    string = [string stringByAppendingString:platString];
                }
            }
            break;
    }
    return string;
}

- (NSString*)accessibilityHint {
    if (self.state != TransportNextDeparturesCellStateLoaded) {
        return nil;
    }
    return [NSString stringWithFormat:NSLocalizedStringFromTable(@"ShowsAllPossibilitiesFromToWithFormat", @"TransportPlugin", nil), self.departureStation.shortName, self.destinationStation.shortName];
}

- (UIAccessibilityTraits)accessibilityTraits {
    switch (self.state) {
        case TransportNextDeparturesCellStateLoading:
            return UIAccessibilityTraitStaticText;
        case TransportNextDeparturesCellStateError:
            return UIAccessibilityTraitStaticText;
        case TransportNextDeparturesCellStateLoaded:
            return UIAccessibilityTraitButton | UIAccessibilityTraitStaticText;
    }
    return UIAccessibilityTraitNone;
}

#pragma mark - Properties

- (TransportStation*)departureStation {
    return self.tripResult ? self.tripResult.from : _departureStation;
}

- (TransportStation*)destinationStation {
    return self.tripResult ? self.tripResult.to : _destinationStation;
}

- (void)setDestinationStation:(TransportStation *)destinationStation {
    if (destinationStation) { //nil is allowed
        [PCUtils throwExceptionIfObject:destinationStation notKindOfClass:[TransportStation class]];
    }
    _destinationStation = destinationStation;
    self.tripResult = nil;
    self.destinationLabelCenterYConstraint.constant = 0.0;
    self.destinationLabel.text = self.destinationStation.shortName;
    [self.contentView layoutIfNeeded];
}

- (void)setState:(TransportNextDeparturesCellState)state {
    _state = state;
    switch (state) {
        case TransportNextDeparturesCellStateLoading:
            self.accessoryType = UITableViewCellAccessoryNone;
            self.destinationLabel.text = self.destinationStation.shortName;
            [self.loadingIndicator startAnimating];
            break;
        case TransportNextDeparturesCellStateLoaded:
            self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            self.destinationLabel.text = self.destinationStation.shortName;
            [self.loadingIndicator stopAnimating];
            break;
        case TransportNextDeparturesCellStateError:
        {
            self.accessoryType = UITableViewCellAccessoryNone;
            static NSString* errorString = nil;
            static dispatch_once_t onceToken;
            dispatch_once(&onceToken, ^{
                errorString = [NSString stringWithFormat:@"(%@)", NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil)];
            });
            NSString* fullString = [NSString stringWithFormat:@"%@ %@", self.destinationStation.shortName, errorString];
            NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
            [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor orangeColor] range:[fullString rangeOfString:errorString]];
            self.destinationLabel.attributedText = attrString;
            [self.loadingIndicator stopAnimating];
            break;
        }
        default:
            [NSException raise:@"Illegal argument" format:@"state is not of enum type TransportNextDeparturesCellState"];
            break;
    }
}

- (void)setTripResult:(QueryTripsResult *)tripResult {
    if (tripResult) { //nil is allowed
        [PCUtils throwExceptionIfObject:tripResult notKindOfClass:[QueryTripsResult class]];
    }
    _tripResult = tripResult;
    self.destinationLabel.text = self.destinationStation.shortName;
    self.state = TransportNextDeparturesCellStateLoaded;
    
    static CGFloat kTime1LabelTopConstraintConstantNormal = 42.0;
    static CGFloat kTime1LabelTopConstraintConstantShiftedUpForPlatform = 35.0;
    static CGFloat kDestinationLabelCenterYConstraintConstantNormal = 13.0;
    static CGFloat kDestinationLabelCenterYConstraintConstantShiftedUpForPlatform = 16.0;
    
    self.time1LabelTopConstraint.constant = kTime1LabelTopConstraintConstantNormal;
    self.destinationLabelCenterYConstraint.constant = kDestinationLabelCenterYConstraintConstantNormal;
    
    if (!self.tripResult) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.time1Label.text = nil;
        self.time2Label.text = nil;
        self.time3Label.text = nil;
        return;
    }
    self.selectionStyle = UITableViewCellSelectionStyleDefault;
    
    NSArray* timeLabels = @[self.time1Label, self.time2Label, self.time3Label];
    NSArray* platformLabels = @[self.platform1Label, self.platform2Label, self.platform3Label];
    

    NSArray* trips = self.tripResult.nonLeftTrips;
    if (trips.count < 3) {
        //reset labels if less than 3 connections, as we might then leave previous content of the cell
        for (UILabel* label in timeLabels) {
            label.text = nil;
        }
    }
    [trips enumerateObjectsUsingBlock:^(TransportTrip* transportTrip, NSUInteger index, BOOL *stop) {
        if (index > 2) {
            *stop = YES;
            return;
        }
        NSString* timeString = nil;
        TransportConnection* firstConnection = transportTrip.firstConnection;
        if (firstConnection) {
            timeString = [TransportUtils automaticTimeStringForTimestamp:(firstConnection.departureTime)/1000.0 maxIntervalForMinutesLeftString:kMaxIntervalForMinutesLeftString];
        } else {
            timeString = [TransportUtils automaticHoursMinutesLeftStringForTimestamp:transportTrip.departureTime/1000.0];
        }
        
        switch (index) {
            case 0:
                self.transportConnection1 = firstConnection;
                break;
            case 1:
                self.transportConnection2 = firstConnection;
                break;
            case 2:
                self.transportConnection3 = firstConnection;
                break;
            default:
                break;
        }
        UILabel* timeLabel = timeLabels[index];
        NSString* lineName = firstConnection.line.veryShortName ?: @"";
        NSString* fullString = nil;
        if ([timeString isEqualToString:kTransportDepartureTimeNowString]) {
            [self setBusImageViewVisible:YES inLabel:timeLabel];
            fullString = [NSString stringWithFormat:@"     %@", lineName];
        } else {
            [self setBusImageViewVisible:NO inLabel:timeLabel];
            fullString = [NSString stringWithFormat:@"%@ %@", timeString, lineName];
        }
        NSMutableAttributedString* fullAttrString = [[NSMutableAttributedString alloc] initWithString:fullString];
        [fullAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor colorWithWhite:0.5 alpha:1.0],
                                        NSFontAttributeName:[UIFont systemFontOfSize:timeLabel.font.fontDescriptor.pointSize-2]}
                                range:[fullString rangeOfString:lineName]];
        timeLabel.attributedText = fullAttrString;
        UILabel* platformLabel = index < 3 ? platformLabels[index] : nil;
        if (firstConnection.departurePosition.length > 0) {
            self.time1LabelTopConstraint.constant = kTime1LabelTopConstraintConstantShiftedUpForPlatform;
            self.destinationLabelCenterYConstraint.constant = kDestinationLabelCenterYConstraintConstantShiftedUpForPlatform;
            platformLabel.text = [NSString stringWithFormat:@" %@ %@", NSLocalizedStringFromTable(@"PlatformShort", @"TransportPlugin", nil), firstConnection.departurePosition];
        } else {
            platformLabel.text = nil;
        }
    }];
    
    self.time1Label.alpha = 0.0;
    self.platform1Label.alpha = 0.0;
    self.time2Label.alpha = 0.0;
    self.platform2Label.alpha = 0.0;
    self.time3Label.alpha = 0.0;
    self.platform3Label.alpha = 0.0;
    [UIView animateWithDuration:0.3 animations:^{
        self.time1Label.alpha = 1.0;
        self.platform1Label.alpha = 1.0;
        self.time2Label.alpha = 1.0;
        self.platform2Label.alpha = 1.0;
        self.time3Label.alpha = 1.0;
        self.platform3Label.alpha = 1.0;
        [self.contentView layoutIfNeeded];
    }];
    
}

- (void)setBusImageViewVisible:(BOOL)visible inLabel:(UILabel*)label {
    static const int kBusImageViewTag = 5;
    UIImageView* busImageView = (UIImageView*)[label viewWithTag:kBusImageViewTag];
    if (visible) {
        if (busImageView) {
            busImageView.hidden = NO;
        } else {
            UIImage* image = [UIImage imageNamed:label == self.time1Label ? @"TransportSmallBusGreen" : @"TransportSmallBus"];
            busImageView = [[UIImageView alloc] initWithImage:image];
            busImageView.alpha = 0.8;
            busImageView.translatesAutoresizingMaskIntoConstraints = NO;
            [label addSubview:busImageView];
            [label addConstraint:[NSLayoutConstraint constraintForCenterYtoSuperview:label forView:busImageView constant:0.0]];
            [label addConstraints:[NSLayoutConstraint constraintsToSuperview:label forView:busImageView edgeInsets:UIEdgeInsetsMake(kNoInsetConstraint, 0.0, kNoInsetConstraint, kNoInsetConstraint)]];
        }
    } else {
        busImageView.hidden = YES;
    }
}

@end
