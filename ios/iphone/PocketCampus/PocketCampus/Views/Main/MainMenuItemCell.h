//
//  MainMenuItemCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainMenuItem.h"

@interface MainMenuItemCell : UITableViewCell

@property (nonatomic) MainMenuItemType menuItemType;
@property (nonatomic, copy) NSString* reuseIdentifier;
@property (nonatomic, weak) IBOutlet  UIImageView* leftImageView;
@property (nonatomic, weak) IBOutlet  UILabel* titleLabel;

+ (MainMenuItemCell*)cellWithMainMenuItemType:(MainMenuItemType)type reuseIdentifier:(NSString *)reuseIdentifier;
+ (CGFloat)heightForMainMenuItemType:(MainMenuItemType)type;

@end
