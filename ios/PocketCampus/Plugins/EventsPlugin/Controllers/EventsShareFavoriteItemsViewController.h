



//  Created by Loïc Gardiol on 18.05.13.



#import <UIKit/UIKit.h>

#import "EventsService.h"

@interface EventsShareFavoriteItemsViewController : UITableViewController

- (id)initWithRelatedEventPool:(EventPool*)eventPool;

@property (nonatomic, strong) EventPool* relatedEventPool;

@property (nonatomic, strong) NSString* prefilledEmail;

@end
