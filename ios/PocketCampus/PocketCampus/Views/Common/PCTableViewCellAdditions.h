//
//  PCTableViewCellWithDownloadIndication.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

extern NSString* PCTableViewCellAdditionsDefaultTextLabelTextStyle;
extern NSString* PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle;

@interface PCTableViewCellAdditions : UITableViewCell

/*
 * If both are YES, only favorite indication is visible
 */
@property (nonatomic, getter = isDownloadedIndicationVisible) BOOL downloadedIndicationVisible;
@property (nonatomic, getter = isFavoriteIndicationVisible) BOOL favoriteIndicationVisible;

@property (nonatomic, getter = isDurablySelected) BOOL durablySelected;

/*
 * If not nil, the text that does not match in textLabel si set to color textLabelDimmedColor
 * Default: nil
 */
@property (nonatomic, strong) NSRegularExpression* textLabelHighlightedRegex;

/*
 * Color for dimmed text in textLabel (used in combination with textLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* textLabelDimmedColor;

/*
 * If not nil, the text that does not match in textLabel si set to color detailTextLabelDimmedColor
 * Default: nil
 */
@property (nonatomic, strong) NSRegularExpression* detailTextLabelHighlightedRegex;

/*
 * Color for dimmed text in textLabel (used in combination with detailTextLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* detailTextLabelDimmedColor;

/*
 * Returns an ideal cell height that fits content yet not too small, based on a cell style, textLabel and detailTextLabel font 'Text Styles' (See [UIFont preferredFontForTextStyle:])
 * If you use this method, you should use same styles once constructing the cell (style and fonts).
 * If you pass nil for a text style, the required text height for the corresponding label is considered 0.0.
 * The result of this method is not cached.
 * The implementation assumes that the cell has no image and labels have nubmerOfLines set to 1.
 */
+ (CGFloat)preferredHeightForStyle:(UITableViewCellStyle)style textLabelTextStyle:(NSString*)textLabelTextStyle detailTextLabelTextStyle:(NSString*)detailTextLabelTextStyle;

/*
 * Same as previous, with textLabelTextStyle = PCTableViewCellAdditionsDefaultTextLabelTextStyle
 * and detailTextLabelTextStyle = PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle
 */
+ (CGFloat)preferredHeightForDefaultTextStylesForCellStyle:(UITableViewCellStyle)style;

@end
