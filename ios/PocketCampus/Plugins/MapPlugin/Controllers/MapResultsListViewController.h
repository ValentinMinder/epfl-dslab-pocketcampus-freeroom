



//  Created by Loïc Gardiol on 15.10.13.



#import <UIKit/UIKit.h>

@interface MapResultsListViewController : UITableViewController

- (id)initWithMapItems:(NSArray*)mapItems selectedInitially:(NSArray*)selectedInitially userValidatedSelectionBlock:(void (^)(NSArray* newlySelected))userValidatedSelectionBlock;

@property (nonatomic, copy) void (^userValidatedSelectionBlock)(NSArray*);

@end
