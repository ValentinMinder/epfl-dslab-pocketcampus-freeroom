/* 
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of PocketCampus.Org nor the
 * 	  names of its contributors may be used to endorse or promote products
 * 	  derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */






//  Created by Loïc Gardiol on 15.01.13.



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
        CLSNSLog(@"!! WARNING: Exception caught in toggleFromBarButtonItem:animated: because of weakness of UIActionSheet API. You should release the action sheet in actionSheet:didDismissWithButtonIndex:");
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
