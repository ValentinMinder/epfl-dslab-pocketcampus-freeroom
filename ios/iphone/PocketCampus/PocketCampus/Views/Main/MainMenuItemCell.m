//
//  MainMenuItemCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainMenuItemCell.h"

#import "PCValues.h"

@interface MainMenuItemCell ()

@property (nonatomic, readwrite, strong) MainMenuItem* menuItem;

@end

@implementation MainMenuItemCell

+ (MainMenuItemCell*)cellWithMainMenuItem:(MainMenuItem*)menuItem reuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"MainMenuItemCell" owner:self options:nil];
    MainMenuItemCell* instance = [topLevelObjects objectAtIndex:0];
    instance.menuItem = menuItem;
    instance.reuseIdentifier = reuseIdentifier;
    [instance load];
    return instance;
}

+ (CGFloat)heightForMainMenuItemType:(MainMenuItemType)type {
    if (type == MainMenuItemTypeButton) {
        return 55.0;
    } else if (type == MainMenuItemTypeSectionHeader) {
        return 55.0;
    } else if (type == MainMenuItemTypeThinSeparator) {
        return 3.0;
    } else {
        @throw [NSException exceptionWithName:@"Illegal argument" reason:@"unsupported MainMenuItem type" userInfo:nil];
        return 0.0;
    }
}

- (void)setEyeButtonState:(EyeButtonState)state {
    switch (state) {
        case EyeButtonStateButtonHidden:
            self.eyeButton.alpha = 0.0;
            break;
        case EyeButtonStateDataHidden:
            self.eyeButton.alpha = 0.4;
            [self.eyeButton setImage:[UIImage imageNamed:@"EyeBlackCrossed"] forState:UIControlStateNormal];
            break;
        case EyeButtonStateDataVisible:
            self.eyeButton.alpha = 0.8;
            [self.eyeButton setImage:[UIImage imageNamed:@"EyeBlack"] forState:UIControlStateNormal];
            break;
        default:
            @throw [NSException exceptionWithName:@"Illegal argument" reason:@"unsupported EyeButtonState" userInfo:nil];
            break;
    }
}

- (void)load
{
    UIView* colorView = [[UIView alloc] init];
    if (self.menuItem.type == MainMenuItemTypeButton) {
        colorView.backgroundColor = [UIColor clearColor];
        self.titleLabel.textColor = [PCValues textColor1];
        self.titleLabel.font = [UIFont boldSystemFontOfSize:19.0];
        self.titleLabel.shadowColor = [UIColor whiteColor];
        self.titleLabel.shadowOffset = [PCValues shadowOffset1];
        UIView* selectedBackgroundView = [[UIView alloc] initWithFrame:self.frame];
        selectedBackgroundView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        selectedBackgroundView.backgroundColor = [UIColor colorWithWhite:0.75 alpha:1.0];
        self.selectedBackgroundView = selectedBackgroundView;
    } else if (self.menuItem.type == MainMenuItemTypeSectionHeader) {
        //Nothing, not supported yet
    } else if (self.menuItem.type == MainMenuItemTypeThinSeparator) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        colorView.backgroundColor = [UIColor colorWithWhite:0.5 alpha:1.0];
    } else {
        NSLog(@"!! ERROR: unsupported MainMenuItem type property at loads");
    }
    self.backgroundView = colorView;
    /*CGRect colorFrame = self.backgroundView.frame;
    colorFrame.origin.y = -10.0;
    colorFrame.size.height += 10.0;
    colorView.frame = colorFrame;*/
    
}

- (IBAction)eyeButtonPressed {
    if ([self.eyeButtonDelegate respondsToSelector:@selector(eyeButtonPressedForMenuItemCell:)]) {
        [self.eyeButtonDelegate eyeButtonPressedForMenuItemCell:self];
    }
}

- (void)setEditing:(BOOL)editing animated:(BOOL)animated {
    [super setEditing:editing animated:animated];
    self.eyeButton.hidden = !editing;
}

@end
