//
//  EventItemCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "EventItemCell.h"

#import "EventItem+Additions.h"

#import "ASIDownloadCache.h"

#import "PCUtils.h"

#import "UIImage+Additions.h"

@interface EventItemCell ()

@property (nonatomic, strong) NSString* customReuseIdentifier;

@property (nonatomic, strong) NSTimer* glowTimer;

@end

@implementation EventItemCell

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"EventItemCell" owner:self options:nil];
    EventItemCell* cell = (EventItemCell*)[elements objectAtIndex:0];
    self = cell;
    if (self) {
        self.customReuseIdentifier = reuseIdentifier;
        if (![PCUtils isIdiomPad]) {
            self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
        self.selectionStyle = UITableViewCellSelectionStyleGray;
        self.titleLabel.text = @"";
        self.subtitleLabel.text = @"";
        self.rightSubtitleLabel.text = @"";
        self.rightSubtitleLabel.textColor = [UIColor colorWithRed:0.156863 green:0.250980 blue:0.458824 alpha:1.0];
        self.eventItem = eventItem;
    }
    return self;
}

+ (CGFloat)height {
    return 64.0;
}

- (NSString *)reuseIdentifier {
    return self.customReuseIdentifier;
}

- (void)setEventItem:(EventItem *)eventItem {
    _eventItem = eventItem;
    self.titleLabel.text = eventItem.eventTitle;
    if (eventItem.secondLine) {
        self.subtitleLabel.text = eventItem.secondLine;
    } else {
        self.subtitleLabel.text = eventItem.eventPlace;
    }
    
    if (eventItem.timeSnippet) {
        self.rightSubtitleLabel.text = eventItem.timeSnippet;
    } else if (eventItem.startDate) {
        self.rightSubtitleLabel.text = [eventItem dateString:EventItemDateStyleShort];
    } else {
        //nothing to show in rightSubtitleLabel
    }
    
    [self layoutSubviews];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat size = [[self class] height];
    self.imageView.frame = CGRectMake(0,0,size,size);
    self.imageView.clipsToBounds = YES;
    self.imageView.contentMode = UIViewContentModeScaleAspectFill;
    
    /*CGRect tmpFrame = self.textLabel.frame;
    tmpFrame.origin.x = 77;
    self.textLabel.frame = tmpFrame;
    
    tmpFrame = self.detailTextLabel.frame;
    tmpFrame.origin.x = 77;
    self.detailTextLabel.frame = tmpFrame;*/
    
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
