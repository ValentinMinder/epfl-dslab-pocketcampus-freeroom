//
//  PCTableViewWithRemoteThumbnails.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

@import UIKit;

@interface PCTableViewWithRemoteThumbnails : UITableView

- (void)setImageURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath;

/*
 * If image as been downloaded, returns it, nil otherwise.
 * If imageProcessingBlock is not NULL, this method returns the processed image.
 */
- (UIImage*)imageAtIndexPath:(NSIndexPath*)indexPath;

/*
 * Same as previous but returns image as it was before being processed by imageProcessingBlock
 */
- (UIImage*)rawImageAtIndexPath:(NSIndexPath*)indexPath;

/*
 * Specify the keypath of the UIImageView in the cells.
 * Default: @"imageView"
 */
@property (nonatomic, strong) NSString* cellsImageViewSelectorString;

/*
 * This block will be executed when image is downloaded but before caching it.
 * If this block is not NULL, returned image replaces downloaded image. 
 * Default: NULL
 */
typedef UIImage* (^ImageProcessingBlock)(NSIndexPath* indexPath, UITableViewCell* cell, UIImage* image);
@property (nonatomic, copy) ImageProcessingBlock imageProcessingBlock;

@property (nonatomic, strong) UIImage* temporaryImage;
@property (nonatomic) NSTimeInterval imagesCacheSeconds;

@end
