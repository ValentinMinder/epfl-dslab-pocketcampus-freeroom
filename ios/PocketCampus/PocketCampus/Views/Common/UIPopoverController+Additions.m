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
    [self togglePopoverFromBarButtonItem:item permittedArrowDirections:arrowDirections othersToDismiss:nil animated:animated];
}

- (void)togglePopoverFromBarButtonItem:(UIBarButtonItem *)item permittedArrowDirections:(UIPopoverArrowDirection)arrowDirections othersToDismiss:(NSArray*)othersToDismiss animated:(BOOL)animated {
    for (id presentedElement in othersToDismiss) {
        if ([presentedElement isKindOfClass:[UIPopoverController class]]) {
            UIPopoverController* popover = (UIPopoverController*)(presentedElement);
            if (popover.isPopoverVisible) {
                [popover dismissPopoverAnimated:NO];
            }
        } else if ([presentedElement isKindOfClass:[UIActionSheet class]]) {
            UIActionSheet* actionSheet = (UIActionSheet*)(presentedElement);
            if (actionSheet.isVisible) {
                [actionSheet dismissWithClickedButtonIndex:actionSheet.cancelButtonIndex animated:NO];
            }
        }
    }
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
