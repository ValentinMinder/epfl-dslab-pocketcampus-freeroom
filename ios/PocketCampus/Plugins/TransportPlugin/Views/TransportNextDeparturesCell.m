//
//  TransportNextDeparturesCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 25.11.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "TransportNextDeparturesCell.h"

#import "TransportService.h"

#import "TransportUtils.h"

@interface TransportNextDeparturesCell ()

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) IBOutlet UILabel* destinationLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* time1Label;
@property (nonatomic, strong) IBOutlet UILabel* time2Label;
@property (nonatomic, strong) IBOutlet UILabel* time3Label;
@property (nonatomic, strong) IBOutlet UILabel* lineLabel;

@property (nonatomic, strong) IBOutlet NSLayoutConstraint* destinationLabelCenterYConstraint;

@property (nonatomic, strong) UIFont* originalLineLabelFont;
@property (nonatomic, strong) UIColor* originalLineLabelTextColor;

@end

@implementation TransportNextDeparturesCell

@synthesize destinationStation = _destinationStation;

#pragma mark - Init

- (id)initWithReuseIdentifier:(NSString*)identifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"TransportNextDeparturesCell" owner:nil options:nil];
    self = (TransportNextDeparturesCell*)elements[0];
    if (self) {
        self.reuseIdentifier = identifier;
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        self.originalLineLabelFont = self.lineLabel.font;
        self.originalLineLabelTextColor = self.lineLabel.textColor;
        self.destinationLabel.text = nil;
        self.time1Label.text = nil;
        self.time2Label.text = nil;
        self.time3Label.text = nil;
        self.lineLabel.text = nil;
    }
    return self;
}


- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    return [self initWithReuseIdentifier:reuseIdentifier];
}

#pragma mark - UITableViewCell overrides

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark - Properties

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
    self.destinationLabel.attributedText = [self destinationAttributedStringWithTransportStation:self.destinationStation];
    [self.contentView layoutIfNeeded];
}

- (void)setState:(TransportNextDeparturesCellState)state {
    _state = state;
    switch (state) {
        case TransportNextDeparturesCellStateLoading:
            [self.loadingIndicator startAnimating];
            break;
        case TransportNextDeparturesCellStateLoaded:
            [self.loadingIndicator stopAnimating];
            self.lineLabel.font = self.originalLineLabelFont;
            self.lineLabel.textColor = self.originalLineLabelTextColor;
            if ([self.lineLabel.text isEqualToString:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil)]) {
                self.lineLabel.text = nil;
            }
            break;
        case TransportNextDeparturesCellStateError:
            [self.loadingIndicator stopAnimating];
            self.lineLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleBody];
            self.lineLabel.textColor = [UIColor orangeColor];
            self.lineLabel.text = NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil);
            break;
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
    self.destinationLabel.attributedText = [self destinationAttributedStringWithTransportStation:self.destinationStation];
    self.state = TransportNextDeparturesCellStateLoaded;
    if (!self.tripResult) {
        self.time1Label.text = nil;
        self.time2Label.text = nil;
        self.time3Label.text = nil;
        return;
    }
    
    NSArray* redundantConnections = [TransportUtils nextRedundantDeparturesFromMessyResult:self.tripResult];
    redundantConnections = [TransportUtils connectionsWithoutAlreadyLeft:redundantConnections];
    NSArray* timeLabels = @[self.time1Label, self.time2Label, self.time3Label];
    if (redundantConnections.count > 0) {
        [redundantConnections enumerateObjectsUsingBlock:^(TransportConnection* connection, NSUInteger index, BOOL *stop) {
            NSString* timeString = [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            UILabel* label = index < 3 ? timeLabels[index] : nil;
            if ([timeString isEqualToString:@"Now"]) {
                [self setBusImageViewVisible:YES inLabel:label];
                label.text = @"     ";
            } else {
                [self setBusImageViewVisible:NO inLabel:label];
                label.text = timeString;
            }
            [label sizeToFit];
        }];
        TransportConnection* connection = [redundantConnections firstObject]; //all have same line anyway
        self.lineLabel.text = connection.line.shortName;
        
    } else {
        NSArray* connections = [TransportUtils connectionsWithoutAlreadyLeft:self.tripResult.connections];
        if (connections.count < 3) {
            //reset labels if less than 3 connections, as we might then leave previous content of the cell
            for (UILabel* label in timeLabels) {
                label.text = nil;
            }
        }
        [connections enumerateObjectsUsingBlock:^(TransportTrip* transportTrip, NSUInteger index, BOOL *stop) {
            if (index > 2) {
                *stop = YES;
                return;
            }
            NSString* timeString = nil;
            TransportConnection* firstConnection = nil;
            if (transportTrip.parts.count == 0) {
                timeString = [TransportUtils automaticHoursMinutesLeftStringForTimestamp:transportTrip.departureTime/1000.0];
            } else {
                if (transportTrip.parts.count > 1 && [(TransportConnection*)[transportTrip.parts firstObject] isFeetConnection]) {
                    //first real connection is actual second one, as one at index 0 is feet
                    firstConnection = transportTrip.parts[1];
                } else {
                    firstConnection = [transportTrip.parts firstObject];
                }
                timeString = [TransportUtils automaticTimeStringForTimestamp:(firstConnection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            }
            
            UILabel* label = timeLabels[index];
            NSString* lineName = firstConnection ? firstConnection.line.shortName : @"";
            NSString* fullString = nil;
            if ([timeString isEqualToString:@"Now"]) {
                [self setBusImageViewVisible:YES inLabel:label];
                fullString = [NSString stringWithFormat:@"     %@", lineName];
            } else {
                [self setBusImageViewVisible:NO inLabel:label];
                fullString = [NSString stringWithFormat:@"%@ %@", timeString, lineName];
            }
            NSMutableAttributedString* fullAttrString = [[NSMutableAttributedString alloc] initWithString:fullString];
            [fullAttrString addAttributes:@{NSForegroundColorAttributeName:[UIColor colorWithWhite:0.5 alpha:1.0],
                                            NSFontAttributeName:[UIFont systemFontOfSize:label.font.fontDescriptor.pointSize-2]}
                                    range:[fullString rangeOfString:lineName]];
            label.attributedText = fullAttrString;
        }];
    }
    
    self.time1Label.alpha = 0.0;
    self.time2Label.alpha = 0.0;
    self.time3Label.alpha = 0.0;
    self.lineLabel.alpha = 0.0;
    self.destinationLabelCenterYConstraint.constant = 13.0;
    [UIView animateWithDuration:0.3 animations:^{
        self.time1Label.alpha = 1.0;
        self.time2Label.alpha = 1.0;
        self.time3Label.alpha = 1.0;
        self.lineLabel.alpha = 1.0;
        [self.contentView layoutIfNeeded];
    }];
    
}

- (NSAttributedString*)destinationAttributedStringWithTransportStation:(TransportStation*)station {
    return [[NSAttributedString alloc] initWithString:station.shortName];
    /*NSString* toString = NSLocalizedStringFromTable(@"To", @"TransportPlugin", nil);
    NSString* fullString = [NSString stringWithFormat:@"%@ %@", toString, station.shortName];
    NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:self.destinationLabel.font.fontDescriptor.pointSize-1],
                                NSForegroundColorAttributeName:[UIColor lightGrayColor]}
                        range:[fullString rangeOfString:toString]];
    return attrString;*/
}

- (void)setBusImageViewVisible:(BOOL)visible inLabel:(UILabel*)label {
    static const int kBusImageViewTag = 5;
    UIImageView* busImageView = (UIImageView*)[label viewWithTag:kBusImageViewTag];
    if (visible) {
        if (busImageView) {
            busImageView.hidden = NO;
        } else {
            busImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"TransportSmallBus"]];
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
