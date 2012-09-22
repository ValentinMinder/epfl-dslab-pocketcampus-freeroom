//
//  GasparViewController.h
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.07.12.
//  Copyright (c) 2012 EPFL. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AuthenticationService.h"

@class EditableTableViewCell;

typedef enum {
    PresentationModeModal, //when presented from bottom => will show cancel button in top bar
    PresentationModeNavStack, //when presented in nav stack normally. Will show a cancel button in the tableview
    PresentationModeTryHidden //silent authentication (user and pass are already stored) will still present on viewControllerForPresentation if no stored user or password
} PresentationMode;

@interface GasparViewController : UIViewController<UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate, AuthenticationServiceDelegate> {
    UITableView* tableView;
    UITextField* usernameTextField;
    UITextField* passwordTextField;
    UISwitch* savePasswordSwitch;
    EditableTableViewCell* usernameCell;
    EditableTableViewCell* passwordCell;
    UITableViewCell* loginCell;
    NSString* errorMessage;
    BOOL isLoggedIn;
    PresentationMode presentationMode;
    BOOL showSavePasswordSwitch; //if no, password is saved by default
    BOOL hideGasparUsageAccountMessage;
    UIActivityIndicatorView* loadingIndicator;
    UIViewController* viewControllerForPresentation;
    AuthenticationService* authenticationService;
    NSString* username;
    NSString* password;
    NSString* token;
}

@property (nonatomic, assign) IBOutlet UITableView* tableView;

@property (assign) id<AuthenticationCallbackDelegate> delegate;
@property (retain) NSString* token;

@property PresentationMode presentationMode;
@property BOOL showSavePasswordSwitch;
@property BOOL hideGasparUsageAccountMessage;
@property (assign) UIViewController* viewControllerForPresentation;

+ (NSString*)localizedTitle;

- (void)authenticateSilentlyToken:(NSString*)token_ delegate:(id<AuthenticationCallbackDelegate>)delegate_; //Warning, should be called only if user/pass already stored and presentationMode is PresentationModeHidden
- (void)focusOnInput;

@end
