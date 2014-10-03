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

#import "RecommendedAppTableViewCell.h"

#import "RecommendedAppThumbView.h"

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
static const NSTimeInterval kRefreshValiditySeconds = 1.0;//300.0; //5 min.

@implementation RecommendedAppsListViewController

#pragma mark - Init

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/recommendedapps";
        self.recommendedAppService = [RecommendedAppsService sharedInstanceToRetain];
    }
    return self;
}

#pragma mark - UIViewController overrides

- (void)viewDidLoad {
    [super viewDidLoad];
    
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;

    self.tableView.rowHeight = 140.0;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshIfNeeded) name:UIApplicationDidBecomeActiveNotification object:[UIApplication sharedApplication]];
    
    self.lgRefreshControl = [[LGARefreshControl alloc] initWithTableViewController:self refreshedDataIdentifier:[LGARefreshControl dataIdentifierForPluginName:@"recommendedapps" dataName:@"recommendedapps"]];
    [self.lgRefreshControl setTarget:self selector:@selector(refresh)];
}

- (void)viewWillAppear:(BOOL)animated  {
    [super viewWillAppear:animated];
    [self trackScreen];
    [self refreshIfNeeded];
}

#pragma mark - Private

- (void)refresh {
    [self.recommendedAppService cancelOperationsForDelegate:self];
    [self.lgRefreshControl startRefreshing];
    RecommendedAppsRequest* request = [[RecommendedAppsRequest alloc] initWithLanguage:[PCUtils userLanguageCode] appStore:AppStore_iOS];
    [self.recommendedAppService getRecommendedApps:request delegate:self];
    
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
            NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"RecommendedAppsDisclaimerView" owner:nil options:nil];
            self.tableView.tableHeaderView = elements[0];
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

#pragma mark - UITableViewDelegate

- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    RecommendedAppCategory* category = self.recommendedAppsResponse.categories[section];
    NSString* title = [category.categoryName uppercaseString];
    PCTableViewSectionHeader* header = [[PCTableViewSectionHeader alloc] initWithSectionTitle:title tableView:tableView showInfoButton:NO];
    return header;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return [PCTableViewSectionHeader preferredHeightWithInfoButton:NO]; //we want all section headers to be same height
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RecommendedAppCategory* category = self.recommendedAppsResponse.categories[indexPath.section];
    RecommendedAppTableViewCell* cell = nil;
    __weak __typeof(self) welf = self;
    if (!cell) {
        cell = [[RecommendedAppTableViewCell alloc] initWithRecommendedApps:[self recommendedAppsInCategory:category] forCategory:category andAppThumbTappedBlock:^(RecommendedAppThumbView *thumbView) {
            RecommendedApp* app = thumbView.recommendedApp;
            
            NSString* appOpenURLPattern = app.appOpenURLPattern;
            if(appOpenURLPattern){
                BOOL canOpen = [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:appOpenURLPattern]];
                if(canOpen){
                    NSString* actualAppOpenURL = [NSString stringWithFormat:appOpenURLPattern, @"org.pocketcampus"];
                    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:actualAppOpenURL]];
                    return;
                }
            }
            NSString* appStoreQuery = app.appStoreQuery;
            NSNumber* appStoreAppId = @([appStoreQuery integerValue]);
            SKStoreProductViewController* productViewController = [SKStoreProductViewController new];
            productViewController.delegate = welf;
            [productViewController loadProductWithParameters:@{SKStoreProductParameterITunesItemIdentifier:appStoreAppId} completionBlock:NULL];
            [self presentViewController:productViewController animated:YES completion:NULL];
        }];
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    }
    cell.textLabel.text = category.categoryName;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.recommendedAppsResponse.categories.count;
}

#pragma mark - SKStoreProductViewControllerDelegate

- (void)productViewControllerDidFinish:(SKStoreProductViewController *)viewController{
    [self dismissViewControllerAnimated:YES completion:NULL];
}
#pragma mark - Dealloc

- (void)dealloc {
    [self.recommendedAppService cancelOperationsForDelegate:self];
}

@end;