//
//  PCTableViewCellWithDownloadIndication.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCTableViewCellWithDownloadIndication : UITableViewCell

@property (nonatomic, getter = isDownloaded) BOOL downloaded;

@end
