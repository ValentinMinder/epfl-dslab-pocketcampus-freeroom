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

@interface PCDatePickerView : UIView

- (instancetype)init;

@property (nonatomic, readonly, strong) IBOutlet UIDatePicker* datePicker;

/**
 * Text that is displayed above the date picker
 * Default: localized "Select a date"
 */
@property (nonatomic, copy) NSString* title;

/**
 * If YES, a Today button is displayed. Action can be handled
 * by setting userTappedTodayBlock.
 * Default: NO
 */
@property (nonatomic) BOOL showTodayButton;

/**
 * Executed when user taps on the Done button.
 * You should typically dismiss the view then.
 */
@property (nonatomic, copy) void (^userValidatedDateBlock)(PCDatePickerView* view, NSDate* date);

/**
 * Executed when tuser taps on the Cancel button
 * You should typically dismiss the view then.
 */
@property (nonatomic, copy) void (^userCancelledBlock)(PCDatePickerView* view);

/**
 * Executed when user taps on the Today button.
 */
@property (nonatomic, copy) void (^userTappedTodayBlock)(PCDatePickerView* view);


- (void)presentInView:(UIView*)view;

- (void)presentFromBarButtonItem:(UIBarButtonItem*)barButtonItem;

- (void)dismiss;

@end
