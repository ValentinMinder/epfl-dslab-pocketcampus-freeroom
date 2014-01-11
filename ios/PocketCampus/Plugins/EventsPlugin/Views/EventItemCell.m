



//  Created by Loïc Gardiol on 01.03.13.



#import "EventItemCell.h"

#import "EventItem+Additions.h"

#import "PCUtils.h"

#import "UIImage+Additions.h"

#import "EventsService.h"

@interface EventItemCell ()

@property (nonatomic, strong) NSString* customReuseIdentifier;

@property (nonatomic, strong) NSTimer* glowTimer;

@end

@implementation EventItemCell

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        if (![PCUtils isIdiomPad]) {
            self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
        self.textLabel.numberOfLines = 2;
        self.textLabel.font = [UIFont boldSystemFontOfSize:13.0];
        self.detailTextLabel.textColor = [UIColor grayColor];
        self.eventItem = eventItem;
    }
    return self;
}

+ (CGFloat)preferredHeight {
    return 70.0;
}

/*- (void)layoutSubviews {
    [super layoutSubviews];
    self.imageView.frame = CGRectMake(self.imageView.frame.origin.x, self.imageView.frame.origin.y, [self.class preferredHeight], [self.class preferredHeight]);
}*/

- (void)setEventItem:(EventItem *)eventItem {
    _eventItem = eventItem;
    self.textLabel.text = self.eventItem.eventTitle;
    self.detailTextLabel.text = self.eventItem.secondLine ? self.eventItem.secondLine : (self.eventItem.timeSnippet ? self.eventItem.timeSnippet : [eventItem dateString:EventItemDateStyleMedium]);
    
    self.favoriteIndicationVisible = self.eventItem ? [[EventsService sharedInstanceToRetain] isEventItemIdFavorite:self.eventItem.eventId] : NO;
}

- (void)setGlowIfEventItemIsNow:(BOOL)glowIfEventItemIsNow {
    _glowIfEventItemIsNow = glowIfEventItemIsNow;
    if (glowIfEventItemIsNow) {
        if (!self.glowTimer) {
            self.glowTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(glowIfNow) userInfo:nil repeats:YES];
        }
    } else {
        [self.glowTimer invalidate];
        self.glowTimer = nil;
        self.backgroundView = nil;
    }
}

- (void)glowIfNow {
    if (!self.backgroundView) {
        UIView* backgroundView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
        backgroundView.backgroundColor = [UIColor colorWithRed:0.760784 green:0.811765 blue:1.000000 alpha:1.0];
        self.backgroundView = backgroundView;
        self.backgroundView.alpha = 0.0;
    }
    CGFloat targetAlpha;
    if (![self.eventItem isNow]) {
        targetAlpha = 0.0;
    } else if (self.backgroundView.alpha < 0.5) {
        targetAlpha = 1.0;
    } else {
        targetAlpha = 0.0;
    }
    //NSLog(@"TargetAlpha: %f", targetAlpha);
    [UIView animateWithDuration:1.0 animations:^{
        self.backgroundView.alpha = targetAlpha;
    }];
}

- (void)dealloc {
    [self.glowTimer invalidate];
}

@end
