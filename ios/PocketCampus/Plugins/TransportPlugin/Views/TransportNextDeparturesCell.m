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
    self.destinationLabel.text = self.destinationStation.shortName;
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
            self.lineLabel.text = nil;
            if (self.tripResult) {
#warning TODO line name
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
    self.destinationLabel.text = self.tripResult.to.shortName;
    self.destinationLabelCenterYConstraint.constant = 10.0;
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
            NSString* timesString = [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            UILabel* label = index < 3 ? timeLabels[index] : nil;
            if ([timesString isEqualToString:@"Now"]) {
                label.text = @"now";
            } else {
                label.text = timesString;
            }
        }];
    } else {
#warning TODO non-redundant connection
    }
}

@end
