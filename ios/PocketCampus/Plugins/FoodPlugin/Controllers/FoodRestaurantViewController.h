



//  Created by Loïc Gardiol on 30.10.13.



#import <UIKit/UIKit.h>

@class EpflRestaurant;

@interface FoodRestaurantViewController : UITableViewController

- (instancetype)initWithEpflRestaurant:(EpflRestaurant*)restaurant;

@property (nonatomic, strong) EpflRestaurant* restaurant;

@end
