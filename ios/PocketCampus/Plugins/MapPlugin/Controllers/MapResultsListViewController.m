//
//  MapResultsListViewController.m
//  PocketCampus
//
//  Created by Loïc Gardiol on 15.10.13.
//  Copyright (c) 2013 EPFL. All rights reserved.
//

#import "MapResultsListViewController.h"

#import "map.h"

#import "PCCenterMessageCell.h"

@interface MapResultsListViewController ()

@property (nonatomic, strong) NSArray* mapItems;
@property (nonatomic, strong) NSArray* selectedInitially;

@end

@implementation MapResultsListViewController

- (id)initWithMapItems:(NSArray*)mapItems selectedInitially:(NSArray*)selectedInitially userValidatedSelectionBlock:(void (^)(NSArray* newlySelected))userValidatedSelectionBlock
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        [PCUtils throwExceptionIfObject:mapItems notKindOfClass:[NSArray class]];
        self.mapItems = mapItems;
        self.selectedInitially = selectedInitially;
        self.userValidatedSelectionBlock = userValidatedSelectionBlock;
        self.title = NSLocalizedStringFromTable(@"AllSearchResults", @"MapPlugin", nil);
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    PCTableViewAdditions* tableViewAdditions = [PCTableViewAdditions new];
    self.tableView = tableViewAdditions;
    tableViewAdditions.rowHeightBlock = ^CGFloat(PCTableViewAdditions* tableView) {
        return [PCTableViewCellAdditions preferredHeightForDefaultTextStylesForCellStyle:UITableViewCellStyleSubtitle];
    };
    if (![PCUtils isIdiomPad]) {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    }
}

- (NSUInteger)supportedInterfaceOrientations
{
    return [PCUtils isIdiomPad] ? UIInterfaceOrientationMaskAll : UIInterfaceOrientationMaskPortrait;
}

#pragma mark - Buttons actions

- (void)cancelButtonPressed {
    [self.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.mapItems.count) {
        return;
    }
    if (!self.userValidatedSelectionBlock) {
        return;
    }
    self.userValidatedSelectionBlock(@[self.mapItems[indexPath.row]]);
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.mapItems && [self.mapItems count] == 0) {
        if (indexPath.row == 1) {
            return [[PCCenterMessageCell alloc] initWithMessage:NSLocalizedStringFromTable(@"NoResult", @"MapPlugin", nil)];
        } else {
            UITableViewCell* cell =[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        }
    }
    
    NSString* const identifier = [(PCTableViewAdditions*)tableView autoInvalidatingReuseIdentifierForIdentifier:@"MapItemCell"];
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:identifier];
        cell.textLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultTextLabelTextStyle];
        cell.detailTextLabel.font = [UIFont preferredFontForTextStyle:PCTableViewCellAdditionsDefaultDetailTextLabelTextStyle];
    }
    
    MapItem* mapItem = self.mapItems[indexPath.row];
    
    cell.textLabel.text = mapItem.title;
    cell.detailTextLabel.text = mapItem.description;
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (self.mapItems) {
        NSInteger count = [self.mapItems count];
        if (count == 0) {
            return 2; //no category message a row index 1
        }
        return count;
    }
    
    return 0;
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    
    if (self.mapItems) {
        return 1;
    }
    return 0;
}

@end
