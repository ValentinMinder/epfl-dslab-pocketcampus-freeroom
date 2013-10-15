//
//  MapRecentSearchesListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MapRecentSearchesListViewController.h"

#import "MapService.h"

#import "PCRecentResultTableViewCell.h"

@interface MapRecentSearchesListViewController ()

@property (nonatomic, strong) MapService* mapService;
@property (nonatomic, strong) NSOrderedSet* recentSearches;

@end

@implementation MapRecentSearchesListViewController

- (id)initWithUserSelectedRecentSearchBlock:(void (^)(NSString* searchPattern))userSelectedRecentSearchBlock {
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        self.mapService = [MapService sharedInstanceToRetain];
        self.userSelectedRecentSearchBlock = userSelectedRecentSearchBlock;
        self.recentSearches = [self.mapService recentSearches];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 0, 0, 0);
    
    MapRecentSearchesListViewController* weakSelf __weak = self;
    [[NSNotificationCenter defaultCenter] addObserverForName:kMapRecentSearchesModifiedNotificationName object:self.mapService queue:nil usingBlock:^(NSNotification *note) {
        weakSelf.recentSearches = [weakSelf.mapService recentSearches];
        [weakSelf.tableView reloadData];
    }];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.recentSearches.count) {
        return;
    }
    if (indexPath.row == 0) { //clear button row
        [self.mapService clearRecentSearches];
        return;
    }
    if (self.userSelectedRecentSearchBlock) {
        self.userSelectedRecentSearchBlock(self.recentSearches[indexPath.row-1]);
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.textLabel.textColor = [PCValues pocketCampusRed];
        cell.textLabel.textAlignment = NSTextAlignmentCenter;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
        cell.textLabel.text = NSLocalizedStringFromTable(@"ClearHistory", @"MapPlugin", nil);
        return cell;
    }
    NSString* pattern = self.recentSearches[indexPath.row-1];
    static NSString* kRecentSearchCell = @"RecentSearchCell";
    PCRecentResultTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kRecentSearchCell];
    if (!cell) {
        cell = [[PCRecentResultTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kRecentSearchCell];
        cell.backgroundColor = [UIColor clearColor];
    }
    cell.textLabel.text = pattern;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.recentSearches.count) {
        return 0;
    }
    return self.recentSearches.count+1; //clear cell at index 0,0
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
