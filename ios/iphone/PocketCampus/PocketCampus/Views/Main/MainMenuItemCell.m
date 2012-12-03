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
        NSLog(@"-> ERROR: unsupported MainMenuItem type property");
        return 0.0;
    }
}

- (void)load
{
    UIView* colorView = [[UIView alloc] init];
    if (self.menuItemType == MainMenuItemTypeButton) {
        colorView.backgroundColor = [UIColor clearColor];
        self.titleLabel.textColor = [PCValues textColor1];
        self.titleLabel.font = [UIFont boldSystemFontOfSize:19.0];
        self.titleLabel.shadowColor = [UIColor whiteColor];
        self.titleLabel.shadowOffset = [PCValues shadowOffset1];
        UIView* selectedBackgroundView = [[UIView alloc] initWithFrame:self.frame];
        selectedBackgroundView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        selectedBackgroundView.backgroundColor = [UIColor colorWithWhite:0.75 alpha:1.0];
        self.selectedBackgroundView = selectedBackgroundView;
    } else if (self.menuItemType == MainMenuItemTypeSectionHeader) {
        //TODO
    } else if (self.menuItemType == MainMenuItemTypeThinSeparator) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        colorView.backgroundColor = [UIColor colorWithWhite:0.5 alpha:1.0];
    } else {
        NSLog(@"-> ERROR: unsupported MainMenuItem type property");
    }
    self.backgroundView = colorView;
    /*CGRect colorFrame = self.backgroundView.frame;
    colorFrame.origin.y = -10.0;
    colorFrame.size.height += 10.0;
    colorView.frame = colorFrame;*/
    
}

@end
