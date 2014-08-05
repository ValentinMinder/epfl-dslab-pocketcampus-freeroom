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

@interface PCDatePickerView ()

@property (nonatomic, strong) IBOutlet UINavigationBar* navBar;
@property (nonatomic, readwrite, strong) IBOutlet UIDatePicker* datePicker;

@property (nonatomic, weak) UITextField* textFieldForInputView; //weak because inputView already retains us (=> prevent retain cycle)

@end

@implementation PCDatePickerView

#pragma mark - Init

- (instancetype)init
{
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:NSStringFromClass([PCDatePickerView class]) owner:nil options:nil];
    self = (PCDatePickerView*)elements[0];
    if (self) {
        self.navBar.topItem.title = NSLocalizedStringFromTable(@"SelectDate", @"PocketCampus", nil);
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    return [self init];
}

#pragma mark - Buttons actions

- (IBAction)doneTapped {
    if (self.userValidatedDateBlock) {
        self.userValidatedDateBlock(self, self.datePicker.date);
    }
}

- (IBAction)cancelTapped {
    if (self.userCancelledBlock) {
        self.userCancelledBlock(self);
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
}

- (void)dismiss {
    if (!self.textFieldForInputView.superview) {
        //not presented
        return;
    }
    [self.textFieldForInputView resignFirstResponder];
    [self.textFieldForInputView removeFromSuperview];
    self.textFieldForInputView = nil;
}

@end
