//
//  RecommendedAppsListViewController.m
//  PocketCampus
//
//  Created by Silviu Andrica on 8/18/14.
//  Copyright (c) 2014 EPFL. All rights reserved.
//

#import "RecommendedAppsListViewController.h"

#import "RecommendedAppsService.h"

#import "RecommendedAppTableViewCell.h"

@interface RecommendedAppsListViewController()<RecommendedAppsServiceDelegate>

@property (nonatomic, strong) RecommendedAppsService* recommendedAppService;
@property (nonatomic, strong) RecommendedAppsResponse* recommendedAppsResponse;

@end

@implementation RecommendedAppsListViewController

- (id)init
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.gaiScreenName = @"/recommended_apps";
        self.recommendedAppService = [RecommendedAppsService sharedInstanceToRetain];
        [self.recommendedAppService getRecommendedAppsWithDelegate:self];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    PCTableViewAdditions* tableViewAdditions = [[PCTableViewAdditions alloc] init];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 130.0;
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.recommendedAppsResponse.categories.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RecommendedAppCategory* category = self.recommendedAppsResponse.categories[indexPath.row];
    RecommendedAppTableViewCell* cell = nil;
    if (!cell) {
        cell = [[RecommendedAppTableViewCell alloc] initWithRecommendedApps:[self recommendedAppsInCategory:category] forCategory:category];
        cell.accessoryType = [PCUtils isIdiomPad] ? UITableViewCellAccessoryNone : UITableViewCellAccessoryDisclosureIndicator;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    }
    cell.textLabel.text = category.categoryName;
    //    cell.accessibilityHint = NSLocalizedStringFromTable(@"ShowsMenuForThisRestaurant", @"FoodPlugin", nil);
    return cell;
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

- (void)serviceConnectionToServerFailed{
    CLS_LOG(@"serviceConnectionToServerFailed");
}

- (void)getRecommendedAppsDidReturn:(RecommendedAppsResponse *)response{
    self.recommendedAppsResponse = response;
    [self.tableView reloadData];
}
@end;