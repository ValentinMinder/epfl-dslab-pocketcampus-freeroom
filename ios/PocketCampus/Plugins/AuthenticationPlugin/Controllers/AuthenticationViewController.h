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




//  Created by Loïc Gardiol on 17.07.12.


#import <UIKit/UIKit.h>

#import "AuthenticationService.h"

@class PCEditableTableViewCell;

typedef enum {
    PresentationModeModal, //when presented from bottom => will show cancel button in top bar
    PresentationModeNavStack, //when presented in nav stack normally. Will show a cancel button in the tableview
    PresentationModeTryHidden //silent authentication (user and pass are already stored) will still present on viewControllerForPresentation if no stored user or password
} PresentationMode;

@interface AuthenticationViewController : UITableViewController


@property (nonatomic, weak) id<AuthenticationCallbackDelegate> delegate;
@property (nonatomic, copy) NSString* token;

@property (nonatomic) PresentationMode presentationMode;
@property (nonatomic) BOOL showSavePasswordSwitch;
@property (nonatomic) BOOL hideGasparUsageAccountMessage;
@property (nonatomic, weak) UIViewController* viewControllerForPresentation;

+ (NSString*)localizedTitle;

- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationCallbackDelegate>)delegate_; //Warning, should be called only if user/pass already stored and presentationMode is PresentationModeHidden
- (void)focusOnInput;

@end
