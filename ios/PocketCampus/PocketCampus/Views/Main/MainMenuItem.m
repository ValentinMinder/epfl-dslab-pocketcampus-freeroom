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

+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image identifier:(NSString*)identifier __attribute__ ((deprecated)) {
    return [self menuItemButtonWithTitle:title leftImage:image highlightedLeftImage:nil identifier:identifier];
}

+ (MainMenuItem*)menuItemButtonWithTitle:(NSString*)title leftImage:(UIImage*)image highlightedLeftImage:(UIImage*)highlightedImage identifier:(NSString*)identifier; {
    MainMenuItem* instance = [[[self class] alloc] init];
    instance.title = title;
    instance.leftImage = image;
    instance.highlightedLeftImage = highlightedImage;
    instance.identifier = identifier;
    return instance;
}

- (NSString*)description {
    return [NSString stringWithFormat:@"<MainMenuItem> title:%@ subtitle:%@ leftImage:%@ identifier:%@", self.title, self.subtitle, self.leftImage, self.identifier];
}

@end
