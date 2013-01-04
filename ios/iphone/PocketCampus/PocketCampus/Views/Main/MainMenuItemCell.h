//
//  MainMenuItemCell.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "MainMenuItem.h"

typedef enum {
    EyeButtonStateButtonHidden = 0,
    EyeButtonStateDataVisible,
    EyeButtonStateDataHidden,
} EyeButtonState;

@protocol EyeButtonDelegate;

@interface MainMenuItemCell : UITableViewCell

@property (nonatomic, readonly, strong) MainMenuItem* menuItem;

@property (nonatomic, weak) id<EyeButtonDelegate> eyeButtonDelegate;


@property (nonatomic, copy) NSString* reuseIdentifier;

@property (nonatomic, weak) IBOutlet UIImageView* leftImageView;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UIButton* eyeButton;

@property (nonatomic) EyeButtonState eyeButtonState; //Eye button can only appear during in editing mode

+ (MainMenuItemCell*)cellWithMainMenuItem:(MainMenuItem*)menuItem reuseIdentifier:(NSString *)reuseIdentifier;
+ (CGFloat)heightForMainMenuItemType:(MainMenuItemType)type;

@end

@protocol EyeButtonDelegate <NSObject>

- (void)eyeButtonPressedForMenuItemCell:(MainMenuItemCell*)cell;

@end
