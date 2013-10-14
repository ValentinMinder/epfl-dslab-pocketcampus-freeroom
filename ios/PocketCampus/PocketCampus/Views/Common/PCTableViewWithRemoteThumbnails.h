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

- (void)setThumbnailURL:(NSURL*)url forCell:(UITableViewCell*)cell atIndexPath:(NSIndexPath*)indexPath;

@property (nonatomic, strong) UIImage* temporaryThumnail;
@property (nonatomic) NSTimeInterval thumbnailsCacheSeconds;

@end
