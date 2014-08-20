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

@import StoreKit;

@interface RecommendedAppsListViewController()<RecommendedAppsServiceDelegate, SKStoreProductViewControllerDelegate>

@property (nonatomic, strong) RecommendedAppsService* recommendedAppService;
@property (nonatomic, strong) RecommendedAppsResponse* recommendedAppsResponse;

@end

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
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
    
    RecommendedAppsRequest* request = [[RecommendedAppsRequest alloc] initWithLanguage:[PCUtils userLanguageCode] appStore:AppStore_iOS];
    [self.recommendedAppService getRecommendedApps:request delegate:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Private

- (NSArray*)recommendedAppsInCategory:(RecommendedAppCategory*)category{
    NSMutableArray* appsInCategory = [NSMutableArray new];
    for(NSNumber* appId in category.appIds){
        RecommendedApp* app = self.recommendedAppsResponse.apps[appId];
        [appsInCategory addObject:app];
    }
    return appsInCategory;
}

#pragma mark - RecommendedAppsServiceDelegate

- (void)getRecommendedAppsForRequest:(RecommendedAppsRequest *)request didReturn:(RecommendedAppsResponse *)response{
    self.recommendedAppsResponse = response;
    [self.tableView reloadData];
    NSArray* elements = [[NSBundle mainBundle] loadNibNamed:@"RecommendedAppsDisclaimerView" owner:nil options:nil];
    self.tableView.tableHeaderView = elements[0];
}

- (void)getRecommendedAppsFailed {
#warning TODO show error message and stop loading
}

- (void)serviceConnectionToServerFailed{
#warning TODO show error message and stop loading
    CLS_LOG(@"serviceConnectionToServerFailed");
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 140.0;
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RecommendedAppCategory* category = self.recommendedAppsResponse.categories[indexPath.row];
    RecommendedAppTableViewCell* cell = nil;
    __weak __typeof(self) welf = self;
    if (!cell) {
        cell = [[RecommendedAppTableViewCell alloc] initWithRecommendedApps:[self recommendedAppsInCategory:category] forCategory:category andAppThumbTappedBlock:^(RecommendedAppThumbView *thumbView) {
            RecommendedApp* app = thumbView.recommendedApp;
            NSString* appStoreQuery = app.appStoreQuery;
            NSNumber* appStoreAppId = @([appStoreQuery integerValue]);
            SKStoreProductViewController* productViewController = [SKStoreProductViewController new];
            productViewController.delegate = welf;
            [productViewController loadProductWithParameters:@{SKStoreProductParameterITunesItemIdentifier:appStoreAppId} completionBlock:NULL];
            [self presentViewController:productViewController animated:YES completion:NULL];
        }];
//        cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    }
    cell.textLabel.text = category.categoryName;
    //    cell.accessibilityHint = NSLocalizedStringFromTable(@"ShowsMenuForThisRestaurant", @"FoodPlugin", nil);
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.recommendedAppsResponse.categories.count;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    RecommendedAppCategory* category = self.recommendedAppsResponse.categories[section];
    return [category.categoryName uppercaseString];
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