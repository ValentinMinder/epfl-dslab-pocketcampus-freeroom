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

//  Created by Loïc Gardiol on 15.10.13.

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
        self.title = NSLocalizedStringFromTable(@"Recents", @"PocketCampus", nil);
        self.mapService = [MapService sharedInstanceToRetain];
        self.userSelectedRecentSearchBlock = userSelectedRecentSearchBlock;
        self.recentSearches = [self.mapService recentSearches];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleDefault];
    };
    self.tableView.separatorInset = UIEdgeInsetsMake(0, 0, 0, 0);
    if (!self.showClearButtonWithinTableView) {
        self.title = NSLocalizedStringFromTable(@"Recents", @"PocketCampus", nil);
        UIBarButtonItem* clearButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Clear", @"PocketCampus", nil) style:UIBarButtonItemStyleBordered target:self action:@selector(clearPressed)];
        clearButton.accessibilityHint = NSLocalizedStringFromTable(@"ClearsHistoryOfRecentSearches", @"PocketCampus", nil);
        self.navigationItem.leftBarButtonItem = clearButton;
    }
    __weak __typeof(self) weakSelf = self;
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
        self.userSelectedRecentSearchBlock(self.recentSearches[indexPath.row - (self.showClearButtonWithinTableView ? 1 : 0)]);
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
        cell.accessibilityHint = NSLocalizedStringFromTable(@"ClearsHistoryOfRecentSearches", @"PocketCampus", nil);
        return cell;
    }
    NSString* pattern = self.recentSearches[indexPath.row - (self.showClearButtonWithinTableView ? 1 : 0)];
    NSString* const kRecentSearchCell = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"RecentSearchCell"];
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
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
