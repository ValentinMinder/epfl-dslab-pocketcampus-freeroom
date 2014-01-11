



//  Created by Loïc Gardiol on 15.10.13.



#import <UIKit/UIKit.h>

@interface MapRecentSearchesListViewController : UITableViewController

- (id)initWithUserSelectedRecentSearchBlock:(void (^)(NSString* searchPattern))userSelectedRecentSearchBlock;

@property (nonatomic, copy) void (^userSelectedRecentSearchBlock)(NSString*);

/*
 * If yes, clear button is not in navigationItem but in the tableview directly
 * Important: must be set before controller's view is loaded
 * Default: NO
 */

@property (nonatomic) BOOL showClearButtonWithinTableView;

@end
