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

//  Created by Loïc Gardiol on 16.12.12.

extern NSString* PCTableViewCellAdditionsDefaultTextLabelTextStyle;
extern NSString* PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle;

@interface PCTableViewCellAdditions : UITableViewCell

/**
 * If both are YES, only favorite indication is visible
 */
@property (nonatomic, getter = isDownloadedIndicationVisible) BOOL downloadedIndicationVisible;
@property (nonatomic, getter = isFavoriteIndicationVisible) BOOL favoriteIndicationVisible;

@property (nonatomic, getter = isDurablySelected) BOOL durablySelected;

/**
 * If not nil, the text that does not match in textLabel si set to color textLabelDimmedColor
 * Default: nil
 */
@property (nonatomic, strong) NSRegularExpression* textLabelHighlightedRegex;

/**
 * Color for dimmed text in textLabel (used in combination with textLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* textLabelDimmedColor;

/**
 * If not nil, the text that does not match in textLabel si set to color detailTextLabelDimmedColor
 * Default: nil
 */
@property (nonatomic, strong) NSRegularExpression* detailTextLabelHighlightedRegex;

/**
 * Color for dimmed text in textLabel (used in combination with detailTextLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* detailTextLabelDimmedColor;

/**
 * Due to a bug in iOS 8 GM, setting an accessoryView will generate an infinite loop.
 * Use this property to achieve the same behavior while avoid the bug.
 * This method uses AutoLayout to set the origin. The view must have size constraints
 * defined (or intrensic content size).
 * On iOS 7, this method simply calls UITableViewCell accessoryView
 */
@property (nonatomic, strong) UIView* accessoryViewViaContentView;

/*
 * Returns an ideal cell height that fits content yet not too small, based on a cell style, textLabel and detailTextLabel font 'Text Styles' (See [UIFont preferredFontForTextStyle:])
 * If you use this method, you should use same styles once constructing the cell (style and fonts).
 * If you pass nil for a text style, the required text height for the corresponding label is considered 0.0.
 * The result of this method is not cached.
 * The implementation assumes that the cell has no image and labels have nubmerOfLines set to 1.
 */
+ (CGFloat)preferredHeightForStyle:(UITableViewCellStyle)style textLabelTextStyle:(NSString*)textLabelTextStyle detailTextLabelTextStyle:(NSString*)detailTextLabelTextStyle NS_EXTENSION_UNAVAILABLE_IOS("");

/*
 * Same as previous, with textLabelTextStyle = PCTableViewCellAdditionsDefaultTextLabelTextStyle
 * and detailTextLabelTextStyle = PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle
 */
+ (CGFloat)preferredHeightForDefaultTextStylesForCellStyle:(UITableViewCellStyle)style NS_EXTENSION_UNAVAILABLE_IOS("");


/*
 * If YES, isAccessibilityElement returns NO and accessibilityElementCount returns 0. Returns super's implementation otherwise.
 * Default: NO
 */
@property (nonatomic) BOOL accessibilityDisabled;

/*
 * If not nil, accessibilityLabel returns value returned by this block, otherwise super's implementation
 * Default: nil
 */
@property (nonatomic, copy) NSString* (^accessibilityLabelBlock)(void);

/*
 * If not nil, accessibilityHint returns value returned by this block, otherwise super's implementation
 * Default: nil
 */
@property (nonatomic, copy) NSString* (^accessibilityHintBlock)(void);

/*
 * If not nil, accessibilityValue returns value returned by this block, otherwise super's implementation
 * Default: nil
 */
@property (nonatomic, copy) NSString* (^accessibilityValueBlock)(void);

/*
 * If not nil, accessibilityTraits returns value returned by this block, otherwise super's implementation
 * Default: nil
 */
@property (nonatomic, copy) UIAccessibilityTraits (^accessibilityTraitsBlock)(void);

@end
