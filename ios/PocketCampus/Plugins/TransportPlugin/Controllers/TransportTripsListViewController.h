

//  Created by Loïc Gardiol on 10.04.12.


@class QueryTripsResult;

@interface TransportTripsListViewController : UITableViewController

- (id)initWithQueryTripResult:(QueryTripsResult*)tripResult;

@property (nonatomic, readonly, strong) QueryTripsResult* tripResult;

@end
