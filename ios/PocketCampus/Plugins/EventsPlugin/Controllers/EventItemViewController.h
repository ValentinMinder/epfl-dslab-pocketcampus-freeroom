



//  Created by Loïc Gardiol on 02.03.13.



#import <UIKit/UIKit.h>

#import "EventsService.h"

@interface EventItemViewController : UIViewController

- (id)initWithEventItem:(EventItem*)item;
- (id)initAndLoadEventItemWithId:(int64_t)eventId;

@property (nonatomic, readonly) int64_t itemId;

- (void)refresh;

@property (nonatomic) BOOL showFavoriteButton; //default: NO

@end
