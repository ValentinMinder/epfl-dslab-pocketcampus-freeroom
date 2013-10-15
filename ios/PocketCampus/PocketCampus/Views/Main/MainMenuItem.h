//
//  MainMenuItem.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 07.10.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MainMenuItem : NSObject

@property (nonatomic, copy) NSString* identifier;

/* will be ignored if type is MainMenuItemTypeThinSeparator */
@property (nonatomic, copy) NSString* title;
@property (nonatomic, copy) NSString* subtitle;

/* will be ignored if type is MainMenuItemTypeThinSeparator or MainMenuItemTypeSectionHeader */
@property (nonatomic, strong) UIImage* leftImage;
@property (nonatomic, strong) UIImage* highlightedLeftImage;

@property (nonatomic) BOOL hidden;

+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image identifier:(NSString*)identifier __attribute__ ((deprecated));

+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image highlightedLeftImage:(UIImage*)highlightedImage identifier:(NSString*)identifier;

@end
