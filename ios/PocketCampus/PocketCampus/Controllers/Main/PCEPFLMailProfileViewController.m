//
//  PCEPFLMailProfileViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 17.04.15.
//  Copyright (c) 2015 PocketCampus.Org. All rights reserved.
//

#import "PCEPFLMailProfileViewController.h"

#import "MBProgressHUD.h"

#import "AuthenticationService.h"

#import "AuthenticationController.h"

@interface PCEPFLMailProfileViewController ()<AuthenticationServiceDelegate>

@property (nonatomic, strong) AuthenticationService* authService;

@end

@implementation PCEPFLMailProfileViewController

#pragma mark - Init

- (instancetype)init {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.authService = [AuthenticationService sharedInstanceToRetain];
        self.title = NSLocalizedStringFromTable(@"EPFLMail", @"PocketCampus", nil);
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

#pragma mark - Actions

- (IBAction)startTapped {
    [self startGetUserAttribtesRequest];
}

- (void)cancelTapped {
    [self.authService cancelOperationsForDelegate:self];
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [MBProgressHUD hideHUDForView:self.view animated:YES];
}

#pragma mark - Private

- (void)startGetUserAttribtesRequest {
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    MBProgressHUD* hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.removeFromSuperViewOnHide = YES;
    hud.labelText = NSLocalizedStringFromTable(@"Preparing", @"PocketCampus", nil);
    hud.detailsLabelText = NSLocalizedStringFromTable(@"TapToCancel", @"PocketCampus", nil);
    UITapGestureRecognizer* cancelGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(cancelTapped)];
    [hud addGestureRecognizer:cancelGesture];
    
    NSMutableArray* attributes = [NSMutableArray arrayWithObjects:kAuthenticationEmailUserAttributeName, kAuthenticationGasparUserAttributeName, nil];
    NSString* sessionId = [AuthenticationController sharedInstance].pocketCampusAuthSessionId ?: @"dummy";
    UserAttributesRequest* request = [[UserAttributesRequest alloc] initWithSessionId:sessionId attributeNames:attributes];
    [self.authService cancelOperationsForDelegate:self];
    [self.authService getUserAttributesWithRequest:request delegate:self];
}

- (void)openBrowserForProfileWithUsername:(NSString*)username email:(NSString*)email {
#warning REMOVE TEST
    NSString* urlString = [NSString stringWithFormat:@"https://test-pocketcampus.epfl.ch/backend/generate_configuration_profile.php?config=email&gaspar=%@&email=%@&lang=%@", username, email, [PCUtils userLanguageCode]];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlString]];
}

#pragma mark - AuthenticationServiceDelegate

- (void)getUserAttributesForRequest:(UserAttributesRequest *)request didReturn:(UserAttributesResponse *)response {
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    switch (response.statusCode) {
        case AuthStatusCode_OK:
        {
            if (response.userAttributes.count != 2) {
                [self getUserAttributesFailedForRequest:request];
                return;
            }
            NSString* email = response.userAttributes[0];
            NSString* gaspar = response.userAttributes[1];
            if (email.length == 0 || gaspar.length == 0) {
                [self getUserAttributesFailedForRequest:request];
                return;
            }
            [self openBrowserForProfileWithUsername:gaspar email:email];
            break;
        }
        case AuthStatusCode_INVALID_SESSION:
        {
            __weak __typeof(self) welf = self;
            [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                [welf startGetUserAttribtesRequest];
            } userCancelled:^{
                // Nothing to do
            } failure:^(NSError *error) {
                [welf getUserAttributesFailedForRequest:request];
            }];
            break;
        }
        default:
            [self getUserAttributesFailedForRequest:request];
            break;
    }
}

- (void)getUserAttributesFailedForRequest:(UserAttributesRequest *)request {
    [MBProgressHUD hideHUDForView:self.view animated:NO];
    [PCUtils showServerErrorAlert];
}

- (void)serviceConnectionToServerFailed {
    [MBProgressHUD hideHUDForView:self.view animated:NO];
    [PCUtils showConnectionToServerTimedOutAlert];
}

#pragma mark - Dealloc

- (void)dealloc
{
    [self.authService cancelOperationsForDelegate:self];
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
}

@end
