//
//  UIActionSheet+Additions.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.01.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIActionSheet (Additions)

/*
 * Call this method from action handler of bar button item to show action sheet or dismiss it
 * with clicked button index cancel automatically if it is already visible
 *
 * IMPORTANT: you must release the action sheet when the delegate method actionSheet:didDismissWithButtonIndex: 
 * has been called. Otherwise, repeated toggling will cause the app to crash (API weakness).
 */
- (void)toggleFromBarButtonItem:(UIBarButtonItem *)item animated:(BOOL)animated;


/*
 * Call this method from action handler of view to show action sheet or automatically dismiss it
 * with clicked button index cancel if it is already visible.
 */
- (void)toggleFromRect:(CGRect)rect inView:(UIView *)view animated:(BOOL)animated;

@end
