/*
 * Copyright (c) 2014, PocketCampus.Org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of PocketCampus.Org nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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

//  Created by Silviu Andrica on 8/18/14.

#import "RecommendedAppsListViewController.h"

#import "RecommendedAppsService.h"

#import "RecommendedAppsForCategoryCell.h"

#import "PCTableViewSectionHeader.h"

@import StoreKit;

@interface RecommendedAppsListViewController()<RecommendedAppsServiceDelegate, SKStoreProductViewControllerDelegate>

@property (nonatomic, strong) RecommendedAppsService* recommendedAppService;
@property (nonatomic, strong) RecommendedAppsResponse* recommendedAppsResponse;
@property (nonatomic, strong) LGARefreshControl* lgRefreshControl;

@end

/*
 * Will refresh if last refresh date is older than kRefreshValiditySeconds ago.
 */
static NSTimeInterval const kRefreshValiditySeconds = 3600.0; //1 hour

static NSInteger const kDisclaimerSection = 0;

@implementation RecommendedAppsListViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.gaiScreenName = @"/recommendedapps";
        self.recommendedAppService = [RecommendedAppsService sharedInstanceToRetain];
        self.recommendedAppsResponse = [self.recommendedAppService getFromCacheRecommendedAppsForRequest:[self createRecommendedAppsRequest]];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    
    self.tableView.separatorColor = [UIColor clearColor];
    
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"recommendedapps" dataName:@"recommendedapps"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self refreshIfNeeded];
}

#pragma mark - Private

- (RecommendedAppsRequest*)createRecommendedAppsRequest {
    return [[RecommendedAppsRequest alloc] initWithLanguage:[PCUtils userLanguageCode] appStore:AppStore_iOS];
}

- (void)refresh {
    [self.recommendedAppService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshing];
    [self.recommendedAppService getRecommendedApps:[self createRecommendedAppsRequest] delegate:self];
    
}

- (void)refreshIfNeeded {
    if (!self.recommendedAppsResponse || [self.lgRefreshControl shouldRefreshDataForValidity:kRefreshValiditySeconds]) {
        [self refresh];
    }
}

- (NSArray*)recommendedAppsInCategory:(RecommendedAppCategory*)category{
    NSMutableArray* appsInCategory = [NSMutableArray new];
    for(NSNumber* appId in category.appIds){
        RecommendedApp* app = self.recommendedAppsResponse.apps[appId];
        [appsInCategory addObject:app];
    }
    return appsInCategory;
}

#pragma mark - RecommendedAppsServiceDelegate

- (void)getRecommendedAppsForRequest:(RecommendedAppsRequest *)request didReturn:(RecommendedAppsResponse *)response {
    switch (response.status) {
        case RecommendedAppsResponseStatus_OK:
        {
            self.recommendedAppsResponse = response;
            [self.tableView reloadData];
            [self.lgRefreshControl endRefreshingAndMarkSuccessful];
            break;
        }
        default:
            [self getRecommendedAppsFailedForRequest:request];
            break;
    }
}

- (void)getRecommendedAppsFailedForRequest:(RecommendedAppsRequest*)request {
    [PCUtils showServerErrorAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ServerErrorShort", @"PocketCampus", nil)];

}

- (void)serviceConnectionToServerFailed {
    [PCUtils showConnectionToServerTimedOutAlert];
    [self.lgRefreshControl endRefreshingWithDelay:2.0 indicateErrorWithMessage:NSLocalizedStringFromTable(@"ConnectionToServerTimedOutShort", @"PocketCampus", nil)];
}

#pragma mark - SKStoreProductViewControllerDelegate

- (void)productViewControllerDidFinish:(SKStoreProductViewController *)viewController{
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UITableViewDelegate

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    switch (section) {
        case kDisclaimerSection:
            return nil;
        default:
        {
            RecommendedAppCategory* category = self.recommendedAppsResponse.categories[section - 1];
            PCTableViewSectionHeader* header = [[PCTableViewSectionHeader alloc] initWithSectionTitle:category.categoryName tableView:tableView showInfoButton:NO];
            return header;
        }
    }
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    switch (section) {
        case kDisclaimerSection:
            return 0.0;
        default:
        {
            return [PCTableViewSectionHeader preferredHeightWithInfoButton:NO]; //we want all section headers to be same height;
        }
    }
    return 0.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.section) {
        case kDisclaimerSection:
            return 60.0;
        default:
            return [RecommendedAppsForCategoryCell preferredHeight];
    }
    return 0.0;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = nil;
    switch (indexPath.section) {
        case kDisclaimerSection:
        {
            UITableViewCell* disclaimerCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            disclaimerCell.contentView.backgroundColor = [UIColor colorWithRed:0.9725 green:0.9725 blue:0.9725 alpha:1.0];
            disclaimerCell.textLabel.font = [UIFont systemFontOfSize:12.0];
            disclaimerCell.textLabel.textColor = [UIColor darkGrayColor];
            disclaimerCell.textLabel.textAlignment = NSTextAlignmentCenter;
            disclaimerCell.textLabel.numberOfLines = 0;
            disclaimerCell.selectionStyle = UITableViewCellSelectionStyleNone;
            disclaimerCell.textLabel.text = NSLocalizedStringFromTable(@"Disclaimer", @"RecommendedAppsPlugin", nil);
            cell = disclaimerCell;
            break;
        }
        default:
        {
            RecommendedAppCategory* category = self.recommendedAppsResponse.categories[indexPath.section - 1];
            __weak __typeof(self) welf = self;
            RecommendedAppsForCategoryCell* categCell = [[RecommendedAppsForCategoryCell alloc] initWithRecommendedApps:[self recommendedAppsInCategory:category] forCategory:category appTappedBlock:^(RecommendedApp *app) {
                NSString* appOpenURLPattern = app.appOpenURLPattern;
                if(appOpenURLPattern){
                    BOOL canOpen = [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:appOpenURLPattern]];
                    if(canOpen){
                        [welf trackAction:@"OpenApp" contentInfo:app.appName];
                        NSString* actualAppOpenURL = [NSString stringWithFormat:appOpenURLPattern, @"org.pocketcampus"];
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:actualAppOpenURL]];
                        return;
                    }
                }
                [welf trackAction:@"ViewAppStoreSheet" contentInfo:app.appName];
                NSString* appStoreQuery = app.appStoreQuery;
                NSNumber* appStoreAppId = @([appStoreQuery integerValue]);
                SKStoreProductViewController* productViewController = [SKStoreProductViewController new];
                productViewController.delegate = welf;
                [productViewController loadProductWithParameters:@{SKStoreProductParameterITunesItemIdentifier:appStoreAppId} completionBlock:NULL];
                [welf presentViewController:productViewController animated:YES completion:NULL];
            }];
            cell = categCell;
            break;
        }
    }
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.recommendedAppsResponse.categories.count > 0) {
        return self.recommendedAppsResponse.categories.count + 1; //disclaimer + categs
    }
    return 0;
}

#pragma mark - Dealloc

- (void)dealloc {
    [self.recommendedAppService cancelOperationsForDelegate:self];
}

@end;