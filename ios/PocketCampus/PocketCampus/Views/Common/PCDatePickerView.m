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

//  Created by Loïc Gardiol on 28.06.14.

#import "PCDatePickerView.h"

@interface PCDatePickerView ()<UIPopoverControllerDelegate>

@property (nonatomic, strong) IBOutlet UINavigationBar* navBar;
@property (nonatomic, readwrite, strong) IBOutlet UIDatePicker* datePicker;

@property (nonatomic, weak) UITextField* textFieldForInputView; //weak because inputView already retains us (=> prevent retain cycle)

@property (nonatomic, strong) UIPopoverController* popoverController;

@property (nonatomic, strong) UIView* dimmingView;

@end

@implementation PCDatePickerView

#pragma mark - Init

- (instancetype)init
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:NSStringFromClass([PCDatePickerView class]) owner:nil options:nil];
    self = (PCDatePickerView*)elements[0];
    if (self) {
        self.navBar.topItem.title = NSLocalizedStringFromTable(@"SelectDate", @"PocketCampus", nil);
        self.showTodayButton = NO; //defaut. Sets the bar items.
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    return [self init];
}

#pragma mark - Bar items

- (void)setShowTodayButton:(BOOL)showTodayButton {
    _showTodayButton = showTodayButton;
    UIBarButtonItem* cancelButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelTapped)];
    if (showTodayButton) {
        UIBarButtonItem* todayButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Today", @"PocketCampus", nil) style:UIBarButtonItemStylePlain target:self action:@selector(todayTapped)];
        self.navBar.topItem.leftBarButtonItems = @[cancelButton, todayButton];
    } else {
        self.navBar.topItem.leftBarButtonItem = cancelButton;
    }
    self.navBar.topItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneTapped)];
}

#pragma mark - Buttons actions

- (void)doneTapped {
    if (self.userValidatedDateBlock) {
        self.userValidatedDateBlock(self, self.datePicker.date);
    }
}

- (void)cancelTapped {
    if (self.userCancelledBlock) {
        self.userCancelledBlock(self);
    }
}

- (void)todayTapped {
    if (self.userTappedTodayBlock) {
        self.userTappedTodayBlock(self);
    }
}

#pragma mark - Public

- (NSString*)title {
    return self.navBar.topItem.title;
}

- (void)setTitle:(NSString *)title {
    self.navBar.topItem.title = title;
}

- (void)presentInView:(UIView*)view {
    [PCUtils throwExceptionIfObject:view notKindOfClass:[UIView class]];
    if (self.textFieldForInputView.superview == view && self.textFieldForInputView.isFirstResponder) {
        //already presented
        return;
    }
    [self dismiss];
    self.navBar.tintColor = view.tintColor;
    self.bounds = CGRectMake(0, 0, view.frame.size.width, self.frame.size.height);
    UITextField* textField = [[UITextField alloc] initWithFrame:CGRectMake(-100.0, 0, 1, 1)];
    textField.alpha = 0.0;
    [view addSubview:textField];
    textField.inputView = self;
    [textField becomeFirstResponder];
    self.textFieldForInputView = textField;
    [self setDimmingViewHidden:NO inView:view animated:YES];
}

- (void)presentFromBarButtonItem:(UIBarButtonItem*)barButtonItem {
    if (![PCUtils isIdiomPad]) {
        UIView* view = [[[UIApplication sharedApplication] windows] firstObject];
        [self presentInView:view];
        return;
    }
    [PCUtils throwExceptionIfObject:barButtonItem notKindOfClass:[UIBarButtonItem class]];
    if (self.popoverController.isPopoverVisible) {
        //already presented
        return;
    }
    if (!self.popoverController) {
        UIViewController* viewController = [[UIViewController alloc] init];
        viewController.view = self;
        viewController.preferredContentSize = self.bounds.size;
        self.popoverController = [[UIPopoverController alloc] initWithContentViewController:viewController];
        self.popoverController.delegate = self;
    }
    [self.popoverController presentPopoverFromBarButtonItem:barButtonItem permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
}

- (void)dismiss {
    if (self.textFieldForInputView.superview) {
        [self.textFieldForInputView resignFirstResponder];
        [self.textFieldForInputView removeFromSuperview];
        self.textFieldForInputView = nil;
        [self setDimmingViewHidden:YES inView:nil animated:YES];
    } else if (self.popoverController) {
        [self.popoverController dismissPopoverAnimated:YES];
        self.popoverController = nil;
    }
}

- (void)setDimmingViewHidden:(BOOL)hidden inView:(UIView*)view animated:(BOOL)animated {
    if (hidden) {
        [UIView animateWithDuration:animated ? 0.25 : 0.0 animations:^{
            self.dimmingView.alpha = 0.0;
        } completion:^(BOOL finished) {
            [self.dimmingView removeFromSuperview];
            self.dimmingView = nil;
        }];
    } else {
        if (!self.dimmingView) {
            self.dimmingView = [[UIView alloc] init];
            self.dimmingView.userInteractionEnabled = YES; //so that it prevents touches below
            self.dimmingView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            self.dimmingView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.35];
            self.dimmingView.alpha = 0.0;
            [self.dimmingView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(cancelTapped)]];
        }
        [view addSubview:self.dimmingView];
        self.dimmingView.frame = view.bounds;
        [UIView animateWithDuration:animated ? 0.25 : 0.0 animations:^{
            self.dimmingView.alpha = 1.0;
        } completion:^(BOOL finished) {
            //nothing
        }];
    }
}

#pragma mark - UIPopoverControllerDelegate

- (void)popoverControllerDidDismissPopover:(UIPopoverController *)popoverController {
    self.popoverController = nil;
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.dimmingView removeFromSuperview];
}

@end
