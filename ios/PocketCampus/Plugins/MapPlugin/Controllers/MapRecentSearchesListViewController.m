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
    self.tableView.rowHeight = [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    self.title = NSLocalizedStringFromTable(@"Recents", @"PocketCampus", nil);
    if (!self.showClearButtonWithinTableView) {
        self.title = NSLocalizedStringFromTable(@"Recents", @"PocketCampus", nil);
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Clear", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(clearPressed)];
    }
    MapRecentSearchesListViewController* weakSelf __weak = self;
    [[NSNotificationCenter defaultCenter] addObserverForName:kMapRecentSearchesModifiedNotification object:self.mapService queue:nil usingBlock:^(NSNotification *note) {
        weakSelf.recentSearches = [weakSelf.mapService recentSearches];
        [weakSelf.tableView reloadData];
    }];
}

- (void)clearPressed {
    [self.mapService clearRecentSearches];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.recentSearches.count) {
        return;
    }
    if (self.showClearButtonWithinTableView && indexPath.row == 0) {
        [self clearPressed];
        return;
    }
    if (self.userSelectedRecentSearchBlock) {
        self.userSelectedRecentSearchBlock(self.recentSearches[indexPath.row - self.showClearButtonWithinTableView ? 1 : 0]);
    }
}

#pragma mark - UITableViewDataSource

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.showClearButtonWithinTableView && indexPath.row == 0) {
        UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.textLabel.textColor = [PCValues pocketCampusRed];
        cell.textLabel.textAlignment = NSTextAlignmentCenter;
        cell.textLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
        cell.textLabel.text = NSLocalizedStringFromTable(@"ClearHistory", @"MapPlugin", nil);
        return cell;
    }
    NSString* pattern = self.recentSearches[indexPath.row - (self.showClearButtonWithinTableView ? 1 : 0)];
    static NSString* const kRecentSearchCell = @"RecentSearchCell";
    PCRecentResultTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:kRecentSearchCell];
    if (!cell) {
        cell = [[PCRecentResultTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kRecentSearchCell];
        cell.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
    }
    cell.textLabel.text = pattern;
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!self.recentSearches.count) {
        return 0;
    }
    return self.recentSearches.count + (self.showClearButtonWithinTableView ? 1 : 0);
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

#pragma mark - Dealloc

- (void)dealloc {
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    @catch (NSException *exception) {}
}

@end
