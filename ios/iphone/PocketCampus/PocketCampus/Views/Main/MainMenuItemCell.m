//
//  MainMenuItemCell.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainMenuItemCell.h"

#import "PCValues.h"

@implementation MainMenuItemCell

+ (MainMenuItemCell*)cellWithMainMenuItemType:(MainMenuItemType)type reuseIdentifier:(NSString *)reuseIdentifier
{
    NSArray* topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"MainMenuItemCell" owner:self options:nil];
    MainMenuItemCell* instance = [topLevelObjects objectAtIndex:0];
    instance.menuItemType = type;
    instance.selectionStyle = UITableViewCellSelectionStyleNone;
    instance.reuseIdentifier = reuseIdentifier;
    return instance;
}

- (void)awakeFromNib
{
    UIView* colorView = [[UIView alloc] init];
    if (self.menuItemType == MainMenuItemTypeButton) {
        colorView.backgroundColor = [UIColor clearColor];
        self.titleLabel.textColor = [PCValues textColor1];
        self.titleLabel.font = [UIFont boldSystemFontOfSize:19.0];
        self.titleLabel.shadowColor = [UIColor whiteColor];
        self.titleLabel.shadowOffset = [PCValues shadowOffset1];
    } else {
        colorView.backgroundColor = [PCValues textColor1];
    }
    self.backgroundView = colorView;
    
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [self setSelected:highlighted animated:animated];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    if (self.menuItemType == MainMenuItemTypeThinSeparator) {
        return;
    }
    // Configure the view for the selected state
    if (selected) {
        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.2];
    } else {
        self.backgroundView.backgroundColor = [UIColor clearColor];
    }
}

@end
