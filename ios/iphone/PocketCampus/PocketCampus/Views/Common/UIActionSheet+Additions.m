//
//  UIActionSheet+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "UIActionSheet+Additions.h"

@implementation UIActionSheet (Additions)

- (void)toggleFromBarButtonItem:(UIBarButtonItem *)item animated:(BOOL)animated {
    if (self.isVisible) {
        [self dismissWithClickedButtonIndex:self.cancelButtonIndex animated:animated];
    } else {
        [self showFromBarButtonItem:item animated:animated];
    }
}

- (void)toggleFromRect:(CGRect)rect inView:(UIView *)view animated:(BOOL)animated {
    if (self.visible) {
        [self dismissWithClickedButtonIndex:self.cancelButtonIndex animated:animated];
    } else {
        [self showFromRect:rect inView:view animated:animated];
    }
}

@end
