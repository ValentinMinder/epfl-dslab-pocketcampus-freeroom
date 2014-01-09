//
//  UISearchBar+Additions.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 09.01.14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "UISearchBar+Additions.h"

@implementation UISearchBar (Additions)

- (void)setBackgroundViewTransparent {
    for (UIView* view in self.allSubviews) {
        if ([NSStringFromClass(view.class) isEqualToString:@"UISearchBarBackground"]) {
            view.layer.opacity = 0.0;
            break;
        }
    }
}

@end
