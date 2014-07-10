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

//  Modified by Loïc Gardiol on 25.05.14.

typedef enum {
    /**
     *
     */
    AuthenticationViewControllerStateAskCredentials = 0,
    AuthenticationViewControllerStateLoading,
    AuthenticationViewControllerStateWrongCredentials,
    AuthenticationViewControllerStateLoggedIn
} AuthenticationViewControllerState;

@interface AuthenticationViewController : UITableViewController

+ (NSString*)localizedTitle;

/**
 * Setting a new state will reload the view to show corresponding UI
 * Default: 0
 */
@property (nonatomic) AuthenticationViewControllerState state;

- (void)setState:(AuthenticationViewControllerState)state animated:(BOOL)animated;

/**
 * If YES, a Cancel button is set as left nav bar item
 * userTappedCancel is executed when tapped.
 * Default: NO
 */
@property (nonatomic) BOOL showCancelButton;

/**
 * Allows to set wether a Done button is visible in right navigation item, for each state.
 * Default: hidden for all states.
 */
- (void)setShowDoneButton:(BOOL)showDoneButton forState:(AuthenticationViewControllerState)state;

/**
 * AuthenticationViewControllerStateAskCredentials,
 * AuthenticationViewControllerStateLoading,
 * AuthenticationViewControllerStateWrongCredentials:
 *      - If YES, shows a "Save password" switch. Value is reflected in login block.
 *
 * Ignored in user states.
 *
 * Default: NO
 */
@property (nonatomic) BOOL showSavePasswordSwitch;

/**
 * AuthenticationViewControllerStateAskCredentials,
 * AuthenticationViewControllerStateLoading,
 * AuthenticationViewControllerStateWrongCredentials:
 *      - Reflects and sets "Save password" switch isOn value
 *
 * Ignored in user states.
 *
 * Default: YES (YES if showSavePasswordSwitch is NO)
 */
@property (nonatomic) BOOL savePasswordSwitchValue;

/**
 * AuthenticationViewControllerStateAskCredentials,
 * AuthenticationViewControllerStateLoading,
 * AuthenticationViewControllerStateWrongCredentials:
 *      - Used to prefill username text field
 *
 * AuthenticationViewControllerStateLoggedIn:
 *      - Used to indicate logged in username
 */
@property (nonatomic, copy) NSString* username;

/**
 * AuthenticationViewControllerStateAskCredentials,
 * AuthenticationViewControllerStateLoading,
 *      - Used to prefill password text field
 *
 * Ignored in user states.
 */
@property (nonatomic, copy) NSString* password;

/**
 * Executed when, in state AuthenticationViewControllerStateAskCredentials or AuthenticationViewControllerStateWrongCredentials,
 * user has filled in credentials and pressed login.
 * IMPORTANT: values of self.username, self.password are updated just before calling this block
 */
@property (nonatomic, copy) void (^loginBlock)(NSString* username, NSString* password, BOOL savePassword);

/**
 * Executed when, in state AuthenticationViewControllerStateLoggedIn,
 * user pressed log out.
 * IMPORTANT: values of self.username and self.password are nullified just before calling this block
 */
@property (nonatomic, copy) void (^logoutBlock)();

/**
 * Executed when, in state AuthenticationViewControllerStateAskCredentials or AuthenticationViewControllerStateWrongCredentials
 * user pressed x button in username text field
 */
@property (nonatomic, copy) void (^userClearedUsernameBlock)();

/**
 * Executed when Cancel button is tapped
 */
@property (nonatomic, copy) void (^userTappedCancelBlock)();

/**
 * Executed when Done button is tapped
 */
@property (nonatomic, copy) void (^userTappedDoneBlock)();

/**
 * Should return message (or nil) to display below the inputs
 * based on current state and values of properties of authViewController
 */
@property (nonatomic, copy) NSString* (^bottomMessageBlock)(AuthenticationViewController* authViewController);

/**
 * Brings up the keyboard for either username or password depending on
 * what's filled already
 */
- (void)focusOnInput;

@end
