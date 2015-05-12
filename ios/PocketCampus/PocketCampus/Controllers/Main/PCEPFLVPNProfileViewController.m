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

//  Created by Loïc Gardiol on 12.05.15.

#import "PCEPFLVPNProfileViewController.h"

#import "MBProgressHUD.h"

#import "AuthenticationService.h"

#import "AuthenticationController.h"

#import "AFNetworking.h"

@interface PCEPFLVPNProfileViewController ()<AuthenticationServiceDelegate>

@property (nonatomic, strong) AuthenticationService* authService;

@end

@implementation PCEPFLVPNProfileViewController

#pragma mark - Init

- (instancetype)init {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.authService = [AuthenticationService sharedInstanceToRetain];
        self.title = NSLocalizedStringFromTable(@"EPFLVPN", @"PocketCampus", nil);
        self.gaiScreenName = @"/dashboard/settings/vpnconfig";
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

#pragma mark - Actions

- (IBAction)startTapped {
    [self trackAction:@"SetupVPN"];
    [self startGetUserAttribtesRequest]; // need to verify that session is valid before openining raw request in browser (openBrowser)
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

- (void)openBrowser {
    NSError* error = nil;
    NSURLRequest* request = [[AFHTTPRequestSerializer serializer] requestBySerializingRequest:[[AuthenticationService sharedInstanceToRetain] pcProxiedRequest] withParameters:@{@"config":@"vpn"} error:&error];
    if (error) {
        [PCUtils showUnknownErrorAlertTryRefresh:NO];
        return;
    }
    [[UIApplication sharedApplication] openURL:request.URL];
}

#pragma mark - AuthenticationServiceDelegate

- (void)getUserAttributesForRequest:(UserAttributesRequest *)request didReturn:(UserAttributesResponse *)response {
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    switch (response.statusCode) {
        case AuthStatusCode_OK:
        {
            [self openBrowser];
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
