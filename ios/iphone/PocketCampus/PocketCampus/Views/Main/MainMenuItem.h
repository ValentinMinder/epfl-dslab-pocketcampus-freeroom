//
//  MainMenuItem.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    MainMenuItemTypeButton = 0,
    MainMenuItemTypeThinSeparator,
    MainMenuItemTypeSectionHeader
} MainMenuItemType;

@interface MainMenuItem : NSObject

@property (nonatomic, readonly) MainMenuItemType type;

@property (nonatomic, copy) NSString* identifier;

/* will be ignored if type is MainMenuItemTypeThinSeparator */
@property (nonatomic, copy) NSString* title;

/* will be ignored if type is MainMenuItemTypeThinSeparator or MainMenuItemTypeSectionHeader */
@property (nonatomic, strong) UIImage* leftImage;

@property (nonatomic) BOOL hidden;

- (id)initWithType:(MainMenuItemType)type;

+ (MainMenuItem*)menuItemThinSeparator;
+ (MainMenuItem*)menuItemSectionHeaderWithTitle:(NSString*)title;
+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image identifier:(NSString*)identifier;

@end
