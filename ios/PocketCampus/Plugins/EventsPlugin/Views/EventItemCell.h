



//  Created by Loïc Gardiol on 01.03.13.



#import <UIKit/UIKit.h>

#import "events.h"

#import "PCTableViewCellAdditions.h"

@interface EventItemCell : PCTableViewCellAdditions

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier;

+ (CGFloat)preferredHeight;

@property (nonatomic, strong) EventItem* eventItem;

@property (nonatomic) BOOL glowIfEventItemIsNow;

@end
