//
//  UIPopoverController+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIPopoverController (Additions)

/*
 * Call this method from action handler of bar button item to show popover or automatically dismiss it
 * if it is already visible.
 */
- (void)togglePopoverFromBarButtonItem:(UIBarButtonItem *)item permittedArrowDirections:(UIPopoverArrowDirection)arrowDirections animated:(BOOL)animated;

/*
 * Call this method from action handler of view to show popover or automatically dismiss it
 * if it is already visible.
 */
- (void)togglePopoverFromRect:(CGRect)rect inView:(UIView *)view permittedArrowDirections:(UIPopoverArrowDirection)arrowDirections animated:(BOOL)animated;
@end
