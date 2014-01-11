

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
