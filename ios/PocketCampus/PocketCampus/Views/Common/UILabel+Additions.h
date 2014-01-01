//
//  UILabel+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 01.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UILabel (Additions)

/*
 * All matches of regex in self.text are set to color highlightedColor and the rest to dimmedColor.
 * Call this after setting text. Must be called again if text is changed.
 * IMPORTANT: Only supported with text (not attributedText)
 * INFO: matching is done in a low priority queue. When result is returned, color is applied on the main queue.
 */
- (void)setHighlightedColor:(UIColor*)highlightedColor forMatchesOfRegex:(NSRegularExpression*)regex dimmedColor:(UIColor*)dimmedColor;

@end
