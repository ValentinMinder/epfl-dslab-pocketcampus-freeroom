

//  Created by Loïc Gardiol on 11.04.12.


@class TransportTrip;

@interface TransportTripPartsViewController : UIViewController

- (id)initWithTransportTrip:(TransportTrip*)trip;

@property (nonatomic, readonly, strong) TransportTrip* trip;

@end
