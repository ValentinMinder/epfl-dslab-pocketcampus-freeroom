



//  Created by Loïc Gardiol on 27.11.13.



#import "TransportTripCell.h"

#import "TransportService.h"

#import "TransportUtils.h"

@interface TransportTripCell ()

@property (nonatomic, copy, readwrite) NSString* reuseIdentifier;

@property (nonatomic, strong) IBOutlet UILabel* depTimeLabel;
@property (nonatomic, strong) IBOutlet UILabel* arrTimeLabel;
@property (nonatomic, strong) IBOutlet UILabel* durationLabel;
@property (nonatomic, strong) IBOutlet UILabel* changesLabel;
@property (nonatomic, strong) IBOutlet UILabel* firstLineLabel;

@end

@implementation TransportTripCell

@synthesize reuseIdentifier = _reuseIdentifier;

#pragma mark - Init

- (id)initWithReuseIdentifier:(NSString*)reuseIdentifier {
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"TransportTripCell" owner:nil options:nil];
    self = (TransportTripCell*)elements[0];
    if (self) {
        self.reuseIdentifier = reuseIdentifier;
        self.depTimeLabel.text = nil;
        self.arrTimeLabel.text = nil;
        self.durationLabel.text = nil;
        self.changesLabel.text = nil;
        self.firstLineLabel.text = nil;
    }
    return self;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    return [self initWithReuseIdentifier:reuseIdentifier];
}

#pragma mark - Properties

- (void)setTrip:(TransportTrip *)trip {
    if (trip) {
        //nil is allowed
        [PCUtils throwExceptionIfObject:trip notKindOfClass:[TransportTrip class]];
    }
    _trip = trip;
    
    TransportConnection* firstConnection = self.trip.firstConnection;
    NSString* depTimeString = nil;
    if (firstConnection) {
        depTimeString = [TransportUtils hourMinutesStringForTimestamp:firstConnection.departureTime/1000.0];
    } else {
        depTimeString = [TransportUtils hourMinutesStringForTimestamp:self.trip.departureTime/1000.0];
    }
    self.depTimeLabel.text = depTimeString;
    self.arrTimeLabel.text = [TransportUtils hourMinutesStringForTimestamp:self.trip.arrivalTime/1000.0];
    self.durationLabel.text = [TransportUtils durationgStringForInterval:((self.trip.arrivalTime/1000.0) - (self.trip.departureTime/1000.0))];
    self.changesLabel.text = [NSString stringWithFormat:@"%u", self.trip.numberOfChanges];
    self.firstLineLabel.text = firstConnection.line.shortName;
    
    static UIColor* normalColor = nil;
    static UIColor* leftColor = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        normalColor = [UIColor colorWithWhite:0.2 alpha:1.0];
        leftColor = [UIColor colorWithWhite:0.6 alpha:1.0];
    });
    self.depTimeLabel.textColor = firstConnection.hasLeft ? leftColor : normalColor;
}

@end
