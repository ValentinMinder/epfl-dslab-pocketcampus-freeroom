//
//  UILabel+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "UILabel+Additions.h"

@implementation UILabel (Additions)

- (void)setHighlightedColor:(UIColor*)highlightedColor forMatchesOfRegex:(NSRegularExpression*)regex dimmedColor:(UIColor*)dimmedColor  {
    [PCUtils throwExceptionIfObject:highlightedColor notKindOfClass:[UIColor class]];
    [PCUtils throwExceptionIfObject:regex notKindOfClass:[NSRegularExpression class]];
    [PCUtils throwExceptionIfObject:dimmedColor notKindOfClass:[UIColor class]];
    __weak __typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_LOW,0), ^{
        NSMutableAttributedString* attrText = [[NSMutableAttributedString alloc] initWithString:self.text attributes:@{NSForegroundColorAttributeName:dimmedColor,NSFontAttributeName:self.font}];
        [regex enumerateMatchesInString:self.text options:0 range:NSMakeRange(0, self.text.length) usingBlock:^(NSTextCheckingResult* result, NSMatchingFlags flags, BOOL* stop) {
            [attrText addAttribute:NSForegroundColorAttributeName value:highlightedColor range:result.range];
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            weakSelf.attributedText = attrText;
        });
    });
}

@end
