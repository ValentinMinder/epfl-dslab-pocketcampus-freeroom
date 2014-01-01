//
//  PCTableViewCellWithDownloadIndication.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

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
 * Color for dimmed text in textLabel (used by textLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* textLabelDimmedColor;

/*
 * If not nil, the text that does not match in textLabel si set to color detailTextLabelDimmedColor
 * Default: nil
 */
@property (nonatomic, strong) NSRegularExpression* detailTextLabelHighlightedRegex;

/*
 * Color for dimmed text in textLabel (used by detailTextLabelHighlightedRegex)
 * Default: [UIColor grayColor] (uses that value if nil);
 */
@property (nonatomic, strong) UIColor* detailTextLabelDimmedColor;

@end
