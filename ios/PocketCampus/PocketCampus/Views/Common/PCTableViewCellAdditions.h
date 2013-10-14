//
//  PCTableViewCellWithDownloadIndication.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 16.12.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PCTableViewCellAdditions : UITableViewCell

@property (nonatomic, getter = isDownloadedIndicationVisible) BOOL downloadedIndicationVisible;
@property (nonatomic, getter = isDurablySelected) BOOL durablySelected;

@end
