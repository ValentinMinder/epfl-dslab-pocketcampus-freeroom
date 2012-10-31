//
//  MainMenuItem.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import "MainMenuItem.h"

@interface MainMenuItem ()

@end

@implementation MainMenuItem

- (id)initWithType:(MainMenuItemType)type
{
    self = [super init];
    if (self) {
        _type = type;
        _identifier = nil;
    }
    return self;
}

+ (MainMenuItem*)menuItemThinSeparator {
    MainMenuItem* instance = [[[self class] alloc] initWithType:MainMenuItemTypeThinSeparator];
    return instance;
}

+ (MainMenuItem*)menuItemSectionHeaderWithTitle:(NSString*)title {
    MainMenuItem* instance = [[[self class] alloc] initWithType:MainMenuItemTypeSectionHeader];
    instance.title = title;
    return instance;
}

+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image identifier:(NSString*)identifier {
    MainMenuItem* instance = [[[self class] alloc] initWithType:MainMenuItemTypeButton];
    instance.title = title;
    instance.leftImage = image;
    instance.identifier = identifier;
    return instance;
}

- (NSString*)description {
    return [NSString stringWithFormat:@"<MainMenuItem> type:%d title:%@ leftImage:%@ identifier:%@", self.type, self.title, self.leftImage, self.identifier];
}

@end
