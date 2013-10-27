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
    [self toggleFromBarButtonItem:item othersToDismiss:nil animated:animated];
}

- (void)toggleFromBarButtonItem:(UIBarButtonItem *)item othersToDismiss:(NSArray*)othersToDismiss animated:(BOOL)animated {
    @try {
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
        if (self.isVisible) {
            [self dismissWithClickedButtonIndex:[self cancelButtonIndex] animated:animated];
        } else {
            [self showFromBarButtonItem:item animated:animated];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"!! WARNING: Exception caught in toggleFromBarButtonItem:animated: because of weakness of UIActionSheet API. You should release the action sheet in actionSheet:didDismissWithButtonIndex:");
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
