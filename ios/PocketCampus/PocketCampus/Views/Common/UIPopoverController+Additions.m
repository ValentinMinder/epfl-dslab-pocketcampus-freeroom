//
//  UIPopoverController+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "UIPopoverController+Additions.h"

@implementation UIPopoverController (Additions)

- (void)togglePopoverFromBarButtonItem:(UIBarButtonItem *)item permittedArrowDirections:(UIPopoverArrowDirection)arrowDirections animated:(BOOL)animated {
    if (self.popoverVisible) {
        [self dismissPopoverAnimated:animated];
    } else {
        [self presentPopoverFromBarButtonItem:item permittedArrowDirections:arrowDirections animated:animated];
    }
}

- (void)togglePopoverFromRect:(CGRect)rect inView:(UIView *)view permittedArrowDirections:(UIPopoverArrowDirection)arrowDirections animated:(BOOL)animated {
    if (self.popoverVisible) {
        [self dismissPopoverAnimated:animated];
    } else {
        [self presentPopoverFromRect:rect inView:view permittedArrowDirections:arrowDirections animated:animated];
    }
}

@end
