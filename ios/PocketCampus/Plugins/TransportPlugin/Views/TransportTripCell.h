



//  Created by Loïc Gardiol on 27.11.13.



#import <UIKit/UIKit.h>

@class TransportTrip;

@interface TransportTripCell : UITableViewCell

- (id)initWithReuseIdentifier:(NSString*)reuseIdentifier;

@property (nonatomic, strong) TransportTrip* trip;

@end
