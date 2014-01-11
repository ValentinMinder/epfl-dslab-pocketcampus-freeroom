



//  Created by Loïc Gardiol on 18.05.13.



#import <UIKit/UIKit.h>

@interface EventsTagsViewController : UITableViewController

- (id)initWithTags:(NSArray*)allTags selectedInitially:(NSSet*)selectedInitially userValidatedSelectionBlock:(void (^)(NSSet* newlySelected))userValidatedSelectionBlock;

@property (nonatomic, copy) void (^userValidatedSelectionBlock)(NSSet*);

//- (IBAction)selectAllPressed;

@end
