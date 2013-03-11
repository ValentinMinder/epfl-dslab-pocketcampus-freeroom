//
//  PCTableViewWithRemoteThumbnails.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 06.03.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "ASIHTTPRequest.h"

#import "ASINetworkQueue.h"

#import "Reachability.h"

@interface PCTableViewWithRemoteThumbnails : UITableView<ASIHTTPRequestDelegate>

/*
 * A request will be started to fetch the picture. imageUpdatedBlock is executed when cell.imageView.image has been updated
 * with the new image. You can influence on image appearance / layout by using this block (for example changing contentMode, then
 * you MUST call layoutSubviews on the cell). NULL is accepted for imageUpdatedBlock.
 */
- (void)setThumbnailURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath;

@property (nonatomic, strong) UIImage* temporaryThumnail;
@property (nonatomic) NSTimeInterval thumbnailsCacheSeconds;

@end
