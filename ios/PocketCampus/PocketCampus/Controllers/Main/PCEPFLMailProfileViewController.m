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

//  Created by Loïc Gardiol on 17.04.15.

#import "PCEPFLMailProfileViewController.h"

#import "MBProgressHUD.h"

#import "AuthenticationService.h"

#import "AuthenticationController.h"

#import "AFNetworking.h"

#import "PCWebViewController.h"

@interface PCEPFLMailProfileViewController ()

@property (nonatomic, strong) AFHTTPRequestOperation* profileURLOperation;

@end

@implementation PCEPFLMailProfileViewController

#pragma mark - Init

- (instancetype)init {
    self = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass(self.class) owner:nil options:nil] firstObject];
    if (self) {
        self.title = NSLocalizedStringFromTable(@"EPFLMail", @"PocketCampus", nil);
        self.gaiScreenName = @"/dashboard/settings/emailconfig";
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self trackScreen];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.view setNeedsLayout];
    });
}

- (NSUInteger)supportedInterfaceOrientations
{
    if ([PCUtils isIdiomPad]) {
        return UIInterfaceOrientationMaskAll;
    } else {
        return UIInterfaceOrientationMaskPortrait;
    }
    
}

#pragma mark - Actions

- (IBAction)startTapped {
    [self trackAction:@"SetupEmail"];
    [self startRequest]; // need to verify that session is valid before openining raw request in browser (openBrowser)
}

- (void)cancelTapped {
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [MBProgressHUD hideHUDForView:self.view animated:YES];
}

#pragma mark - Private

- (void)startRequest {
    [MBProgressHUD hideAllHUDsForView:self.view animated:YES];
    MBProgressHUD* hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.removeFromSuperViewOnHide = YES;
    hud.labelText = NSLocalizedStringFromTable(@"Preparing", @"PocketCampus", nil);
    hud.detailsLabelText = NSLocalizedStringFromTable(@"TapToCancel", @"PocketCampus", nil);
    UITapGestureRecognizer* cancelGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(cancelTapped)];
    [hud addGestureRecognizer:cancelGesture];
    
    NSError* error = nil;
    
    NSURLRequest* pcRequest = [[AuthenticationService sharedInstanceToRetain] pcProxiedRequest];
    NSURLRequest* request = [[AFHTTPRequestSerializer serializer] requestBySerializingRequest:pcRequest withParameters:@{@"config": @"email"} error:&error];
    
    if (error) {
        [PCUtils showUnknownErrorAlertTryRefresh:NO];
        return;
    }
    
    [self.profileURLOperation cancel];
    [self.profileURLOperation setCompletionBlockWithSuccess:NULL failure:NULL];
    
    __weak __typeof(self) welf = self;
    self.profileURLOperation = [[AFHTTPRequestOperationManager manager] HTTPRequestOperationWithRequest:request success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [MBProgressHUD hideHUDForView:welf.view animated:YES];
        if (![responseObject isKindOfClass:[NSDictionary class]]) {
            [PCUtils showUnknownErrorAlertTryRefresh:NO];
            return;
        }
        NSDictionary* json = responseObject;
        NSError* error = nil;
        NSURLRequest* finalRequest = [[AFHTTPRequestSerializer serializer] requestBySerializingRequest:pcRequest withParameters:json error:&error];
        [[UIApplication sharedApplication] openURL:finalRequest.URL];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [MBProgressHUD hideHUDForView:welf.view animated:YES];
        if (!operation.response) {
            [PCUtils showConnectionToServerTimedOutAlert];
            return;
        }
        switch (operation.response.statusCode) {
            case 407:
            {
                [[AuthenticationController sharedInstance] addLoginObserver:self success:^{
                    [welf startRequest];
                } userCancelled:^{
                    // Nothing to do
                } failure:^(NSError *error) {
                    [PCUtils showUnknownErrorAlertTryRefresh:NO];
                }];
                break;
            }
            default:
                [PCUtils showUnknownErrorAlertTryRefresh:NO];
                break;
        }
    }];
    self.profileURLOperation.responseSerializer = [AFJSONResponseSerializer serializer];
    [self.profileURLOperation start];
    
}

#pragma mark - Dealloc

- (void)dealloc
{
    [[AuthenticationController sharedInstance] removeLoginObserver:self];
    [self.profileURLOperation cancel];
    [self.profileURLOperation setCompletionBlockWithSuccess:NULL failure:NULL];
}

@end
