/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */


//  Created by Loïc Gardiol on 01.03.13.

#import "EventItemCell.h"

#import "EventItem+Additions.h"

#import "EventsService.h"

static NSString* kTextLabelTextStyle;
static NSString* kDetailTextLabelTextStyle;

@interface EventItemCell ()

@property (nonatomic, strong) NSString* customReuseIdentifier;

@property (nonatomic, strong) NSTimer* glowTimer;

@end

@implementation EventItemCell

#pragma mark - Init

- (id)initWithEventItem:(EventItem*)eventItem reuseIdentifier:(NSString*)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:reuseIdentifier];
    if (self) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            kTextLabelTextStyle = UIFontTextStyleFootnote;
            kDetailTextLabelTextStyle = UIFontTextStyleFootnote;
        });
        self.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        self.textLabel.numberOfLines = 2;
        self.textLabel.font = [UIFont boldSystemFontOfSize:[UIFont preferredFontForTextStyle:kTextLabelTextStyle].pointSize];;
        self.detailTextLabel.numberOfLines = 0;
        self.detailTextLabel.font = [UIFont preferredFontForTextStyle:kDetailTextLabelTextStyle];
        self.eventItem = eventItem;
    }
    return self;
}

#pragma mark - Public

+ (CGFloat)preferredHeight {
    static const CGFloat kDefaultCoeff = 2.0;
    static CGFloat coeff = kDefaultCoeff;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserverForName:UIContentSizeCategoryDidChangeNotification object:nil queue:nil usingBlock:^(NSNotification *notif) {
            coeff = 0.0;
        }];
    });
    if (coeff == 0.0) {
        NSString* contentSize = [[UIApplication sharedApplication] preferredContentSizeCategory];
        if ([contentSize isEqualToString:UIContentSizeCategoryExtraSmall]) {
            coeff = 2.3;
        } else if ([contentSize isEqualToString:UIContentSizeCategoryExtraExtraExtraLarge]) {
            coeff = 2.3;
        } else {
            coeff = kDefaultCoeff; //Default
        }
    }
    return floorf([self preferredHeightForStyle:UITableViewCellStyleSubtitle textLabelTextStyle:kTextLabelTextStyle detailTextLabelTextStyle:kDetailTextLabelTextStyle]*coeff);
}

+ (CGSize)preferredImageSize {
    return CGSizeMake([EventItemCell preferredHeight], [EventItemCell preferredHeight]);
}

- (void)setEventItem:(EventItem *)eventItem {
    _eventItem = eventItem;
    self.textLabel.text = self.eventItem.eventTitle;

    NSString* dateTimeInfo = eventItem.timeSnippet ?: self.eventItem.shortDateString;
    NSString* secondaryInfo = eventItem.secondLine ?: (eventItem.eventPlace ?: eventItem.eventSpeaker);
    
    static const NSUInteger kMaxSecondaryInfoLength = 40;
    if (secondaryInfo.length > kMaxSecondaryInfoLength) {
        secondaryInfo = [[secondaryInfo substringToIndex:kMaxSecondaryInfoLength] stringByAppendingString:@"…"];
    }
    
    NSString* fullString = nil;
    if (dateTimeInfo.length > 0 && secondaryInfo.length > 0) {
        fullString = [NSString stringWithFormat:@"%@\n%@", dateTimeInfo, secondaryInfo];
    } else if (secondaryInfo.length > 0) {
        fullString = secondaryInfo;
    } else {
        fullString = dateTimeInfo;
    }
    if (fullString) {
        NSMutableAttributedString* attrString = [[NSMutableAttributedString alloc] initWithString:fullString];
        if (secondaryInfo.length > 0) {
            [attrString addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:[fullString rangeOfString:secondaryInfo]];
        }
        self.detailTextLabel.attributedText = attrString;
    }
    
    self.favoriteIndicationVisible = eventItem ? [[EventsService sharedInstanceToRetain] isEventItemIdFavorite:eventItem.eventId] : NO;
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

#pragma mark - Private

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
