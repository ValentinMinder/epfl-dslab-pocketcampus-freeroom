



//  Created by Loïc Gardiol on 25.11.13.



#import "TransportNextDeparturesCell.h"

#import "TransportService.h"

#import "TransportUtils.h"

@interface TransportNextDeparturesCell ()

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) IBOutlet UILabel* destinationLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView* loadingIndicator;
@property (nonatomic, strong) IBOutlet UILabel* time1Label;
@property (nonatomic, strong) IBOutlet UILabel* platform1Label;
@property (nonatomic, strong) IBOutlet UILabel* time2Label;
@property (nonatomic, strong) IBOutlet UILabel* platform2Label;
@property (nonatomic, strong) IBOutlet UILabel* time3Label;
@property (nonatomic, strong) IBOutlet UILabel* platform3Label;
@property (nonatomic, strong) IBOutlet UILabel* lineLabel;

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
            self.accessoryType = UITableViewCellAccessoryNone;
            [self.loadingIndicator startAnimating];
            break;
        case TransportNextDeparturesCellStateLoaded:
            self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            [self.loadingIndicator stopAnimating];
            self.lineLabel.font = self.originalLineLabelFont;
            self.lineLabel.textColor = self.originalLineLabelTextColor;
            if ([self.lineLabel.text isEqualToString:NSLocalizedStringFromTable(@"Error", @"PocketCampus", nil)]) {
                self.lineLabel.text = nil;
            }
            break;
        case TransportNextDeparturesCellStateError:
            self.accessoryType = UITableViewCellAccessoryNone;
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
    
    NSArray* redundantConnections = [TransportUtils nextRedundantDeparturesFromMessyResult:self.tripResult purgeAlreadyLeft:YES];
    NSArray* timeLabels = @[self.time1Label, self.time2Label, self.time3Label];
    NSArray* platformLabels = @[self.platform1Label, self.platform2Label, self.platform3Label];
    
    if (redundantConnections.count > 0) {
        [redundantConnections enumerateObjectsUsingBlock:^(TransportConnection* connection, NSUInteger index, BOOL *stop) {
            NSString* timeString = [TransportUtils automaticTimeStringForTimestamp:(connection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            UILabel* timeLabel = index < 3 ? timeLabels[index] : nil;
            if ([timeString isEqualToString:@"Now"]) {
                [self setBusImageViewVisible:YES inLabel:timeLabel];
                timeLabel.text = @"     ";
            } else {
                [self setBusImageViewVisible:NO inLabel:timeLabel];
                timeLabel.text = timeString;
            }
            UILabel* platformLabel = index < 3 ? platformLabels[index] : nil;
            if (connection.departurePosition) {
                self.time1LabelTopConstraint.constant = kTime1LabelTopConstraintConstantShiftedUpForPlatform;
                self.destinationLabelCenterYConstraint.constant = kDestinationLabelCenterYConstraintConstantShiftedUpForPlatform;
                platformLabel.text = [NSString stringWithFormat:@" %@ %@", NSLocalizedStringFromTable(@"PlatformShort", @"TransportPlugin", nil), connection.departurePosition];
            } else {
                platformLabel.text = nil;
            }
        }];
        TransportConnection* connection = [redundantConnections firstObject]; //all have same line anyway
        self.lineLabel.text = connection.line.veryShortName;
        
    } else {
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
                timeString = [TransportUtils automaticTimeStringForTimestamp:(firstConnection.departureTime)/1000.0 maxIntervalForMinutesLeftString:15.0];
            } else {
                timeString = [TransportUtils automaticHoursMinutesLeftStringForTimestamp:transportTrip.departureTime/1000.0];
            }
            
            UILabel* timeLabel = timeLabels[index];
            NSString* lineName = firstConnection ? firstConnection.line.veryShortName : @"";
            NSString* fullString = nil;
            if ([timeString isEqualToString:@"Now"]) {
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
            if (firstConnection.departurePosition) {
                self.time1LabelTopConstraint.constant = kTime1LabelTopConstraintConstantShiftedUpForPlatform;
                self.destinationLabelCenterYConstraint.constant = kDestinationLabelCenterYConstraintConstantShiftedUpForPlatform;
                platformLabel.text = [NSString stringWithFormat:@" %@ %@", NSLocalizedStringFromTable(@"PlatformShort", @"TransportPlugin", nil), firstConnection.departurePosition];
            } else {
                platformLabel.text = nil;
            }
        }];
    }
    
    self.time1Label.alpha = 0.0;
    self.platform1Label.alpha = 0.0;
    self.time2Label.alpha = 0.0;
    self.platform2Label.alpha = 0.0;
    self.time3Label.alpha = 0.0;
    self.platform3Label.alpha = 0.0;
    self.lineLabel.alpha = 0.0;
    [UIView animateWithDuration:0.3 animations:^{
        self.time1Label.alpha = 1.0;
        self.platform1Label.alpha = 1.0;
        self.time2Label.alpha = 1.0;
        self.platform2Label.alpha = 1.0;
        self.time3Label.alpha = 1.0;
        self.platform3Label.alpha = 1.0;
        self.lineLabel.alpha = 1.0;
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
