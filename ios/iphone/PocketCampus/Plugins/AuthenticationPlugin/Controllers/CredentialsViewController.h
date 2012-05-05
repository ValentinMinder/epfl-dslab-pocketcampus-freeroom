
#import <UIKit/UIKit.h>

#import "AuthenticationService.h"

#import "authentication.h"

@interface CredentialsViewController : UIViewController<UITextViewDelegate, AuthenticationServiceDelegate> {
    IBOutlet UITextField* usernameField;
    IBOutlet UITextField* passwordField;
    IBOutlet UIButton* loginButton;
    IBOutlet UIActivityIndicatorView* centerActivityIndicator;
    IBOutlet UILabel* serviceTitleLabel;
    IBOutlet UILabel* centerMessageLabel;
    NSString* username;
    NSString* password;
    NSString* tequilaCookie;
    //int typeOfService; // temporary
    TequilaKey* applicationTequilaKey; // temporary
    int state;
    AuthenticationService* authenticationService;
    
    UIViewController<AuthenticationCallbackDelegate>* iViewController;
}

- (IBAction)usernameFieldEditingDidEnd:(id)sender;
- (IBAction)passwordFieldEditingDidEnd:(id)sender;
- (IBAction)loginButtonTouchUpInside:(id)sender;
- (IBAction)cancelButtonTouchUpInside:(id)sender;

- (id)initWithCallback:(UIViewController<AuthenticationCallbackDelegate>*)aViewController;

@end
