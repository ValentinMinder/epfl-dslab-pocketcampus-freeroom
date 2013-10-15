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

@property (nonatomic, weak) IBOutlet UIImageView* leftImageView;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UILabel* subtitleLabel;
@property (nonatomic, weak) IBOutlet UIButton* eyeButton;

@property (nonatomic, weak) IBOutlet UIView* line1;
@property (nonatomic, weak) IBOutlet UIView* line2;

@property (nonatomic, readwrite, strong) MainMenuItem* menuItem;

@end

@implementation MainMenuItemCell

+ (MainMenuItemCell*)cellWithMainMenuItem:(MainMenuItem*)menuItem reuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"MainMenuItemCell" owner:self options:nil];
    MainMenuItemCell* instance = [topLevelObjects objectAtIndex:0];
    instance.menuItem = menuItem;
    instance.reuseIdentifier = reuseIdentifier;
    instance.selectionStyle = UITableViewCellSelectionStyleDefault;
    instance.titleLabel.textColor = [PCValues textColor1];
    return instance;
}

+ (CGFloat)height {
    return 55.0;
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

- (void)setMenuItem:(MainMenuItem *)menuItem {
    [PCUtils throwExceptionIfObject:menuItem notKindOfClass:[MainMenuItem class]];
    _menuItem = menuItem;
    self.titleLabel.text = menuItem.title;
    self.subtitleLabel.text = menuItem.subtitle;
    self.leftImageView.image = menuItem.leftImage;
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

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    self.leftImageView.image = highlighted && self.menuItem.highlightedLeftImage ? self.menuItem.highlightedLeftImage : self.menuItem.leftImage;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    self.leftImageView.image = selected && self.menuItem.highlightedLeftImage ? self.menuItem.highlightedLeftImage : self.menuItem.leftImage;
}

@end
